package imap;

/**
 * Thrown upon "NO" response. Further information
 * is contained in the message. 
 * @author Andrew
 *
 */
public class StatusNoException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public StatusNoException(String str) {
		super(str);
	}
}
