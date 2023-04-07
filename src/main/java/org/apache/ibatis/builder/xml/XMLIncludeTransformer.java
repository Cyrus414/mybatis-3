/*
 *    Copyright 2009-2022 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.builder.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.parsing.PropertyParser;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Frank D. Martinez [mnesarco]
 */
public class XMLIncludeTransformer {

  private final Configuration configuration;
  private final MapperBuilderAssistant builderAssistant;

  public XMLIncludeTransformer(Configuration configuration, MapperBuilderAssistant builderAssistant) {
    this.configuration = configuration;
    this.builderAssistant = builderAssistant;
  }

  public void applyIncludes(Node source) {
    Properties variablesContext = new Properties();
    Properties configurationVariables = configuration.getVariables();
    Optional.ofNullable(configurationVariables).ifPresent(variablesContext::putAll);
    // 递归处理<include>标签
    // include标签内的${}变量会被替换，其子标签中的${}变量也会被替换
    // source节点下的非<include>标签的${}变量不会被替换
    applyIncludes(source, variablesContext, false);
  }

  /**
   * Recursively apply includes through all SQL fragments.
   * 主要是处理<include>标签，将<include>标签指向的sql片段替换到<include>标签的位置
   * 同时处理给定标签内所有的${}变量
   *
   * @param source
   *          Include node in DOM tree
   * @param variablesContext
   *          Current context for static variables with values
   * @param included 是否需要处理${}变量
   */
  private void applyIncludes(Node source, final Properties variablesContext, boolean included) {
    if ("include".equals(source.getNodeName())) { // <include> 标签节点
      // 找到include的目标节点
      Node toInclude = findSqlFragment(getStringAttribute(source, "refid"), variablesContext);
      // 获取include标签中指定的变量
      Properties toIncludeContext = getVariablesContext(source, variablesContext);
      // include目标节点中也可能有include标签，递归处理
      applyIncludes(toInclude, toIncludeContext, true);
      // 检查include节点是否和source节点在同一个document中，如果不在同一个document中，需要import
      // ? 为什么要import?
      if (toInclude.getOwnerDocument() != source.getOwnerDocument()) {
        toInclude = source.getOwnerDocument().importNode(toInclude, true);
      }

      // <sql id="target"> taget sql <where> target tags </where> </sql>
      // <sql id="source">
      //   <include refid="target" />
      // </sql>

      // 把include指向的目标节点替换到使用include的节点中，方便后续处理
      source.getParentNode().replaceChild(toInclude, source);
      // <sql id="source">
      //   <sql id="target"> taget sql <where> target tags </where> </sql>
      // </sql>

      // 如上所示，现在sql标签里面还有一个sql标签，需要把sql标签里面的内容移到外面
      while (toInclude.hasChildNodes()) {
        toInclude.getParentNode().insertBefore(toInclude.getFirstChild(), toInclude);
      }

      // <sql id="source">
      //   taget sql <where> target tags </where>
      //   <sql id="target"> taget sql <where> target tags </where> </sql>
      // </sql>

      // 删除多余的sql标签
      toInclude.getParentNode().removeChild(toInclude);

      // <sql id="source">
      //   taget sql <where> target tags </where>
      // </sql>

    } else if (source.getNodeType() == Node.ELEMENT_NODE) { // 其他标签节点
      if (included && !variablesContext.isEmpty()) {
        // replace variables in attribute values
        // 替换标签属性值中的${}变量
        NamedNodeMap attributes = source.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
          Node attr = attributes.item(i);
          attr.setNodeValue(PropertyParser.parse(attr.getNodeValue(), variablesContext));
        }
      }
      // 处理子节点
      NodeList children = source.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        applyIncludes(children.item(i), variablesContext, included);
      }
    } else if (included && (source.getNodeType() == Node.TEXT_NODE || source.getNodeType() == Node.CDATA_SECTION_NODE) // 文本节点
        && !variablesContext.isEmpty()) {
      // replace variables in text node
      source.setNodeValue(PropertyParser.parse(source.getNodeValue(), variablesContext));
    }
  }

  /**
   * 从configuration中根据refid获取sql片段, 并返回一个新的Node对象
   *
   * @param refid sql片段的id，可以是当前namespace的，也可以是其他namespace的，但是必须是全限定名
   * @param variables 用来替换refid中${}变量的
   * @return
   */
  private Node findSqlFragment(String refid, Properties variables) {
    // refid 也可以是 ${} 变量, 但是只有在初始化时才会解析
    refid = PropertyParser.parse(refid, variables);
    // namespace.refid
    // q: 可以手动指定其他namespace的sql片段吗?
    // a: 可以, 但是必须是全限定名
    refid = builderAssistant.applyCurrentNamespace(refid, true);
    try {
      // 从configuration中获取sql片段, 并返回一个新的Node对象
      XNode nodeToInclude = configuration.getSqlFragments().get(refid);
      return nodeToInclude.getNode().cloneNode(true);
    } catch (IllegalArgumentException e) {
      throw new IncompleteElementException("Could not find SQL statement to include with refid '" + refid + "'", e);
    }
  }

  private String getStringAttribute(Node node, String name) {
    return node.getAttributes().getNamedItem(name).getNodeValue();
  }

  /**
   *   <include refid="someinclude">
   *     <property name="prefix" value="Some"/>
   *     <property name="include_target" value="sometable"/>
   *   </include>
   *
   *  解析include标签中的property标签并将其转换为Properties对象返回
   *
   * Read placeholders and their values from include node definition.
   *
   * @param node
   *          Include node instance
   * @param inheritedVariablesContext
   *          Current context used for replace variables in new variables values
   * @return variables context from include instance (no inherited values)
   */
  private Properties getVariablesContext(Node node, Properties inheritedVariablesContext) {
    Map<String, String> declaredProperties = null;
    NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node n = children.item(i);
      if (n.getNodeType() == Node.ELEMENT_NODE) {
        String name = getStringAttribute(n, "name");
        // Replace variables inside
        String value = PropertyParser.parse(getStringAttribute(n, "value"), inheritedVariablesContext);
        if (declaredProperties == null) {
          declaredProperties = new HashMap<>();
        }
        if (declaredProperties.put(name, value) != null) {
          throw new BuilderException("Variable " + name + " defined twice in the same include definition");
        }
      }
    }
    if (declaredProperties == null) {
      return inheritedVariablesContext;
    } else {
      Properties newProperties = new Properties();
      newProperties.putAll(inheritedVariablesContext);
      newProperties.putAll(declaredProperties);
      return newProperties;
    }
  }
}
