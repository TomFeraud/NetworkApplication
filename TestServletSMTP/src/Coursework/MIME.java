package Coursework;

import java.io.File;
import java.io.FileInputStream;

public class MIME {
	private String version;
	private String type;
	private String boundary;
	private String contentTypeText;
	private String contentImage;
	private String contentEncoding;
	private String contentDisposition;
	
	//
	private String contentTypeFile;
	
	//for an image named picture.jpg
	public MIME(){
		this.version = "MIME-Version: 1.0";
		this.type = "Content-Type:multipart/mixed";
		this.boundary = "hjE170891tpbdzfd__FV_BLsdjjdsq";	
		this.contentTypeText = "Content-Type:text/plain; format=flowed; charset=US-ASCII";
		this.contentImage = "Content-Type:image/jpeg;name=\"picture.jpg";
		this.contentEncoding = "Content-Transfer-Encoding:base64";
		this.contentDisposition = "Content-Disposition:attachment;filename=\"picture.jpg";		
	}
	
	public MIME(String contentType, String fileName){
		this.version = "MIME-Version: 1.0";
		this.type = "Content-Type:multipart/mixed";
		this.boundary = "hjE170891tpbdzfd__FV_BLsdjjdsq";	
		this.contentTypeText = "Content-Type:text/plain; format=flowed; charset=US-ASCII";
		this.contentTypeFile = "Content-Type:"+contentType+";name=\""+fileName+"\"";
		this.contentEncoding = "Content-Transfer-Encoding:base64";
		this.contentDisposition = "Content-Disposition:attachment;filename=\""+fileName+"\"";	
	}
		
	
	public String encodeFile(String inputFile) throws Exception {		
		String stringEncoded = "";
		int len = (int) (new File(inputFile)).length();
		FileInputStream fis = new FileInputStream(inputFile);
		byte[] buf = new byte[len];
		int i = 0, n;
		do {
			n = fis.read(buf, i, len - i);
			if (n != -1)
				i += n;
		} while (i < len && n != -1);
		java.util.Base64.Encoder e = java.util.Base64.getEncoder();
		byte[] b = e.encode(buf);

		stringEncoded = new String(b);
		System.out.println(stringEncoded);
		fis.close();
		return stringEncoded;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBoundary() {
		return boundary;
	}

	public void setBoundary(String boundary) {
		this.boundary = boundary;
	}


	public String getContentTypeText() {
		return contentTypeText;
	}


	public void setContentTypeText(String contentTypeText) {
		this.contentTypeText = contentTypeText;
	}


	public String getContentImage() {
		return contentImage;
	}


	public void setContentImage(String contentImage) {
		this.contentImage = contentImage;
	}


	public String getContentEncoding() {
		return contentEncoding;
	}


	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}


	public String getContentDisposition() {
		return contentDisposition;
	}


	public void setContentDisposition(String contentDisposition) {
		this.contentDisposition = contentDisposition;
	}

	public String getContentTypeFile() {
		return contentTypeFile;
	}

	public void setContentTypeFile(String contentTypeFile) {
		this.contentTypeFile = contentTypeFile;
	}
	
	
	
}

