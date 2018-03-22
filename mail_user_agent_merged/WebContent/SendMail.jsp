<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>Send e-Mail</title>
        <link type="text/css" rel="stylesheet" href="form.css" />

    </head>
    <body>
   
        <form action="sendMail" method="post" enctype="multipart/form-data">
            <fieldset>
                <legend>Send an e-mail</legend>
                <p>You can send an e-mail using the following form:</p>

                <label for="from">From <span class="requis">*</span></label>
                <input type="email" id="emailFrom" name="emailFrom" value="${username}" size="20" maxlength="30" readonly="true"/>
                <span class="erreur">${form.erreurs['emailFrom']}</span>
                <br />

                <label for="to">To <span class="requis">*</span></label>
                <input type="email" id="emailTo" name="emailTo" value="" size="20" maxlength="30" />
                <span class="erreur">${form.erreurs['emailTo']}</span>
                <br />

                <label for="subject">Subject <span class="requis">*</span></label>
               <input type="text" id="subject" name="subject" value="" size="20" maxlength="40" />
                <span class="erreur">${form.erreurs['subject']}</span>
                <br />

                <label for="message">Message</label>
                <textarea rows="4" cols="50" id="message" name="message" maxlength="500"></textarea> 
                <span class="erreur">${form.erreurs['message']}</span>
                <br />
                
                <label for="fichier">Select a file <span class="requis">*</span></label>
                <input type="file" id="file" name="file" />
                <span class="erreur">${form.erreurs['file']}</span>
                <br />

                <input type="submit" value="Send" class="sansLabel" />
                <br />
                
 

            </fieldset>
        </form>
    </body>
</html>