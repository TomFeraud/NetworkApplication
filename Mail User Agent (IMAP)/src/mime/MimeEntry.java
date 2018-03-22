package mime;

/**
 * Defines information for a mime entry.
 * 
 * Both MimeElement and MimeEntryList extend this.
 * 
 * @author Andrew
 */
public abstract class MimeEntry {
	String boundary;
	String contentMajor = "unknown";
	String contentMinor = "unknown";
	String encoding = "";
	String charset = "";
	String fileName = "";
	String contentId = "";
	
	public synchronized String getContentId() {
		return contentId;
	}
	public synchronized void setContentId(String contentId) {
		this.contentId = contentId;
	}
	
	public synchronized String getEncoding() {
		return encoding;
	}
	public synchronized void setEncoding(String encoding) {
		this.encoding = encoding.trim();
	}
	public synchronized String getCharset() {
		return charset;
	}
	public synchronized void setCharset(String charset) {
		this.charset = charset.trim();
	}	
	
	public synchronized String toString() {
		return "type: " + contentMajor + " / " + contentMinor + 
				", Content-Transfer-Encoding: " 
					+ encoding + ", charset: " + charset
					+ ", file name: " + fileName + 
					", contentId: " + this.getContentId(); 
	}
	
	public synchronized String getBoundary() {
		return boundary;
	}
	public synchronized void setBoundary(String boundary) {
		this.boundary = boundary;
	}

	public synchronized String getFileName() {
		return fileName;
	}
	public synchronized void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public synchronized String getContentMajor() {
		return contentMajor;
	}
	public synchronized void setContentMajor(String contentMajor) {
		this.contentMajor = contentMajor.trim();
	}
	public synchronized String getContentMinor() {
		return contentMinor;
	}
	public synchronized void setContentMinor(String contentMinor) {
		this.contentMinor = contentMinor.trim();
	}
}
