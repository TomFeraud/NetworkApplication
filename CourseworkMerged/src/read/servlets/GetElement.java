package read.servlets;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import read.imap.Imap4Session;
import read.mime.MimeElement;
import read.mime.MimeMessage;

/**
 * Servlet implementation class GetElement
 */
public class GetElement extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetElement() {
        super();
        // TODO Auto-generated constructor stub
    }

	//flat list of Mime elements in an email
	HashMap<Integer, Object> elements;
	//messages
	HashMap<Integer, MimeMessage> messages;
	//http session
	HttpSession httpSession;
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("in GetElement get");
		
		//get session
		httpSession = request.getSession();		
		
		//get session attributes
		Imap4Session imapSession = 
			(Imap4Session) httpSession.getAttribute("imapSession");
		
		try {
					
			//only proceed if the user has an active imap session
			if(imapSession != null) {
				//get the messages from the session
				messages = (HashMap<Integer, MimeMessage>) 
						httpSession.getAttribute("messages");
				
				//get the elements from the session
				elements = (HashMap<Integer, Object>) 
						httpSession.getAttribute("elements");
				
				String[] id = request.getParameterValues("id");
				if(id != null) {
					getMimeEntry(request, response, Integer.parseInt(id[0]));
				} 
			} else {
				//if their is no active imap session, redirect to login
				//inform user of error
				request.setAttribute("warning", "Please Log In");
				redirectToLogin(request, response);
			}
		} catch (IOException e) {
			//inform user of error
			request.setAttribute("warning", e.getMessage());
		}
	}
	
	public void getMimeEntry(HttpServletRequest request,
			HttpServletResponse response, int i) throws IOException {
		ServletOutputStream out = response.getOutputStream();
		Object obj = elements.get(i);
		
		
		if(obj.getClass() == MimeElement.class) {
			MimeElement element = (MimeElement) obj;
			System.out.println(element.getContentMajor());
			if(element.getContentMajor().
				toLowerCase().equals("image") || element.getContentMajor().
			toLowerCase().equals("video")){

				response.setContentType(element.getContentMajor() 
						+ "/" + element.getContentMinor());

				try {
					byte[] bytes = element.getContentBytes();
					for(byte b : bytes)
						response.getOutputStream().write(b);
					response.getOutputStream().flush();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			else {
				out.print(element.getContentAsString());
			}
		}
	}

	private void redirectToLogin(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		} catch (IOException e) {
			//inform user of error
			request.setAttribute("warning", e.getMessage());
		} catch (ServletException e) {
			//inform user of error
			request.setAttribute("warning", e.getMessage());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
