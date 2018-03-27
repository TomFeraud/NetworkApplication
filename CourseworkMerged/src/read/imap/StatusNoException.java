package read.imap;

/**
 * Thrown upon "NO" response. Further information
 * is contained in the message. 
 */
public class StatusNoException extends Exception {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs a new StatusNoException
	 * @param str the error String
	 */
	public StatusNoException(String str) {
		super(str);
	}
}
