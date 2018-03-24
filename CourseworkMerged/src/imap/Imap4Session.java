package imap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import mime.MimeEntryList;
import mime.MimeMessage;
import utility.HeaderUtils;
import utility.StringUtils;

public class Imap4Session {
	String url;
	int port;
	String username;
	String password;
	SSLSocket sock;
	BufferedReader read;
	InputStream in;
	OutputStream write;
	int sessionMessageNo = 0; //message id to track conversation thread
	String sessionMessageCode = "mssgNo~#_=_";
	
	//list of available mail boxes
	ArrayList<String> mailBoxes;
	
	//current mailbox data
	int messageCount = 0;
	HashMap<Integer, MimeMessage> messageSummaries;
	
		public Imap4Session(String url) {
		this(url, 993);
	}
	
	public Imap4Session(String url, int port) {
		this.url = url;
		this.port = port;
	}
	
	/***
	 * Returns the number of messages in the selected mailbox
	 * @return the number of messages in the selected mailbox
	 */
	public synchronized int getMessageCount() {
		return messageCount;
	}
	
	public synchronized void connect() throws 
			StatusBadException, StatusNoException,
			IOException {
		
		
		//http://www.macs.hw.ac.uk/cs/online/na/5/8.xhtml
		SSLSocketFactory f = (SSLSocketFactory) 
				SSLSocketFactory.getDefault();
		
		System.out.println("Creating socket for: " + url + ":" + port);
		
		sock = (SSLSocket) f.createSocket(url, port);
		in = sock.getInputStream();
		read = new BufferedReader(
				new InputStreamReader(in));
		
		write = sock.getOutputStream();
		
		String inLine = read.readLine();
		
		String status = inLine.split(" ")[1];
		
		switch(status) {
			case "OK":
				return;
			case "BAD":
				throw new StatusBadException("");
			case "NO":
				throw new StatusNoException("");
		}
	}

	public synchronized void login(String username, String password) 
			throws StatusBadException, StatusNoException, 
			IOException {
		String lineOut = sessionMessageCode + sessionMessageNo + 
				" LOGIN " + username + " " + password + "\r\n";
		sessionMessageNo++;
		
		this.username = username;
		this.password = password;

		write.write(lineOut.getBytes());
		
		String inLine = read.readLine();
		
		//throw errors if needed, or just return
		processStatusCode(inLine);
	}
	
	/**
	 * Extracts a status code from a response. Returns if OK, 
	 * throws StatusNoException on NO and StatusBadException on BAD.
	 * 
	 * If response supplies error information, this is added to the
	 * thrown object.
	 * 
	 * @param inLine the response to parse
	 * @throws StatusNoException thrown on NO response
	 * @throws StatusBadException thrown on BAD response
	 */
	public synchronized void processStatusCode(String inLine) throws 
	StatusNoException, StatusBadException {
		String status = inLine.split(" ")[1];
		
		switch(status) {
		case "OK":
			return;
		case "BAD":
			//if there's more info, add it to the exception
			{
				String[] parts = inLine.split("BAD ");
				String errorInfo = "";
				//if parts is longer than one, there's a message after code
				if(parts.length > 1)
					errorInfo = parts[1];
				System.out.println("throwing StatusBadException " + errorInfo);
				throw new StatusBadException(errorInfo);
			}
		case "NO":
			//if there's more info, add it to the exception
			{
				String[] parts = inLine.split("NO ");
				String errorInfo = "";
				//if parts is longer than one, there's a message after code
				if(parts.length > 1)
					errorInfo = parts[1];
				System.out.println("throwing StatusNoException " + errorInfo);
				throw new StatusNoException(errorInfo);
			}
		}
	}
	
	/**
	 * Updates list of mailboxes from server
	 * @throws StatusNoException
	 * @throws StatusBadException
	 * @throws IOException 
	 * @throws ConnectionLostException 
	 */
	public synchronized void updateMailBoxes() throws
			StatusNoException, StatusBadException, IOException {
		String lineOut = sessionMessageCode + sessionMessageNo + " LIST \"\" \"*\"\r\n";
		sessionMessageNo++;
		mailBoxes = new ArrayList<String>();
		
		write.write(lineOut.getBytes());
		
		String inLine = read.readLine();
		
		//loop until status whose ID matches this initiating
		//sessionMessageNo is returned
		while(!checkIfReturned(inLine)) {
			
			//actual folder name is demarcated by "/"
			int place = inLine.indexOf("\"/\" ");
			//get folder name
			String folderName = inLine.substring(place + 4, inLine.length());
			//remove leading and trailing speech marks
			folderName = StringUtils.stripSpeechMarks(folderName);
			
			mailBoxes.add(folderName);
			inLine = read.readLine();
		}

		//throw errors if needed, or just return
		processStatusCode(inLine);
	}
	
	/**
	 * Returns a list of all mailboxes. If mailboxes have not been
	 * read from server, this method invokes the updateMailBoxes()
	 * to populate the list. Otherwise it returns them from memory.
	 * 
	 * @return ArrayList of all mailboxes.
	 * @throws StatusBadException 
	 * @throws StatusNoException 
	 * @throws IOException 
	 * @throws ConnectionLostException 
	 */
	public synchronized ArrayList<String> getMailBoxes()
			throws StatusNoException, StatusBadException, IOException 
			{
		//if mailBoxes haven't been read from server, read them
		if(mailBoxes == null)
			updateMailBoxes();
		//return the list of mailboxes
		return mailBoxes;
	}
	
