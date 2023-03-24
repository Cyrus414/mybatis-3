package com.cyrus.mybatis.session;

/**
 * Description here
 *
 * @author Cyrus Chen
 * @since 2023-03-24 10:24 AM
 */
public class DefaultSqlSessionFactory  implements SqlSessionFactory {
  private final Configuration configuration;

  public DefaultSqlSessionFactory(Configuration configuration) {
    this.configuration = configuration;

  }

  @Override
  public SqlSession openSession() {
    return null;
  }
}
