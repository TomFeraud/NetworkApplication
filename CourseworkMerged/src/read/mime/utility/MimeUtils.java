package read.mime.utility;

public class MimeUtils {
	/**
	 * Extract the boundary marker from a header
	 * @param currentHeader the header to process
	 * @return
	 */
	public static String parseBoundary(String currentHeader) {
		//get everything after the = sign
		String boundary = currentHeader.substring
				(currentHeader.indexOf("\""), currentHeader.length());
		
		//remove any quotation marks
		boundary = utility.StringUtils.stripSpeechMarks(boundary);
		
		return boundary;
	}

}
