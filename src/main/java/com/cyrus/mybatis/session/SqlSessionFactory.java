package com.cyrus.mybatis.session;

/**
 * Description here
 *
 * @author Cyrus Chen
 * @since 2023-03-24 10:24 AM
 */
public interface SqlSessionFactory {
  SqlSession openSession();

}
