package edu.buffalo.cse.irf14;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import sun.font.FontFamily;
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
	private List<String[]> userQueryTerms ;
	private  boolean isPhraseQuery;
	IndexReader reader ;
	
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
		this.userQueryTerms = null;
		this.isPhraseQuery = false;
		this.reader = null;
	}
	
	/**
	 * Method to execute given query in the Q mode
	 * @param userQuery : Query to be parsed and executed
	 * @param model : Scoring Model to use for ranking results
	 */
	public void query(String userQuery, ScoringModel model) {
		//TODO: IMPLEMENT THIS METHOD
		userQueryTerms = null;
		isPhraseQuery = false;
		if(userQuery == null || userQuery.isEmpty() || model == null)
		{
			this.stream.print("Invalid Input");
		}
		else
		{
		Long startTime = System.currentTimeMillis();
		Map<String, Map<String ,  ArrayList<Integer>>> result = evaluateQuery(userQuery);
		Map<String, Double> rankedResult = new HashMap<String, Double>();
		switch(model)
		{
			case TFIDF:
			{
				if(result != null)
				{
					rankedResult = computeVSMScores(userQueryTerms, userQuery, isPhraseQuery, result);
				}
				else
					rankedResult = null;
				break;
			}
			case OKAPI:
			{
				if(result != null)
				{
					rankedResult = computeOKAPIScores(userQueryTerms, userQuery, isPhraseQuery, result);
				}
				else
					rankedResult = null;
				break;
			}

		}
		
		Map<String, String[]> snippets = new HashMap<String, String[]>();
		if(rankedResult != null)
		{
			int count = 1;
			for(Map.Entry<String, Double> entry : rankedResult.entrySet())
			{
	
				String[] snip = generateSnippet(entry, userQueryTerms , result);
				snippets.put(entry.getKey(), snip);
				count++;
				if(count > 10)
					break;
			}
		}
		
		
		Long endTime = System.currentTimeMillis();
		
		this.stream.println("-----------");
		this.stream.println("QUERY : " + userQuery);
		this.stream.println("QUERY TIME : " + Long.toString(endTime - startTime) + " ms");
		this.stream.println("-----------------Results---------------");
		if(rankedResult != null)
		{
			int count = 1;
			for(Map.Entry<String, Double> entry : rankedResult.entrySet())
			{
				this.stream.println();
				this.stream.println("RANK : "  + count + " FILEID : " + entry.getKey() );
				this.stream.println("TITLE : " + snippets.get(entry.getKey())[0]);
				DecimalFormat f = new DecimalFormat("0.00000");
				this.stream.println("RELEVANCY SCORE : " + f.format(entry.getValue()));
				this.stream.println();
				String snip = snippets.get(entry.getKey())[1];
				if(snip.length() > 0)
				{
					this.stream.print("....");
					this.stream.print(snip.charAt(0));
					for(int z = 1; z < snip.length() ; z++){
						this.stream.print(snip.charAt(z));
						if( z % 80 == 0)
							this.stream.print("\n");
					}
					this.stream.print("....");
				}
				else
				{
					this.stream.println("No snippet available.");
				}

				count++;
				this.stream.println("\n-----------");
				if(count > 10)
					break;
			}
		}
		else
		{
			this.stream.println("-----The query returned zero results !!-----");
		}
		
	
		}
	}
	
	public Map<String, Map<String ,  ArrayList<Integer>>> evaluateQuery(String userQuery )
	{
		
		isPhraseQuery = false;
		Map <String, Map<String ,  ArrayList<Integer>>> result = new LinkedHashMap<String, Map<String ,  ArrayList<Integer>>>();
		Query query = QueryParser.parse(userQuery, "OR");
		LinkedList<String> executeQuery = query.getEvalOrder();
		userQueryTerms = new LinkedList<String[]>();
		ListIterator<String> iter = executeQuery.listIterator();
		
		if(executeQuery.size() == 1)
		{
			String operand = iter.next();
			iter.remove();
			String[] analyzedoperand = getAnalyzedTerm(operand);
			if(reader == null || reader.getType().toString().toLowerCase() != analyzedoperand[0].toLowerCase() )
				reader = new IndexReader(indexDir, IndexType.valueOf(analyzedoperand[0].toUpperCase()));
			userQueryTerms.add(analyzedoperand);
			
			
			
			if(userQuery.contains("\""))
			{
				 isPhraseQuery = true;
				 result = processPhraseQuery(userQuery ,analyzedoperand , reader);
			}
			else
			{
				result = reader.getPostingsWithPosIndexes(analyzedoperand[1]);
			}
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
						if(Arrays.equals(analyzedoperand1, analyzedoperand2))
							userQueryTerms.add(analyzedoperand1);
						else
						{
							userQueryTerms.add(analyzedoperand1);
							userQueryTerms.add(analyzedoperand2);

						}
						if(analyzedoperand1[0].equals(analyzedoperand2[0]))
						{
							
							if(reader == null || reader.getType().toString().toLowerCase() != analyzedoperand1[0].toLowerCase() )
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
							if(reader == null || reader.getType().toString().toLowerCase() != analyzedoperand1[0].toLowerCase() )
								reader = new IndexReader(indexDir, IndexType.valueOf(analyzedoperand1[0].toUpperCase()));
							result = reader.getPostingsWithPosIndexes(analyzedoperand1[1]);
							if(reader == null || reader.getType().toString().toLowerCase() != analyzedoperand2[0].toLowerCase() )
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
									if(executeQuery.size() != 0)
										iter.add("--RESULT--");	
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
						
						if(!Arrays.equals(userQueryTerms.get(userQueryTerms.size()-1), analyzedoperand))
							userQueryTerms.add(analyzedoperand);
						
						if(reader == null || reader.getType().toString().toLowerCase() != analyzedoperand[0].toLowerCase() )
							reader = new IndexReader(indexDir, IndexType.valueOf(analyzedoperand[0].toUpperCase()));
						if(operator.trim().equals("#"))
						{
							result = reader.unionPostings(result, reader.getPostingsWithPosIndexes(analyzedoperand[1]));
							if(executeQuery.size() != 0)
								iter.add("--RESULT--");	
						}
						if(operator.trim().equals("+"))
						{
							if(analyzedoperand[1].contains("<"))
							{
								result = reader.notPostings(result,
										reader.getPostingsWithPosIndexes(analyzedoperand[1].replace("<", "").replace(">", "")));
								if(executeQuery.size() != 0)
									iter.add("--RESULT--");	
							}
							else
							{
								result = reader.intersectPostings(result, reader.getPostingsWithPosIndexes(analyzedoperand[1]));
								if(executeQuery.size() != 0)
									iter.add("--RESULT--");	
							}
						}
					}
				}
			
		}
		
		return result;
		
	
	}
	public Map<String, Map<String, ArrayList<Integer>>> processPhraseQuery(String userQuery , String[] analyzedPhraseOperand , IndexReader reader)
	{
		String[] phraseTokens = analyzedPhraseOperand[1].split(" ");
		
		Map<String, Map<String, ArrayList<Integer>>> phraseQueryTempResult = new HashMap<String, Map<String,ArrayList<Integer>>>();
		Map<String, Map<String, ArrayList<Integer>>> phraseQueryResult = new HashMap<String, Map<String,ArrayList<Integer>>>();

		phraseQueryTempResult = reader.query(phraseTokens);
		
		for(Map.Entry<String, Map<String, ArrayList<Integer>>> docEntry : phraseQueryTempResult.entrySet())
		{
			Map<String, ArrayList<Integer>> termEntries =  docEntry.getValue();
			 ArrayList<Integer> phrasePositions =  new  ArrayList<Integer>();
			ArrayList<Integer> posIndexFirstElement =  termEntries.get(phraseTokens[0]);
			ArrayList<Integer> posIndexLastElement =  termEntries.get(phraseTokens[phraseTokens.length - 1]);
			
			for( int m = 0 ; m< posIndexFirstElement.size() ; m++)
			{
				Integer indexM = posIndexFirstElement.get(m);
				for(int n = 0 ; n < posIndexLastElement.size() ; n++)
				{
					Integer indexN = posIndexLastElement.get(n);
					int diff = indexM - indexN  ;
					diff = Math.abs(diff);
					if(diff <= phraseTokens.length * 5)
					{
						phrasePositions.add(indexM);
						Map<String, ArrayList<Integer>> phrasePosting = new HashMap<String, ArrayList<Integer>>();
						phrasePosting.put(analyzedPhraseOperand[1], phrasePositions);
						phraseQueryResult.put(docEntry.getKey(), phrasePosting);
					}
				}
			}
			

		}
		
		return phraseQueryResult;
	}
	
	public String[] generateSnippet(Map.Entry<String , Double> rankedEntry , List<String[]> userQueryTerms , Map<String , Map<String, ArrayList<Integer>>> result)
	{
		String docFile = "";
		String snippetTitle = "";
		try {
			
			
			String line;
			boolean title = true;
			BufferedReader br = new BufferedReader(new FileReader(this.corpusDir  + File.separator + rankedEntry.getKey()) );
			
				while((line = br.readLine()) != null)
				{
					if(!line.isEmpty())
					{
						if(title == true)
							snippetTitle = line;
						title = false;
						docFile = docFile.concat(" " + line);
						
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	 Map<String, ArrayList<Integer>> docPosIndexes = result.get(rankedEntry.getKey());
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
	 String bestSnippet = "";
	 int bestRating = 0;
	 
	 for( int i = 0 ; i< posIndexes.size() ; i++ )
	 {
		 int rating = 0 ;
		 String snippetContent = ""; 
		
		
		if(posIndexes.get(i) - 100 < 0 )
		{
			if(posIndexes.get(i) + 100 < docFile.length() -1 )
				snippetContent = docFile.substring(0 , posIndexes.get(i) + 100 );
			else
				snippetContent = docFile.substring(0, docFile.length()-1);
		}
		 else if(posIndexes.get(i) + 100 > docFile.length())
		 {
			if( posIndexes.get(i) - 100 < 0)
				snippetContent = docFile.substring(0, docFile.length()-1 );
			else
				snippetContent = docFile.substring(posIndexes.get(i) - 100 , docFile.length() -1);
		 }
		 else
		 {
			 snippetContent = docFile.substring( posIndexes.get(i) - 100 , posIndexes.get(i) + 100 );
		 }
		
		
		for(int j = 0 ; j < userQueryTerms.size() ; j++)
		{
			String[] qterm = userQueryTerms.get(j);
			
			if(snippetContent.matches(".*[\\s]"+qterm[1]+".*"))
			{
				rating++;
			}
			
		}
		if(rating >= bestRating)
		{
			bestRating = rating;
			bestSnippet = snippetContent;
		}
	 }
	 
	 String[] snippet = new String[2];
	 snippet[0] = snippetTitle;
	 snippet[1] = bestSnippet;
	 
	 return snippet;
	}
	
	// TFIDF  - Vector Space Model
	public  Map<String, Double> computeVSMScores( List<String[]> userQueryTerms , String userQuery, boolean isPhraseQuery, Map <String, Map<String ,  ArrayList<Integer>>> unrankedResult )
	{
		//For Document Vector
		
				if (unrankedResult != null && unrankedResult.size() >0)
				{
				Map<String, Double> normalizationForD = new HashMap<String, Double>();
				Map<String , Map<String, Integer>> forwardIndex  = null;
				
				
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
					if(reader == null || reader.getType().toString().toLowerCase() != qTerm[0].toLowerCase() )
						reader = new IndexReader(indexDir , IndexType.valueOf(qTerm[0].toUpperCase()));
					
					int N = reader.getTotalValueTerms();
					Map<String, Map<String, ArrayList<Integer>>> postings = new HashMap<String, Map<String,ArrayList<Integer>>>();;
					if(isPhraseQuery)
					{
						postings = processPhraseQuery(userQuery, qTerm, reader);
					}
					else
					{
						 postings = reader.getPostingsWithPosIndexes(qTerm[1]);

					}
					int dfTerm = 1 ;
					if(postings!=null)
						dfTerm = postings.size();
					
					weightTerminQ[i] = (tfqTerm) * (Math.log(N/dfTerm)); //For Query Vector:  tf, idf weighting (log (N/df))  , cosine normalization
					
					 forwardIndex = reader.readForwardIndex();

					//For Document Vector :  tf , no idf, cosine normalization
					for(Map.Entry<String, Map<String, ArrayList<Integer>>> entry : unrankedResult.entrySet())
					{
						String fileID = entry.getKey();
						Map <String , ArrayList<Integer>> docResult = entry.getValue();
						double wfTD = 0.0;
						
						
						if(docResult.containsKey(qTerm[1]))
						{
							 wfTD = wfTD +   forwardIndex.get(fileID).get(qTerm[1]);
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
				
				//Normalization factors for each of document 
				for (Map.Entry<String , Map <String , ArrayList<Integer>>> entry : unrankedResult.entrySet())
				{
					String fileID = entry.getKey();
					
					Map<String, Integer> termsInDoc = forwardIndex.get(fileID);
					
					Double sumSquares = 0.0 ;
					
					for (Map.Entry<String, Integer> terms : termsInDoc.entrySet())
					{
						sumSquares = sumSquares + Math.pow(terms.getValue() , 2);
					}
					normalizationForD.put(entry.getKey(), Math.sqrt(sumSquares ));
					
				}
				Double normalizationQ = 0.0;
			
				for(int i = 0; i< weightTerminQ.length ; i++)
				{
					normalizationQ = normalizationQ + Math.pow(weightTerminQ[i],2);
				}
				
				normalizationQ = Math.sqrt(normalizationQ);
				
				for(Map.Entry<String, Double> entry : scores.entrySet())
				{
					Double finalScore = entry.getValue() / (normalizationForD.get(entry.getKey()) * normalizationQ )  ;
					scores.put(entry.getKey(), finalScore);
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

				 HashMap<String, Double> rankedResult = new LinkedHashMap<String, Double>();
				for (Map.Entry<String, Double> entry : unrankedOutput) {
					rankedResult.put(entry.getKey(), entry.getValue());
				}
				
				return rankedResult;
				}
				else
				{
					return null;
				}
	}
	
	public  Map<String, Double> computeOKAPIScores( List<String[]> userQueryTerms , String userQuery,boolean isPhraseQuery, Map <String, Map<String ,  ArrayList<Integer>>> unrankedResult )
	{
		if(unrankedResult!= null && unrankedResult.size() > 0 )
		{
			Map<String , Map<String, Integer>> forwardIndex  = null;
		HashMap<String, Double> rankedResult = new LinkedHashMap<String, Double>();
		Map<String, Double> scores = new HashMap<String, Double>(); // fileID, scores
		double oKapiK1 = 1.2; // document term frequency scaling calibration
		double oKapiK3 = 1.2; // query term frequency calibration - Use for long queries
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
				if(reader == null || reader.getType().toString().toLowerCase() != qTerm[0].toLowerCase() )
					reader = new IndexReader(indexDir , IndexType.valueOf(qTerm[0].toUpperCase()));
				
				int N = reader.getTotalValueTerms();
				Map<String, Map<String, ArrayList<Integer>>> postings = new HashMap<String, Map<String,ArrayList<Integer>>>();
				if(isPhraseQuery)
				{
					 postings = processPhraseQuery(userQuery, qTerm, reader);
				}
				else
				{
					postings = reader.getPostingsWithPosIndexes(qTerm[1]);
				}
				int dfTerm = postings.size();
				Double iDFt = Math.log(N/dfTerm);
				Double avgDocLength = reader.getAverageDocumentLength();
				//Map<String , Map <String , Integer>> forwardIndex = reader.readForwardIndex();
				forwardIndex = reader.readForwardIndex();
				for(Map.Entry<String, Map<String, ArrayList<Integer>>> entry : unrankedResult.entrySet())
				{
					String fileID = entry.getKey();
					Map <String , ArrayList<Integer>> docResult = entry.getValue();
					double tfTD = 0.0;
					
					
					if(docResult.containsKey(qTerm[1]))
					{
						tfTD = forwardIndex.get(fileID).get(qTerm[1]);
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
		else
		{
			return null;
		}
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
			if(string[0].toLowerCase().trim().equals("term"))
				 analyzer = fact.getAnalyzerForField(FieldNames.CONTENT, stream);
			else if(string[0].toLowerCase().trim().equals("author"))
				 analyzer = fact.getAnalyzerForField(FieldNames.AUTHOR, stream);
			else if(string[0].toLowerCase().trim().equals("category"))
				 analyzer = fact.getAnalyzerForField(FieldNames.CATEGORY, stream);
			else if(string[0].toLowerCase().trim().equals("place"))
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
				string[1] = stream.next().toString();
				while(stream.hasNext())
					string[1] = string[1] +" "+ stream.next().toString()  ;	
			}
			string[1].trim();
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
		String line = "";
		boolean firstLine = true;
		String[] temp;
		String numQueries = "" ;
		if(queryFile != null)
		{
			Map<String , String > idsQuery = new HashMap<String, String>() ;
			try {
				BufferedReader br = new BufferedReader(new FileReader(queryFile));
				while ((line = br.readLine()) != null) {
					if(firstLine)
					{
						temp = line.split("=");
						numQueries = temp[1];
						firstLine = false;
					}
					else
					{
					 	int j = line.indexOf(":");
					 	String queryId = line.substring(0 , j);
					 	String query = line.substring(j+1 , line.length());
					 	query = query.replace("{", "").replace("}", "");
					 	idsQuery.put(queryId, query);
					}
					
					
				}
				br.close();
			}
				catch (IOException e) {
					
					e.printStackTrace();
				}
			
				this.stream.println("numResults="+numQueries);

				for(Map.Entry<String, String> query : idsQuery.entrySet())
				{
					
					userQueryTerms = null;
					isPhraseQuery = false;
					ScoringModel model;
					if(query.getValue() == null || query.getValue().isEmpty())
					{
						this.stream.print("Invalid Input");
					}
					else
					{
					Long startTime = System.currentTimeMillis();
					Map<String, Map<String ,  ArrayList<Integer>>> result = evaluateQuery(query.getValue());
					Map<String, Double> rankedResult = new HashMap<String, Double>();
					model = ScoringModel.TFIDF;
					switch(model)
					{
						case TFIDF:
						{
							if(result != null)
							{
								rankedResult = computeVSMScores(userQueryTerms, query.getValue(), isPhraseQuery, result);
							}
							else
								rankedResult = null;
							break;
						}
						case OKAPI:
						{
							if(result != null)
							{
								rankedResult = computeOKAPIScores(userQueryTerms, query.getValue(), isPhraseQuery, result);
							}
							else
								rankedResult = null;
							break;
						}

					}
					this.stream.print(query.getKey()+":{");
					int count = 1;
					for(Map.Entry<String, Double> fileRes : rankedResult.entrySet())
					{
						DecimalFormat f = new DecimalFormat("0.00000");
						this.stream.print(fileRes.getKey()+"#"+f.format(fileRes.getValue()));
						if(count != 10 && count != rankedResult.size()  )
							this.stream.print(", ");
						
						count = count + 1;
						
						if(count > 10)
							break;
					}
					this.stream.print("}");
					this.stream.println();
					
					
					
				}
			}
				
				
			} 
		else
		{
			this.stream.println("No Input File provided");
		}
		
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
