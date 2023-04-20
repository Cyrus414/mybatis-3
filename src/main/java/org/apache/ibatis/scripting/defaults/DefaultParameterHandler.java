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
package org.apache.ibatis.scripting.defaults;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public class DefaultParameterHandler implements ParameterHandler {

  private final TypeHandlerRegistry typeHandlerRegistry;

  private final MappedStatement mappedStatement;
  // 参数对象是个啥？
  // 1. 如果是简单类型，那么就是基本类型或者包装类型
  // 2. 如果是复杂类型，那么就是实体类
  // 3. 如果是集合类型，那么就是List、Set、Map
  // 4. 如果是数组类型，那么就是数组
  // 5. 如果是null，那么就是null
  // 6. 如果是Map类型，那么就是Map
  // 7. 如果是其他类型，那么就是其他类型
  private final Object parameterObject;
  private final BoundSql boundSql;
  private final Configuration configuration;

  public DefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
    this.mappedStatement = mappedStatement;
    this.configuration = mappedStatement.getConfiguration();
    this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
    this.parameterObject = parameterObject;
    this.boundSql = boundSql;
  }

  @Override
  public Object getParameterObject() {
    return parameterObject;
  }

  @Override
  public void setParameters(PreparedStatement ps) {
    ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
    // 1. 获取参数映射列表
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    if (parameterMappings != null) {
      // 2. 遍历参数映射列表
      for (int i = 0; i < parameterMappings.size(); i++) {
        ParameterMapping parameterMapping = parameterMappings.get(i);
        // 3. 如果参数映射中的参数模式不是输出模式
        if (parameterMapping.getMode() != ParameterMode.OUT) {
          // 4. 获取参数映射中的参数名称和参数值
          Object value;

          // propertyName形如：user.id、user[1].id、user
          String propertyName = parameterMapping.getProperty();

          // 下面这段判断逻辑是为了拿到具体的参数值
          // 这里的参数值可能是基本类型、包装类型、实体类、Map、List、Set、数组等
          // 所以需要分别处理

          // 4.1 参数名是否出现在additionalParameters中
          if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
            value = boundSql.getAdditionalParameter(propertyName);
          } else if (parameterObject == null) { // 4.2 参数对象是否为null
            value = null;
          } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) { // 4.3 指定了具体的类型处理器
            value = parameterObject;
          } else {
            // 4.4 从参数对象中获取参数值
            MetaObject metaObject = configuration.newMetaObject(parameterObject);
            value = metaObject.getValue(propertyName);
          }

          // 5. 获取参数映射中的类型处理器
          TypeHandler typeHandler = parameterMapping.getTypeHandler();
          // 6. 获取参数映射中的jdbc类型
          JdbcType jdbcType = parameterMapping.getJdbcType();
          if (value == null && jdbcType == null) {
            jdbcType = configuration.getJdbcTypeForNull();
          }
          // 7. 使用typeHandler设置参数值
          try {
            typeHandler.setParameter(ps, i + 1, value, jdbcType);
          } catch (TypeException | SQLException e) {
            throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
          }
        }
      }
    }
  }

}
