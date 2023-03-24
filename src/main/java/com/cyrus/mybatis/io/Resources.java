package com.cyrus.mybatis.io;

import java.io.InputStream;

/**
 * Description here
 *
 * @author Cyrus Chen
 * @since 2023-03-24 10:20 AM
 */
public class Resources {
  private Resources() {

  }

  public static InputStream getResourceAsStream(String path) {
    return Resources.class.getClassLoader().getResourceAsStream(path);
  }
}
