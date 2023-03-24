package com.cyrus.mybatis.execute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Description here
 *
 * @author Cyrus Chen
 * @since 2023-03-24 4:09 PM
 */
public class Executor {
  private Connection connection;

  public Executor(Connection connection) {
    this.connection = connection;
  }

  public <T> T query(String statement, Map<String, Object> parameterMap) throws SQLException {
    SqlPair sqlPair = preHandle(statement, parameterMap);
    PreparedStatement preparedStatement = connection.prepareStatement(sqlPair.getSql());
    List<Object> parameters = sqlPair.getParameters();
    for (int i = 0; i < parameters.size(); i++) {
      preparedStatement.setObject(i + 1, parameters.get(i));
    }

    return null;
  }

  private SqlPair preHandle(String statement, Map<String, Object> parameterMap) {
    
    return null;
  }

  class SqlPair {
    private final String sql;
    private final List<Object> parameters;

    public SqlPair(String sql, List<Object> parameters) {
      this.sql = sql;
      this.parameters = parameters;
    }

    public String getSql() {
      return sql;
    }

    public List<Object> getParameters() {
      return parameters;
    }
  }
}
