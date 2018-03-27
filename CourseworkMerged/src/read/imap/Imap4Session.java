package read.imap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import read.mime.MimeEntryList;
import read.mime.MimeMessage;
import utility.HeaderUtils;
import utility.StringUtils;

/**
 * Represents the an IMAP4Session and its state
 */
public class Imap4Session {
	String url; //server URL
	int port;	//server port
	String username;	//credentials
	String password;	//credentials
	SSLSocket sock;		//represents the endpoint of an SSL connection
	BufferedReader read; 	//for reading input from server
	InputStream in;		//the receiving data from IMAP server
	OutputStream write;	//for sending data to IMAP server
	int sessionMessageNo = 0; //message id to track conversation thread
	String sessionMessageCode = "mssgNo~#_=_";	//ID to keep track of the dialogue
	
	//list of available mail boxes
	ArrayList<String> mailBoxes;
	
	//current mailbox data
	int messageCount = 0;
	
	//a list of messages
	HashMap<Integer, MimeMessage> messageSummaries;
	
	/**
	 * Constructs a new IMAP4Session with the default port
	 * @param url the address of the IMAP server
	 */
	public Imap4Session(String url) {
		this(url, 993);
	}
	
	/**
	 * Constructs a new IMAP4Session
	 * @param url the address of the IMAP server
	 * @param port the port of the IMAP server
	 */
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
	
	/**
	 * Connects to the server
	 * @throws StatusBadException
	 * @throws StatusNoException
	 * @throws IOException
	 */
	public synchronized void connect() throws 
			StatusBadException, StatusNoException,
			IOException {
		
		
		//http://www.macs.hw.ac.uk/cs/online/na/5/8.xhtml
		SSLSocketFactory f = (SSLSocketFactory) 
				SSLSocketFactory.getDefault();
		
		System.out.println("Creating socket for: " + url + ":" + port);
		
		//setup an SSL connection to the server
		sock = (SSLSocket) f.createSocket(url, port);
		
		//get InputStream and Reader
		in = sock.getInputStream();
		read = new BufferedReader(
				new InputStreamReader(in));
		
		//get OutputStream to send bytes to server
		write = sock.getOutputStream();
		
		//read the first line
		String inLine = read.readLine();
		
		//get the status from the line
		String status = inLine.split(" ")[1];
		
		//check status
		switch(status) {
			case "OK":
				return;
			//throw the relevant exceptions
			case "BAD":
				throw new StatusBadException("");
			case "NO":
				throw new StatusNoException("");
		}
	}

