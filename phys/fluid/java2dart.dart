// Copyright 2014 Stefan Matthias Aust. Licensed under http://opensource.org/licenses/MIT.
// From https://gist.github.com/sma/8180927
library java2dart;

import 'dart:io';
import 'dart:convert';

class Scanner {
  /// Holds the current token or the empty string on end of input
  String currentToken;

  int position;

  Iterator<Match> _matches;

  /// Constructs a new scanner to tokenize [source].
  Scanner(String source) {
    _matches = new RegExp(
        '\\s+|//.*\$|/\\*[\\s\\S]*?\\*/|'             // whitespace & comments
        '(0x[0-9a-fA-F]+|'                            // numbers
        '(?:\\d+(?:\\.\\d*)?|\\.\\d+)'                // numbers
        '(?:[eE][-+]?\\d+)?[lLfF]?|'                  // numbers
        '[\\w\$_]+|'                                  // names & keywords
        '"(?:\\\\.|[^"])*?"|\'(?:\\\\.|[^\'])+?\'|'   // strings & characters
        '&&|\\|\\||\\+\\+|--|'                        // operators
        '[+\\-*/%&^|]=?|<<=?|>>>?=?|[=<>!]=?|~|'      // operators
        '[.]{3}|[.,;()[\\]{}?:])|(.)',                // syntax
        multiLine: true).allMatches(source).iterator;
    advance();
  }

  /// Advances [currentToken] to the next token in the source.
  String advance() {
    String token = currentToken;
    while (_matches.moveNext()) {
      Match m = _matches.current;
      if (m[1] != null) {

        position = m.input.substring(0, m.start).split("\n").length;
        currentToken = m[1];
        return token;
      }
      if (m[2] != null) {
        error("unknown token ${m[2]} at ${m.start}");
      }
    }
    currentToken = "";
    return token;
  }

  /// Returns true if the current token matches [token] and false otherwise.
  /// Advances to the next token if the current token was matched.
  bool at(String token) {
    if (currentToken == token) {
      advance();
      return true;
    }
    return false;
  }

  /// Advances [currentToken] if it matches [token] and raises an error otherwise.
  void expect(String token) {
    if (!at(token)) {
      error("ERR1: expected $token but found $currentToken");
    }
  }

  /// Returns true if [currentToken] is a name.
  bool get isName => currentToken.startsWith(new RegExp("[\\w\$_]"));

  /// Returns true if [currentToken] is a number.
  bool get isNumber => currentToken.startsWith(new RegExp("\\.?[\\d]"));

  /// Returns true if [currentToken] is a string.
  bool get isString => currentToken.isNotEmpty && (currentToken[0] == '"' || currentToken[0] == "'");

  void error(String message) {
    throw new Exception("at line " + position.toString() + ": " + message);
  }
}

class Parser extends Scanner {
  /// Constructs a new parser to parse [source].
  Parser(String source) : super(source);

  /// Returns [node] after parsing a semicolon.
  andSemicolon(node) { expect(";"); return node; }

  /// Returns the result of calling [parser] if [at(token)] is true and null otherwise.
  parseIfAt(String token, parser()) => at(token) ? parser() : null;

  /// Returns a list of results from calling [parser] as long as [at(token)] is true.
  List parseWhile(String token, parser()) {
    var results = [];
    while (at(token)) {
      results.add(parser());
    }
    return results;
  }

  /// Returns a list of results from calling [parser] as long as [at(token)] is false.
  List parseWhileNot(token, parser()) {
    var results = [];
    while (!at(token)) {
      results.add(parser());
    }
    return results;
  }

  /// Returns a list of results from calling [parser], separated by [separator].
  List parseList(parser(), {String ifAt, String separator: ","}) {
    if  (ifAt == null || at(ifAt)) {
      var results = [parser()];
      while (at(separator)) {
        results.add(parser());
      }
      return results;
    } else {
      return null;
    }
  }

  // declarations ---------------------------------------------------------------------------------

  // ["package" qualifiedName ";"] { importStatement } { typeDeclaration }
  parseCompilationUnit() {
    String packageName = parseIfAt("package", () => andSemicolon(parseQualifiedName()));
    var imports = parseWhile("import", parseImportStatement);
    var declarations = parseWhileNot("", parseTypeDeclaration);
    return [AST.CompilationUnit, packageName, imports, declarations];
  }

  // "import" name {"." name} ["." "*"] ";"
  // no static
  parseImportStatement() {
    var names = [parseName()];
    while (at(".")) {
      names.add(at("*") ? "*" : parseName());
    }
    expect(";");
    return [AST.Import, names.join(".")];
  }

  // classDeclaration | interfaceDeclaration
  // no enum
  parseTypeDeclaration() {
    var modifiers = parseModifiers();
    if (at("class")) return parseClassDeclaration(modifiers);
    if (at("interface")) return parseInterfaceDeclaration(modifiers);
    error("expected class or interface but found $currentToken.");
  }

