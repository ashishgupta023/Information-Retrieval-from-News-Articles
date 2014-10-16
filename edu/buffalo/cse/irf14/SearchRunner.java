package edu.buffalo.cse.irf14;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;

/**
 * Main class to run the searcher.
 * As before implement all TODO methods unless marked for bonus
 * @author nikhillo
 *
 */
public class SearchRunner {
	public enum ScoringModel {TFIDF, OKAPI};
	
	String indexDir ;
	String corpusDir;
	char mode;
	PrintStream stream;
	
	
	/**
	 * Default (and only public) constuctor
	 * @param indexDir : The directory where the index resides
	 * @param corpusDir : Directory where the (flattened) corpus resides
	 * @param mode : Mode, one of Q or E
	 * @param stream: Stream to write output to
	 */
	public SearchRunner(String indexDir, String corpusDir, 
			char mode, PrintStream stream) {
		//TODO: IMPLEMENT THIS METHOD
		
		this.indexDir = indexDir;
		this.corpusDir = corpusDir;
		this.mode = mode;
		this.stream = stream;
	}
	
	/**
	 * Method to execute given query in the Q mode
	 * @param userQuery : Query to be parsed and executed
	 * @param model : Scoring Model to use for ranking results
	 */
	public void query(String userQuery, ScoringModel model) {
		//TODO: IMPLEMENT THIS METHOD
		
		IndexReader reader = null;
		
		Map <String, Integer> result = new LinkedHashMap<String, Integer>();
		Query query = QueryParser.parse(userQuery, "OR");
		LinkedList<String> executeQuery = query.getEvalOrder();
		
		ListIterator<String> iter = executeQuery.listIterator();
		
		if(executeQuery.size() == 1)
		{
			String operand = iter.next();
			iter.remove();
			String[] analyzedoperand = getAnalyzedTerm(operand);
			reader = new IndexReader(indexDir, IndexType.valueOf(analyzedoperand[0].toUpperCase()));
			result = reader.getPostings(analyzedoperand[1]);
		}
		else if(executeQuery.size() >1)
		{
			
				while(iter.hasNext())
				{
					String operand2 = null;
					String operand1 = null;
					String node = iter.next();
					while( iter.hasNext() &&  (!node.equals("#") && !node.equals("+") ) )
					{
						node = iter.next();
					}
					String operator = node;
					iter.remove();
					if(iter.hasPrevious())
					{
						operand2 = iter.previous();
						iter.remove();
					}
					if(iter.hasPrevious())
					{
						 operand1 = iter.previous();
						iter.remove();
					}
					if(!operand1.equals("--RESULT--") && !operand2.equals("--RESULT--"))
					{
						String[] analyzedoperand1 = getAnalyzedTerm(operand1);
						String[] analyzedoperand2 = getAnalyzedTerm(operand2)	;		
						
						if(analyzedoperand1[0].equals(analyzedoperand1[0]))
						{
							
							reader = new IndexReader(indexDir, IndexType.valueOf(analyzedoperand1[0].toUpperCase()));
							if(operator.trim().equals("#"))
							{
								result = reader.orQuery(analyzedoperand1[1] , analyzedoperand2[1]);
								if(executeQuery.size() != 0)
									iter.add("--RESULT--");	
							}
							else if( operator.trim().equals("+"))
							{
								if(analyzedoperand1[1].contains("<") || analyzedoperand2[1].contains("<"))
								{
									result = reader.notPostings(reader.getPostings(analyzedoperand1[1].replace("<", "").replace(">", "")),
											reader.getPostings(analyzedoperand2[1].replace("<", "").replace(">", "")));
								}
								else
								{
									result = reader.query(analyzedoperand1[1] , analyzedoperand2[1]);
									if(executeQuery.size() != 0)
										iter.add("--RESULT--");	
								}
							}
						}
						else
						{
							reader = new IndexReader(indexDir, IndexType.valueOf(analyzedoperand1[0].toUpperCase()));
							result = reader.getPostings(analyzedoperand1[1]);
							reader = new IndexReader(indexDir , IndexType.valueOf(analyzedoperand2[0].toUpperCase()));
							Map <String, Integer> tempresult = reader.getPostings(analyzedoperand2[1]);
							if(operator.trim().equals("#"))
							{
								result = reader.unionPostings(result, tempresult);
								if(executeQuery.size() != 0)
									iter.add("--RESULT--");	
							}
							if(operator.trim().equals("+"))
							{
								if(analyzedoperand1[1].contains("<") || analyzedoperand2[1].contains("<"))
								{
									result = reader.notPostings(reader.getPostings(analyzedoperand1[1].replace("<", "").replace(">", "")),
											reader.getPostings(analyzedoperand2[1].replace("<", "").replace(">", "")));
								}
								else
								{
									result = reader.intersectPostings(result, tempresult);
									if(executeQuery.size() != 0)
										iter.add("--RESULT--");	
								}
							}
						}
					}
					else
					{
						String[] analyzedoperand = null;
						if(operand1.equals("--RESULT--"))
						{
							analyzedoperand = getAnalyzedTerm(operand2);		
						}
						else if(operand2.equals("--RESULT--"))
						{
							analyzedoperand = getAnalyzedTerm(operand1);
						}
						reader = new IndexReader(indexDir, IndexType.valueOf(analyzedoperand[0].toUpperCase()));
						if(operator.trim().equals("#"))
						{
							result = reader.unionPostings(result, reader.getPostings(analyzedoperand[1]));
							if(executeQuery.size() != 0)
								executeQuery.push("--RESULT--");	
						}
						if(operator.trim().equals("+"))
						{
							if(analyzedoperand[1].contains("<"))
							{
								result = reader.notPostings(result,
										reader.getPostings(analyzedoperand[1].replace("<", "").replace(">", "")));
							}
							else
							{
								result = reader.intersectPostings(result, reader.getPostings(analyzedoperand[1]));
								if(executeQuery.size() != 0)
									executeQuery.push("--RESULT--");	
							}
						}
					}
				}
			
		}
		
		System.out.println(result);
	
	}
	
