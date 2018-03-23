package send.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import send.MailContent;
import send.MailForm;
import send.SMTP;

@SuppressWarnings("serial")
@MultipartConfig
public class SendMail extends HttpServlet {
	public static final String ATT_USER = "mailContent";
	public static final String ATT_FORM = "form";
	public static final String VIEW = "/SendMail.jsp";
	public static final String VIEW_FAIL = "/SendMail.jsp"; // If the data entered are incorrect, we stay on
																	// this page with the errors displayed
	public static final String VIEW_SUCCESS = "MailBox"; // If it's correct we switch pages

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Display the "send mail" interface
		this.getServletContext().getRequestDispatcher(VIEW).forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		MailForm form = new MailForm();
		MailContent mailContent = form.setupMail(request);
		
		

		// Storage of the form in the request object
		request.setAttribute(ATT_FORM, form);
		request.setAttribute(ATT_USER, mailContent);

		if (form.getResultat().equalsIgnoreCase("Success")) {
			response.sendRedirect(VIEW_SUCCESS);
			@SuppressWarnings("unused")
			SMTP testSMTP = new SMTP(mailContent);
		} else {
			this.getServletContext().getRequestDispatcher(VIEW_FAIL).forward(request, response);
		}

	}

}
