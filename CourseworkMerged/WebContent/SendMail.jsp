<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="application/xhtml+xml; charset=utf-8" pageEncoding="utf-8"%>

    <head>
        <title>Send e-Mail</title>
        <link type="text/css" rel="stylesheet" href="sendMail.css" />
     	

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
        <form action="sendMail" method="post" enctype="multipart/form-data">
            <fieldset>
                <legend>Send an e-mail</legend>
                
                
                <div class="block">
				    <label for="emailFrom">From <span class="requis">*</span></label>
				    <input type="email" id="emailFrom" name="emailFrom" value="${username}" size="20" maxlength="30" readonly="readonly"/>
					<span class="erreur">${form.erreurs['emailFrom']}</span>
				</div>
				
				<div class="block">
				    <label for="emailTo">To <span class="requis">*</span></label>
                	<input type="email" id="emailTo" name="emailTo" value="<c:out value=" ${param.emailTo}"/>" size="20" maxlength="30" />
                	<span class="erreur">${form.erreurs['emailTo']}</span>
				</div>
				
				<div class="block">
                	<label for="subject">Subject <span class="requis">*</span></label>
	                <input type="text" id="subject" name="subject" value="<c:out value="${param.subject}"/>" size="30" maxlength="50" />
               		<span class="erreur">${form.erreurs['subject']}</span>
				</div>
				
				<div class="block">
                	<label for="message">Message:</label>
                	<textarea rows="12" cols="50" id="message" name="message" maxlength="10000"></textarea> 
             	   <span class="erreur">${form.erreurs['message']}</span>
				</div>
				
                
                <label for="file" class="file">Select a file</label>
                <input type="file" id="file" name="file" />
                <span class="erreur">${form.erreurs['file']}</span>
                <br /><br />

                <input type="submit" value="Send" id="send" class="send" />
                <br />
                
 

            </fieldset>
        </form>
	</div>
	<p><a href="http://validator.w3.org/check?uri=referer">Valid XHTML?</a></p>
	<p><a href="http://jigsaw.w3.org/css-validator/check/referer">Valid CSS?</a></p>
    </body>
</html>