	public synchronized void selectMailBox(String mailBox) 
			throws StatusNoException, StatusBadException, IOException {
		String lineOut = sessionMessageCode + sessionMessageNo + " SELECT \"" + mailBox + "\"\r\n";
		sessionMessageNo++;
		
		write.write(lineOut.getBytes());
		
		String inLine;
		inLine = read.readLine(); 

		while(!checkIfReturned(inLine)) {
			
			//if line states number of existing messages, extract data
			if(inLine.split(" ")[2].toLowerCase().equals("exists"))
			{
				messageCount = Integer.parseInt(inLine.split(" ")[1]);
			}
			
			inLine = read.readLine(); 
		}
		
		//throw errors if needed, or just return
		processStatusCode(inLine);
	}
	
	public synchronized void fetchMessageSummaries(int start, int end)
			throws StatusNoException, StatusBadException, IOException {
		String lineOut = sessionMessageCode + sessionMessageNo + " FETCH " + start
				+ ":" + end + " (body[header.fields (subject date from to content-type flags)])\r\n";
		
		sessionMessageNo++;
		messageSummaries = new HashMap<Integer, MimeMessage>();
		
		write.write(lineOut.getBytes());
		
		String inLine = read.readLine();
		String[] parts;
		
		int currentId;
		MimeMessage currentMessage = null;

		while(!checkIfReturned(inLine)) {
			parts = inLine.split(" ");
			System.out.println("getting summaries: " + inLine);
			
			//if line is introducing a mail item
			if(parts.length > 3 && parts[2].toLowerCase().equals("fetch")) {
				currentId = Integer.parseInt(parts[1]);
				currentMessage = new MimeMessage(currentId, this);

				messageSummaries.put(currentId, currentMessage);
			}
			
			//parse line of fetch data and put data into
			//the current message if it exists
			HeaderUtils.parseFetchedSummaryHeaders(currentMessage, inLine);
			inLine = read.readLine();
		}
		
		
		//throw errors if needed, or just return
		processStatusCode(inLine);
	}

	/**
	 * Fetches body data from server and populates the given message
	 * 
	 * @note could bits the bits of MIME parsing this class be moved into
	 * the MimeEntryList?
	 * @param message the message whose data to fetch and populate
	 * @return success or failure
	 * @throws StatusBadException 
	 * @throws StatusNoException 
	 * @throws IOException 
	 * @throws ConnectionLostException 
	 * @throws Exception
	 */
	public synchronized void fetchMessageBody(MimeMessage message) 
			throws StatusNoException, StatusBadException, IOException {
		String lineOut = sessionMessageCode + sessionMessageNo + " FETCH " + message.getId()
				+ " (body[])\r\n";
		
		sessionMessageNo++;

		write.write(lineOut.getBytes());
		
		String inLine = read.readLine();
		//list will be parsed in order so used linked list
		ArrayList<String> content = new ArrayList<String>();
		

		while(!checkIfReturned(inLine)) {
			inLine = inLine.trim();
			System.out.println("> " + inLine);

			//ending boundary should be added, but not flags
			//or numbered return message
			if(!inLine.toLowerCase().startsWith("flags") &&
					!checkIfReturned(inLine))
				content.add(inLine + "\r");
			inLine = read.readLine();
		} 
		
		MimeEntryList mimeContent = 
				new MimeEntryList(content);
		
		//copy fetched and parsed content into the calling message
		message.setBody(mimeContent);
			
		//throw errors if needed, or just return
		processStatusCode(inLine);
	}
	
	public synchronized void addFlag(int id, String flag) throws StatusNoException, 
		StatusBadException, IOException {
		String lineOut = sessionMessageCode + sessionMessageNo + " STORE " + id
				+ " +FLAGS (\\" + flag + ")\r\n";
		
		System.out.println("Sending message: " + lineOut);
		
		write.write(lineOut.getBytes());
		
		String inLine;
		inLine = read.readLine(); 
		
		sessionMessageNo++;
		

		while(!checkIfReturned(inLine)) {
			
			System.out.println("Deleting: " + inLine);
			inLine = read.readLine(); 
		}
		
		//throw errors if needed, or just return
		processStatusCode(inLine);
	}
	
	public synchronized void logout() throws IOException, 
	StatusNoException, StatusBadException {
		String lineOut = sessionMessageCode + sessionMessageNo + " LOGOUT\r\n";
		
		System.out.println("Sending message: " + lineOut);
		
		write.write(lineOut.getBytes());
		
		String inLine;
		inLine = read.readLine(); 
		
		sessionMessageNo++;
		
	
		while(!checkIfReturned(inLine)) {
			
			System.out.println("Logging out: " + inLine);
			inLine = read.readLine(); 
	}
	
	//throw errors if needed, or just return
	processStatusCode(inLine);
	
	//close the connection
	sock.close();
	
	System.out.println("Successfully logged out.");
	}
	
	public synchronized void expunge() throws IOException, 
		StatusNoException, StatusBadException {
		String lineOut = sessionMessageCode + sessionMessageNo + " EXPUNGE\r\n";
		
		System.out.println("Sending message: " + lineOut);
		
		write.write(lineOut.getBytes());
		
		String inLine;
		inLine = read.readLine(); 
		
		sessionMessageNo++;
		

		while(!checkIfReturned(inLine)) {
			
			System.out.println("Deleting: " + inLine);
			inLine = read.readLine(); 
		}
		
		//throw errors if needed, or just return
		processStatusCode(inLine);
	}
	
	/***
	 * Processes a line and returns whether it marks the end of the response
	 * @param str the line to process
	 * @return whether response has ended
	 */
	public synchronized boolean checkIfReturned(String str) {
		return str.startsWith(sessionMessageCode + (sessionMessageNo - 1) + " ");
	}
	
	public synchronized HashMap<Integer, MimeMessage> getMessageSummaries() {
		return messageSummaries;
	}
	
	public synchronized void close() throws IOException {
		sock.close();
	}
}
