package edu.buffalo.cse.irf14.analysis;

public class SpecialCharFilter extends TokenFilter {
	

	public SpecialCharFilter(TokenStream stream) {
		super(stream);
	}
	
	
	
	public Token trimSpecialChars(Token token)
	{
		String currText = token.getTermText();
		currText = currText.replaceAll("[^a-zA-Z0-9-.]", "");
		if(currText.matches("[a-zA-Z]+-[a-zA-Z]+"))
			currText = currText.replaceAll("-", "");

		token.setTermText(currText);
		token.setTermBuffer(currText.toCharArray());
		return token;
	}
	
	
	@Override
	public boolean increment() throws TokenizerException {
		
		
		
		if(filterStream.hasNext())
		{
			currToken = filterStream.next();
			currToken = trimSpecialChars(currToken);
			
		}
		
		return filterStream.hasNext();
	}
	
	@Override
	public TokenStream getStream() {
		
		return filterStream;
	}

}