	private static String[] getAnalyzedTerm(String operand ) {
		
		String[] string = null;
		Tokenizer tknizer = new Tokenizer();
		Analyzer analyzer = null;
		AnalyzerFactory fact = AnalyzerFactory.getInstance();
		
		if(operand.contains(":"))
		{
			string = operand.split(":");
		}
		try {
			TokenStream stream = tknizer.consume(string[1]);
			if(string[0].trim().equals("Term"))
				 analyzer = fact.getAnalyzerForField(FieldNames.CONTENT, stream);
			else if(string[0].trim().equals("Author"))
				 analyzer = fact.getAnalyzerForField(FieldNames.AUTHOR, stream);
			else if(string[0].trim().equals("Category"))
				 analyzer = fact.getAnalyzerForField(FieldNames.CATEGORY, stream);
			else if(string[0].trim().equals("Place"))
				 analyzer = fact.getAnalyzerForField(FieldNames.PLACE, stream);

			while (analyzer.increment()) {
				
			}
			stream = analyzer.getStream();
			stream.reset();
			string[1] = stream.next().toString();
			return string;
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return string;
	}
	
	/**
	 * Method to execute queries in E mode
	 * @param queryFile : The file from which queries are to be read and executed
	 */
	public void query(File queryFile) {
		//TODO: IMPLEMENT THIS METHOD
	}
	
	/**
	 * General cleanup method
	 */
	public void close() {
		//TODO : IMPLEMENT THIS METHOD
	}
	
	/**
	 * Method to indicate if wildcard queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean wildcardSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF WILDCARD BONUS ATTEMPTED
		return false;
	}
	
	/**
	 * Method to get substituted query terms for a given term with wildcards
	 * @return A Map containing the original query term as key and list of
	 * possible expansions as values if exist, null otherwise
	 */
	public Map<String, List<String>> getQueryTerms() {
		//TODO:IMPLEMENT THIS METHOD IFF WILDCARD BONUS ATTEMPTED
		return null;
		
	}
	
	/**
	 * Method to indicate if speel correct queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean spellCorrectSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF SPELLCHECK BONUS ATTEMPTED
		return false;
	}
	
	/**
	 * Method to get ordered "full query" substitutions for a given misspelt query
	 * @return : Ordered list of full corrections (null if none present) for the given query
	 */
	public List<String> getCorrections() {
		//TODO: IMPLEMENT THIS METHOD IFF SPELLCHECK EXECUTED
		return null;
	}
}
