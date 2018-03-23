package Coursework;

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
		//use HTML escape character for � signs
		str = str.replaceAll("�", "&pound");
		
		//accents
		str = str.replaceAll("�", "&Agrave");
		str = str.replaceAll("�", "&Aacute");
		str = str.replaceAll("�", "&Egrave");
		str = str.replaceAll("�", "&Eacute");
		str = str.replaceAll("�", "&Ograve");
		str = str.replaceAll("�", "&Oacute");
		str = str.replaceAll("�", "&Ugrave");
		str = str.replaceAll("�", "&Uacute");
		str = str.replaceAll("�", "&agrave");
		str = str.replaceAll("�", "&aacute");
		str = str.replaceAll("�", "&egrave");
		str = str.replaceAll("�", "&eacute");
		str = str.replaceAll("�", "&ograve");
		str = str.replaceAll("�", "&oacute");
		str = str.replaceAll("�", "&ugrave");
		str = str.replaceAll("�", "&uacute");
		str = str.replaceAll("�", "&oacute");
		str = str.replaceAll("�", "&ugrave");
		str = str.replaceAll("�", "&uacute");

		//umlauts
		str = str.replaceAll("�", "&Auml");
		str = str.replaceAll("�", "Uuml");
		str = str.replaceAll("�", "&Ouml;");
		str = str.replaceAll("�", "&auml");
		str = str.replaceAll("�", "&uuml");
		str = str.replaceAll("�", "&ouml;");	
		
		//eszett
		str = str.replaceAll("�", "&szlig;");
		
		//quotes
		str = str.replaceAll("�", "&#8220;");
		str = str.replaceAll("�", "&#8221;");
		str = str.replaceAll("�", "&lsquo;");
		str = str.replaceAll("�", "&#8217;");
		return str;
	}
}
