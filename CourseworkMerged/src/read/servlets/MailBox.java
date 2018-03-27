package read.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import utility.ServletUtils;
import read.imap.Imap4Session;
import read.imap.StatusBadException;
import read.imap.StatusNoException;
import read.mime.MimeMessage;

/**
 * Displays the contents of a mailbox
 */
public class MailBox extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MailBox() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("in MailBox get");
		
		//get session
		HttpSession httpSession = request.getSession();
		
		//get session attributes
		Imap4Session imapSession = 
			(Imap4Session) httpSession.getAttribute("imapSession");
		
		try {
			//only proceed if the user has an active imap session
			if(imapSession != null) {
				//added to delete links to test if they were clicked
				//in the inbox and not refreshed/hit from back/forward buttons
				httpSession.setAttribute("validator", System.currentTimeMillis());
				
				//test if a new mailbox has been selected in as parameter
				String mailBox[] = request.getParameterValues("mailBox");
				
				//test if a page number has been supplied
				String page[] = request.getParameterValues("page");
				int pageNo = 1;
				
				if(page != null) {
					pageNo = Integer.parseInt(page[0]);
					System.out.println("Page no: " + pageNo);
				}
				
				if(mailBox != null) {
					//set the new mail box in the session
					httpSession.setAttribute("currentMailBox", mailBox[0]);
				}
				
				@SuppressWarnings("unchecked")
				ArrayList<String> mailBoxes = 
						(ArrayList<String>) httpSession.getAttribute("mailBoxes");
	
				String currentMailBox = (String)
						httpSession.getAttribute("currentMailBox");
				
				imapSession.selectMailBox(currentMailBox);
			
				int emailCount = imapSession.getMessageCount();
				
				
				if(emailCount > 0) {
					System.out.println("Message count: " + emailCount);
					HashMap<Integer, MimeMessage> messages;
					
					//get the number of pages
					int noOfPages = emailCount / 25;
					//if there's remaining emails, add a page for them
					if(emailCount % 25 > 0)
						noOfPages++;
					
					System.out.println("Message count: " + emailCount);
					System.out.println("Full pages: " + noOfPages);
					
					//each page has 25 entries, so the page number 
					//offsets the start by (pageNo-1) * 25. 
					//Recent emails have higher numbers,
					//so deduct the offset
					int pageEnd = emailCount - (pageNo - 1) * 25;
					//a page is 25 entries long, so set the end to be
					//25 away from the start
					int pageStart = pageEnd - 24;
					
					//the last page may have less than 25 items,
					//so make sure the start doesn't fall below 1
					if(pageStart < 1)
						pageStart = 1;
					
					System.out.println("start: " + pageStart);
					System.out.println("end: " + pageEnd);
				
					imapSession.fetchMessageSummaries(pageStart, pageEnd);
					messages = imapSession.getMessageSummaries();
					
					for (String str : mailBoxes)
						System.out.println("!! " + str);
					
					//save messages in the session
					httpSession.setAttribute("messages", messages);
					
					//save attributes to the session
					httpSession.setAttribute
						("currentMailBox", currentMailBox);
					httpSession.setAttribute
						("pageNo", pageNo);
					
					//save attributes to the request
					request.setAttribute("mailBoxes", mailBoxes);
					request.setAttribute("currentMailBox", currentMailBox);
					request.setAttribute("messages", messages);
					request.setAttribute("pageStart", pageStart);
					request.setAttribute("pageEnd", pageEnd);
					request.setAttribute("noOfPages", noOfPages);
				}
				request.setAttribute("emailCount", emailCount);
				
				//display the mail box jsp
				request.getRequestDispatcher("/mailbox.jsp").forward(request, response);
			} else {
				//if there is no active imap session, redirect to login
				//to inform user of error
				request.setAttribute("warning", "Please Log In");
				ServletUtils.redirectToLogin(request, response);
			}
		} catch (StatusNoException e) {
			//inform user of error
			request.setAttribute("warning", e.getMessage());
			ServletUtils.redirectToLogin(request, response);
		} catch (StatusBadException e) {
			//inform user of error
			request.setAttribute("warning", e.getMessage());
			ServletUtils.redirectToLogin(request, response);
		} catch (IOException e) {
			//inform user of error
			request.setAttribute("warning", e.getMessage());
			ServletUtils.redirectToLogin(request, response);
		} catch (ServletException e) {
			//inform user of error
			request.setAttribute("warning", e.getMessage());
			ServletUtils.redirectToLogin(request, response);
		}
		
	}
	


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("in MailBox post");
	}

}
