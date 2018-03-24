package imap;

public class StatusBadException extends Exception {
	/**
	 * Thrown upon "BAD" response. Further information
	 * is contained in the message. 
	 */
	private static final long serialVersionUID = 1L;

	public StatusBadException(String str) {
		super(str);
	}
}
