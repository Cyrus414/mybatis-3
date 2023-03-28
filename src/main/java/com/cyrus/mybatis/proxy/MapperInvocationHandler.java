package com.cyrus.mybatis.proxy;

import com.cyrus.mybatis.session.MappedStatement;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Description here
 *
 * @author Cyrus Chen
 * @since 2023-03-24 3:54 PM
 */
public class MapperInvocationHandler implements InvocationHandler {
  private final Map<String, MappedStatement> statementMap;

  public MapperInvocationHandler() {
    statementMap = new HashMap<>();
  }

  public void addMappedStatement(String id, MappedStatement mappedStatement) {
    statementMap.put(id, mappedStatement);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    String name = method.getName();

    MappedStatement mappedStatement = statementMap.get(name);

    if (mappedStatement != null) {
      return mappedStatement.execute(args);
    } else {
      throw new RuntimeException("No statement found for " + name);
    }
  }
}
