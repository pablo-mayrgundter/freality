package math.util;

import java.io.IOException;
import java.io.File;
import java.io.ByteArrayInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Xml {

  static private DocumentBuilder getBuilder() {
    try {
      return DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (final ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  final Document doc;

  /** @throws RuntimeException to mask ParserConfigurationException if XML subsystem fails. */
  /** @throws IllegalArgumentException to mask SAXException if given string is not valid XML. */
  public Xml(final File f) {
    try {
      doc = getBuilder().parse(f);
    } catch (SAXException e) {
      throw new IllegalArgumentException(e);
    } catch (IOException ee) {
      // Impossible.
      throw new IllegalArgumentException(ee);
    }
  }

  /** @throws RuntimeException to mask ParserConfigurationException if XML subsystem fails. */
  /** @throws IllegalArgumentException to mask SAXException if given string is not valid XML. */
  public Xml(final String s) {
    try {
      doc = getBuilder().parse(new ByteArrayInputStream(s.getBytes()));
    } catch (SAXException e) {
      throw new IllegalArgumentException(e);
    } catch (IOException ee) {
      // Impossible.
      throw new IllegalArgumentException(ee);
    }
  }

  /** @throws ParserConfigurationException as a RuntimeException if XML subsystem fails. */
  public Xml() {
    doc = getBuilder().newDocument();
  }

  public String get(final String expr) {
    try {
      final XPath xpath = XPathFactory.newInstance().newXPath();
      return xpath.evaluate(expr, doc);
    } catch (final XPathExpressionException e) {
      return null;
    }
  }

  public NodeList getNodes(final String expr) {
    try {
      final XPath xpath = XPathFactory.newInstance().newXPath();
      return (NodeList) xpath.evaluate(expr, doc, XPathConstants.NODESET);
    } catch (final XPathExpressionException e) {
      return null;
    }
  }
}