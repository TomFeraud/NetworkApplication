package servlets;

import imap.Imap4Session;
import imap.StatusBadException;
import imap.StatusNoException;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import utility.ServletUtils;

/**
 * Servlet implementation class Logout
 */
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Logout() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("in Logout get");
		
		//get session
		HttpSession httpSession = request.getSession();
		
		//get session attributes
		Imap4Session imapSession = 
			(Imap4Session) httpSession.getAttribute("imapSession");
		
		try {
			//only proceed if the user has an active imap session
			if(imapSession != null) {
				//logout and remove the IMAP session object 
				//from HTTP session
				imapSession.logout();
				httpSession.removeAttribute("imapSession");
				
				//inform user of successful logout
				request.setAttribute("info", "Successfully logged out.");

				ServletUtils.redirectToLogin(request, response);
				
			} else {
				//if there is no active imap session, redirect to login
				//to inform user of error
				request.setAttribute("warning", "Please Log In");
				ServletUtils.redirectToLogin(request, response);
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
		}
	}
}
