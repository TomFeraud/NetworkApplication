package Coursework;

public class Message {
	private String hostName = "";
	private String from = "";
	private String to = "";
	private String subject = "";
	private String message = "";

	private MIME mimeBody;

	public Message() {
		from = "";
		to = "";
		subject = "";
		message = "";
		mimeBody = new MIME();
	}

	public Message(String from, String to, String subject, String message) {
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.message = message;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getUserName() {
		String[] userName = from.split("\\@");
		return userName[0];
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public MIME getMimeBody() {
		return mimeBody;
	}

	public void setMimeBody(MIME mimeBody) {
		this.mimeBody = mimeBody;
	}

	public String getMessageMimeFormated() {
		String stringMimeBody = "";
		stringMimeBody += this.getMimeBody().getVersion() + "\n";
		stringMimeBody += this.getMimeBody().getType() + ";boundary=\"" + this.getMimeBody().getBoundary() + "\""
				+ "\n";
		stringMimeBody += "--" + this.getMimeBody().getBoundary() + "\n";
		stringMimeBody += this.getMimeBody().getContentTypeText() + "\n";
		stringMimeBody += "\n";
		stringMimeBody += this.getMessage() + "\n";
		stringMimeBody += "--" + this.getMimeBody().getBoundary() + "\n";
		stringMimeBody += this.getMimeBody().getContentImage() + "\n";
		stringMimeBody += this.getMimeBody().getContentEncoding() + "\n";
		stringMimeBody += this.getMimeBody().getContentDisposition() + "\n";
		stringMimeBody += "\n";
		try {
			stringMimeBody += this.getMimeBody().encodeFile("picture.jpg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Failed in trycatch picture");
		}
		stringMimeBody += "\n";
		stringMimeBody += "--" + this.getMimeBody().getBoundary() + "--\n.\n";

		return stringMimeBody;
	}

}
