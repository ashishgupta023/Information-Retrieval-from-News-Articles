package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.List;

public class SymbolFilter extends TokenFilter{

	
	public SymbolFilter(TokenStream stream) {
		super(stream);
	}

	// Handles punctuations
	private Token trimPunctuations(Token token)
	{
		String currText = token.getTermText();	
		//Remove the punctuation marks ?,.,! at the end of the token
		currText = currText.replaceAll("[?.!]*\\z", "");
		token.setTermText(currText);
		token.setTermBuffer(currText.toCharArray());
		return token;
	}
	
	// Handles Apostrophes		
	public Token trimApostrophes(Token token)
	{
		String currText = token.getTermText();	
		// Remove apostrophes at the end of the token
		currText = currText.replaceAll("[']*\\z", "");
		
		//Handle intermediate apostrophes and contractions
		if(currText.matches("^.*[a-zA-Z]*'[a-z]*.*$"))
		{
				if(commonContractions.containsKey(currText.toLowerCase()))
				{
					if(currText.matches("^[A-Z].*"))
					{
						currText = commonContractions.get(currText.toLowerCase());	
						currText = Character.toUpperCase(currText.charAt(0)) + currText.substring(1);
					}
					else
					{
						currText = commonContractions.get(currText.toLowerCase());	

					}
				}
				
			
		}
		//Remove apostrophes at the start of the token
				currText = currText.replaceAll("\\A[']*", "");
		currText = currText.replaceAll("'[a-z]+\\z*", "");
		currText = currText.replaceAll("'", "");

		
		token.setTermText(currText);
		token.setTermBuffer(currText.toCharArray());
		return token;
	}
	
	//Handle Hyphens
	public Token trimHyphens(Token token)
	{
		String currText = token.getTermText();
		
		// Remove hyphens at the end of the token
		currText = currText.replaceAll("[-]*\\z", "");
		//Remove hyphens at the start of the token
		currText = currText.replaceAll("\\A[-]*", "");
		
		//Handle hyphens with only alphabets
		if(currText.matches("^[a-zA-Z]*-[a-zA-z]*$"))
		{
			currText = currText.replaceAll("-", " ");
		}
		
		
		token.setTermText(currText);
		token.setTermBuffer(currText.toCharArray());
		return token;
		
	}
	
	@Override
	public boolean increment() throws TokenizerException {
		try
		{
		
		if(filterStream.hasNext())
		{
			currToken = filterStream.next();
			currToken = trimPunctuations(currToken);
			currToken = trimApostrophes(currToken);
			currToken = trimHyphens(currToken);	
			
		
		}
		}
		catch(Exception e)
		{
		}
		
		return filterStream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		
		return filterStream;
	}

	
	


}
