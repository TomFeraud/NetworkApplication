package read.mime;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;


/**
 * An implementation of MimeEntry for storing actual message content
 */
public class MimeElement extends MimeEntry {
	/**
	 * Add a line to the element content
	 * @param str the line to add
	 * @return
	 */
	public synchronized boolean addLine(String str) {
		return content.add(str);
	}
	
	/**
	 * Set the element content
	 * @param content the new content
	 */
	public synchronized void setContent(ArrayList<String> content) {
		this.content = content;
	}
	
	/**
	 * Returns the the raw element content
	 * @return the raw element content
	 */
	public synchronized ArrayList<String> getContent() {
		return content;
	}
	
	/**
	 * Returns the decoded element content
	 * @return the decoded element content
	 * 
	 * @https://www.javaworld.com/article
	 * /3240006/java-language/
	 * base64-encoding-and-decoding-in-java-8.html?page=2
	 */
	public synchronized String getContentAsString() {
		StringBuilder build = new StringBuilder();
		String str = "";
		
		//if base64 encoding has been used, decode it first
		if (encoding.toLowerCase().equals("base64")) {
			System.out.println("decoding base64");
			//convert the ArrayList into a String
			for (String line : content) {
				build.append(line);
			}
			
			//assign the content to str
			str = build.toString();
			
			//get the decoder
			Base64.Decoder decoder = Base64.getMimeDecoder();
			//decode the element content
			byte[] decoded = decoder.decode(str);
			try {
				//convert bytes to a String
				str = new String(decoded, "UTF-8");

			} catch (UnsupportedEncodingException e) {
				//show failures
				str = "<<Cannot decode Base64>>";
			}
			
		}
		else if(encoding.toLowerCase().equals("quoted-printable")) {
			System.out.println("decoding quoted printable");
			build = new StringBuilder();
			
			//use the apache code library to decode quoted-printable text
			//uses the default charset to convert from encoded type
			//instantiates a QuoutedPrintableCodec suitable for the chosen charset
			QuotedPrintableCodec q;
			if(charset.toLowerCase().equals("iso-8859-1"))
				q = new QuotedPrintableCodec(Charsets.ISO_8859_1);
			else if(charset.toLowerCase().equals("utf-8")) {
				q = new QuotedPrintableCodec(Charsets.UTF_8);
				System.out.println("UTF-8");
			}
			
			//if CharSet doesn't match anything, assume its Windows-1252
			else
				q = new QuotedPrintableCodec(Charsets.ISO_8859_1);
				//q = new QuotedPrintableCodec(Charsets.UTF_8);			
			
			
			for (String line : content) {
				build.append(line);
			}
			
			str = build.toString();
			
			try {
				//decode the string
				str = q.decode(str);
			} catch (DecoderException e) {
				e.printStackTrace();
			}
			
			} else {
				for (String line : content) {
					build.append(line);
			}
			
			str = build.toString();
		} 
		return str;
	}
	
	/**
	 * Return the element content as a byte array.
	 * If needed, the content is first decoded.
	 * @return the content as a decoded byte array
	 * 
	 * @https://www.javaworld.com/article
	 * /3240006/java-language/
	 * base64-encoding-and-decoding-in-java-8.html?page=2
	 */
	public synchronized byte[] getContentBytes() {
		StringBuilder build = new StringBuilder();
		String str = "";
		byte[] decoded = null;
		
		//if base64 encoding has been used, decode it first
		if (encoding.toLowerCase().equals("base64")) {
			for (String line : content) {
				build.append(line);
			}
			
			str = build.toString();
			
			Base64.Decoder decoder = Base64.getMimeDecoder();
			decoded = decoder.decode(str);
		}
		
		return decoded;
	}
	
	ArrayList<String> content = new ArrayList<String>();
}
