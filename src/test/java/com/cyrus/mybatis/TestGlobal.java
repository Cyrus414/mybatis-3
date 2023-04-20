package com.cyrus.mybatis;

import com.cyrus.mybatis.io.Resources;
import com.cyrus.mybatis.session.SqlSession;
import com.cyrus.mybatis.session.SqlSessionFactory;
import com.cyrus.mybatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.learn.StudentDO;
import org.apache.ibatis.learn.StudentMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Description here
 *
 * @author Cyrus Chen
 * @since 2023-03-24 10:21 AM
 */
public class TestGlobal {
  public static void main(String[] args) {
    InputStream resourceAsStream = Resources.getResourceAsStream("learn/mybatis-config.xml");

    SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
    SqlSessionFactory factory = builder.build(resourceAsStream);

    try (SqlSession sqlSession = factory.openSession()) {
      StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);

      List<StudentDO> studentDOS = mapper.queryAll();
      for (StudentDO studentDO : studentDOS) {
        System.out.println(studentDO);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
