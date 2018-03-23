package servlets;

import imap.Imap4Session;
import imap.StatusBadException;
import imap.StatusNoException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import utility.HTMLUtils;
import utility.ServletUtils;
import mime.MimeElement;
import mime.MimeEntry;
import mime.MimeEntryList;
import mime.MimeMessage;

/**
 * Servlet implementation class ViewMail
 */
public class ViewMail extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	
	//flat list of Mime elements in an email
	HashMap<Integer, Object> elements;
	//messages
	HashMap<Integer, MimeMessage> messages;
	//http session
	HttpSession httpSession;
	
    public ViewMail() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("in ViewMail get");
		
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
				
				String[] id = request.getParameterValues("id");
				if(id != null) {
					elements = new HashMap<Integer, Object>();
					
					//save elements into session
					httpSession.setAttribute("elements", elements);
					
					readEmail(request, response, Integer.parseInt(id[0]));
					
					//display the mail box jsp
					request.getRequestDispatcher("/viewmail.jsp")
						.forward(request, response);
					
				} 
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
	
	public void readEmail(HttpServletRequest request,
			HttpServletResponse response, int id) throws 
	StatusNoException, StatusBadException, IOException {
	
		response.setContentType("text/html;Charset=UTF-8");
		
		elements = new HashMap<Integer, Object>();
		
		//save elements into session
		httpSession.setAttribute("elements", elements);
	
		MimeMessage currentMessage = messages.get(id);
		
		MimeEntryList content = 
				currentMessage.getBody();
		
		//add the mail content to a request and forward it
		String emailBody = 
				getContentFromMimeList(request, response, content);
		request.setAttribute("emailBody", emailBody);
		
		String subject = currentMessage.getSubject();
		request.setAttribute("subject", subject);
		
		String date = currentMessage.getDate();
		request.setAttribute("date", date);
		
		String fromAdd = currentMessage.getFromEmail();
		request.setAttribute("fromAdd", fromAdd);
		
		String fromName = currentMessage.getFromName();
		request.setAttribute("fromName", fromName);
		
		String toAdd = currentMessage.getToEmail();
		request.setAttribute("toAdd", toAdd);
		
		String toName = currentMessage.getToName();
		request.setAttribute("toName", toName);
	}
	
	public String getContentFromMimeList(HttpServletRequest request,
			HttpServletResponse response, MimeEntryList content) 
					throws IOException {
		StringBuilder data = new StringBuilder("");
		String parsed;
		
		data.append("<!--" + content.getContentMajor() + "/" +
				content.getContentMinor() + "-->");
		
		//get the entries
		ArrayList<MimeEntry> entries;
		entries = content.getEntries();
		
		//keep track of image ids for inserting embedded images
		TreeMap<Integer, String> imageIds = new TreeMap<Integer, String>();
		
		//if message is a multipart alternative, remove the plain
		//text part
		if(content.getContentMajor().
				toLowerCase().equals("multipart") &&
					content.getContentMinor().
						toLowerCase().equals("alternative")) {

			int plainTextId = -1;
			
			for(int i = 0; i < entries.size(); i++) {
				MimeEntry entry = entries.get(i);
				if(entry.getContentMajor().equals("text"))
				{
					if(entry.getContentMinor().
							toLowerCase().equals("plain")) {
						plainTextId = i;
				}
			}
			
			//if plain text is found remove it
				if(plainTextId >= 0) {
					entries.remove(plainTextId);
				}
			}
		}
			
		//iterate through the entries
		for (int i = 0; i < entries.size(); i++) {
			System.out.println(entries.get(i).toString());
			if(entries.get(i).getClass() == MimeElement.class) {
				MimeElement currentElement = (MimeElement) entries.get(i);
				System.out.println(currentElement.toString());
				System.out.println(currentElement.getContentAsString());
				//add to list of elements
				elements.put(i, currentElement);
				
				data.append("<!--<a href='GetElement?id=" +
				i + "'>" + i + ": " + currentElement + "</a><br/><hr/>-->\n");
				if(currentElement.getContentMajor().toLowerCase().equals("text")
						|| currentElement.getContentMajor().toLowerCase().equals("unknown")) {

					//escape chars for HTML output
					parsed = HTMLUtils.escapeHTML
						(currentElement.getContentAsString());
					
					data.append("<div class='textElement'>" +
							parsed + "</div>");
				}
				else if(currentElement.getContentMajor().toLowerCase().equals("image")) {
					data.append("<a href='GetElement?id=" + i + "'>"
							+ "<div class='thumbnail'>"
							+ "<img src='GetElement?id=" + i + "'/></div></a>\n");
					
					//save image id
					imageIds.put(i, currentElement.getContentId());
				}
				data.append("\n");
			}
			else if(entries.get(i).getClass() == MimeEntryList.class) {
				System.out.println("servlet found nested multipart");
				MimeEntryList list = (MimeEntryList) entries.get(i);
				data.append(getContentFromMimeList(request,
						response, list));
			}
		}
		
		//convert to String and insert embedded images
		String dataString = data.toString();
		
		Set<Integer> keys = imageIds.keySet();
		for(int key : keys) {
			System.out.println("Replacing: " + "cid:" + imageIds.get(key) +
					" with: GetElement?id=" + key);
			dataString = dataString.replaceAll("cid:" + imageIds.get(key), 
					"GetElement?id=" + key);
		}
			
		//put content into the request so the JSP can display it
		request.setAttribute("emailBody", dataString);
		
		return dataString;
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
