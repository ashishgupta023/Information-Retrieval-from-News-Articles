/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * @author nikhillo
 * Class that parses a given file into a Document
 */
public class Parser {
	/**
	 * Static method to parse the given file into the Document object
	 * @param filename : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws ParserException In case any error occurs during parsing
	 */
	public static Document parse(String filename) throws ParserException  {
		// TODO YOU MUST IMPLEMENT THIS
		boolean title = true;
		boolean author = true;
		boolean placeDate = true;
		Document d = new Document();

		try
		{
			if(filename!=null)
			{
				
				BufferedReader br = new BufferedReader(new FileReader(filename));
				String line;
				String content = null;
				//Check on a linux machine
				StringTokenizer tokenizer = new StringTokenizer(filename,File.separator);
				String[] tempContent = filename.split("\\"+File.separator);
				d.setField(FieldNames.CATEGORY, tempContent[tempContent.length-2]);
				d.setField(FieldNames.FILEID, tempContent[tempContent.length-1]);
				while ((line = br.readLine()) != null) {
					if (!line.isEmpty()) {
						
						if(title == true)
						{
							d.setField(FieldNames.TITLE , line);
							title = false;
						}
					    // Check for variations of AUTHOR , separate hasAuthor
						else if(  line.contains("<AUTHOR>") && author == true )
						{
							line = line.replaceAll("\\</{0,1}AUTHOR>", "");
							if (line.contains(","))
							{
								String [] authorDetails = line.split(",");
								d.setField(FieldNames.AUTHOR, authorDetails[0].replaceAll("(By|by|BY)", "").trim());
								d.setField(FieldNames.AUTHORORG, authorDetails[1].trim() );
							}
							else
							{
								d.setField(FieldNames.AUTHOR, line);
							}
								
							author = false;
					    }
						else
						{
							if(placeDate == true)
							{
								int hyphenIndex = line.indexOf("-");
								System.out.println("=============" + line);
								if(hyphenIndex > -1)
								{
									String placeDateStr = line.substring(0,hyphenIndex);
									int tempIndex = placeDateStr.lastIndexOf(",");
									if(tempIndex > -1)
									{
										d.setField(FieldNames.PLACE, placeDateStr.substring(0, tempIndex).trim());
										d.setField(FieldNames.NEWSDATE, placeDateStr.substring(tempIndex+1, placeDateStr.length()).trim());
										placeDate = false;
										content = line.substring(hyphenIndex+1 , line.length());
									}
								}
							}
							else
							{
								if(content == null)
								{
									content = line;
								}
								else
								{
									content = content.concat(" ");
									content = content.concat(line);
								}	
							}
						}
					}			
				}
				d.setField(FieldNames.CONTENT, content);
				br.close();
			}
			else
			{
				System.out.println("--File Name cannot be null--");
				throw new ParserException();
				
			}
		}
		catch(IOException e)
		{
			System.out.println("--Problem initiating the IO stream with the given file name--");
			throw new ParserException();
		}
		
		
		return d;

	}


}
