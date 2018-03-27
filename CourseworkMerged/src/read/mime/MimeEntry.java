package read.mime;

/**
 * Defines information for a mime entry.
 * 
 * Both MimeElement and MimeEntryList extend this.
 */
public abstract class MimeEntry {
	//Attributes of this entry
	String boundary; //boundary marker for multipart entries
	String contentMajor = "unknown"; //the major content type
	String contentMinor = "unknown"; //the minor content type
	String encoding = ""; //the encoding
	String charset = ""; // the charset
	String fileName = ""; //the filename for binary files
	String contentId = ""; //the ID of the entry
	
	/**
	 * Returns the ID of this entry
	 * @return the entry's ID
	 */
	public synchronized String getContentId() {
		return contentId;
	}
	
	/**
	 * Sets the entry's ID
	 * @param contentId
	 */
	public synchronized void setContentId(String contentId) {
		this.contentId = contentId;
	}
	
	/**
	 * Returns the encoding of the entry
	 * @return the encoding of the entry
	 */
	public synchronized String getEncoding() {
		return encoding;
	}
	
	/**
	 * Sets the encoding of the entry
	 * @param encoding
	 */
	public synchronized void setEncoding(String encoding) {
		this.encoding = encoding.trim();
	}
	
	/**
	 * Gets the charset of the entry
	 * @return the charset of the entry
	 */
	public synchronized String getCharset() {
		return charset;
	}
	
	/**
	 * Sets the charset of the entry
	 * @param charset the new charset
	 */
	public synchronized void setCharset(String charset) {
		this.charset = charset.trim();
	}	
	
	
	/**
	 * Returns the details of the entry as a String
	 * @return entry details as a String
	 */
	public synchronized String toString() {
		return "type: " + contentMajor + " / " + contentMinor + 
				", Content-Transfer-Encoding: " 
					+ encoding + ", charset: " + charset
					+ ", file name: " + fileName + 
					", contentId: " + this.getContentId(); 
	}
	
	/**
	 * Returns the boundary marker
	 * @return the boundary marker
	 */
	public synchronized String getBoundary() {
		return boundary;
	}
	
	/**
	 * Sets the boundary marker
	 * @param boundary the new boundary marker
	 */
	public synchronized void setBoundary(String boundary) {
		this.boundary = boundary;
	}

	/**
	 * Returns the filename if one is set
	 * @return the filename
	 */
	public synchronized String getFileName() {
		return fileName;
	}
	
	/**
	 * Sets the filename
	 * @param fileName
	 */
	public synchronized void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * The major type of the element
	 * @return the major type of the element
	 */
	public synchronized String getContentMajor() {
		return contentMajor;
	}
	
	/**
	 * Sets the major content type
	 * @param contentMajor the major content type as String
	 */
	public synchronized void setContentMajor(String contentMajor) {
		this.contentMajor = contentMajor.trim();
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
		this.contentMinor = contentMinor.trim();
	}
}
