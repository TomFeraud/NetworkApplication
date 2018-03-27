package utility;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Base64.Decoder;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;

import read.mime.MimeEntry;
import read.mime.MimeMessage;

/**
 * Provides methods for common operations on mail headers
 */
public class HeaderUtils {
	/**
	 * Pulls out attributes pertaining to summary information from a header and puts 
	 * them in the destination MimeMessage
	 * @param destination the MimeMessage whose attributes to set
	 * @param source the header from which to extract the data
	 */
	public static void parseFetchedSummaryHeaders(MimeMessage destination, String source) {
		String currentHeader = source.trim();
		//some responses seem to have multiple headers on one line
		//separated by ;', so get the first part
		if(source.contains(";")) {
			currentHeader = currentHeader.substring(0, 
					source.indexOf(";"));
			currentHeader = currentHeader.trim();
		}
		
		String[] parts = currentHeader.split(" ");
		
		//if line has from details, add them to current message summary
		if(parts.length > 0 && parts[0].toLowerCase().equals("from:"))
		{
			parseFromField(currentHeader, destination);
		}
		
		//if line has to details, add them to current message summary
		if(parts.length > 0 && parts[0].toLowerCase().equals("to:"))
		{
			parseToField(currentHeader, destination);
		}
		
		
		//if line has date, add it to current message summary
		if(parts.length > 0 && parts[0].toLowerCase().equals("date:"))
		{
			destination.setDate(currentHeader.substring(6, 
					currentHeader.length()));
		}
		
		//if line has subject, add it to current message summary
		if(parts.length > 0 && parts[0].toLowerCase().equals("subject:"))
		{
			String subjectString;
			
			if(currentHeader.length() > "subject: ".length()) {
			subjectString = currentHeader.
					toLowerCase().substring("subject: ".length(), 
					currentHeader.length());
		
			subjectString = checkAndParseEncodedHeader(subjectString);
			} else {
				subjectString = "No Subject";
			}
			
			destination.setSubject(subjectString);
			
		}

		//if line has message type, add it to current message summary
		if(parts.length > 0 && parts[0].toLowerCase().equals("content-type:"))
		{
			String typeString = currentHeader.substring(14, 
					currentHeader.length());
			String[] types = typeString.split("/");
			
			destination.setContentMajor(types[0]);
			destination.setContentMinor(types[1]);
		}
				
		//some responses seem to have multiple headers on one line
		//separated by ;', the first one has been processed, so
		//recursively process the rest of the line if it exists
		if(source.contains(";")) {
			String nextHeaders = source.substring(source.indexOf(";")+1,
					source.length());
			nextHeaders = nextHeaders.trim();
			
			if(nextHeaders.length() > 1)
				parseFetchedSummaryHeaders(destination, nextHeaders);
		}
	}
	
	/***
	 * Extracts email address and name from an IMAP response
	 * and places the details into the give MessageSummary
	 * @param str the string to parse
	 * @param m the MessageSummary in which to place the data
	 */
	public static void parseFromField(String str, MimeMessage m) {
		String[] from = str.split(": ");
		String[] fromParts = from[1].split(" <");

		//if email has a from name and email
		if(fromParts.length > 1) {
			String fromName = fromParts[0];
			String fromEmail = fromParts[1];
			
			//remove <> from email, if they exist
			fromEmail = StringUtils.stripBrackets(fromEmail);
			
			//remove leading and trailing speech marks
			fromName = StringUtils.stripSpeechMarks(fromName);
			
			//if parts contain base64 or quoted-printable
			//encoding, convert them
			fromName = checkAndParseEncodedHeader(fromName);
			fromEmail = checkAndParseEncodedHeader(fromEmail);
			
			m.setFromName(fromName);
			m.setFromEmail
				(fromEmail.substring(0, fromEmail.length()));
		}
		//if email contains just an email
		else {
			String fromEmail = fromParts[0];
			
			//remove <> from email, if they exist
			fromEmail = StringUtils.stripBrackets(fromEmail);
			
			//if part contains base64 or quoted-printable
			//encoding, convert it
			fromEmail = checkAndParseEncodedHeader(fromEmail);
			
			m.setFromEmail
				(fromEmail.substring(0, fromEmail.length()));
		}	
	}
	
