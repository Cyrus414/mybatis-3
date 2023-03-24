package com.cyrus.mybatis.session;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description here
 *
 * @author Cyrus Chen
 * @since 2023-03-24 10:48 AM
 */
public class Configuration {
  private final Map<String, DataSource> dataSourceMap;

  private final List<String> mapperList;

  private String defaultEnvironmentName;

  public Configuration() {
    dataSourceMap = new HashMap<>();
    mapperList = new ArrayList<>();
  }

  public void addDataSource(String name, DataSource dataSource) {
    dataSourceMap.put(name, dataSource);
  }

  public DataSource getDataSource(String name) {
    return dataSourceMap.get(name);
  }

  public void addMapper(String mapper) {
    mapperList.add(mapper);
  }

  public List<String> getMapperList() {
    return mapperList;
  }

  public String getDefaultEnvironmentName() {
    return defaultEnvironmentName;
  }

  public void setDefaultEnvironmentName(String defaultEnvironmentName) {
    this.defaultEnvironmentName = defaultEnvironmentName;
  }
}
