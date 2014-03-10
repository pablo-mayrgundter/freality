package lang.pl.slang;

/**
 * ClassWriter writes a textual representation of a Class object.
 */
class ClassSource {

  final Class clazz;
  StringBuffer buf;

  ClassSource(Class clazz) {
    this.clazz = clazz;
  }

  public String toString() {
    buf = new StringBuffer();
    write();
    return buf.toString();
  }

  void beginWrite() {
    packageStr();
    classBeginStr();
  }

  void endWrite() {
    classEndStr();
  }

  void write() {
    beginWrite();
    endWrite();
  }

  void packageStr() {
    String pkg = clazz.getName();
    int ndx;
    if ((ndx = pkg.indexOf(".")) != -1) {
      pkg = pkg.substring(0, ndx);
    }
    if (pkg.length() > 0) {
      buf.append("package " + pkg + ";\n\n");
    }
  }

  void classBeginStr() {
    buf.append("class ");
    buf.append(clazz.getSimpleName());
    buf.append(" {\n");
  }

  void classEndStr() {
    buf.append("}");
  }

  static void indent(StringBuffer buf) {
    String tmp = buf.toString();
    tmp = tmp.replaceAll("^","  ");
    buf.setLength(0);
    buf.append(tmp);
  }

  public static void main(String [] args) {
    ClassSource cw = new ClassSource(ClassSource.class);
    System.out.println(cw.toString());
  }
}