  static const modifierTokens = const["abstract", "public", "protected", "private", "static", "final", "synchronized"];

  parseModifiers() {
    var modifiers = [];
    while (modifierTokens.contains(currentToken)) {
      modifiers.add(advance());
    }
    return [AST.Modifiers, modifiers];
  }

  // [modifiers] [typeParameter] "class" name ["extends" type] ["implements" type {"," type] classBody
  parseClassDeclaration(modifiers) {
    var className = parseName();
    var typeParameters = parseIfAt("<", parseTypeParameters);
    var superclassType = parseIfAt("extends", parseType);
    var interfaceTypes = parseList(parseType, ifAt:"implements");
    return [AST.Class, modifiers, className, typeParameters, superclassType, interfaceTypes, parseClassOrInterfaceBody()];
  }

  // [modifiers] [typeParameter] "interface" name ["extends" type {"," type}] interfaceBody
  parseInterfaceDeclaration(modifiers) {
    var interfaceName = parseName();
    var typeParameters = parseIfAt("<", parseTypeParameters);
    var interfaceTypes = parseList(parseType, ifAt:"extends");
    return [AST.Interface, modifiers, interfaceName, typeParameters, interfaceTypes, parseClassOrInterfaceBody()];
  }

  // "{" {memberDeclaration} "}"
  // should distinguish class & interface (has no static, no method bodies)
  parseClassOrInterfaceBody() {
    expect("{");
    return parseWhileNot("}", parseMemberDeclaration);
  }

  parseMemberDeclaration() {
    parseIfAt("<", parseTypeParameters); // ignored
    var modifiers = parseModifiers();
    if (modifiers[1].length == 1 &&  modifiers[1][0] == "static" && at("{")) {
      return [AST.StaticInitializer, parseStatementBlock()];
    }
    if (at("class")) return parseClassDeclaration(modifiers);
    if (at("interface")) return parseInterfaceDeclaration(modifiers);
    String name = parseName(); // could be a type or constructor
    if (at("(")) { // it's a constructor declaration
      var parameters = parseParameterList();
      var throws = parseThrowsDeclaration();
      expect("{");
      return [AST.Constructor, modifiers, name, parameters, throws, parseStatementBlock()];
    }
    var type = parseArrayType(parseType(name));
    name = parseName();
    if (at("(")) { // it's a method declaration
      var parameters = parseParameterList();
      type = parseArrayType(type);
      var throws = parseThrowsDeclaration();
      var statements;
      if (!at(";")) {
        expect("{");
        statements = parseStatementBlock();
      } else {
        statements = [];
      }
      return [AST.Method, modifiers, type, name, parameters, throws, statements];
    } else { // must be variable declaration
      var declarations = parseVariableDeclarations(modifiers, type, name);
      expect(";");
      return declarations;
    }
  }

  List parseThrowsDeclaration() {
    return parseList(parseType, ifAt: "throws");
  }

  List parseParameterList() {
    var parameters = [];
    while (!at(")")) {
      parameters.add(parseParameter());
      if (at(")")) break;
      expect(",");
    }
    return parameters;
  }

  parseParameter() {
    var type = parseType();
    type = parseArrayType(type);
    bool rest = at("...");
    var name = parseName();
    type = parseArrayType(type);
    return [rest ? AST.RestParameter : AST.Parameter, type, name];
  }

  parseVariableDeclarations(modifiers, type, name) {
    var type2 = parseArrayType(type);
    var init = parseInitializer();
    var declarations = [[AST.VariableDeclaration, modifiers, type2, name, init]];
    while (at(",")) {
      name = parseName();
      type2 = parseArrayType(type);
      init = parseInitializer();
      declarations.add([AST.VariableDeclaration, modifiers, type2, name, init]);
    }
    return [AST.VariableDeclarations, declarations];
  }

  parseInitializer() {
    if (at("=")) {
      if (at("{")) {
        return parseArrayInitializer();
      } else {
        return parseExpression();
      }
    }
    return null;
  }

  parseArrayInitializer() {
    var elements = [];
    while (!at("}")) {
      if (at("{")) {
        elements.add(parseArrayInitializer());
      } else {
        elements.add(parseExpression());
      }
      if (at("}")) break;
      expect(",");
    }
    return [AST.ArrayInitializer, elements];
  }

  // types ----------------------------------------------------------------------------------------

  static const primitiveTypes = const["boolean", "byte", "char", "short", "int", "float", "long", "double", "void"];

  parseType([String name=""]) {
    if (name == "") {
      name = advance();
    }
    var type = parseSimpleType(name);
    if (at("<")) {
      type = [AST.GenericType, type, parseTypeParameters()];
    }
    return type;
  }

