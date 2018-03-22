<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<%! @SuppressWarnings("unchecked")%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="application/xhtml+xml; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="read.mime.MimeMessage" %>
<head>
<title>Login</title>
</head>
<body>
	<c:if test="${empty(imapSession)}">
		<div class = "warning">${warning}</div>
		<div class = "info">${info}</div>
	
		<form action="Login" method="post">
				<p>Server: <input type="text" name="server" list="servers" required="true" value="imap.mail.co.uk"/></p>
					<datalist id="servers">
						<option value="imap.mail.co.uk"></option>
						<option value="outlook.office365.com"></option>
					</datalist>	
				<p>Port: <input type="text" name="port" value="993" required="true"/></p>
				<p>User name: <input type="text" name="username" value="F21NA-TEST@mail.co.uk" required="true"/></p>
				<p>Password: <input type="password" name="password" required="true"/></p>
				<p><input type="submit"/></p>
		</form>
	</c:if>
	<c:if test="${not empty(imapSession)}">
		Already logged in.
		<%response.sendRedirect("MailBox"); %>
	</c:if>
	<a href="http://validator.w3.org/check?uri=referer">Valid HTML?</a>
</body>
</html>
