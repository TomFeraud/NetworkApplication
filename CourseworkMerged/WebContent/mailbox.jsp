<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<%@page import="read.mime.MimeMessage"%>
<%@page import="java.util.HashMap"%>
<%! @SuppressWarnings("unchecked")%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="ISO-8859-1"%>
<%@ page import="read.mime.MimeMessage" %>
<!-- Citations: http://www.the-art-of-web.com/javascript/confirm/ -->
<head>
	<!--   <link rel="stylesheet" href="main.css"/> -->
	<!--  <link rel="stylesheet" href="mailbox.css"/> -->
	 <link rel="stylesheet" href="main.css"/>	
	<title>${currentMailBox}</title>
</head>
<body>
	<!-- refresh the page after 30 seconds -->
	<script>setTimeout(function(){location.reload();}, 30000);</script>
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
	<div class="contentWrapper">
		<div class = "spacer"><br/><br/></div>
		<div class="content">
			<div class="emailList">
				<%int iteration = 0; %>
				<c:if test="${emailCount > '0'}">
					<c:forEach var = "i" begin = "${pageStart}" end = "${pageEnd}">
						<!-- find out if line is odd or even and colour accordingly -->
						<div class="l${i%2}">
							<% /*messy way of getting current message from HashMap and storing
							in the page context*/
							//get current item in reverse message order
							int end = (Integer) request.getAttribute("pageEnd");
							
							pageContext.setAttribute("currentMessage", 
									((HashMap<Integer, MimeMessage>) 
											request.getAttribute("messages"))
												.get(end - iteration)); 
												
							iteration++;%>
							<div class="emailListItem">
									<span class = "deleteLink">
										<a href="DeleteMessage?id=${currentMessage.getId()}&validator=${validator}" 
											onClick="return confirm('Are you sure you want to delete this email?')">
											Delete
										</a>
									</span>
									<span class = "fromField">
										${currentMessage.getFromEmail()}
									</span>
									<span class = "toField">
										${currentMessage.getToEmail()}
									</span>
									<span class = "subjectField">
										<a href="ViewMail?id=${currentMessage.getId()}">
											${currentMessage.getSubject()}
										</a>
									</span>
									<%-- <span class = "dateField">
										${currentMessage.getDate()}
									</span> --%>
							</div>
						</div>
					</c:forEach>
				</c:if>
			</div>
			<div class = "warning">${warning}</div>
			<div class = "info">${info}</div>
			
			
			<div class="pageLinks">
				<div class= "pageLink">
					<c:forEach var = "i" begin = "${1}" end = "${noOfPages}">
						<a href="MailBox?mailBox=${currentMailBox}&page=${i}">${i}</a>
					</c:forEach>
				</div>
			</div>
		</div>
	</div>
	
	<a href="http://validator.w3.org/check?uri=referer">Valid HTML?</a>
</body>
</html>