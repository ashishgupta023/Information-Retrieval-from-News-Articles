/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.HashMap;
import java.util.Map;

/**
 * The abstract class that you must extend when implementing your 
 * TokenFilter rule implementations.
 * Apart from the inherited Analyzer methods, we would use the 
 * inherited constructor (as defined here) to test your code.
 * @author nikhillo
 *
 */
public abstract class TokenFilter implements Analyzer {
	/**
	 * Default constructor, creates an instance over the given
	 * TokenStream
	 * @param stream : The given TokenStream instance
	 */
	TokenStream filterStream = null;
	Map<String, String> commonContractions ;
	Map<String, Integer> months ;

	Token currToken ;
	
	public TokenFilter(TokenStream stream) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		
		//initialize the stream
		filterStream = stream;
		
		//Common Contractions List
		commonContractions = new HashMap<String, String>();
		commonContractions.put("i'm", "I am");
		commonContractions.put("we're" , "we are");
		commonContractions.put("they're" , "they are");
		commonContractions.put("i've" , "I have");
		commonContractions.put("should've" , "should have");
		commonContractions.put("would've" , "would have");
		commonContractions.put("they'd" , "they would");
		commonContractions.put("she'll" , "she will");
		commonContractions.put("they'll" , "they will");
		commonContractions.put("'em", "them");
		commonContractions.put("isn't", "is not");
		commonContractions.put("don't", "do not");
		commonContractions.put("won't", "will not");
		commonContractions.put("shan't", "shall not");
		commonContractions.put("shouldn't", "should not");
		commonContractions.put("can't", "cannot");
		commonContractions.put("couldn't", "could not");

		


		
		//Months
		months = new HashMap<String, Integer>();
		months.put("january", 1);
		months.put("jan", 1);
		months.put("february", 2);
		months.put("feb", 2);
		months.put("march",3);
		months.put("mar", 3);
		months.put("april", 4);
		months.put("apr", 4);
		months.put("may", 5);
		months.put("june", 6);
		months.put("jun", 6);
		months.put("july", 7);
		months.put("jul", 7);
		months.put("august", 8);
		months.put("aug", 8);
		months.put("september", 9);
		months.put("sept", 9);
		months.put("october", 10);
		months.put("oct", 10);
		months.put("november", 11);
		months.put("nov",11);
		months.put("december", 12);
		months.put("dec", 12);
		



		



		

	}
}
