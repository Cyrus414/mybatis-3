package org.apache.ibatis.learn;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.submitted.permissions.Resource;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Description here
 *
 * @author Cyrus Chen
 * @since 2022-12-28 9:03 AM
 */
public class Learn1 {
  public static void main(String[] args) throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("learn/mybatis-config.xml");

    SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
    SqlSessionFactory factory = builder.build(resourceAsStream);

    try (SqlSession sqlSession = factory.openSession()) {
      StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);

      List<StudentDO> studentDOS = mapper.queryAll();
      for (StudentDO studentDO : studentDOS) {
        System.out.println(studentDO);
      }
    }
  }

  @Test
  public void testInit() throws IOException {
    InputStream inputStream = Resources.getResourceAsStream("learn/mybatis-config.xml");
    XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, null,null); // 看这里
    Configuration configuration = parser.parse();

    configuration.addLoadedResource("learn/StudentMapper.xml");
    configuration.addMapper(StudentMapper.class);

    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);

    SqlSession sqlSession = sqlSessionFactory.openSession();
    StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
    System.out.println(mapper);

  }

  @Test
  public void testInit2() throws IOException {
    InputStream inputStream = Resources.getResourceAsStream("learn/mybatis-config.xml");
    XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, null,null); // 看这里
    Configuration configuration = parser.parse();

    String resource = "learn/StudentMapper.xml";
    try(InputStream mapperIs = Resources.getResourceAsStream(resource)) {
      XMLMapperBuilder mapperParser = new XMLMapperBuilder(mapperIs, configuration, resource, configuration.getSqlFragments());
      mapperParser.parse();
    }

    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);

    SqlSession sqlSession = sqlSessionFactory.openSession();
    StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
    System.out.println(mapper);


  }
}
