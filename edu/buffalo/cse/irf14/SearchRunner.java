package edu.buffalo.cse.irf14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import sun.misc.IOUtils;
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
		
		Map <String, Map<String ,  ArrayList<Integer>>> result = new LinkedHashMap<String, Map<String ,  ArrayList<Integer>>>();
		Query query = QueryParser.parse(userQuery, "OR");
		LinkedList<String> executeQuery = query.getEvalOrder();
		List<String[]> userQueryTerms = new LinkedList<String[]>();
		ListIterator<String> iter = executeQuery.listIterator();
		
		if(executeQuery.size() == 1)
		{
			String operand = iter.next();
			iter.remove();
			String[] analyzedoperand = getAnalyzedTerm(operand);
			reader = new IndexReader(indexDir, IndexType.valueOf(analyzedoperand[0].toUpperCase()));
			userQueryTerms.add(analyzedoperand);
			result = reader.getPostingsWithPosIndexes(analyzedoperand[1]);
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
						userQueryTerms.add(analyzedoperand1);
						userQueryTerms.add(analyzedoperand2);
						if(analyzedoperand1[0].equals(analyzedoperand2[0]))
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
									result = reader.notPostings(reader.getPostingsWithPosIndexes(analyzedoperand1[1].replace("<", "").replace(">", "")),
											reader.getPostingsWithPosIndexes(analyzedoperand2[1].replace("<", "").replace(">", "")));
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
							result = reader.getPostingsWithPosIndexes(analyzedoperand1[1]);
							reader = new IndexReader(indexDir , IndexType.valueOf(analyzedoperand2[0].toUpperCase()));
							Map <String, Map<String, ArrayList<Integer>>> tempresult = reader.getPostingsWithPosIndexes(analyzedoperand2[1]);
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
									result = reader.notPostings(reader.getPostingsWithPosIndexes(analyzedoperand1[1].replace("<", "").replace(">", "")),
											reader.getPostingsWithPosIndexes(analyzedoperand2[1].replace("<", "").replace(">", "")));
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
						
						userQueryTerms.add(analyzedoperand);
						reader = new IndexReader(indexDir, IndexType.valueOf(analyzedoperand[0].toUpperCase()));
						if(operator.trim().equals("#"))
						{
							result = reader.unionPostings(result, reader.getPostingsWithPosIndexes(analyzedoperand[1]));
							if(executeQuery.size() != 0)
								executeQuery.push("--RESULT--");	
						}
						if(operator.trim().equals("+"))
						{
							if(analyzedoperand[1].contains("<"))
							{
								result = reader.notPostings(result,
										reader.getPostingsWithPosIndexes(analyzedoperand[1].replace("<", "").replace(">", "")));
							}
							else
							{
								result = reader.intersectPostings(result, reader.getPostingsWithPosIndexes(analyzedoperand[1]));
								if(executeQuery.size() != 0)
									executeQuery.push("--RESULT--");	
							}
						}
					}
				}
			
		}
		
		System.out.println(result);
		System.out.println(" -----Vector Space Model -----");
		
		Map<String, Double> rankedByVSM = computeVSMScores(userQueryTerms, userQuery, result);
		System.out.println(rankedByVSM);
		for(Map.Entry<String, Double> entry : rankedByVSM.entrySet())
		{
			String docFile = "";
			try {
				
				
				String line;
				boolean title = true;
				BufferedReader br = new BufferedReader(new FileReader(this.corpusDir  + File.separator + entry.getKey()) );
				
					while((line = br.readLine()) != null)
					{
						if(!line.isEmpty())
						{
							if(title == true)
								System.out.println(line);
							title = false;
							docFile = docFile.concat(" " + line);
							
						}
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		 Map<String, ArrayList<Integer>> docPosIndexes = result.get(entry.getKey());
		 int maxTf = 0 ;
		 String termWithMaxTf = "";
		 for(Map.Entry<String, ArrayList<Integer>> docEntry : docPosIndexes.entrySet())
		 {
			 if(docEntry.getValue().size() > maxTf)
			 {
				maxTf = docEntry.getValue().size();
			 	termWithMaxTf = docEntry.getKey();
			 }
		 }
		
		 ArrayList<Integer> posIndexes = docPosIndexes.get(termWithMaxTf);
		 int bestSnippet = 0;
		 int bestRating = 0;
		 for( int i = 0 ; i< posIndexes.size() ; i++ )
		 {
			 int rating = 0 ;
			 
			String snippet ;
			
			if(posIndexes.get(i) - 100 < 0 )
				 snippet = docFile.substring(0 , posIndexes.get(i) + 100 );
			 else if(posIndexes.get(i) + 100 > docFile.length())
				 snippet = docFile.substring(posIndexes.get(i) - 100 , docFile.length()-1 );
			 else
				 snippet = docFile.substring( posIndexes.get(i) - 100 , posIndexes.get(i) + 100 );
			
			
			for(int j = 0 ; j < userQueryTerms.size() ; j++)
			{
				String[] qterm = userQueryTerms.get(j);
				
				if(snippet.matches(".*[\\s]"+qterm[1]+".*"))
				{
					rating++;
				}
			}
			if(rating > bestRating)
			{
				bestRating = rating;
				bestSnippet = i;
			}
		 }
		 if(posIndexes.get(bestSnippet) - 100 < 0 )
			 System.out.println(docFile.substring(0 , posIndexes.get(bestSnippet) + 100 ));
		 else if(posIndexes.get(bestSnippet) + 100 > docFile.length())
			 System.out.println(docFile.substring(posIndexes.get(bestSnippet) - 100 , docFile.length()-1 ));
		 else
			 System.out.println(docFile.substring( posIndexes.get(bestSnippet) - 100 , posIndexes.get(bestSnippet) + 100 ));
		 break;
		}
		//System.out.println(" ----- OKAPI Model -----");
		//System.out.println(computeOKAPIScores(userQueryTerms, userQuery, result));
	}
	
	// TFIDF  - Vector Space Model
	public  Map<String, Double> computeVSMScores( List<String[]> userQueryTerms , String userQuery, Map <String, Map<String ,  ArrayList<Integer>>> unrankedResult )
	{
		//For Document Vector
		
				IndexReader reader ;
				Map<String, Double> normalizationForD = new HashMap<String, Double>();
				
				
				//Normalization factors for each of document 
				/*for (Map.Entry<String , Map <String , ArrayList<Integer>>> entry : unrankedResult.entrySet())
				{
					Double[] weightTerminD = new Double[userQueryTerms.size()];
					for(int i = 0 ; i < userQueryTerms.size() ; i++)
					{
						String[] op = userQueryTerms.get(i);
						Map <String , ArrayList<Integer>> docResult = entry.getValue();
						if(docResult.containsKey(op[1]))
							weightTerminD[i] = (double)docResult.get(op[1]).size() ;
						else
							weightTerminD[i] = 0.0;
						
					}
					Double sumSquares = 0.0;
					for(int i = 0 ; i< weightTerminD.length ; i++)
					{
						sumSquares = sumSquares +  Math.pow(weightTerminD[i], 2); 
					}
					normalizationForD.put(entry.getKey(),  Math.sqrt(sumSquares));
					
				}*/
				
				
				
			
				Double[] weightTerminQ = new Double[userQueryTerms.size()];
				Map<String, Double> scores = new HashMap<String, Double>(); // fileID, scores
				for(int i = 0 ; i < userQueryTerms.size() ; i++)
				{
					String[] qTerm = userQueryTerms.get(i);
					int tfqTerm = 0;
					int index = 0;
					while(index != -1)
					{
						index = userQuery.indexOf(qTerm[1],index);
						if(index != -1)
						{
							index = index + qTerm[1].length();
							tfqTerm++;
						}
						
					}
					reader = new IndexReader(indexDir , IndexType.valueOf(qTerm[0].toUpperCase()));
					
					int N = reader.getTotalValueTerms();
					Map<String, Map<String, ArrayList<Integer>>> postings = reader.getPostingsWithPosIndexes(qTerm[1]);
					int dfTerm = postings.size();
					weightTerminQ[i] = (tfqTerm) * (Math.log(N/dfTerm)); //For Query Vector: normal tf, idf weighting (log (N/df))  , no normalization
					//For Document Vector : normal tf , no idf, no normalization
					for(Map.Entry<String, Map<String, ArrayList<Integer>>> entry : unrankedResult.entrySet())
					{
						String fileID = entry.getKey();
						Map <String , ArrayList<Integer>> docResult = entry.getValue();
						double wfTD = 0.0;
						if(docResult.containsKey(qTerm[1]))
						{
							 wfTD =  docResult.get(qTerm[1]).size();
							 //System.out.println(wfTD);
						}
						if(scores.containsKey(fileID))
						{
							
							scores.put(fileID, scores.get(fileID) + (weightTerminQ[i] * wfTD) );
						}
						else
						{
							scores.put(fileID, (weightTerminQ[i] * wfTD));
						}
					}
					
				}
				
				
				
				/*for(Map.Entry<String, Double> entry : scores.entrySet())
				{
					Double finalScore = entry.getValue() / (normalizationForD.get(entry.getKey())  )  ;
					scores.put(entry.getKey(), finalScore);
				}*/
			
				List<Entry<String, Double>> unrankedOutput = new LinkedList<Map.Entry<String, Double>>(
						scores.entrySet());
				
				 Collections.sort(unrankedOutput, new Comparator<Entry<String, Double>>() {

					@Override
					public int compare(Entry<String, Double> val1,
							Entry<String, Double> val2) {
						return val2.getValue().compareTo(val1.getValue());
					}
				});

				 HashMap<String, Double> rankedResult = new LinkedHashMap<String, Double>();
				for (Map.Entry<String, Double> entry : unrankedOutput) {
					rankedResult.put(entry.getKey(), entry.getValue());
				}
				
				return rankedResult;
	}
	
	public  Map<String, Double> computeOKAPIScores( List<String[]> userQueryTerms , String userQuery, Map <String, Map<String ,  ArrayList<Integer>>> unrankedResult )
	{
		IndexReader reader;			
		HashMap<String, Double> rankedResult = new LinkedHashMap<String, Double>();
		Map<String, Double> scores = new HashMap<String, Double>(); // fileID, scores
		double oKapiK1 = 1.5; // document term frequency scaling calibration
		double oKapiK3 = 1.5; // query term frequency calibration - Use for long queries
		double oKapiB = 0.75; // scaling term weight by document length
		 for(int i = 0 ; i < userQueryTerms.size() ; i++)
		 {
			 	String[] qTerm = userQueryTerms.get(i);
				int tfqTerm = 0;
				int index = 0;
				while(index != -1)
				{
					index = userQuery.indexOf(qTerm[1],index);
					if(index != -1)
					{
						index = index + qTerm[1].length();
						tfqTerm++;
					}
					
				}
				reader = new IndexReader(indexDir , IndexType.valueOf(qTerm[0].toUpperCase()));
				
				int N = reader.getTotalValueTerms();
				Map<String, Map<String, ArrayList<Integer>>> postings = reader.getPostingsWithPosIndexes(qTerm[1]);
				int dfTerm = postings.size();
				Double iDFt = Math.log(N/dfTerm);
				Double avgDocLength = reader.getAverageDocumentLength();

				for(Map.Entry<String, Map<String, ArrayList<Integer>>> entry : unrankedResult.entrySet())
				{
					String fileID = entry.getKey();
					Map <String , ArrayList<Integer>> docResult = entry.getValue();
					double tfTD = 0.0;
					if(docResult.containsKey(qTerm[1]))
					{
						tfTD = docResult.get(qTerm[1]).size();
					}
					
					int docLength = reader.getDocumentLength(fileID);
					Double oKapiScoreNumerator = ((oKapiK1+1) * tfTD) * (oKapiK3 + 1) * tfqTerm;
					Double oKapiScoreDenominator = ( (oKapiK1 *( (1-oKapiB) + (oKapiB * ( docLength / avgDocLength)  )) ) + tfTD ) * (oKapiK3 + tfqTerm) ;
					
					Double dRSV = (iDFt * oKapiScoreNumerator) / oKapiScoreDenominator; //RSVd - Retrieval Status Value
					
					if(scores.containsKey(fileID))
					{
						
						scores.put(fileID, scores.get(fileID) + dRSV );
					}
					else
					{
						scores.put(fileID, dRSV );
					}
				}
				
		}
	
			List<Entry<String, Double>> unrankedOutput = new LinkedList<Map.Entry<String, Double>>(
					scores.entrySet());
			
			 Collections.sort(unrankedOutput, new Comparator<Entry<String, Double>>() {

				@Override
				public int compare(Entry<String, Double> val1,
						Entry<String, Double> val2) {
					return val2.getValue().compareTo(val1.getValue());
				}
			});

			rankedResult = new LinkedHashMap<String, Double>();
			for (Map.Entry<String, Double> entry : unrankedOutput) {
				rankedResult.put(entry.getKey(), entry.getValue());
			}
			
			return rankedResult;
	}
	
	private static String[] getAnalyzedTerm(String operand ) {
		
		String[] string = null;
		Tokenizer tknizer = new Tokenizer();
		Analyzer analyzer = null;
		AnalyzerFactory fact = AnalyzerFactory.getInstance();
		boolean isNotToken = false;
		if(operand.contains("<"))
		{
			isNotToken = true;
			operand = operand.replace("<", "");
			operand = operand.replace(">", "");
		}
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
			if(isNotToken)
			{
				string[1] = "<"+ stream.next().toString() + ">";
			}
			else
			{
				string[1] =  stream.next().toString() ;	
			}
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
