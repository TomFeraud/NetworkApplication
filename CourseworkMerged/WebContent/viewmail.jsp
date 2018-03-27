<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="application/xhtml+xml; charset=utf-8" pageEncoding="utf-8"%>
<head>
	<link rel="stylesheet" href="viewmail.css"/>
	<title>View mail</title>
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
		<div class = "spacer"></div>
		<div class="content">
			<div class = "details">
				<div class = "padDetails">
					<div><label>From:</label> ${fromAdd} (${fromName})</div>
					<div><label>Subject:</label> ${subject}</div>
					<div><label>Date:</label> ${date}</div>
					<!-- <div>To: ${toAdd} (${toName})</div> -->
					<div><label>Message:</label></div><hr class = "line" /> 
				</div>
			</div>
			<div class = "body">
				<object type="text/html" data="body.jsp" class="emailBody"></object>
			</div>
		</div>
		
	</div>
	<p><a href="http://validator.w3.org/check?uri=referer" class ="valid">Valid XHTML?</a></p>
	<p><a href="http://jigsaw.w3.org/css-validator/check/referer">Valid CSS?</a></p>
	
</body>
</html>