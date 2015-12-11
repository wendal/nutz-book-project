<#macro admin2SimpleLayout title="">
<#assign rsbase="${cdnbase!}${base}/rs/admin2">
<#assign adminbase="${base}/admin2">
<!DOCTYPE html>
<html lang="en">
<head>
  <#include "header.ftl">
</head>
<body>
<!-- Form area -->
<div class="admin-form">
  <div class="container">
    <div class="row">
      <div class="col-md-12">
        <!-- Widget starts -->
        <#nested/>
      </div>
    </div>
  </div> 
</div>
<#include "footer.ftl">
</body>
</html>
</#macro>