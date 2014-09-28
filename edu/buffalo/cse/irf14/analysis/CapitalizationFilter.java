package edu.buffalo.cse.irf14.analysis;

import java.util.Locale;

public class CapitalizationFilter extends TokenFilter {
	CapitalizationFilter(TokenStream stream) {
		super(stream);
		}
	
	private boolean checkIfLastWordOfSentence(Token token) {
		String lastWordRegex = "[/.]$";
		String tokenText = token.getTermText();
		boolean isLastWord = tokenText.matches(lastWordRegex);
		return isLastWord;
	}
	
	
	Token checkWordCaps(Token token) {
		boolean tokenCaps = token.getTermText().matches("[A-Z]");
		if(!tokenCaps) {		// if word in not in CAPS, then skip
			return token;
		}

		/* If word is in CAPS, then check all words ahead of it till sentence ends, if any token is small, return token as it is. */
		if(getStream().hasNext()) {	
			for(Token tokenNext = getStream().next(); !checkIfLastWordOfSentence(tokenNext); tokenNext = getStream().next()) {
				if(tokenNext.getTermText().matches("[a-z]")) {
					return token;
				}
				
			}
		}
		
		/* If all the words till end of a sentence are capital, then make every word small */
		getStream().reset();
		if(getStream().hasPrevious()) {
			for(Token tokenPrevious = getStream().previous(); !checkIfLastWordOfSentence(tokenPrevious); tokenPrevious = getStream().previous()) {
				tokenPrevious.setTermText(tokenPrevious.getTermText().toLowerCase());
			}
		}
		return token;
	}	
	
	Token checkCamelCaseRule(Token token) {
		String camelCaseRegex = "[A-Z]([A-Z0-9]*[a-z][a-z0-9]*[A-Z]|[a-z0-9]*[A-Z][A-Z0-9]*[a-z])[A-Za-z0-9]*";
		String sentenceEndRegex = "[/.]$";
		String tokenText = token.getTermText();
		boolean isTokenCamelCase = tokenText.matches(camelCaseRegex);
		if(getStream().hasPrevious()) {
			boolean isSentenceEnd = getStream().previous().getTermText().matches(sentenceEndRegex);
			getStream().next();		// to set Interator back to position from where we started.
			if(isSentenceEnd && isTokenCamelCase) {
				return token;
			}
		}
		String lowerCapsTokenText = token.getTermText().toLowerCase(Locale.getDefault());
		token.setTermText(lowerCapsTokenText);
		return token;
	}
	
	/* The words like San Fransisco have to been combined by this rule */
	Token checkCamelCaseCombination(Token token) {
		String camelCaseRegex = "[A-Z]([A-Z0-9]*[a-z][a-z0-9]*[A-Z]|[a-z0-9]*[A-Z][A-Z0-9]*[a-z])[A-Za-z0-9]*";
		String tokenText = token.getTermText();

		boolean isTokenCamelCase = tokenText.matches(camelCaseRegex);
		if(isTokenCamelCase) {
			int i = 0;
			while(getStream().hasNext()) {
				Token tokenNext = getStream().next();

				if(tokenNext.getTermText().matches(camelCaseRegex)) {
					token.setTermText(tokenText + tokenNext.getTermText());
					tokenNext.setTermText("");
					i++;
				}
				else {
					break;
				}
				
				if(this.checkIfLastWordOfSentence(token)) {
					break;
				}
			}
			
			/* Moving Iterator back to position where it started */
			while(i>0) {	
				if(getStream().hasPrevious()) {
					getStream().previous();
					i--;
				}
				
			}

		}
		
		return token;
	}
	
	@Override
	public boolean increment() throws TokenizerException {
		
		try
		{
		if(filterStream.hasNext()) {
			currToken = filterStream.next();
			currToken = checkCamelCaseRule(currToken);
			currToken = checkWordCaps(currToken);
			currToken = checkWordCaps(currToken);
		}
		}
		catch(Exception e)
		{
			System.out.println("--Problem in applying Capitalization Filter");
		}
		return filterStream.hasNext();

	}
	
	@Override
	public TokenStream getStream() {
		return filterStream;
	}
}

