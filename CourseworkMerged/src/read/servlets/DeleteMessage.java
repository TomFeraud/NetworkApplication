package read.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import read.imap.Imap4Session;
import read.imap.StatusBadException;
import read.imap.StatusNoException;
import utility.ServletUtils;

/**
 * Servlet implementation class DeleteMessage
 */
public class DeleteMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteMessage() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("in DeleteMessage get");
		
		//get session
		HttpSession httpSession = request.getSession();
		
		//get session attributes
		Imap4Session imapSession = 
			(Imap4Session) httpSession.getAttribute("imapSession");
		
		//is this a genuine click?
		boolean validated = false;
		
		String[] validator = request.getParameterValues("validator");
		if(validator != null) {
			String sessionValidator = httpSession.getAttribute("validator").toString();
			System.out.println("[0] " + validator[0]);
			System.out.println("ses " + sessionValidator);
			if(validator[0].equals(sessionValidator)) {
				validated = true;
				System.out.println("validatators match");
			}
			else
				System.out.println("validatators don't match");
			
			//change session validation code to prevent refresh deletes
			httpSession.setAttribute("validator", System.currentTimeMillis());
		}
		
	
		try {
			//if the click is a from a delete link
			if(validated) {
				//only proceed if the user has an active imap session
				//and the request comes from a genuine delete click
				if(imapSession != null && validated) {
					String[] id = request.getParameterValues("id");
					if(id != null) {
						imapSession.addFlag(Integer.parseInt(id[0]), "Deleted");
						imapSession.expunge();
					}
					System.out.println("Forwarding.");
					//refresh the mail box
					request.getRequestDispatcher("MailBox?mailBox=" +
							httpSession.getAttribute("currentMailBox") +
							"&page=" + httpSession.getAttribute("pageNo"))
								.forward(request, response);							
				} else {
					//if there is no active imap session, redirect to login
					//to inform user of error
					request.setAttribute("warning", "Please Log In");
					ServletUtils.redirectToLogin(request, response);
				}
			} //if not validate, go back to the inbox
			else {
				ServletUtils.redirectToInbox(request, response);
			}

		} catch (StatusNoException e) {
			//inform user of error
			request.setAttribute("warning", e.getMessage());
			ServletUtils.redirectToInbox(request, response);
		} catch (StatusBadException e) {
			//inform user of error
			request.setAttribute("warning", e.getMessage());
			ServletUtils.redirectToInbox(request, response);
		} catch (IOException e) {
			//inform user of error
			request.setAttribute("warning", e.getMessage());
			ServletUtils.redirectToLogin(request, response);
		} catch (ServletException e) {
			//inform user of error
			request.setAttribute("warning", e.getMessage());
			ServletUtils.redirectToInbox(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
