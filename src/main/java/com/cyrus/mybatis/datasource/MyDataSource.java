package com.cyrus.mybatis.datasource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Description here
 *
 * @author Cyrus Chen
 * @since 2023-03-24 12:20 PM
 */
public class MyDataSource implements DataSource {
  private final String url;
  private final String username;
  private final String password;

  public MyDataSource(Properties properties) {
    String driver = properties.getProperty("driver");

    try {
      Class.forName(driver);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    url = properties.getProperty("url");

    username = properties.getProperty("username");
    password = properties.getProperty("password");

  }

  @Override
  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(url, username, password);
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    return DriverManager.getConnection(url, username, password);
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {

    return new PrintWriter(System.out);
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {

  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {

  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return 0;
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return null;
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return null;
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return false;
  }
}
