package com.cyrus.mybatis.session;

import java.io.Closeable;

/**
 * Description here
 *
 * @author Cyrus Chen
 * @since 2023-03-24 10:26 AM
 */
public interface SqlSession extends Closeable {
  <T> T getMapper(Class<T> mapperClass);
}
