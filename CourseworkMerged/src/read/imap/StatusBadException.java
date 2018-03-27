package read.imap;

/**
 * Thrown upon "BAD" response. Further information
 * is contained in the message. 
 */
public class StatusBadException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new StatusBadException
	 * @param str the error String
	 */
	public StatusBadException(String str) {
		super(str);
	}
}
