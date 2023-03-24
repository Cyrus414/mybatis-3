package com.cyrus.mybatis;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.junit.Test;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description here
 *
 * @author Cyrus Chen
 * @since 2023-03-24 4:18 PM
 */
public class TestOgnl {

  @Test
  public void testRegex() {
    Pattern pattern = Pattern.compile("#\\{([a-zA-Z\\.]+)\\}");
    String sql = "select * from user where id = #{id} and name = #{name}";
    Matcher matcher = pattern.matcher(sql);

    while (matcher.find()) {
      String group = matcher.group(0);
      System.out.println(group);
    }

  }

  @Test
  public void testOgnl() throws OgnlException {
    Student student = new Student();
    student.setId(1);
    student.setName("cyrus");
    Map defaultContext = Ognl.createDefaultContext(student);

    Object id = Ognl.getValue("#{id}", defaultContext, student);
    System.out.println(id);
    Object name = Ognl.getValue("#{name}", defaultContext, student);
    System.out.println(name);

  }

  class Student {
    private int id;

    private String name;

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}
