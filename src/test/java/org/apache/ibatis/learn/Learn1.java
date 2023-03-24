package org.apache.ibatis.learn;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.submitted.permissions.Resource;

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
}
