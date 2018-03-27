package utility;

public class HTMLUtils {
	/**
	 * Escapes special characters for output in HTML
	 * 
	 * @cite https://www.freeformatter.com/html-entities.html
	 * 
	 * @param str
	 * @return
	 */
	public static String escapeHTML(String str) {
		//use HTML escape character for £ signs
		str = str.replaceAll("£", "&pound");
		
		//accents
		str = str.replaceAll("À", "&Agrave");
		str = str.replaceAll("Á", "&Aacute");
		str = str.replaceAll("È", "&Egrave");
		str = str.replaceAll("É", "&Eacute");
		str = str.replaceAll("Ò", "&Ograve");
		str = str.replaceAll("Ó", "&Oacute");
		str = str.replaceAll("Ù", "&Ugrave");
		str = str.replaceAll("Ú", "&Uacute");
		str = str.replaceAll("à", "&agrave");
		str = str.replaceAll("á", "&aacute");
		str = str.replaceAll("è", "&egrave");
		str = str.replaceAll("é", "&eacute");
		str = str.replaceAll("ò", "&ograve");
		str = str.replaceAll("ó", "&oacute");
		str = str.replaceAll("ù", "&ugrave");
		str = str.replaceAll("ú", "&uacute");
		str = str.replaceAll("ó", "&oacute");
		str = str.replaceAll("ù", "&ugrave");
		str = str.replaceAll("ú", "&uacute");

		//umlauts
		str = str.replaceAll("Ä", "&Auml");
		str = str.replaceAll("Ü", "Uuml");
		str = str.replaceAll("Ö", "&Ouml;");
		str = str.replaceAll("ä", "&auml");
		str = str.replaceAll("ü", "&uuml");
		str = str.replaceAll("ö", "&ouml;");	
		
		//eszett
		str = str.replaceAll("ß", "&szlig;");
		
		//quotes
		str = str.replaceAll("“", "&#8220;");
		str = str.replaceAll("”", "&#8221;");
		str = str.replaceAll("‘", "&lsquo;");
		str = str.replaceAll("’", "&#8217;");
		
		//other
		str = str.replaceAll("·", "&#149;");
		return str;
	}
}
