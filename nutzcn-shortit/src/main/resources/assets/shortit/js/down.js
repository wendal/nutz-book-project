$(function() {
  var path = location.pathname.split('/');
  $("#fileHref").attr('href', '../api/down/' + path[path.length - 1]);
});
