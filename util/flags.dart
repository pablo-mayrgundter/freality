#library('util/flags');

#import('dart:html');

class Flags {

  var params;

  Flags() {
    var href = window.location.href;
    var praw = href.split(new RegExp('/&/'));
    var p = {};
    for (var i in praw) {
      var param = praw[i];
      var parts = param.split('=');
      p[parts[0]] = parts[1];
    }
    params = p;
  }

  String get(String name, String defaultVal) {
    var val = params[name];
    return val ? val : defaultVal;
  }
}
