package send;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import utility.HTMLUtils;

//@MultipartConfig
public class MailForm {
	private static final String FIELD_FROM = "emailFrom";
	private static final String FIELD_TO = "emailTo";
	private static final String FIELD_SUBJECT = "subject";
	private static final String FIELD_MESSAGE = "message";
	private static final String FIELD_FILE = "file";

	private String resultat;
	private Map<String, String> erreurs = new HashMap<String, String>();

	public String getResultat() {
		return resultat;
	}

	public Map<String, String> getErreurs() {
		return erreurs;
	}

	/**
	 * Set up the content of our mail by equests parameters (e.g. from, subject) to
	 * our servlet.
	 * 
	 * @param request
	 * @return mailContent
	 * @throws IOException
	 * @throws ServletException
	 */
	public MailContent setupMail(HttpServletRequest request) throws IOException, ServletException {
		String emailFrom = getFieldValue(request, FIELD_FROM);
		String emailTo = getFieldValue(request, FIELD_TO);
		String subject = getFieldValue(request, FIELD_SUBJECT);
		String message = getFieldValue(request, FIELD_MESSAGE);

		System.out.println("FROM:" + emailFrom);

		Part part = request.getPart(FIELD_FILE);
		String file = "";
		System.out.println("Part content type: " + part.getContentType()); // image/jpeg
		System.out.println("Part name:" + part.getName()); // file
		System.out.println("Part size : " + part.getSize()); // 5258 (OK)
		System.out.println("Part get submitted file name: " + part.getSubmittedFileName()); // picture.jpg

		if (part.getSize() != 0) {
			InputStream inputS = part.getInputStream();
			byte[] imageBytes = new byte[(int) part.getSize()];
			inputS.read(imageBytes, 0, imageBytes.length);
			inputS.close();

			java.util.Base64.Encoder enc = java.util.Base64.getEncoder();
			byte[] b = enc.encode(imageBytes);
			file = new String(b);
			file = HTMLUtils.escapeHTML(file);
			// System.out.println(file);
		}

		MailContent mailContent = new MailContent();

		try {
			validationEmail(emailFrom);
		} catch (Exception e) {
			setErreur(FIELD_FROM, e.getMessage());
		}
		mailContent.setEmailFrom(emailFrom);

		try {
			validationEmail(emailTo);
		} catch (Exception e) {
			setErreur(FIELD_TO, e.getMessage());
		}
		mailContent.setEmailTo(emailTo);

		try {
			validationSubject(subject);
		} catch (Exception e) {
			setErreur(FIELD_SUBJECT, e.getMessage());
		}
		mailContent.setSubject(subject);

		if (message == null) {
			mailContent.setMessage(""); // Otherwise send "null"
		} else {
			mailContent.setMessage(message);
		}
		try {
			validationFile(file);
		} catch (Exception e) {
			setErreur(FIELD_FILE, e.getMessage());
		}
		mailContent.setFile(file);
		mailContent.setFileContentType(part.getContentType());
		mailContent.setFileName(part.getSubmittedFileName());

		if (erreurs.isEmpty()) {
			resultat = "Success";
		} else {
			resultat = "Failed";
		}

		return mailContent;
	}

	/**
	 * Validates the format of the e-mail typed.
	 * 
	 * @param email
	 * @throws Exception
	 */
	private void validationEmail(String email) throws Exception {
		if (email != null) {
			if (!email.matches("([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)")) {
				throw new Exception("Please enter a correct address.");
			}
		} else {
			throw new Exception("Please enter an address.");
		}
	}

	/**
	 * Validates that a subject has been informed.
	 * 
	 * @param subject
	 * @throws Exception
	 */
	private void validationSubject(String subject) throws Exception {
		if (subject == null || subject.equalsIgnoreCase("")) {
			throw new Exception("Please add a subject.");
		}
	}

	@SuppressWarnings("unused") // Could be useful
	private void validationMessage(String message) throws Exception {
		if (message.length() <= 1) {
			throw new Exception("Please enter a message > 1 char");
		}
	}

	/**
	 * Checks if the file is not null.
	 * 
	 * @param file
	 * @throws Exception
	 */
	private void validationFile(String file) throws Exception {
		if (file == null) {
			throw new Exception("Error linked to the file.");
		}
	}

	/**
	 * Add an error message corresponding to the field
	 * 
	 * @param field
	 * @param message
	 */
	private void setErreur(String field, String message) {
		erreurs.put(field, message);
	}

	/**
	 *  Return null if a field is empty, its content otherwise
	 *  
	 * @param request
	 * @param nomChamp
	 * @return the value of the field requested
	 */
	private static String getFieldValue(HttpServletRequest request, String nomChamp) {
		String valeur = request.getParameter(nomChamp);
		System.out.println("Value: " + valeur);
		if (valeur == null || valeur.trim().length() == 0) {
			return null;
		} else {
			return valeur.trim();
		}
	}
}
