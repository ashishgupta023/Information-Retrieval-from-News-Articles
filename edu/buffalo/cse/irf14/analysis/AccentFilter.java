package edu.buffalo.cse.irf14.analysis;

import java.text.Normalizer;


public class AccentFilter extends TokenFilter {

	public AccentFilter(TokenStream stream) {
		super(stream);
	}
	
	
	public Token trimAccents(Token token) 
	{
		String currText = token.getTermText();
		currText = Normalizer.normalize(currText, Normalizer.Form.NFD); 
		currText = currText.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		
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
				currToken = trimAccents(currToken);
			}
		}
		catch(Exception e)
		{
			System.out.println("--Problem in applying the Accent Filter--");
		}
		
		return filterStream.hasNext();
	}
	
	@Override
	public TokenStream getStream() {
		
		return filterStream;
	}

}
