package read.mime;

import java.io.IOException;

import read.imap.Imap4Session;
import read.imap.StatusBadException;
import read.imap.StatusNoException;

/**
 * Represents a MIME encoded email message 
 */
public class MimeMessage {
	/**
	 * Create a new MimeMessage
	 * @param id 
	 * @param parent
	 */
	public MimeMessage(int id, Imap4Session parent) {
		this.id = id;
		this.parent = parent;
	}

	/**
	 * Returns the Message subject
	 * @return the Message subject
	 */
	public synchronized String getSubject() {
		return subject;
	}
	
	/**
	 * Sets the message subject
	 * @param the message subject
	 */
	public synchronized void setSubject(String subject) {
		this.subject = subject;
	}
	
	/**
	 * Returns the name of the sender
	 * @return the name of the sender
	 */
	public synchronized String getFromName() {
		return fromName;
	}
	
	/**
	 * Sets the name of the sender
	 * @param fromName then name of the sender
	 */
	public synchronized void setFromName(String fromName) {
		this.fromName = fromName;
	}
	
	/**
	 * returns the email address of the sender
	 * @return the email address of the sender
	 */
	public synchronized String getFromEmail() {
		return fromEmail;
	}
	
	/**
	 * Sets the email address of the sender
	 * @param fromEmail the email address of the sender
	 */
	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}
	
	/**
	 * Returns the date on which the email was received/created
	 * @return the date on which the email was received/created
	 */
	public synchronized String getDate() {
		return date;
	}
	
	/**
	 * Sets the date on which the email was received/created
	 * @param date the date on which the email was received/created
	 */
	public synchronized void setDate(String date) {
		this.date = date;
	}
	
	/**
	 * Returns the email's ID number
	 * @return the email's ID number
	 */
	public synchronized int getId() {
		return id;
	}
	
	/**
	 * Sets the email's ID number
	 * @param id the email's ID number
	 */
	public synchronized void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Returns the body of the email mess as a list of
	 * components derived from the MIME elements.
	 * 
	 * Lists can be nested, reflecting multipart messages
	 * nested in multipart messages.
	 * 
	 * The body is retrieved via IMAP if it has not already
	 * been downloaded, otherwise it is stored and returned.
	 * 
	 * @return MimeEntryList containing body sections
	 * @throws StatusBadException 
	 * @throws StatusNoException 
	 * @throws IOException 
	 * @throws ConnectionLostException 
	 * @throws Exception
	 */
	public synchronized MimeEntryList getBody()
			throws StatusNoException, StatusBadException, IOException {
		System.out.println("Message: " + id);
		if(body == null) {
			System.out.println("Reading: " + id);
			parent.fetchMessageBody(this);
		}
		
		return body;
	}
	
	/**
	 * Sets the content to an arbitrary list of MIME parts
	 * @param body an arbitrary list of MIME parts
	 */
	public synchronized void setBody(MimeEntryList body) {
		this.body = body;
	}
	
	
	/**
	 * Returns the major content type
	 * @return the major content type
	 */
	public synchronized String getContentMajor() {
		return contentMajor;
	}

	/**
	 * Sets the major content type
	 * @param contentMajor the major content type
	 */
	public synchronized void setContentMajor(String contentMajor) {
		this.contentMajor = contentMajor;
	}

	/**
	 * Returns the minor content type
	 * @return the minor content type
	 */
	public synchronized String getContentMinor() {
		return contentMinor;
	}

	/**
	 * Sets the minor content type
	 * @param contentMinor the minor content type
	 */
	public synchronized void setContentMinor(String contentMinor) {
		this.contentMinor = contentMinor;
	}

	/**
	 * Returns message details as a String
	 * @return the message details as a String
	 */
	public synchronized String toString() {
		StringBuilder str = new StringBuilder("ID: " + id);
		if(fromEmail != null)
			str.append(" From: " + fromName + " " + fromEmail);
		if(toEmail != null)
			str.append(" To: " + toName + " " + toEmail);
		str.append(" Date: " + date
				+ " Subject: " + subject + " Content: " + 
				contentMajor + " " + contentMinor);
		
		return(str.toString());
	}
	
	/**
	 * Returns the recipient name
	 * @return the recipient name
	 */
	public synchronized String getToName() {
		return toName;
	}

	/**
	 * Sets the recipient name
	 * @param toName the recipient name
	 */
	public synchronized void setToName(String toName) {
		this.toName = toName;
	}

	/**
	 * Returns the recipient email address
	 * @return the recipient email address
	 */
	public synchronized String getToEmail() {
		return toEmail;
	}

	/**
	 * Sets the recipient email address
	 * @param toEmail the recipient email address
	 */
	public synchronized void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	//Message attributes
	String subject; //the subject
	String fromName; //the sender name
	String fromEmail; //the sender email address
	String toName; //the recipient name
	String toEmail; //the recipient email address
	String date; //the send/creation date
	MimeEntryList body = null; //the content as a list of MIME parts
	String contentMajor; //the major content type
	String contentMinor; //the minor content type
	Imap4Session parent; //the session which created this MimeMessage
	int id; //the message ID
}
