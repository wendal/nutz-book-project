$(function() {
  var path = location.pathname.split('/');
  var url = '../api/read/' + path[path.length - 1];
  $.post(url, function (j) {
    $('#content').text(j);
    prettyPrint();
  }, "text");
});
