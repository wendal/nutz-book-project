<%@ page import="java.security.MessageDigest"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
	/* *
	 jsp中集成 BeeCloud js button
	 * */
%>
<%!
String getMessageDigest(String s) {
    char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    try {
        byte[] buffer = s.getBytes("UTF-8");
        //获得MD5摘要算法的 MessageDigest 对象
        MessageDigest mdTemp = MessageDigest.getInstance("MD5");

        //使用指定的字节更新摘要
        mdTemp.update(buffer);

        //获得密文
        byte[] md = mdTemp.digest();

        //把密文转换成十六进制的字符串形式
        int j = md.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = md[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }

        return new String(str);
    } catch (Exception e) {
        return null;
    }
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no">
<title>spay demo</title>
</head>
<body>
<h1>asdfsadfasfasdfasdf</h1><p/>
<h1>asdfsadfasfasdfasdf</h1><p/>
<h1>asdfsadfasfasdfasdf</h1><p/>
<h1>asdfsadfasfasdfasdf</h1><p/>
<h1>asdfsadfasfasdfasdf</h1><p/>
<button id="test" style="size: 100px;">test online</button>
</div>
</body>
<!--1.添加控制台活的的script标签-->
<script id='spay-script' src='returnscripts.js?appId=7af02c41-b1f0-4239-a365-16d6d26e2993'></script>
<script src="./qrcode.js"></script>
<body>
	<%
        String app_id = "7af02c41-b1f0-4239-a365-16d6d26e2993";
        String app_secret = "98c51c3d-5545-4ffd-99e9-6bd2d66576fc";
        String title = "testPay";
        String amount = "1"; //单位分
        String out_trade_no = "test" + System.currentTimeMillis();

        //2.根据订单参数生成 订单签名 sign
        String sign = getMessageDigest(app_id + title + amount + out_trade_no + app_secret);
        String optional = "{\"msg\":\"addtion msg\"}";
    %>

<script type="text/javascript">
    document.getElementById("test").onclick = function() {
		//alert("fuck");

        BC.err = function(data) {
            //注册错误信息接受
            alert(data["ERROR"]);
        }
        /**
        * 3. 需要支付时调用BC.click接口传入参数
        * 注: sign的解释见后文
        */
        BC.click({
            "title":"<%=title%>", //商品名
            "amount":"<%=amount%>",  //总价（分）
            "out_trade_no":"<%=out_trade_no%>", //自定义订单号
            "sign":"<%=sign%>", //商品信息hash值，含义和生成方式见下文
            "return_url" : "http://payservice.beecloud.cn/spay/result.php", //支付成功后跳转的商户页面,可选，默认为http://payservice.beecloud.cn/spay/result.php
            "optional" : <%=optional%>//可选，自定义webhook的optional回调参数
        });

        /**
        * click调用错误返回：默认行为console.log(err)
        */
        BC.err = function(err) {
            //err 为object, 例 ｛”ERROR“ : "xxxx"｝;
        }
    };
</script>
</body>
</html>
