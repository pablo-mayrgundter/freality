// Copyright 2014 Stefan Matthias Aust. Licensed under http://opensource.org/licenses/MIT.
// From https://gist.github.com/sma/8180927
import 'dart:io' as dart_io;

// collections ------------------------------------------------------------------------------------

abstract class java_util_List<E> {
  int size();
  bool add(E e);
  bool remove(Object o);
  void clear();
  java_util_Iterator<E> iterator();
}

class java_util_ArrayList<E> extends java_util_List<E> {
  List<E> _elements;

  java_util_ArrayList([arg]) {
    if (arg == null || arg is num) {
      _elements = [];
    } else if (arg is java_util_ArrayList) {
      _elements = arg._elements;
    } else throw "unsupported argument type";
  }

  int size() => _elements.length;

  bool add(E e) { _elements.add(e); return true; }

  bool remove(Object o) => _elements.remove(o);

  void clear() => _elements.clear();

  java_util_Iterator<E> iterator() => new java_util_Iterator.dart(_elements.iterator);
}

class java_util_Iterator<E> {
  final Iterator<E> _iterator;
  bool _hasNext;

  java_util_Iterator.dart(this._iterator) {
    _hasNext = _iterator.moveNext();
  }

  bool hasNext() => _hasNext;

  E next() {
    E e = _iterator.current;
    _hasNext = _iterator.moveNext();
    return e;
  }

  void remove() {
    throw "unsupported operation exception";
  }
}

// core -------------------------------------------------------------------------------------------

class Integer {
  static final int MAX_VALUE = 2147483647, MIN_VALUE = -2147483648;
}


class Character {
  static bool isWhitespace(int ch) {
    return new String.fromCharCode(ch).startsWith(new RegExp("\\s"));
  }

  static int toLowerCase(int ch) {
    return new String.fromCharCode(ch).toLowerCase().codeUnitAt(0);
  }
}

bool java_equalsIgnoreCase(String s1, String s2) {
  return s1 == s2 || s1.toLowerCase() == s2.toLowerCase();
}

// io ---------------------------------------------------------------------------------------------

abstract class java_io_InputStream {
  void close();
  int read();
}

abstract class java_io_OutputStream {
  void close();
  void flush();
  void write(obj);
}

class _OutStream extends java_io_OutputStream {
  void close() {}

  void flush() {}

  void write(obj) {
    if (obj is int) {
      dart_io.stdout.writeCharCode(obj);
    } else if (obj is List<int>){
      dart_io.stdout.add(obj);
    } else throw "unsupported argument type";
  }

  void print(String s) => dart_io.stdout.write(s);

  void println([String s = ""]) => dart_io.stdout.writeln(s);
}

class _InStream extends java_io_InputStream {
  void close() {}

  int read() => dart_io.stdin.readByteSync();
}

class System {
  static final out = new _OutStream();
  static final in_ = new _InStream();

  static int currentTimeMillis() => new DateTime.now().millisecondsSinceEpoch;

  static void exit(int code) => dart_io.exit(code);

  static void arraycopy(List src, int si, List dst, int di, int n) {
    dst.setRange(di, di + n, src.sublist(si));
  }
}

class java_io_IOException implements Exception {
  final String message;

  java_io_IOException(this.message);

  void printStackTrace() {
    print("Exception: $message");
  }
}

class java_io_FileNotFoundException extends java_io_IOException {
  java_io_FileNotFoundException(String message) : super(message);
}

class java_io_File {
  final String _name;

  java_io_File(this._name);

  bool delete() { new dart_io.File(_name).deleteSync(); return true; }

  bool mkdir() { new dart_io.Directory(_name).createSync(); return true; }

  List<java_io_File> listFiles() => new dart_io.Directory(_name).listSync().map((e) => new java_io_File(e.path));

  String getAbsolutePath() => new dart_io.File(_name).absolute.path;

  String getName() => _name;
}