  parseSimpleType(String name) {
    if (primitiveTypes.contains(name)) {
      return [AST.PrimitiveType, name];
    } else {
      var names = [name];
      while (at(".")) {
        names.add(parseName());
      }
      return [AST.ReferenceType, names.join(".")];
    }
  }

  parseArrayType(type) {
    while (at("[")) {
      expect("]");
      type = [AST.ArrayType, type];
    }
    return type;
  }

  parseTypeParameters() {
    var parameters = [parseTypeParameter()];
    while (at(",")) {
      parameters.add(parseTypeParameter());
    }
    expect(">");
    return [AST.TypeParameters, parameters];
  }

  String parseTypeParameter() {
    return at("?") ? "?" : parseQualifiedName();
  }

  // statements -----------------------------------------------------------------------------------

  parseStatementBlock() {
    return [AST.Block, parseWhileNot("}", parseStatement)];
  }

  parseStatement() {
    if (at("{")) return parseStatementBlock();
    if (at("if")) return parseIfStatement();
    if (at("do")) return parseDoWhileStatement();
    if (at("while")) return parseWhileStatement();
    if (at("for")) return parseForStatement();
    if (at("try")) return parseTryStatement();
    if (at("switch")) return parseSwitchStatement();
    if (at("synchronized")) {
      var expression = parseParenthesizedExpression();
      return ["synchronized", expression, parseStatement()];
    }
    if (at("return")) {
      var expression;
      if (!at(";")) {
        expression = andSemicolon(parseExpression());
      }
      return ["return", expression];
    }
    if (at("throw")) {
      return ["throw", andSemicolon(parseExpression())];
    }
    if (at("assert")) {
      var e = parseExpression();
      var m = parseIfAt(":", parseExpression);
      expect(";");
      return ["assert", e, m];
    }
    if (at("break")) {
      var label;
      if (!at(";")) {
        label = andSemicolon(parseName());
      }
      return ["break", label];
    }
    if (at("continue")) {
      var label;
      if (!at(";")) {
        label = andSemicolon(parseName());
      }
      return ["continue", label];
    }
    if (at(";")) return ["pass"];
    var expression = parseExpression();
    if (expression[0] == "variable" && at(":")) {
      return ["label", expression[1]];
    }
    expect(";");
    if (expression[0] == "variable_declarations") {
      return expression;
    }
    return ["expression", expression];
  }

  parseIfStatement() {
    var c = parseParenthesizedExpression();
    var t = parseStatement();
    var e = parseIfAt("else", parseStatement);
    return ["if", c, t, e];
  }

  parseDoWhileStatement() {
    var s = parseStatement();
    expect("while");
    var e = andSemicolon(parseParenthesizedExpression());
    return ["dowhile", e, s];
  }

  parseWhileStatement() {
    var e = parseParenthesizedExpression();
    var s = parseStatement();
    return ["whiledo", e, s];
  }

  parseForStatement() {
    var a = [], b, c = [];
    expect("(");
    if (!at(";")) {
      var e = parseExpression();
      if (at(":")) {
        // e must be variable_declarations with a single variable_declaration
        assert(e[0] == "variable_declarations" && e[1].length == 1);
        b = parseExpression();
        expect(")");
        var s = parseStatement();
        return ["foreach", e[1][0], b, s];
      }
      a.add(e);
      while (at(",")) {
        a.add(parseExpression());
      }
      expect(";");
    }
    if (!at(";")) {
      b = andSemicolon(parseExpression());
    }
    if (!at(")")) {
      c.add(parseExpression());
      while (at(",")) {
        c.add(parseExpression());
      }
      expect(")");
    }
    var s = parseStatement();
    return ["for", a, b, c, s];
  }

  parseTryStatement() {
    var s = parseStatement();
    var c = [];
    while (at("catch")) {
      expect("(");
      var p = parseParameter();
      expect(")");
      c.add(["catch", p, parseStatement()]);
    }
    var f = parseIfAt("finally", parseStatement);
    return ["try", s, c, f];
  }

  parseSwitchStatement() {
    var expression = parseParenthesizedExpression();
    expect("{");
    var statements = [];
    while (!at("}")) {
      if (at("case")) {
        var e = parseExpression();
        expect(":");
        statements.add(["case", e]);
      } else if (at("default")) {
        expect(":");
        statements.add(["default"]);
      } else {
        statements.add(parseStatement());
      }
    }
    return ["switch", expression, statements];
  }

  parseParenthesizedExpression() {
    expect("(");
    var expression = parseExpression();
    expect(")");
    return expression;
  }

  // expressions -----------------------------------------------------------------------------------------------------

  static const assignmentOperators = const["=", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "<<=", ">>=", ">>>="];

  parseExpression() {
    var e = parseConditionalExpression();
    if (assignmentOperators.contains(currentToken)) {
      var operator = advance();
      return [operator, e, parseExpression()];
    }
    return e;
  }

