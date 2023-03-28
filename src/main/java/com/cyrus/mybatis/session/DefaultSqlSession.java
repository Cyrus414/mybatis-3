package com.cyrus.mybatis.session;

import com.cyrus.mybatis.proxy.MapperInvocationHandler;
import com.cyrus.mybatis.util.DocumentUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description here
 *
 * @author Cyrus Chen
 * @since 2023-03-24 10:29 AM
 */
public class DefaultSqlSession implements SqlSession {
  private final Connection connection;
  private final Configuration configuration;

  private final Map<Class<?>, Object> mapperMap;

  public DefaultSqlSession(Connection connection, Configuration configuration) {
     this.connection = connection;
     this.configuration = configuration;
     mapperMap = new HashMap<>();

     processMappers();
  }

  private void processMappers() {
    List<String> mapperList = configuration.getMapperList();
    for (String mapper : mapperList) {
      loadMapperFromXML(mapper);
    }
  }

  private void loadMapperFromXML(String filePath) {
    InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(filePath);

    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = null;
    try {
      documentBuilder = documentBuilderFactory.newDocumentBuilder();
      Document document = documentBuilder.parse(resourceAsStream);


      Element mapper = DocumentUtil.getElement(document, "mapper");
      String namespace = mapper.getAttribute("namespace");

      Class<?> mapperClass = Class.forName(namespace);
      if (mapperMap.containsKey(mapperClass)) {
        throw new RuntimeException("mapper已经存在");
      }

      MapperInvocationHandler mapperInvocationHandler = new MapperInvocationHandler();

      NodeList childNodes = mapper.getChildNodes();
      // 遍历所有的子节点
      for (int i = 0; i < childNodes.getLength(); i++) {
        Node node = childNodes.item(i);
        if (node instanceof Element) {
          Element element = (Element) node;
          String nodeName = element.getNodeName();
          if ("select".equals(nodeName)) {
            // 解析select节点
            MappedStatement mappedStatement = processSelect(element);
            mapperInvocationHandler.addMappedStatement(mappedStatement.getId(), mappedStatement);
          }
        }
      }

      // 使用动态代理生成代理对象
      Object o = Proxy.newProxyInstance(mapperClass.getClassLoader(), new Class[]{mapperClass} , mapperInvocationHandler);
      mapperMap.put(mapperClass, o);

    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

  }

  private MappedStatement processSelect(Element element) {
    String id = element.getAttribute("id");
    String resultType = element.getAttribute("resultType");
    String sql = element.getTextContent();

    // 将解析出来的信息封装成MappedStatement对象
    return new MappedStatement(id, resultType, sql);
  }


  @Override
  public <T> T getMapper(Class<T> mapperClass) {
    Object o = mapperMap.get(mapperClass);
    if (o == null) {
      throw new RuntimeException("没有找到对应的mapper");
    }
    return (T) o;
  }

  @Override
  public void close() throws IOException {

  }
}
