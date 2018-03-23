<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="application/xhtml+xml; charset=utf-8" pageEncoding="utf-8"%>

    <head>
        <title>Send e-Mail</title>
        <link type="text/css" rel="stylesheet" href="send.css" />

    </head>
    <body>
   
        <form action="sendMail" method="post" enctype="multipart/form-data">
            <fieldset>
                <legend>Send an e-mail</legend>

                <label for="emailFrom">From <span class="requis">*</span></label>
                <input type="email" id="emailFrom" name="emailFrom" value="${username}" size="20" maxlength="30" readonly="readonly"/>
                <span class="erreur">${form.erreurs['emailFrom']}</span>
                <br />

                <label for="emailTo">To <span class="requis">*</span></label>
                <input type="email" id="emailTo" name="emailTo" value="<c:out value=" ${param.emailTo}"/>" size="20" maxlength="30" />
                <span class="erreur">${form.erreurs['emailTo']}</span>
                <br />

                <label for="subject">Subject <span class="requis">*</span></label>
               <input type="text" id="subject" name="subject" value="<c:out value="${param.subject}"/>" size="30" maxlength="50" />
                <span class="erreur">${form.erreurs['subject']}</span>
                <br />

                <label for="message">Message</label>
                <textarea rows="12" cols="50" id="message" name="message" maxlength="10000"></textarea> 
                <span class="erreur">${form.erreurs['message']}</span>
                <br />
                
                <label for="file">Select a file <span class="requis">*</span></label>
                <input type="file" id="file" name="file" />
                <span class="erreur">${form.erreurs['file']}</span>
                <br />

                <input type="submit" value="Send" class="sansLabel" />
                <br />
                
 

            </fieldset>
        </form>
        
        <a href="http://validator.w3.org/check?uri=referer">Valid HTML?</a>
    </body>
</html>