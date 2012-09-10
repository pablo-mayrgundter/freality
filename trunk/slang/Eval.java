package slang;

import java.io.*;
import java.net.URI;
import java.util.Arrays;
import javax.tools.*;

class Eval {

  /**
   * A file object used to represent source coming from a string.
   */
  static class JavaSourceFromString extends SimpleJavaFileObject {
    /**
     * The source code of this "file".
     */
    final String code;

    /**
     * Constructs a new JavaSourceFromString.
     * @param name the name of the compilation unit represented by this file object
     * @param code the source code for the compilation unit represented by this file object
     */
    JavaSourceFromString(String name, String code) {
      super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),
            Kind.SOURCE);
      this.code = code;
    }

    @Override
      public CharSequence getCharContent(boolean ignoreEncodingErrors) {
      return code;
    }
  }

  static boolean run(String className, String src)
    throws IOException,
           ClassNotFoundException,
           InstantiationException,
           IllegalAccessException {
    src = String.format("public class %s implements Runnable {\n  public void run() {\n    %s\n  }\n}\n",
                        className, src);
    return compile(className, src);
  }

  static boolean compile(String className, String src)
    throws IOException,
           ClassNotFoundException,
           InstantiationException,
           IllegalAccessException {
    Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(new JavaSourceFromString(className, src));
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
    boolean success = compiler.getTask(null, null, diagnostics, null, null, compilationUnits).call();

    for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
      System.err.format("Error on line %d in %d%n",
                        diagnostic.getLineNumber());
    }

    if (success) {
      final Runnable r = (Runnable) Class.forName(className).newInstance();
      r.run();
      return true;
    }
    System.err.println("Failed!");
    return false;
  }
}