  parseConditionalExpression() {
    var e = parseOrExpression();
    if (at("?")) {
      var t = parseConditionalExpression();
      expect(":");
      return ["?:", e, t, parseConditionalExpression()];
    }
    return e;
  }

  parseOrExpression() {
    var e = parseAndExpression();
    while (at("||")) {
      e = ["||", e, parseAndExpression()];
    }
    return e;
  }

  parseAndExpression() {
    var e = parseBitIorExpression();
    while (at("&&")) {
      e = ["&&", e, parseBitIorExpression()];
    }
    return e;
  }

  parseBitIorExpression() {
    var e = parseBitXorExpression();
    while (at("|")) {
      e = ["|", e, parseBitXorExpression()];
    }
    return e;
  }

  parseBitXorExpression() {
    var e = parseBitAndExpression();
    while (at("^")) {
      e = ["^", e, parseBitAndExpression()];
    }
    return e;
  }

  parseBitAndExpression() {
    var e = parseEqualityExpression();
    while (at("&")) {
      e = ["&", e, parseEqualityExpression()];
    }
    return e;
  }

  parseEqualityExpression() {
    var e = parseRelationalExpression();
    while (true) {
      if (at("==")) {
        e = ["==", e, parseRelationalExpression()];
      } else if (at("!=")) {
        e = ["!=", e, parseRelationalExpression()];
      } else return e;
    }
  }

  parseRelationalExpression() {
    var e = parseBitShiftExpression();
    while (true) {
      if (at("<")) {
        // hack: could be a variable declaration (still missing "?" case)
        var e2 = parseBitShiftExpression();
        if (e[0] == "variable" && e2[0] == "variable" && (currentToken == "," || currentToken == ">")) {
          var parameters = [e2[1]];
          while (!at(">")) {
            expect(",");
            parameters.add(parseQualifiedName());
          }
          var type = parseArrayType(["generic_type", parseSimpleType(e[1]), ["type_parameters", parameters]]);
          var name = parseName();
          return parseVariableDeclarations(null, type, name);
        }
        e = ["<", e, e2];
      } else if (at("<=")) {
        e = ["<=", e, parseBitShiftExpression()];
      } else if (at(">")) {
        e = [">", e, parseBitShiftExpression()];
      } else if (at(">=")) {
        e = [">=", e, parseBitShiftExpression()];
      } else if (at("instanceof")) {
        e = ["instanceof", e, parseBitShiftExpression()];
      } else return e;
    }
  }

  parseBitShiftExpression() {
    var e = parseAdditionExpression();
    while (true) {
      if (at("<<")) {
        e = ["<<", e, parseAdditionExpression()];
      } else if (at(">>")) {
        e = [">>", e, parseAdditionExpression()];
      } else if (at(">>>")) {
        e = [">>>", e, parseAdditionExpression()];
      } else return e;
    }
  }

  parseAdditionExpression() {
    var e = parseMultiplicationExpression();
    while (true) {
      if (at("+")) {
        e = ["+", e, parseMultiplicationExpression()];
      } else if (at("-")) {
        e = ["-", e, parseMultiplicationExpression()];
      } else return e;
    }
  }

  parseMultiplicationExpression() {
    var e = parsePrefixExpression();
    while (true) {
      if (at("*")) {
        e = ["*", e, parsePrefixExpression()];
      } else if (at("/")) {
        e = ["/", e, parsePrefixExpression()];
      } else if (at("%")) {
        e = ["%", e, parsePrefixExpression()];
      } else return e;
    }
  }

  parsePrefixExpression() {
    if (at("++")) {
      return ["++p", parsePrefixExpression()];
    }
    if (at("--")) {
      return ["--p", parsePrefixExpression()];
    }
    if (at("+")) {
      return ["+p", parsePrefixExpression()];
    }
    if (at("-")) {
      return ["-p", parsePrefixExpression()];
    }
    if (at("!")) {
      return ["!", parsePrefixExpression()];
    }
    if (at("~")) {
      return ["~", parsePrefixExpression()];
    }
    return parsePostfixExpression();
  }

  parsePostfixExpression() {
    var e = parsePrimary();
    if (at("++")) {
      return ["p++", e];
    }
    if (at("--")) {
      return ["p--", e];
    }
    return e;
  }

  parsePrimary() {
    var e = parseLiteral();
    while (true) {
      if (at("(")) { // simple call
        assert(e[0] == "variable");
        e = ["call", null, e[1], parseArguments()];
      } else if (at("[")) {
        // hack: could be a variable declaration
        if (e[0] == "variable" && at("]")) {
          var type = ["array_type", parseSimpleType(e[1])];
          return parseVariableDeclarations(null, parseArrayType(type), parseName());
        }
        var index = parseExpression();
        expect("]");
        e = ["[]", e, index];
      } else if (at(".")) { // method call or field access
        var name = parseName();
        if (at("(")) { // method call
          e = ["call", e, name, parseArguments()];
        } else {
          e = ["field", e, name];
        }
      } else return e;
    }
  }

