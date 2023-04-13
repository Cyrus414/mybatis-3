package com.cyrus.mybatis;

import org.junit.Test;

import java.util.Locale;

public class TestAlias {
  @Test
  public void testAlias() {
    String name = TestAlias.class.getSimpleName();
    System.out.println(name);
    System.out.println(name.toLowerCase(Locale.ENGLISH));

    System.out.println(name.substring(2, 100));
  }

}
