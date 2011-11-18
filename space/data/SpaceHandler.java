package space.data;

import space.model.CelestialBody;
import space.model.Color;
import space.model.Coordinate;
import space.model.Epoch;
import space.model.ObservedLocation;
import space.model.Orbit;
import space.model.Planet;
import space.model.Rings;
import space.model.Star;
import space.model.Universe;

import org.freality.util.Measure;
import static org.freality.util.Measure.Unit.*;
import org.freality.util.Measure.Magnitude.*;

import java.io.File;
import java.io.FileWriter;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Converts an XML datasource of celestial information into a CelestialBody Iterator.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.4 $
 */
public class SpaceHandler extends DefaultHandler {

  final Map<String,CelestialBody> bodyMap;
  final Map<String,Set<String>> childMap;

  final Stack<StackFrame> frameStack;
  StackFrame curFrame = null;
  final class StackFrame {
    String type;
    String name;
    final Map<String,String> params;
    StackFrame (final String type, final String name, final Map<String,String> params) throws SAXException {
      if (type == null || name == null)
        throw new SAXException("System must have type and name attributes.");
      this.type = type;
      this.name = name;
      this.params = params;
    }
    public String toString() { return "StackFrame: " + type + ", " + name; }
  }

  /**
   * Initializes lookup tables and lists for storing data for return
   * by parseToIterator(String).
   */
  SpaceHandler() {
    bodyMap = new LinkedHashMap<String,CelestialBody>();
    childMap = new LinkedHashMap<String,Set<String>>();
    frameStack = new Stack<StackFrame>();
  }

  public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

