<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<%! @SuppressWarnings("unchecked")%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="application/xhtml+xml; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="read.mime.MimeMessage" %>
<head>
<title>Login</title>
<link rel="stylesheet" href="login.css"/>
</head>
<body>
	<c:if test="${empty(imapSession)}">
		<div class = "warning">${warning}</div>
		<div class = "info">${info}</div>
	
		<form action="Login" method="post">
		
		<div class="block">
		    <label>Server:</label>
		    <input type="text" name="server" list="servers" required="required" value="outlook.office365.com"/><datalist id="servers">
						<option value="imap.mail.co.uk"></option>
						<option value="outlook.office365.com"></option>
					</datalist>	
		</div>
		<div class="block">
		    <label>Port:</label>
		    <input type="text" name="port" value="993" required="required"/>
		</div>
		<div class="block">
		    <label>User name:</label>
		    <input type="text" name="username" value="" required="required"/>		    
		</div>
		<div class="block">
		    <label>Password:</label>
		    <input type="password" name="password" required="required"/>		    
		</div>
		
		<p><input type="submit"/></p>
				
		</form>
	</c:if>
	<c:if test="${not empty(imapSession)}">
		Already logged in.
		<%response.sendRedirect("MailBox"); %>
	</c:if>
	<br/><br/><br/><br/>
	<p><a href="http://validator.w3.org/check?uri=referer">Valid XHTML?</a></p>
	<p><a href="http://jigsaw.w3.org/css-validator/check/referer">Valid CSS?</a></p>
	<p class="names">Tom Feraud &amp; Andrew Graham</p>


</body>
</html>
