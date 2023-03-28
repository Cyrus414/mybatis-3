package com.cyrus.mybatis.session;

import com.cyrus.mybatis.datasource.MyDataSource;
import com.cyrus.mybatis.util.DocumentUtil;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Description here
 *
 * @author Cyrus Chen
 * @since 2023-03-24 10:23 AM
 */
public class SqlSessionFactoryBuilder {

  public SqlSessionFactory build(InputStream resourceAsStream) {

    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      Document document = documentBuilder.parse(resourceAsStream);

      Element configurationElement = DocumentUtil.getElement(document, "configuration");
      Configuration configuration = buildConfiguration(configurationElement);

      return new DefaultSqlSessionFactory(configuration);
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 将xml文件中的configuration节点解析成Configuration对象
   *
   * @param configurationElement xml中configuration节点
   * @return Configuration对象
   */
  private Configuration buildConfiguration(Element configurationElement) {
    Configuration configuration = new Configuration();

    // 提取environment信息
    processEnvironment(configurationElement, configuration);

    // 提取mapper信息
    processMapper(configurationElement, configuration);

    return configuration;
  }

  private void processMapper(Element configurationElement, Configuration configuration) {
    Element mappers = DocumentUtil.getElement(configurationElement, "mappers");
    NodeList mapper = mappers.getElementsByTagName("mapper");
    for (int i = 0; i < mapper.getLength(); i++) {
      Element mapperElement = (Element) mapper.item(i);
      String resource = mapperElement.getAttribute("resource");
      configuration.addMapper(resource);
    }
  }

  private void processEnvironment(Element configurationElement, Configuration configuration) {
    // 解析environments节点
    Element environments = DocumentUtil.getElement(configurationElement, "environments");

    // 解析每个environment节点，将其转换成DataSource对象
    NodeList environment = environments.getElementsByTagName("environment");
    for (int i = 0; i < environment.getLength(); i++) {
      Element environmentElement = (Element) environment.item(i);

      String id = environmentElement.getAttribute("id");
      DataSource dataSource = getDataSource(environmentElement);

      configuration.addDataSource(id, dataSource);
    }
    String defaultEnvironmentName = environments.getAttribute("default");
    configuration.setDefaultEnvironmentName(defaultEnvironmentName);
  }

  private DataSource getDataSource(Element dataSourceElement) {
    Properties properties = getProperties(dataSourceElement);

    return new MyDataSource(properties);
  }

  private Properties getProperties(Element dataSourceElement) {
    String driver = dataSourceElement.getAttribute("driver");
    String url = dataSourceElement.getAttribute("url");
    String username = dataSourceElement.getAttribute("username");
    String password = dataSourceElement.getAttribute("password");

    Properties properties = new Properties();
    properties.setProperty("user", username);
    properties.setProperty("password", password);
    properties.setProperty("url", url);
    properties.setProperty("driver", driver);
    return properties;
  }

  private Element getDefaultEnvironment(Element configuration) {
    Element environments = DocumentUtil.getElement(configuration, "environments");
    String defaultEnvironment = environments.getAttribute("default");

    NodeList environment = environments.getElementsByTagName("environment");
    for (int i = 0; i < environment.getLength(); i++) {
      Element environmentElement = (Element) environment.item(i);
      String id = environmentElement.getAttribute("id");
      if (id.equals(defaultEnvironment)) {
        return environmentElement;
      }
    }
    return null;
  }

}
