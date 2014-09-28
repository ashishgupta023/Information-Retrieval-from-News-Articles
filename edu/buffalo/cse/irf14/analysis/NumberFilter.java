package edu.buffalo.cse.irf14.analysis;

public class NumberFilter extends TokenFilter  {
	NumberFilter(TokenStream stream)  {
		super(stream);
		}
	
	private Token filterNumbers(Token token) {
		String numRegex = "(\\d+[,%/.]*\\d*)[%.]*";
		if (token.isDate()) {			// This Method has to be created
			return token;
		}
		String tokenText = token.getTermText();
		if(tokenText.matches(numRegex)) {
			String tokenValue = token.getTermText().replaceAll("[0-9]*", "").replaceAll("[.,]*", "");
			token.setTermText(tokenValue);
			token.setTermBuffer(tokenValue.toCharArray());
			return token;
		}
		
		return null;
	}

	@Override
	public boolean increment() throws TokenizerException {
		try
		{
		if(filterStream.hasNext()) {
			currToken = filterStream.next();
			currToken = filterNumbers(currToken);			// In case of StopWord token, this will be empty string, please check how to use this. I am confused still in the flow..
		}
		}
		catch(Exception e)
		{
			System.out.println("--Problem in applying Number Filter--");
		}
		
		return filterStream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return filterStream;
	}
}
