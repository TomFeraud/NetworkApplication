package servlets;

import imap.Imap4Session;
import imap.StatusBadException;
import imap.StatusNoException;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Login
 */
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		String[] username = request.getParameterValues("username");
		String[] password = request.getParameterValues("password");
		String[] port = request.getParameterValues("port");
		String[] server = request.getParameterValues("server");
		
		//get session
		HttpSession httpSession = request.getSession();
		
		try {
		//if correct fields are supplied, open a new connection
			if(username != null && password != null && 
					server != null  && port != null ) {
				Imap4Session imapSession = new Imap4Session(server[0], Integer.parseInt(port[0]));
				ArrayList<String> mailBoxes;
				
				imapSession.connect();
				imapSession.login(username[0], password[0]);
				imapSession.updateMailBoxes();
				mailBoxes = imapSession.getMailBoxes();
				
				httpSession.setAttribute("imapSession", imapSession);
				httpSession.setAttribute("mailBoxes", mailBoxes);
				
				//select the INBOX
				httpSession.setAttribute("currentMailBox", "INBOX");
				
				//save the mailboxes to the session
				httpSession.setAttribute("mailBoxes", mailBoxes);
				
				//show the mailbox
				response.sendRedirect("MailBox");
			}
		} catch (StatusBadException e) {
			//inform user of error
			request.setAttribute("warning", e.getMessage());
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		} catch (StatusNoException e) {
			//inform user of error (bad credentials)
			request.setAttribute("warning", e.getMessage());
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		}
	}

}