    if (qName.equalsIgnoreCase("system")) {
      frameStack.push(curFrame = new StackFrame(atts.getValue("type").toLowerCase(),
                                                atts.getValue("name").toLowerCase(),
                                                new HashMap<String,String>()));
    } else if (atts.getLength() == 1) {
      curFrame.params.put(qName.toLowerCase(), atts.getValue("value"));
    }
  }

  double largestOrbit = 0;

  public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    if (qName.equalsIgnoreCase("system")) {
      if (!frameStack.isEmpty()) {
        curFrame = frameStack.pop();
      }

      CelestialBody body = makeBody();
      if (body != null) {
        String name = curFrame.name;
        bodyMap.put(name, body);
        Set<String> children = childMap.get(body.parent);
        if (children == null) {
          childMap.put(body.parent, children = new LinkedHashSet<String>());
        }
        children.add(name);
        if ((body instanceof Planet) 
            && ((Planet) body).orbit.semiMajorAxis.scalar > largestOrbit) { // HACK - Add comparable() to Measure.
          largestOrbit = ((Planet) body).orbit.semiMajorAxis.scalar;
        }
      }
    }
  }

  CelestialBody makeBody() {
    if (curFrame.type.equalsIgnoreCase("star")) {
      return new Star(curFrame.name,
                      frameStack.isEmpty() ? "" : frameStack.peek().name,
                      getTagValue("apparentMagnitude"),
                      (float) getTagValue("colorIndex"),
                      getTagMeasure("mass"),
                      getTagValue("density"),
                      getTagMeasure("meanRadius"),
                      getTagMeasure("siderealRotationPeriod"),
                      getTagValue("axialInclination"),
                      new ObservedLocation(new Coordinate(getTagValue("declination"),
                                                          getTagValue("ascension"), new Measure(0.0, LENGTH)), Epoch.J2000));
    } else if (curFrame.type.equalsIgnoreCase("planet")) {
      return new Planet(curFrame.name,
                        frameStack.isEmpty() ? "" : frameStack.peek().name,
                        getTagValue("apparentMagnitude"),
                        Color.fromBVColorIndex((float) getTagValue("color")),
                        getTagMeasure("mass"),
                        getTagValue("density"),
                        getTagMeasure("meanRadius"),
                        getTagMeasure("siderealRotationPeriod"),
                        getTagValue("albedo"),
                        getTagValue("equatorialGravity"),
                        getTagValue("escapeVelocity"),
                        getTagValue("axialInclination"),
                        new ObservedLocation(new Coordinate(getTagValue("declination"),
                                                            getTagValue("ascension"), new Measure(0.0, LENGTH)), Epoch.J2000),
                        new Orbit(getTagValue("argumentOfPerihelion"),
                                  getTagValue("declination"),
                                  getTagValue("eccentricity"),
                                  getTagValue("inclination"),
                                  getTagValue("longitudeOfPerihelion"),
                                  getTagValue("longitudeOfAscendingnode"),
                                  getTagValue("meanAnomoly"),
                                  getTagValue("meanLongitude"),
                                  getTagValue("obliquity"),
                                  new Measure(0.0, LENGTH),// getTagMeasure("periheliondistance"),
                                  getTagMeasure("semiMajorAxis"),
                                  getTagMeasure("siderealOrbitPeriod")),// HACK - was: ... * vr.cpack.space.model.Orbit.HOURS_IN_SIDEREAL_YEAR)
                        false); // HACK

    } else if (curFrame.type.equalsIgnoreCase("rings")) {
      return new Rings(curFrame.name,
                       frameStack.isEmpty() ? "" : frameStack.peek().name,
                       getTagMeasure("innerSemiMajorAxis"),
                       getTagMeasure("outerSemiMajorAxis"));
    } // Could handle star cluster, galaxy, galaxy cluster, etc. here.
    return null;
  }

  /**
   * Helper to consolidate lookup code.
   */
  private final double getTagValue(final String attribute) {
    final String value = curFrame.params.get(attribute.toLowerCase());
    return value == null ? 1.0 : Double.parseDouble(value);
  }

  /**
   * Helper to consolidate lookup code.
   */
  private final Measure getTagMeasure(final String attribute) {
    final String value = curFrame.params.get(attribute.toLowerCase());
    if (value == null) {
      System.err.println("bogus: " + attribute);
      return null;
    }
    return Measure.parseMeasure(value);
  }

  public static class DataAndExtents {
    public final Map<String, ? extends CelestialBody> bodies;
    public final Map<String, Set<String>> childMap;
    public final double largestOrbit;
    DataAndExtents(final Map<String, ? extends CelestialBody> bodies,
                   final Map<String,Set<String>> childMap,
                   final double largestOrbit) {
      this.bodies = bodies;
      this.childMap = childMap;
      this.largestOrbit = largestOrbit;
    }
  }

  /**
   * Expose the given XML input source as an iterator of
   * CelestialBody.
   */
  public static DataAndExtents parseToBodyMap(final String inputSource) {
    // Load scene elements from XML description.
    SpaceHandler handler = new SpaceHandler();
    try {
      SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      parser.parse(new URL(inputSource).openStream(), handler);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Reverse ordering so parents come before children in iterator.
    final ArrayList<String> bodyNames = new ArrayList<String>();
    for (final String bodyName : handler.bodyMap.keySet()) {
      bodyNames.add(bodyName);
    }
    final LinkedHashMap<String, CelestialBody> reversedMap = new LinkedHashMap<String, CelestialBody>();
    for (int i = bodyNames.size() - 1; i >= 0; i--) {
      reversedMap.put(bodyNames.get(i), handler.bodyMap.get(bodyNames.get(i)));
    }
    return new DataAndExtents(reversedMap, handler.childMap, handler.largestOrbit);
  }

  static {
    org.freality.io.loader.java.Handler.register();
  }
  public static void main(String [] args) throws Exception {
    final DataAndExtents dae = SpaceHandler.parseToBodyMap("java:space/data/solarSystem.xml");
    final Map<String,? extends CelestialBody> bodyMap = dae.bodies;
    if (args.length == 1) {
      System.out.println((CelestialBody) bodyMap.get(args[0]));
    } else {
      for (final String bodyName : bodyMap.keySet()) {
        File f = new File(bodyName);
        f.createNewFile();
        FileWriter w = new FileWriter(f);
        CelestialBody b = (CelestialBody) bodyMap.get(bodyName);
        String json = b.toString();
        json = json.substring(1, json.length());
        String type;
        if (b.getClass() == Planet.class) {
          type = "\"planet\"";
        } else if (b.getClass() == Star.class) {
          type = "\"star\"";
        } else {
          continue;
        }
        Set<String> children = dae.childMap.get(bodyName);
        String childStr = "[]";
        if (children != null) {
          childStr = "[";
          int i = 0, n = children.size();
          for (String child : children) {
            childStr += "\"" + child + "\"";
            if (i < n - 1) {
              childStr += ",";
            }
            i++;
          }
          childStr += "]";
        }
        json = "{\n\"type\":" + type + ",\n"
          + json
          + ",\n\"system\": " + childStr
          + "\n}\n";
        w.write(json);
        w.close();
        f.renameTo(new File(bodyName+".json"));
      }
    }
  }
}
