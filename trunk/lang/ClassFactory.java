package lang;

class ClassFactory {

  String name;
  StringBuffer programBuf = new StringBuffer();

  ClassFactory(String name){
    this.name = name;
  }

  void beginWrite(StringBuffer programBuf){
    classBeginStr(programBuf);
  }

  void endWrite(StringBuffer programBuf){
    if(Boolean.getBoolean("main")) 
      mainStr(programBuf);
    classEndStr(programBuf);
  }

  void write(StringBuffer programBuf){
    beginWrite(programBuf);
    endWrite(programBuf);
  }

  void classBeginStr(StringBuffer programBuf){
    programBuf.append("class ");
    programBuf.append(name);
    programBuf.append(" {\n");
  }

  void classEndStr(StringBuffer programBuf){
    programBuf.append("}");
  }

  void mainStr(StringBuffer programBuf){
    programBuf.append("public static void main(String [] args){\n}\n");
  }

  static void indent(StringBuffer buf){
    String tmp = buf.toString();
    tmp = tmp.replaceAll("^","  ");
    buf.setLength(0);
    buf.append(tmp);
  }

  public static void main(String [] args){
    //    if(Boolean.get("main") == true)
    ClassFactory cf = new ClassFactory(args[0]);
    StringBuffer programBuf = new StringBuffer();
    cf.write(programBuf);
    System.out.println(programBuf.toString());
  }
}
