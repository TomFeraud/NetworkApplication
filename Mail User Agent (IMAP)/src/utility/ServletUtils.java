package utility;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletUtils {
	public static void redirectToLogin(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			request.getSession().setAttribute("imapSession", null);
			System.out.println("Redirecting to login.");
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}
	
	public static void redirectToInbox(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			request.getRequestDispatcher("MailBox").forward(request, response);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}
}