  parseArguments() {
    var arguments = [];
    while (!at(")")) {
      arguments.add(parseExpression());
      if (at(")")) break;
      expect(",");
    }
    return arguments;
  }

  // needed to distinguish casts from parenthesized expressions
  static const literalFirst = const["++", "+", "--", "-", "!", "~", "("];

  parseLiteral() {
    if (at("null")) return ["literal", null];
    if (at("true")) return ["literal", true];
    if (at("false")) return ["literal", false];
    if (at("new")) {
      var type = parseType();
      if (at("[")) {
        while (at("]")) {
          if (at("{")) {
            var list = [];
            while (!at("}")) {
              list.add(parseExpression());
              if (at("}")) {
                break;
              }
              expect(",");
            }
            return ["new_array_from", type, list];
          }
          type = ["array_type", type];
          expect("[");
        }
        var expression = parseExpression();
        expect("]");
        return ["new_array", type, expression];
      }
      if (at("(")) {
        return ["new_instance", type, parseArguments()];
      }
      error("unexpected $currentToken after new $type");
    }
    if (at("(")) {
      var e = parseExpression();
      expect(")");
      // need to distinguish expression in parentheses from a cast
      if (e[0] == "variable") {
        if (isName || isNumber || isString || literalFirst.contains(currentToken)) {
          return ["cast", e[1], parsePrefixExpression()];
        }
      }
      return e;
    }
    if (isString) {
      return ["literal", advance()];
    }
    if (isNumber) {
      var n = advance();
      if (n.contains(new RegExp("[lLfF]\$"))) {
        n = n.substring(0, n.length - 1);
      }
      return ["literal", num.parse(n)];
    }
    // need to distinguish type declarations and variables
    // a sequence of two names should be a type declaration
    // ("foo[] bar" or "foo<x> bar" is not detected, see "hack")
    if (isName) {
      var name = advance();
      if (isName) {
        var type = parseArrayType(parseType(name));
        return parseVariableDeclarations(null, type, parseName());
      }
      return ["variable", name];
    }
    error("unexpected $currentToken in expression");
  }

  // utilities -------------------------------------------------------------------------------------------------------

  String parseQualifiedName() {
    return parseList(parseName, separator: ".").join(".");
  }

  String parseName() {
    if (!isName) {
      error("ERR2: expected NAME but found $currentToken");
    }
    return advance();
  }
}

abstract class AST {
  // declarations
  static const CompilationUnit = "unit"; /* String packageName, List<AST> imports, List<AST> declarations */
  static const Import = "import"; /* String qualifiedName */
  static const Modifiers = "modifiers"; /* List<String> modifiers */
  static const Class = "class"; /* AST modifiers, String className, AST typeParameters, AST superclassType, List<AST> interfaceTypes, List<AST> classBody */
  static const Interface = "interface"; /* AST modifiers, String interfaceName, AST typeParameters, List<AST> interfaceTypes, List<AST> classBody */
  static const StaticInitializer = "static_initializer"; /* AST block */
  static const Constructor = "constructor"; /* AST modifiers, String constructorName, List<AST> parameters, List<AST> throws, AST block */
  static const Method = "method"; /* AST modifiers, AST returnType, String methodName, List<AST> parameters, List<AST> throws, AST block */
  static const Parameter = "parameter"; /* AST type, AST name */
  static const RestParameter = "rest_parameter"; /* AST type, AST name */
  static const VariableDeclaration = "variable_declaration"; /* AST modifiers, AST type, String name, AST initializer */
  static const VariableDeclarations = "variable_declarations"; /* List<AST> declarations */
  static const ArrayInitializer = "array"; /* List<AST> elements */

  // types
  static const GenericType = "generic_type"; /* AST type, AST parameters */
  static const PrimitiveType = "primitive_type"; /* String name */
  static const ReferenceType = "reference_type"; /* String qualifiedName */
  static const ArrayType = "array_type"; /* AST type */
  static const TypeParameters = "type_parameters"; /* List<String> parameters */

  // statements
  static const Block = "block"; /* List<AST> statements */
  static const Assert = "assert";
  static const Break = "break";
  static const Continue = "continue";
  static const DoWhile = "dowhile";
  static const Expression = "expression"; /* AST expression */
  static const For = "for";
  static const ForEach = "foreach";
  static const If = "if";
  static const Label = "label";
  static const Pass = "pass";
  static const Return = "return";
  static const Throw = "throw";
  static const While = "whiledo";
  static const Try = "try";
  static const Catch = "catch";
  static const Finally = "finally";
  static const Switch = "switch";
  static const Case = "case";
  static const Default = "default";

