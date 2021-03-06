/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;


/**
 * @author nikhillo
 * Class that converts a given string into a {@link TokenStream} instance
 */
public class Tokenizer {

	private String regexDelimiter ;
	/**
	 * Default constructor. Assumes tokens are whitespace delimited
	 */
	public Tokenizer() {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		
		regexDelimiter = "\\s";
		
	}
	
	/**
	 * Overloaded constructor. Creates the tokenizer with the given delimiter
	 * @param delim : The delimiter to be used
	 */
	public Tokenizer(String delim) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		regexDelimiter = delim;
	}
	
	/**
	 * Method to convert the given string into a TokenStream instance.
	 * This must only break it into tokens and initialize the stream.
	 * No other processing must be performed. Also the number of tokens
	 * would be determined by the string and the delimiter.
	 * So if the string were "hello world" with a whitespace delimited
	 * tokenizer, you would get two tokens in the stream. But for the same
	 * text used with lets say "~" as a delimiter would return just one
	 * token in the stream.
	 * @param str : The string to be consumed
	 * @return : The converted TokenStream as defined above
	 * @throws TokenizerException : In case any exception occurs during
	 * tokenization
	 */
	public TokenStream consume(String str) throws TokenizerException {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		TokenStream tokenStream = new TokenStream() ;
		int processedLength = 0;
		if(str != null && !str.isEmpty() )
		{

			String[] strTok = str.split(regexDelimiter);
			for (String tok : strTok) {
				if(!tok.trim().isEmpty() && tok != null)
				{
					Token token = new Token();
					token.setTermText(tok);
					token.setTermBuffer(tok.toCharArray());
					token.setPosIndex(str.toLowerCase().indexOf(tok.toLowerCase() , processedLength));
					processedLength = processedLength + tok.length() + 1;
					tokenStream.add(token);
				}

			}
		}
		else
		{
			throw new TokenizerException();
		}
		
		return tokenStream;

		
	}
}
