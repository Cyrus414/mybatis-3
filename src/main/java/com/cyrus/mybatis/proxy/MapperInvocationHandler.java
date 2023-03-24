package com.cyrus.mybatis.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Description here
 *
 * @author Cyrus Chen
 * @since 2023-03-24 3:54 PM
 */
public class MapperInvocationHandler implements InvocationHandler {
//  private Map<String, SqlExecutor> sqlExecutorMap;

  private String nameSpace;

  public MapperInvocationHandler(String nameSpace) {
    this.nameSpace = nameSpace;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    String name = method.getName();

    return null;
  }
}
