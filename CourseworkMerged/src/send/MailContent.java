package send;

public class MailContent {
	private String hostName = "";
	private String emailFrom = "";
	private String emailTo = "";
	private String subject = "";
	private String message = "";
	private MIME mimeFormat;
	private String file = "";
	private String fileName = "";
	private String fileContentType = "";

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}

	public String getEmailTo() {
		return emailTo;
	}

	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
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

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public MIME getMimeFormat() {
		return mimeFormat;
	}

	public void setMimeFormat(MIME mimeFormat) {
		this.mimeFormat = mimeFormat;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	/**
	 * Creates a MIME message using the message typed by the user and adding hearders
	 * and boundaries to it.
	 * 
	 * @return stringMimeBody
	 */
	public String getMessageMimeFormated() {
		String fileData = "";
		if (this.getFile() != "") {
			mimeFormat = new MIME(this.getFileContentType(), this.getFileName());
			fileData += "--" + this.getMimeFormat().getBoundary() + "\n";
			fileData += this.getMimeFormat().getContentTypeFile() + "\n";
			fileData += this.getMimeFormat().getContentEncoding() + "\n";
			fileData += this.getMimeFormat().getContentDisposition() + "\n";
			fileData += "\n";
			fileData += this.getFile();
		} else {
			mimeFormat = new MIME();
		}

		String stringMimeBody = "";
		mimeFormat.getVersion();
		stringMimeBody += mimeFormat.getVersion() + "\n";
		stringMimeBody += this.getMimeFormat().getType() + ";boundary=\"" + this.getMimeFormat().getBoundary() + "\""
				+ "\n";
		stringMimeBody += "--" + this.getMimeFormat().getBoundary() + "\n";
		stringMimeBody += this.getMimeFormat().getContentTypeText() + "\n";
		stringMimeBody += "\n";
		stringMimeBody += this.getMessage() + "\n";

		if (this.getFile() != "") {
			stringMimeBody += fileData + "\n";
		}

		stringMimeBody += "\n";
		stringMimeBody += "--" + this.getMimeFormat().getBoundary() + "--\n.\n";

		return stringMimeBody;
	}

}
