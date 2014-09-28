package edu.buffalo.cse.irf14.analysis;

import java.util.HashMap;

public class StopWordFilter extends TokenFilter {
	
	private HashMap<String, Integer> stopWordMap = new HashMap<String, Integer>();
	
	public StopWordFilter(TokenStream stream)  {
		super(stream);
		
		/* Initialize Stop Words List */
		
		String[] stopWordList = new String[] {"a", "able", "about", "across", "after", "all", "almost", "also","am","among","an","and","any","are","as","at","be","because","been","but","by","can","cannot","could","dear","did","do","does","either","else","ever","every","for","from","get","got","had","has","have","he","her","hers","him","his","how","however","i","if","in","into","is","it","its","just","least","let","like","likely","may","me","might","most","must","my","neither","no","nor","not","of","off","often","on","only","or","other","our","own","rather","said","say","says","she","should","since","so","some","than","that","the","their","them","then","there","these","they","this","tis","to","too","twas","us","wants","was","we","were","what","when","where","which","while","who","whom","why","will","with","would","yet","you","your" };
		for(String stopWord : stopWordList) {
			stopWordMap.put(stopWord, 1);
		}
	}
	private Token filterStopWords(Token token) {
			String tokenText = token.getTermText();
			if (stopWordMap.containsKey(tokenText)) {
				filterStream.remove();
			}
			return token;
		}

	@Override
	public boolean increment() throws TokenizerException {
		
		try
		{
		if(filterStream.hasNext()) {
			currToken = filterStream.next();
			currToken = filterStopWords(currToken);			// In case of StopWord token, this will be empty string, please check how to use this. I am confused still in the flow..
		}
		}
		catch(Exception e)
		{
			System.out.println("-- Problem in applying StopWord Filter--");
		}
		
		return filterStream.hasNext();
		
	}

	@Override
	public TokenStream getStream() {
		return filterStream;
	}
}