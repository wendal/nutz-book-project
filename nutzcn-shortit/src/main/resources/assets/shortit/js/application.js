$(function () {
  navbar();
  footer();
  //disqus();
  //googleAnalytics();
});

function navbar() {
  $('<div class="navbar-inner"><div class="container"><a class="brand" href="/s/">短点!</a></div></div>').appendTo($('#navbar'));
}

function footer() {
  var $unstyledUl = $('<ul class="unstyled"></ul>');

  var $firstUlFooterLinks = $('<ul class="footer-links"></ul>');
  $firstUlFooterLinks
    .append($('<li>Powered by <a target="_blank" href="https://github.com/nutzam/nutz">Nutz</a></li>'))
    .append($('<li>Thanks <a target="_blank" href="http://twitter.github.com/bootstrap/index.html">Bootstrap</a></li>'));

  var $secondUlFooterLinks = $('<ul class="footer-links"></ul>');
  $secondUlFooterLinks
  .append($('<li><a target="_blank" href="http://nutz.cn/yvr/list">返回论坛</a></li>'))
  .append($('<li><a target="_blank" href="http://nutzam.com">Nutz 官网地址</a></li>'));

  $unstyledUl
    .append($('<li class="footer-links">Coded and designed by <a target="_blank" href="https://github.com/nutzam?tab=members">Nutz Production Committee</a> &copy; 2012</li>'))
    .append($('<li></li>').append($firstUlFooterLinks))
    .append($('<li></li>').append($secondUlFooterLinks))
    //.append($('<li class="footer-links" style="display: none" id="site-qrcode"><img src="https://chart.googleapis.com/chart?chs=72x72&amp;cht=qr&amp;choe=UTF-8&amp;chl=http%3A%2F%2Fwww.nutz.cn%2F"></li>'))
    //.append($('<li class="footer-links"><a href="javascript:void(0);" id="site-qrcode-str">显示本网站QR Code</a></li>'))
    .append($('<li class="footer-links"><a title="查看备案信息" target="_blank" href="http://www.miibeian.gov.cn">苏ICP备 10226088号-17</a></li>'));

  $('<div class="container"></div>').append($unstyledUl).appendTo($('.footer'));

  //qrcodeToggle("site", "显示本站QR Code", "隐藏本站QR Code");
}

function qrcodeToggle(id, showStr, hideStr) {
  $("#" + id + "-qrcode-str").toggle(
    function () {
      $("#" + id + "-qrcode").show();
      $("#" + id + "-qrcode-str").html(hideStr);
    },
    function () {
      $("#" + id + "-qrcode").hide();
      $("#" + id + "-qrcode-str").html(showStr);
    }
  );
}

function disqus() {
  /* * * CONFIGURATION VARIABLES: EDIT BEFORE PASTING INTO YOUR WEBPAGE * * */
  var disqus_shortname = 'shortit2';

  /* * * DON'T EDIT BELOW THIS LINE * * */
  (function() {
      var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;
      dsq.src = 'http://' + disqus_shortname + '.disqus.com/embed.js';
      (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);
  })();
}

function googleAnalytics() {
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-36753226-1']);
  _gaq.push(['_setDomainName', 'nutz.cn']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
}
