

  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta charset="utf-8">
  <!-- Title and other stuffs -->
  <title>${title}-${conf["website.title"]}</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="${conf['website.long_description']}">
  <meta name="keywords" content="${conf['website.keywords']}">
  <meta name="author" content="${conf['website.author']}">
  <link rel="canonical" href="${conf["website.urlbase"]}" itemprop="url"/>


  <!-- Stylesheets -->
  <link href="${rsbase}/css/bootstrap.min.css" rel="stylesheet">
  <!-- Font awesome icon -->
  <link rel="stylesheet" href="${rsbase}/css/font-awesome.min.css"> 
  <!-- jQuery UI -->
  <link rel="stylesheet" href="${rsbase}/css/jquery-ui.css"> 
  <!-- Calendar -->
  <link rel="stylesheet" href="${rsbase}/css/fullcalendar.css">
  <!-- prettyPhoto -->
  <link rel="stylesheet" href="${rsbase}/css/prettyPhoto.css">  
  <!-- Star rating -->
  <link rel="stylesheet" href="${rsbase}/css/rateit.css">
  <!-- Date picker -->
  <link rel="stylesheet" href="${rsbase}/css/bootstrap-datetimepicker.min.css">
  <!-- CLEditor -->
  <link rel="stylesheet" href="${rsbase}/css/jquery.cleditor.css">  
  <!-- Data tables -->
  <link rel="stylesheet" href="${rsbase}/css/jquery.dataTables.css"> 
  <!-- Bootstrap toggle -->
  <link rel="stylesheet" href="${rsbase}/css/jquery.onoff.css">
  <!-- Main stylesheet -->
  <link href="${rsbase}/css/style.css" rel="stylesheet">
  <!-- Widgets stylesheet -->
  <link href="${rsbase}/css/widgets.css" rel="stylesheet">   
  
  <script src="${rsbase}/js/respond.min.js"></script>
  <!--[if lt IE 9]>
  <script src="${rsbase}/js/html5shiv.js"></script>
  <![endif]-->

  <!-- Favicon -->
  <link rel="shortcut icon" href="${base}/rs/logo/logo.png">


<!-- JS -->
<script src="${rsbase}/js/jquery.js"></script> <!-- jQuery -->
<script src="${rsbase}/js/bootstrap.min.js"></script> <!-- Bootstrap -->
<script src="${rsbase}/js/jquery-ui.min.js"></script> <!-- jQuery UI -->
<script src="${rsbase}/js/moment.min.js"></script> <!-- Moment js for full calendar -->
<script src="${rsbase}/js/fullcalendar.min.js"></script> <!-- Full Google Calendar - Calendar -->
<script src="${rsbase}/js/jquery.rateit.min.js"></script> <!-- RateIt - Star rating -->
<script src="${rsbase}/js/jquery.prettyPhoto.js"></script> <!-- prettyPhoto -->
<script src="${rsbase}/js/jquery.slimscroll.min.js"></script> <!-- jQuery Slim Scroll -->
<script src="${rsbase}/js/jquery.dataTables.min.js"></script> <!-- Data tables -->

<!-- jQuery Flot -->
<script src="${rsbase}/js/excanvas.min.js"></script>
<script src="${rsbase}/js/jquery.flot.js"></script>
<script src="${rsbase}/js/jquery.flot.resize.js"></script>
<script src="${rsbase}/js/jquery.flot.pie.js"></script>
<script src="${rsbase}/js/jquery.flot.stack.js"></script>

<!-- jQuery Notification - Noty -->
<script src="${rsbase}/js/jquery.noty.js"></script> <!-- jQuery Notify -->
<script src="${rsbase}/js/themes/default.js"></script> <!-- jQuery Notify -->
<script src="${rsbase}/js/layouts/bottom.js"></script> <!-- jQuery Notify -->
<script src="${rsbase}/js/layouts/topRight.js"></script> <!-- jQuery Notify -->
<script src="${rsbase}/js/layouts/top.js"></script> <!-- jQuery Notify -->
<!-- jQuery Notification ends -->

<script src="${rsbase}/js/sparklines.js"></script> <!-- Sparklines -->
<script src="${rsbase}/js/jquery.cleditor.min.js"></script> <!-- CLEditor -->
<script src="${rsbase}/js/bootstrap-datetimepicker.min.js"></script> <!-- Date picker -->
<script src="${rsbase}/js/jquery.onoff.min.js"></script> <!-- Bootstrap Toggle -->
<script src="${rsbase}/js/filter.js"></script> <!-- Filter for support page -->
<script src="${rsbase}/js/custom.js"></script> <!-- Custom codes -->
<script src="${rsbase}/js/charts.js"></script> <!-- Charts & Graphs -->
<script>
$(document).ready(function () {
  if($.support.pjax) {
  	$(document).pjax('a[data-pjax]', '#content', {fragment: '#content',maxCacheLength:0,timeout: 8000});
  }
  if (console)
  	console.log("^_^ 在找源码?这么巧. footer就有地址哦 https://github.com/wendal/nutz-book-project");
});
</script>