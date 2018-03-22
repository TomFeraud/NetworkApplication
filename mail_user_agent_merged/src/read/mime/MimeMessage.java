package read.mime;

import java.io.IOException;

import read.imap.Imap4Session;
import read.imap.StatusBadException;
import read.imap.StatusNoException;

public class MimeMessage {
	public MimeMessage(int id, Imap4Session parent) {
		this.id = id;
		this.parent = parent;
	}

	public synchronized String getSubject() {
		return subject;
	}
	public synchronized void setSubject(String subject) {
		this.subject = subject;
	}
	public synchronized String getFromName() {
		return fromName;
	}
	public synchronized void setFromName(String fromName) {
		this.fromName = fromName;
	}
	public synchronized String getFromEmail() {
		return fromEmail;
	}
	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}
	public synchronized String getDate() {
		return date;
	}
	public synchronized void setDate(String date) {
		this.date = date;
	}
	public synchronized int getId() {
		return id;
	}
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
	
	public synchronized void setBody(MimeEntryList body) {
		this.body = body;
	}
	
	public synchronized String getContentMajor() {
		return contentMajor;
	}

	public synchronized void setContentMajor(String contentMajor) {
		this.contentMajor = contentMajor;
	}

	public synchronized String getContentMinor() {
		return contentMinor;
	}

	public synchronized void setContentMinor(String contentMinor) {
		this.contentMinor = contentMinor;
	}

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
	
	public synchronized String getToName() {
		return toName;
	}

	public synchronized void setToName(String toName) {
		this.toName = toName;
	}

	public synchronized String getToEmail() {
		return toEmail;
	}

	public synchronized void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	String subject;
	String fromName;
	String fromEmail;
	String toName;
	String toEmail;
	String date;
	MimeEntryList body = null;
	String contentMajor;
	String contentMinor;
	Imap4Session parent;
	int id;
}
