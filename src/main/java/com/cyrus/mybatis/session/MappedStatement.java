package com.cyrus.mybatis.session;

public class MappedStatement {
  private String id;
  private String resultType;
  private String sql;

  public MappedStatement(String id, String resultType, String sql) {
    this.id = id;
    this.resultType = resultType;
    this.sql = sql;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getResultType() {
    return resultType;
  }

  public void setResultType(String resultType) {
    this.resultType = resultType;
  }

  public String getSql() {
    return sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }

  public Object execute(Object[] args) {
    System.out.println("execute sql: " + sql);
    return null;
  }
}
