package read.mime;

import java.util.ArrayList;

import utility.HeaderUtils;

public class MimeEntryList extends MimeEntry {
	ArrayList<MimeEntry> entries;

	//the level of nesting, with 0 being root
	int level;
	
	public MimeEntryList(ArrayList<String> body, int level) {
		this.level = level;
		parseMultiPart(body);
	}
	
	public MimeEntryList(ArrayList<String> body) {
		this(body, 0);
	}

	public synchronized int getLevel() {
		return level;
	}
	
	public synchronized void parseMultiPart(ArrayList<String> body) {
		entries = new ArrayList<MimeEntry>();
		MimeElement currentEntry = null;
		boolean innerHeadersRead = false;
		boolean outerHeadersRead = false;
		int iteration = 0;
		
		//headers need to be joined back with body if the
		//part is recursively parsed
		ArrayList<String> currentHeaders = new ArrayList<String>();
		
		for(String line : body) {
			//System.out.print("Parsing @" + level + ": " + line);
			
			//parse outer headers of multipart message			
			if(!outerHeadersRead) {
				//add headers to list
				currentHeaders.add(line);
				
				//a blank line after the headers signifies end of
				//headers
				if(line.length() == 1 || line.length() == 0) {
					outerHeadersRead = true;
					System.out.println("outer headers read");
					
					//if this message is not a multipart, the outer headers
					//are effectively the inner headers, so create a new
					//mimeElement and copy the data to it
					if(!this.getContentMajor().equals("multipart")) {
						//create entry and add to list
						currentHeaders = new ArrayList<String>();
						currentEntry = new MimeElement();
						entries.add(currentEntry);
												
						//copy data
						//add code for copying data
						currentEntry.setEncoding(encoding);
						currentEntry.setBoundary(boundary);
						currentEntry.setCharset(charset);
						currentEntry.setFileName(fileName);
						currentEntry.setContentMajor(contentMajor);
						currentEntry.setContentMinor(contentMinor);
						
						System.out.println("inner headers read");
						innerHeadersRead = true;
					}
				}
				
				//System.out.println("outer header");
				HeaderUtils.parseFetchedContentHeaders(this, line);
			}
			
			//if the part is ended, break
			if(line.contains("--" + boundary + "--") && boundary != null) {
				//test if last element contains nested elements and parse
				//it recursively if it does
				if(currentEntry != null && 
						currentEntry.getContentMajor().equals(("multipart"))) {
					System.out.println("End of multipart message. "
							+ "Recursively parsing.");
					
					//headers need to be read for recursive parsing
					currentEntry.getContent().addAll(0, currentHeaders);
					
					//convert entry into entry list
					MimeEntryList currentList =
							new MimeEntryList(currentEntry.getContent());
					
					//remove the Entry from the list and replace it
					//with a nested list
					entries.remove(currentEntry);
					entries.add(currentList);
				}
				
				//System.out.println("BREAK");
				break;
			}
			
			//new mime entry is starting
			if(line.contains("--" + boundary) && boundary != null) {
				//if a previous element has been processed,
				//test if it's a nested multipart message
				if(currentEntry != null && 
						currentEntry.getContentMajor().equals(("multipart"))) {
					System.out.println("End of multipart message. "
							+ "Recursively parsing.");
					
					//headers need to be readded for recursive parsing
					currentEntry.getContent().addAll(0, currentHeaders);
					
					//convert entry into entry list
					MimeEntryList currentList =
							new MimeEntryList(currentEntry.getContent());
					
					//remove the Entry from the list and replace it
					//with a nested list
					entries.remove(currentEntry);
					entries.add(currentList);
				}
				
				System.out.println("Adding entry");
				innerHeadersRead = false;
				currentHeaders = new ArrayList<String>();
				currentEntry = new MimeElement();
				entries.add(currentEntry);
			}

			else if(!innerHeadersRead && currentEntry != null){
				//add headers to list
				currentHeaders.add(line);
				
				//a blank line after the headers signifies start of
				//content 
				if(line.length() == 1 || line.length() == 0) {
					innerHeadersRead = true;
					System.out.println("inner headers read");
				}
				//System.out.println("inner header");
				//pares inner header
				HeaderUtils.parseFetchedContentHeaders(currentEntry, line);
				
			}
			else if (currentEntry != null){
				//add line, but skip it if its the last line consisting 
				//of only a closing bracket
				if(!(iteration == body.size()-1) &&
						!line.equals(")")) {
					currentEntry.addLine(line);
				}
				if((iteration == body.size()-1) &&
						line.equals(")")) {
					System.out.println("Skipping closing \"(\"");
				}
			}
			iteration++;
		}
	}
	
	/**
	 * Returns all the entries in this list. Entries are of type Object.
	 * Entries maybe downcast to another MimeEntryList or a MimeEntry 
	 * after their types are checked.
	 * @return List of entries
	 */
	public synchronized ArrayList<MimeEntry> getEntries() {
		return entries;
	}
	
	public synchronized String toString() {
		if(level == 0)
			return "type: " + contentMajor + " / " + contentMinor + ", number of "
					+ "entries: " + entries.size() + " @root";
		else
			return "type: " + contentMajor + " / " + contentMinor + ", number of "
			+ "entries: " + entries.size() + " @" + level;
	}
}