package com.cyrus.mybatis.session;

import com.cyrus.mybatis.proxy.MapperInvocationHandler;

import java.io.IOException;
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

  private Map<Class<?>, Object> mapperMap;

  public DefaultSqlSession(Connection connection, Configuration configuration) {
     this.connection = connection;
     this.configuration = configuration;
     mapperMap = new HashMap<>();

     processMappers();
  }

  private void processMappers() {
    List<String> mapperList = configuration.getMapperList();
    for (String mapper : mapperList) {
      try {
        Class<?> mapperClass = Class.forName(mapper);
        // 获取接口的全限定名
        String nameSpace = mapperClass.getName();

        // 使用动态代理生成代理对象
        Object o = Proxy.newProxyInstance(mapperClass.getClassLoader(), mapperClass.getInterfaces(), new MapperInvocationHandler(nameSpace));
        mapperMap.put(mapperClass, o);

      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }



  @Override
  public <T> T getMapper(Class<T> mapperClass) {

    return null;
  }

  @Override
  public void close() throws IOException {

  }
}
