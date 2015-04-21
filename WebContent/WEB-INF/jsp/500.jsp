<%@page import="org.nutz.lang.Strings"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="org.nutz.mvc.Mvcs"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isErrorPage="true" trimDirectiveWhitespaces="true"
    session="false"%>
<% response.setStatus(500); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>出错啦</title>
</head>
<body>
<div>
<%
	Throwable e = exception;
	if (e == null) {
		Object obj = request.getAttribute("obj");
		if (obj != null && obj instanceof Throwable) {
			e = (Throwable)obj;
		} else {
			if (Mvcs.getActionContext() != null) {
				e = Mvcs.getActionContext().getError();
			}
		}
	}
%>
	<h2>请求的路径: <%=(request.getAttribute("javax.servlet.forward.request_uri") + (request.getQueryString() == null ? "" : "?" + request.getQueryString())) %></h2><p/>
	<%
		if (Mvcs.getActionContext() != null) {
	%>
	<h2>请求的方法: <%=Mvcs.getActionContext().getMethod() %></h2><p/>
	<%
		}
	if (e != null) {
	%>
	
	<h2>异常堆栈如下:</h2><p/>
	<pre>
		<code class="lang-java">
<%
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(bao);
			
			e.printStackTrace(pw);
			pw.flush();
%>
<%=Strings.escapeHtml(new String(bao.toByteArray())) %>
		</code>
	</pre>
<%	
	}
%>
</div>
</body>
</html>