	/***
	 * Extracts to email address and name from an IMAP response
	 * and places the details into the give MessageSummary
	 * @param str the string to parse
	 * @param m the MessageSummary in which to place the data
	 */
	public static void parseToField(String str, MimeMessage m) {
		String[] to = str.split(": ");
		String[] toParts = to[1].split(" <");

		//if email has a from name and email
		if(toParts.length > 1) {
			String toName = toParts[0];
			String toEmail = toParts[1];
			
			//remove <> from email, if they exist
			toEmail = StringUtils.stripBrackets(toEmail);
			
			//remove leading and trailing speech marks
			toName = StringUtils.stripSpeechMarks(toName);
			
			//if parts contain base64 or quoted-printable
			//encoding, convert them
			toName = checkAndParseEncodedHeader(toName);
			toEmail = checkAndParseEncodedHeader(toEmail);
			
			m.setToName(toName);
			m.setToEmail
				(toEmail.substring(0, toEmail.length()));
		}
		//if email contains just an email
		else {
			String toEmail = toParts[0];
			
			//remove <> from email, if they exist
			toEmail = StringUtils.stripBrackets(toEmail);
			
			//if part contains base64 or quoted-printable
			//encoding, convert it
			toEmail = checkAndParseEncodedHeader(toEmail);
			
			m.setToEmail
				(toEmail.substring(0, toEmail.length()));
		}	
	}
	/**
	 * Checks if line contains BASE64 or Quoted Printable
	 * strings and converts it if found.
	 * @param str the string to parse
	 * @return the parsed string
	 * I used the following resources for reference:
	 * @cite https://docs.oracle.com/javase/7/docs/api/java/lang/StringBuilder.html
	 * @cite http://www.macs.hw.ac.uk/cs/online/na/4/7.xhtml
	 * @cite https://ncona.com/2011/06/using-utf-8-characters-on-an-e-mail-subject/
	 * @cite https://commons.apache.org/proper/commons-codec/apidocs/org/apache
	 * /commons/codec/net/QuotedPrintableCodec.html
	 */
	public static String checkAndParseEncodedHeader(String str) {
		//if line has UTF parts, parse them
		if(str.toLowerCase().contains("=?utf")) {
			//split constituent parts
			String[] parts = str.split("=\\?");

			//if there's any data before the beginning of UTF section
			for(int i = 0; i < parts.length; i++) {
				//chop of terminating "?="
				if(parts[i].length() >= 2 &&
						parts[i].substring(parts[i].length() -2, 
						parts[i].length()).equals("?=")) {
					parts[i] = parts[i].substring(0, parts[i].length() - 2);
				}

				//if part contains UTF-8 Base64 encoding, convert it
				if(parts[i].toLowerCase().contains("utf-8?b?")) {
					
					parts[i] = parts[i].substring(
							"utf-8?B?".length(), parts[i].length());
					Decoder decoder = Base64.getDecoder();
					byte[] bytes = decoder.decode(parts[i]);
					String result;
					try {
						result = new String(bytes, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						result = "<<Cannot decode Base64>>";
					}
					parts[i] = result;
				}
							
				//parse quoted printable UTF-8
				if (parts[i].toLowerCase().contains("utf-8?q?")) {

					
					parts[i] = parts[i].substring(
							"utf-8?q?".length(), parts[i].length());

					String result = "";
					//use the apache code library to decode quoted-printable text
					//uses the default charset, UTF-8
					QuotedPrintableCodec q = new QuotedPrintableCodec();
					
					try {
						result = q.decode(parts[i]);
					} catch (DecoderException e) {
						e.printStackTrace();
					}
					
					parts[i] = result;
				}
				
				//parse quoted printable Windows-1252
				if (parts[i].toLowerCase().contains("Windows-1252?Q?")) {
					parts[i] = parts[i].substring(
							"Windows-1252?Q?".length(), parts[i].length());
					
					String result = "";
					
					//use the apache code library to decode quoted-printable text
					//uses the default charset to convert from ISO_8859_1
					QuotedPrintableCodec q = new QuotedPrintableCodec(Charsets.ISO_8859_1);
					
					try {
						result = q.decode(parts[i]);
					} catch (DecoderException e) {
						result = "<<Cannot decode quoted printable>>";
					}
					
					parts[i] = result;
				}
			}
			
			//reconstruct parts
			StringBuilder reconstruct = new StringBuilder();
			for(String part : parts) {
				reconstruct.append(part);
			}
			
			//assign reconstructed string to str
			str = reconstruct.toString();
		}
		
		//return results
		return str;
	}

	/**
	 * Pulls out headers from pertaining to MIME part and puts them in
	 * the given MimeEntry
	 * @param destination the MimeEntry whose attributes to set
	 * @param source the header
	 */
	public static void parseFetchedContentHeaders(MimeEntry destination, 
			String source) {
		String currentHeader = source.trim();
		//some responses seem to have multiple headers on one line
		//separated by ;', so get the first part
		if(source.contains(";")) {
			currentHeader = currentHeader.substring(0, 
					source.indexOf(";"));
			currentHeader = currentHeader.trim();
		}
		
		String[] parts = currentHeader.split(" ");
		
		//if line has message type, add it to current message summary
		if(parts.length > 0 && parts[0].toLowerCase().equals("content-type:"))
		{
			String typeString = currentHeader.substring(14, 
					currentHeader.length());
			String[] types = typeString.split("/");
			
			destination.setContentMajor(types[0]);
			destination.setContentMinor(types[1]);
		}
		
		//single part messages may carry charset in main headers
		if(parts.length > 0 && parts[0].toLowerCase().startsWith("charset="))
		{
			String charset = currentHeader.substring("charset=".length(), 
					currentHeader.length());
			destination.setCharset(StringUtils.stripSpeechMarks(charset));
		}
		
		//single part messages may carry charset in main headers
		if(parts.length > 0 && currentHeader.startsWith("filename="))
		{
			String fileName = currentHeader.substring("filename=".length(), 
					currentHeader.length());
			destination.setFileName(StringUtils.stripSpeechMarks(fileName));
		}
		
		//single part messages may carry content transfer encoding
		if(parts.length > 0 && parts[0].toLowerCase().startsWith
				("content-transfer-encoding:"))
		{
			String encoding = 
					currentHeader.substring("content-transfer-encoding: ".length(), 
					currentHeader.length());
			destination.setEncoding(encoding);
		}
		
		
		//if line specifies the boundary marker code
		if(parts.length > 0 && parts[0].toLowerCase().contains("boundary=\""))
		{
			//get everything after the = sign
			String boundary = read.mime.utility.MimeUtils.parseBoundary(currentHeader);
			destination.setBoundary(boundary);
		}
		
		
		//if line specifies the boundary marker code
		if(parts.length > 0 && parts[0].toLowerCase().contains("content-id:"))
		{
			String contentId = currentHeader.substring("Content-ID: ".length(), 
					currentHeader.length());
			destination.setContentId(StringUtils.stripBrackets(contentId));
		}
		
		
		//some responses seem to have multiple headers on one line
		//separated by ;', the first one has been processed, so
		//recursively process the rest of the line if it exists
		if(source.contains(";")) {
			String nextHeaders = source.substring(source.indexOf(";")+1,
					source.length());
			nextHeaders = nextHeaders.trim();
			
			if(nextHeaders.length() > 1)
				parseFetchedContentHeaders(destination, nextHeaders);
		}
	}


}
