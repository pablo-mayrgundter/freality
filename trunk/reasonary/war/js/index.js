function $(id) {
  return document.getElementById(id);
}

function init() {
  var r = new Resource('/test').get(function(json) {
      $('recent').innerHTML = json.yo;
    });
}
