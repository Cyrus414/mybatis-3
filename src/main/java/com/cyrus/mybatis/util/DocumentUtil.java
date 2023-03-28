package com.cyrus.mybatis.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DocumentUtil {
  private DocumentUtil() {
  }

  public static Element getElement(Element element, String tagName) {
    NodeList nodeList = element.getElementsByTagName(tagName);
    return (Element) nodeList.item(0);
  }

  public static Element getElement(Document document, String tagName) {
    NodeList configuration = document.getElementsByTagName(tagName);
    return (Element) configuration.item(0);
  }
}
