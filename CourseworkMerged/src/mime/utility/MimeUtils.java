package mime.utility;

public class MimeUtils {

	public static String parseBoundary(String currentHeader) {
		//get everything after the = sign
		String boundary = currentHeader.substring
				(currentHeader.indexOf("\""), currentHeader.length());
		
		//remove any quotation marks
		boundary = utility.StringUtils.stripSpeechMarks(boundary);
		
		return boundary;
	}

}
