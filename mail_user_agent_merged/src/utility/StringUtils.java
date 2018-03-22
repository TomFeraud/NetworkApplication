package utility;

public class StringUtils {
	/***
	 * Removes leading and trailing speech marks
	 * @param str string whose speech marks to remove
	 * @return string with leading and trailing speech marks removed
	 */
	public static String stripSpeechMarks(String str) {
		str = str.trim();
		
		if(str.substring(0, 1).equals("\""))
			str = str.substring(1, str.length());
		
		if(str.substring(str.length()-1, str.length()).equals("\"")) {
			str = str.substring(0, str.length() -1);
		}
		return str;
	}
	
	/***
	 * Removes leading and trailing angle brackets <> from email 
	 * addresses
	 * @param str string whose angle brackets to remove
	 * @return string with leading and trailing angle brackets removed
	 */
	public static String stripBrackets(String str) {
		str = str.trim();
		
		if(str.substring(0, 1).equals("<"))
			str = str.substring(1, str.length());
		
		if(str.substring(str.length()-1, str.length()).equals(">"))
			str = str.substring(0, str.length() -1);
		
		return str;
	}
}
