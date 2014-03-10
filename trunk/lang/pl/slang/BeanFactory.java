package lang.pl.slang;

/**
 * Prints a Java ean class definition, given a specification of member
 * variable types and names.
 */
class BeanFactory extends ClassFactory {

    String [] types;
    String [] names;
    StringBuffer programBuf;

    BeanFactory(String className, String [] types, String [] names) {
        super(className);
        this.types = types;
        this.names = names;
    }

    void write(StringBuffer programBuf){
        super.beginWrite(programBuf);
        this.programBuf = new StringBuffer();
        for(int i = 0; i < types.length; i++) {
            memberStr(types[i], names[i]);
            getStr(types[i], names[i]);
            setStr(types[i], names[i]);
        }
        //    indent(this.programBuf);
        programBuf.append(this.programBuf);
        super.endWrite(programBuf);
    }

    void memberStr(String type, String name){
        programBuf.append(type);
        programBuf.append(" ");
        programBuf.append(name);
        programBuf.append(";\n");
    }

    void getStr(String type, String memberName){
        programBuf.append("public ");
        programBuf.append(type);
        programBuf.append(" get");
        final char [] nameToUpper = memberName.toCharArray();
        nameToUpper[0] = (char)(((int)nameToUpper[0]) - 32);
        programBuf.append(nameToUpper);
        programBuf.append("(){ ");
        programBuf.append("return ");
        programBuf.append(memberName);
        programBuf.append("; }\n");
    }

    void setStr(String type, String memberName){
        programBuf.append("public void set");
        final char [] nameToUpper = memberName.toCharArray();
        nameToUpper[0] = (char)(((int)nameToUpper[0]) - 32);
        programBuf.append(nameToUpper);
        programBuf.append("(");
        programBuf.append(type);
        programBuf.append(" ");
        programBuf.append(memberName);
        programBuf.append("){ ");
        programBuf.append("this.");
        programBuf.append(memberName);
        programBuf.append(" = ");
        programBuf.append(memberName);
        programBuf.append("; }\n");
    }

    public static void main(String [] args){
        BeanFactory cf = new BeanFactory(args[0],
                                         args[1].split(","),
                                         args[2].split(","));
        StringBuffer programBuf = new StringBuffer();
        cf.write(programBuf);
        System.out.println(programBuf.toString());
    }
}
