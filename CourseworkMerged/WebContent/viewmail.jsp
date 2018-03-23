<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="utf-8"%>
<head>
	<!-- <link rel="stylesheet" href="main.css"/> -->
	<link rel="stylesheet" href="send.css"/>
	<meta http-equiv="Content-Type" content="application/xhtml+xml; charset=utf-8" />
	<title>Insert title here</title>
</head>
<body>
	<div class = "header">
				<div class = "headerLink">
				<a href="Logout">
					Logout
				</a>
		</div>
		<div class = "headerLink">
				<a href="SendMail.jsp">
					Compose
				</a>
			</div>	
		<c:forEach var="mailBox" items="${mailBoxes}" >
			<div class = "headerLink">
				<a href='MailBox?mailBox=${fn:replace(mailBox, " ", "%20")}'>
					${mailBox}
				</a>
			</div>	
		</c:forEach>
	</div>
	
	
	<div class = "warning">${warning}</div>
	<div class = "info">${info}</div>
	<div class="contentWrapper">
		<div class = "spacer"><br/><br/></div>
		<div class="content">
			<p>Subject: ${subject}</p>
			<p>Date: ${date}</p>
			<p>From: ${fromAdd} (${fromName})</p>
			<p>To: ${toAdd} (${toName})</p>
			${emailBody}
		</div>
	</div>
	<a href="http://validator.w3.org/check?uri=referer">Valid HTML?</a>
	
</body>
</html>