  // expressions
  static const Assign = "=";
  static const PlusAssign = "+=";
  static const MinusAssign = "-=";
  static const MultiplyAssign = "*=";
  static const DivideAssign = "/=";
  static const ModuloAssign = "%=";
  static const AndAssign = "&=";
  static const IorAssign = "|=";
  static const XorAssign = "^=";
  static const LeftShiftAssign = "<<=";
  static const RightShiftAssign = ">>=";
  static const URightShiftAssign = ">>>=";
  static const Conditional = "?:";
  static const Or = "||";
  static const And = "&&";
  // ...
}

/// Translates Java nodes into Dart (printed to [stdout]).
class Translator {
  StringSink _sink;
  Map _funcs;
  int _indent = 0;
  Map _imports = new Map();
  Set _types = new Set();
  Resolver _resolver = new Resolver();

  Translator([this._sink]) {
    if (_sink == null) _sink = stdout;
    _funcs = {
      AST.CompilationUnit: (node) {
        if (node[1] != null) {
          emit("library ${node[1]};");
          if (node[2].isNotEmpty) {
            newline();
          }
        }
        node[2].forEach(translate);
        node[3].forEach(translate);
      },
      AST.Import: (node) {
        // import a compatibility file instead
        if (_imports.isEmpty) {
          emit("import 'java.dart';");
        }
        // to replace simple names with qualified names
        _imports[node[1].split(".").last] = node[1].replaceAll(".", "_");
      },
      AST.Class: (node) {
        newline();
        var abst = node[1][1].contains("abstract") ? "abstract " : "";
        var name = node[2];
        var type = node[3] != null ? translate(node[3]) : "";
        var ext = node[4] != null ? " extends ${translate(node[4]).replaceAll(".", "_")}" : "";
        var imp = node[5] != null ? " with ${node[5].map(translate).join(", ").replaceAll(".", "_")}" : "";
        emit("${abst}class $name$type$ext$imp");
        _resolver.beginScope();
        emit("{");
        indent();
        node[6].where((n) => n[0] != "class").forEach(translate); // See below
        dedent();
        emit("}");
        _resolver.endScope();
        // Dart doesn't support nested classes
        node[6].where((n) => n[0] == "class").forEach(translate);
      },
      AST.Interface: (node) {
        newline();
        var name = node[2];
        var type = node[3] != null ? translate(node[3]) : "";
        var imp = node[4] != null ? " with ${node[4].map(translate).join(", ").replaceAll(".", "_")}" : "";
        emit("abstract class $name$type$imp");
        _resolver.beginScope();
        emit("{");
        indent();
        node[5].where((n) => n[0] != "class").forEach(translate); // See below
        dedent();
        emit("}");
        _resolver.endScope();
        // Dart doesn't support nested classes
        node[5].where((n) => n[0] == "class").forEach(translate);
      },
      AST.VariableDeclarations: (node) {
        node[1].forEach((n) => emit(translate(n) + ";"));
      },
      AST.VariableDeclaration: (node) {
        var mods = node[1] != null ? translate(node[1]) : "";
        var type = translate(node[2]);
        if (type == "int") mods = mods.replaceAll("final", "const");
        var name = _dartName(node[3]);
        var init = node[4] != null ? " = ${translate(node[4])}" : "";
        _resolver.bind(name, type);
        return "$mods$type $name$init";
      },
      AST.ArrayInitializer: (node) {
        return "[${node[1].map(translate).map(_strip).join(", ")}]";
      },
      AST.Modifiers: (node) {
        var mods = "";
        if (node[1].contains("static")) mods += "static ";
        if (node[1].contains("final")) mods += "final ";
        return mods;
      },
      AST.Constructor: (node) {
        _resolver.beginScope();
        newline();
        var mods = node[1] != null ? translate(node[1]) : "";
        var name = _dartName(node[2]);
        emit("$mods$name(${node[3].map(translate).join(", ")})");
        emit("{");
        indent();
        translate(node[5]);
        dedent();
        emit("}");
        _resolver.endScope();
      },
      AST.Method: (node) {
        _resolver.beginScope();
        newline();
        var mods = node[1] != null ? translate(node[1]) : "";
        var type = translate(node[2]);
        var name = _dartName(node[3]);
        emit("$mods$type $name(${node[4].map(translate).join(", ")})${node[6].isEmpty ? ";": ""}");
        if (node[6].isNotEmpty) {
          emit("{");
          indent();
          translate(node[6]);
          dedent();
          emit("}");
        }
        _resolver.endScope();
      },
      AST.Parameter: (node) {
        var type = translate(node[1]), name = _dartName(node[2]);
        _resolver.bind(name, type);
        return "$type $name";
      },
      AST.RestParameter: (node) {
        var type = translate(node[1]), name = _dartName(node[2]);
        _resolver.bind(name, type);
        return "List<$type> $name /*XXX*/";
      },
      AST.Block: (node) {
        _resolver.beginScope();
        node[1].forEach(translate);
        _resolver.endScope();
      },
      AST.PrimitiveType: (node) {
        return _dartType(node[1]);
      },
      AST.ReferenceType: (node) {
        _types.add(node[1]); // TODO should only collect self-defined types
        var imported = _imports[node[1]];
        return imported != null ? imported : node[1];
      },
      AST.ArrayType: (node) {
        return "List<${translate(node[1])}>";
      },
      AST.GenericType: (node) {
        var type = translate(node[1]);
        if (node[2] != null && node[2][1][0] != "?") {
          type = "$type${translate(node[2])}";
        }
        return type;
      },
      AST.TypeParameters: (node) {
        return "<${node[1].join(", ")}>";
      },
      AST.Expression: (node) {
        emit("${_strip(translate(node[1]))};");
      },
      "call": (node) {
        var receiver = node[1] != null ? translate(node[1]) : "";
        if (node[1] != null && node[2] == "equals" && node[3].length == 1) {
          return "($receiver == ${translate(node[3][0])})";
        }
        if (node[1] != null && node[2] == "length" && node[3].length == 0) {
          return "$receiver.length";
        }
        if (node[1] != null && node[2] == "charAt" && node[3].length == 1) {
          return "$receiver.codeUnitAt(${_strip(translate(node[3][0]))})";
        }
        if (node[1] != null && node[2] == "equalsIgnoreCase" && node[3].length == 1) {
          return "java_equalsIgnoreCase($receiver, ${_strip(translate(node[3][0]))})";
        }
        if (node[1] != null && node[2] == "append" && node[3].length == 1) {
          if (_resolver.resolve(node[1]) == "StringBuffer") {
            node[2] = "write";
          }
        }
        if (receiver != "") receiver += ".";
        return "$receiver${_dartName(node[2])}(${node[3].map(translate).map(_strip).join(", ")})";
      },
      "field": (node) {
        return "${translate(node[1])}.${_dartName(node[2])}";
      },
      "literal": (node) {
        if (node[1] is String) {
          node[1] = node[1].replaceAll("\$", "\\\$");
          if (node[1][0] == "'") { // it's a char not a string
            return "${node[1]}.codeUnitAt(0)";
          }
        }
        return "${node[1]}";
      },
      "variable": (node) {
        return _dartName(node[1]);
      },
      "new_array": (node) {
        return "new List<${translate(node[1])}>(${translate(node[2])})";
      },
      "new_array_from": (node) {
        return "new List<${translate(node[1])}>.from([${node[2].map(translate).map(_strip).join(", ")}])";
      },
      "new_instance": (node) {
        return "new ${translate(node[1])}(${node[2].map(translate).map(_strip).join(", ")})";
      },
      // TODO use precedence to omit unneeded parentheses
      "+": _binaryExpression("+"),
      "-": _binaryExpression("-"),
      "*": _binaryExpression("*"),
      "/": _binaryExpression("~/"), // int only
      "%": _binaryExpression("%"),
      "&": _binaryExpression("&"),
      "|": _binaryExpression("|"),
      "^": _binaryExpression("^"),
      "<<": _binaryExpression("<<"),
      ">>": _binaryExpression(">>"),
      ">>>": _binaryExpression(">>"),
      "&&": _binaryExpression("&&"),
      "||": _binaryExpression("||"),
      "<": _binaryExpression("<"),
      "<=": _binaryExpression("<="),
      ">": _binaryExpression(">"),
      ">=": _binaryExpression(">="),
      "==": _binaryExpression("=="),
      "!=": _binaryExpression("!="),
      "=": _binaryExpression("="),
      "+=": _binaryExpression("+="),
      "-=": _binaryExpression("-="),
      "*=": _binaryExpression("*="),
      "/=": _binaryExpression("~/="), // int only
      "&=": _binaryExpression("&="),
      "|=": _binaryExpression("|="),
      "[]": (node) { return "${translate(node[1])}[${_strip(translate(node[2]))}]"; },
      "p++": (node) { return "${translate(node[1])}++"; },
      "p--": (node) { return "${translate(node[1])}--"; },
      "++p": (node) { return "(++${translate(node[1])})"; },
      "--p": (node) { return "(--${translate(node[1])})"; },
      "!": (node) { return "(!${translate(node[1])})"; },
      "~": (node) { return "(~${translate(node[1])})"; },
      "-p": (node) { return "(-${translate(node[1])})"; },
      "+p": (node) { return "(+${translate(node[1])})"; },
      "cast": (node) {
        //return "(${translate(node[2])} as ${translate(node[1])})";
        return translate(node[2]);
      },
      "?:": (node) {
        return "(${translate(node[1])} ? ${translate(node[2])} : ${translate(node[3])})";
      },
      "if": (node) {
        emit("if (${_strip(translate(node[1]))}) {");
        indent();
        translate(node[2]);
        if (node[3] != null) {
          dedent();
          emit("} else {");
          indent();
          translate(node[3]);
        }
        dedent();
        emit("}");
      },
      "return": (node) {
        if (node[1] != null) {
          emit("return ${_strip(translate(node[1]))};");
        } else {
          emit("return;");
        }
      },
      "for": (node) {
        var i = [];
        node[1].forEach((n) {
          if (n[0] == 'variable_declarations') i.addAll(n[1]);
          else i.add(n);
        });
        if (i.length > 1) {
          // Dart doesn't like multiple declarations in for so we move them
          i.forEach((n) => emit("${translate(n)};"));
          i = [];
        }
        emit("for (${i.map(translate).join(", ")}; "
          "${node[2] != null ? _strip(translate(node[2])) : ""}; "
          "${node[3].map(translate).join(", ")}) {");
        indent();
        translate(node[4]);
        dedent();
        emit("}");
      },
      "foreach": (node) {
        emit("for (${translate(node[1])} in ${translate(node[2])}) {");
        indent();
        translate(node[3]);
        dedent();
        emit("}");
      },
      "dowhile": (node) {
        emit("do {");
        indent();
        translate(node[2]);
        dedent();
        emit("} while (${_strip(translate(node[1]))});");
      },
      "whiledo": (node) {
        emit("while (${_strip(translate(node[1]))}) {");
        indent();
        translate(node[2]);
        dedent();
        emit("}");
      },
      "try": (node) {
        emit("try {");
        indent();
        translate(node[1]);
        dedent();
        node[2].forEach(translate);
        if (node[3] != null) {
          emit("} finally {");
          indent();
          translate(node[3]);
          dedent();
        }
        emit("}");
      },
      "catch": (node) {
        emit("} on ${translate(node[1][1])} catch (${_dartName(node[1][2])}) {");
        indent();
        translate(node[2]);
        dedent();
      },
      "assert": (node) {
        emit("assert(${_strip(translate(node[1]))});");
      },
      "break": (node) {
        if (node[1] != null) {
          emit("break ${node[1]};");
        }
        emit("break;");
      },
      "continue": (node) {
        if (node[1] != null) {
          emit("continue ${node[1]};");
        }
        emit("continue;");
      },
      "switch": (node) {
        emit("switch (${translate(node[1])}) {");
        indent();
        indent();
        node[2].forEach(translate);
        dedent();
        dedent();
        emit("}");
      },
      "case": (node) {
        dedent();
        emit("case ${translate(node[1])}:");
        indent();
      },
      "default": (node) {
        dedent();
        emit("default:");
        indent();
      },
      "label": (node) {
        emit("${node[1]}:");
      },
      "throw": (node) {
        emit("throw ${translate(node[1])};");
      },
      "pass": (node) {
      },
    };
    _resolver.beginScope();
  }