	/**
	 * Attempts a login with the supplied credentials
	 * @param username
	 * @param password
	 * @throws StatusBadException
	 * @throws StatusNoException
	 * @throws IOException
	 */
	public synchronized void login(String username, String password) 
			throws StatusBadException, StatusNoException, 
			IOException {
		//create message to attempt to login
		String lineOut = sessionMessageCode + sessionMessageNo + 
				" LOGIN " + username + " " + password + "\r\n";
		
		//increment session message no for the next message
		sessionMessageNo++;
		
		//store credentials in instance
		this.username = username;
		this.password = password;

		//send the login message
		write.write(lineOut.getBytes());
		
		//get the response
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
		//get the part of the String after the first space
		String status = inLine.split(" ")[1];
		
		//throw relevant exceptions if the status is not OK
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
		//create the LIST message
		String lineOut = sessionMessageCode + sessionMessageNo + " LIST \"\" \"*\"\r\n";
		sessionMessageNo++;
		
		//allocate a new ArrayList<String> for the list of mailboxes
		mailBoxes = new ArrayList<String>();
		
		//send the LIST message
		write.write(lineOut.getBytes());
		
		//read the first line
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
	
	/**
	 * Sets the current mailbox to the one provided
	 * @param mailBox the mailbox to select
	 * @throws StatusNoException
	 * @throws StatusBadException
	 * @throws IOException
	 */
	public synchronized void selectMailBox(String mailBox) 
			throws StatusNoException, StatusBadException, IOException {
		//create the SELECT message
		String lineOut = sessionMessageCode + sessionMessageNo + " SELECT \"" + mailBox + "\"\r\n";
		sessionMessageNo++;
		
		//send the SELECT message
		write.write(lineOut.getBytes());
		
		String inLine;
		//read the first line
		inLine = read.readLine(); 

		//loop until status whose ID matches this initiating
		//sessionMessageNo is returned
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
	
	/**
	 * Get the details for a range of emails in the current inbox
	 * @param start the first email in the range
	 * @param end the last email in the range
	 * @throws StatusNoException
	 * @throws StatusBadException
	 * @throws IOException
	 */
	public synchronized void fetchMessageSummaries(int start, int end)
			throws StatusNoException, StatusBadException, IOException {
		//construct FETCH request
		String lineOut = sessionMessageCode + sessionMessageNo + " FETCH " + start
				+ ":" + end + " (body[header.fields (subject date from to content-type flags)])\r\n";
		
		sessionMessageNo++;
		
		//allocate a new new HashMap<Integer, MimeMessage>() for the messages
		messageSummaries = new HashMap<Integer, MimeMessage>();
		
		//send the FETCH request
		write.write(lineOut.getBytes());
		
		//read the first line
		String inLine = read.readLine();
		String[] parts;
		
		int currentId;
		MimeMessage currentMessage = null;

		//loop until status whose ID matches this initiating
		//sessionMessageNo is returned
		while(!checkIfReturned(inLine)) {
			//split the line into parts demarcated by a space
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
		//create FETCH request for a whole message
		String lineOut = sessionMessageCode + sessionMessageNo + " FETCH " + message.getId()
				+ " (body[])\r\n";
		
		sessionMessageNo++;

		//send the request for the whole message
		write.write(lineOut.getBytes());
		
		String inLine = read.readLine();
		//list will be parsed in order so used linked list
		ArrayList<String> content = new ArrayList<String>();
		
		//loop until status whose ID matches this initiating
		//sessionMessageNo is returned
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
		
		//construct a MimeEntryList from the raw content
		MimeEntryList mimeContent = 
				new MimeEntryList(content);
		
		//copy fetched and parsed content into the calling message
		message.setBody(mimeContent);
			
		//throw errors if needed, or just return
		processStatusCode(inLine);
	}
	
	/**
	 * Set a flag on a message
	 * @param id the message to flag
	 * @param flag the flag to set
	 * @throws StatusNoException
	 * @throws StatusBadException
	 * @throws IOException
	 */
	public synchronized void addFlag(int id, String flag) throws StatusNoException, 
		StatusBadException, IOException {
		//construct a store request to set the given flag in the given message
		String lineOut = sessionMessageCode + sessionMessageNo + " STORE " + id
				+ " +FLAGS (\\" + flag + ")\r\n";
		
		System.out.println("Sending message: " + lineOut);
		
		//send the message
		write.write(lineOut.getBytes());
		
		String inLine;
		
		//read the first line
		inLine = read.readLine(); 
		
		sessionMessageNo++;
		
		//loop until status whose ID matches this initiating
		//sessionMessageNo is returned
		while(!checkIfReturned(inLine)) {
			
			System.out.println("Deleting: " + inLine);
			inLine = read.readLine(); 
		}
		
		//throw errors if needed, or just return
		processStatusCode(inLine);
	}
	
	/**
	 * Log the user out and end the session
	 * @throws IOException
	 * @throws StatusNoException
	 * @throws StatusBadException
	 */
	public synchronized void logout() throws IOException, 
	StatusNoException, StatusBadException {
		//construct LOGOUT request
		String lineOut = sessionMessageCode + sessionMessageNo + " LOGOUT\r\n";
	
		System.out.println("Sending message: " + lineOut);
		
		//send the LOGOUT request
		write.write(lineOut.getBytes());
		
		String inLine;
		inLine = read.readLine(); 
		
		//increment message counters
		sessionMessageNo++;
		
		//loop until status whose ID matches this initiating
		//sessionMessageNo is returned
		while(!checkIfReturned(inLine)) {
			
			System.out.println("Logging out: " + inLine);
			//read the next line
			inLine = read.readLine(); 
		}
		
		//throw errors if needed, or just return
		processStatusCode(inLine);
		
		//close the connection
		sock.close();
		
		System.out.println("Successfully logged out.");
	}
	
	/**
	 * Deletes all messages with the "Deleted" flag
	 * @throws IOException
	 * @throws StatusNoException
	 * @throws StatusBadException
	 */
	public synchronized void expunge() throws IOException, 
		StatusNoException, StatusBadException {
		//create the EXPUNGE request
		String lineOut = sessionMessageCode + sessionMessageNo + " EXPUNGE\r\n";
		
		System.out.println("Sending message: " + lineOut);
		
		//send the EXPUNGE request
		write.write(lineOut.getBytes());
		
		String inLine;
		//read the first line of reply
		inLine = read.readLine(); 
		
		sessionMessageNo++;
		
		//loop until status whose ID matches this initiating
		//sessionMessageNo is returned
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
	
	/**
	 * Return the message summaries fetched by fetchMessageSummaries()
	 * @return
	 */
	public synchronized HashMap<Integer, MimeMessage> getMessageSummaries() {
		return messageSummaries;
	}
	
	/**
	 * Close the SSL connection
	 * @throws IOException
	 */
	public synchronized void close() throws IOException {
		sock.close();
	}
}