  Function _binaryExpression(String op) {
    return (node) { return "(${translate(node[1])} $op ${translate(node[2])})"; };
  }

  String _dartType(String type) {
    // map Java type to Dart type
    return const{
      "boolean": "bool",
      "byte": "int",
      "char": "int",
      "short": "int",
      "int": "int",
      "float": "double",
      "long": "int",
      "double": "double",
      "void" : "void"
    }[type];
  }

  String _dartName(String name) {
    // do not use Dart reserved words
    if (const["in", "is"].contains(name)) name += "_";
    // do not use the same name as for types
    if (_types.contains(name)) name += "_";
    return name;
  }

  String _strip(String s) {
    // ignore parentheses on top level
    if (s.startsWith("(") && s.endsWith(")")) {
      return s.substring(1, s.length - 1);
    }
    return s;
  }

  void newline() {
    _sink.write("\n");
  }

  void emit(String line) {
    for (int i = 0; i < _indent; i++) {
      _sink.write('    ');
    }
    _sink.writeln(line);
  }

  void indent() { _indent++; }

  void dedent() { _indent--; }

  translate(node) {
    return _funcs[node[0]](node);
  }
}

class Resolver {
  List<Map<String, String>> _scopes = [];

  void beginScope() {
    _scopes.add({});
  }

  void endScope() {
    _scopes.removeLast();
  }

  void bind(String variable, String type) {
    _scopes.last[variable] = type;
  }

  String lookup(String variable) {
    for (int i = _scopes.length - 1; i >= 0; i--) {
      String type = _scopes[i][variable];
      if (type != null) return type;
    }
    return null;
  }

  String resolve(node) {
    // we can resolve variables but nothing more
    if (node[0] == 'variable') return lookup(node[1]);
    return null;
  }
}

void main(List<String> args) {
  if (args.length > 0) {
    var source = new File(args[0]).readAsStringSync();
    var node = new Parser(source).parseCompilationUnit();
    new File("/tmp/json").writeAsStringSync(jsonEncode(node));
  } else {
    var node = jsonDecode(new File("/tmp/json").readAsStringSync());
    new Translator().translate(node);
  }
}
