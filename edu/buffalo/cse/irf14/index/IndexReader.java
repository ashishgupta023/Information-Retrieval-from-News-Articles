/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
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

/**
 * @author nikhillo
 * Class that emulates reading data back from a written index
 */
public class IndexReader {
	
	IndexType type;
	String indexDir;
	private static Index termIndex;
	private static Index authorIndex;
	private static Index categoryIndex;
	private static Index placeIndex;
	private static Map<String , Map<String , Integer>> forwardIndex;
	private static DocumentDictionary  dictionary;
	FileInputStream fileInputStream;
	ObjectInputStream objectInputStream;
	
	
	/**
	 * Default constructor
	 * @param indexDir : The root directory from which the index is to be read.
	 * This will be exactly the same directory as passed on IndexWriter. In case 
	 * you make subdirectories etc., you will have to handle it accordingly.
	 * @param type The {@link IndexType} to read from
	 */
	public IndexReader(String indexDir, IndexType type) {
		//TODO
		this.type = type;
		this.indexDir = indexDir;
		this.fileInputStream = null;
		this.objectInputStream = null;
		termIndex = null;
		categoryIndex = null;
		placeIndex = null;
		authorIndex = null;
		forwardIndex = null;
		dictionary = null;
	}
	
	
	public IndexType getType(){
		return type;
	}
	
	private Index readIndex(IndexType type)
	{
		try {
			
			fileInputStream = new FileInputStream(this.indexDir+ File.separator + type.toString() + File.separator + type.toString() );
			objectInputStream = new ObjectInputStream(new BufferedInputStream(fileInputStream));
			Index tempIndex = (Index)objectInputStream.readObject();
			objectInputStream.close();
			fileInputStream.close();

			return tempIndex;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IOException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public Map<String , Map<String, Integer>> readForwardIndex()
	{
		if(forwardIndex == null)
		{
			try {
				fileInputStream = new FileInputStream(this.indexDir+ File.separator + "forwardIndex" );
				objectInputStream = new ObjectInputStream(new BufferedInputStream(fileInputStream));
				Map<String , Map<String, Integer>> forwardIndex = (Map<String , Map<String, Integer>>)objectInputStream.readObject();
				objectInputStream.close();
				fileInputStream.close();
	
				return forwardIndex;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(IOException e)
			{
				e.printStackTrace();
			} catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			return forwardIndex;
		}
		return null;
	}
	
	private DocumentDictionary readDictionary()
	{
		try {
			fileInputStream = new FileInputStream(this.indexDir+ File.separator + "dictionary" );
			objectInputStream = new ObjectInputStream(new BufferedInputStream(fileInputStream));
			DocumentDictionary tempDict = (DocumentDictionary)objectInputStream.readObject();
			objectInputStream.close();
			fileInputStream.close();

			return tempDict;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IOException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Double getAverageDocumentLength()
	{
		if(dictionary == null)
			dictionary = readDictionary();
		Double avgDocLength = 0.0;
		for(Map.Entry<Integer, HashMap<String, Integer>> entry: dictionary.dict.entrySet())
		{
			HashMap<String, Integer> val = entry.getValue();
			for(Map.Entry<String, Integer> docEntry : val.entrySet())
			{
				avgDocLength = avgDocLength + docEntry.getValue();
			}
		}
		
		avgDocLength = avgDocLength / dictionary.dict.size();
		
		
		return avgDocLength;
	}
	
	public Integer getDocumentLength(String docID)
	{
		if(dictionary == null)
			dictionary = readDictionary();
		Integer docLength = 0;
		for(Map.Entry<Integer, HashMap<String, Integer>> entry: dictionary.dict.entrySet())
		{
			HashMap<String, Integer> val = entry.getValue();
			for(Map.Entry<String, Integer> docEntry : val.entrySet())
			{
				if (docEntry.getKey().equals(docID))
				{
					docLength  = docEntry.getValue();
				}
			}
		}
		return docLength;
	}
	
	/**
	 * Get total number of terms from the "key" dictionary associated with this 
	 * index. A postings list is always created against the "key" dictionary
	 * @return The total number of terms
	 */
	public int getTotalKeyTerms() {
		//TODO : YOU MUST IMPLEMENT THIS
		
		
		if(this.type == IndexType.TERM)
		{
			if(termIndex == null)
				termIndex = readIndex(IndexType.TERM);
			return termIndex.getNumTerms();
		}
		else if (this.type == IndexType.AUTHOR)
		{
			if(authorIndex == null)
				authorIndex = readIndex(IndexType.AUTHOR);
			return authorIndex.getNumTerms();
		}
		else if(this.type == IndexType.CATEGORY)
		{
			if(categoryIndex ==  null)
				categoryIndex = readIndex(IndexType.CATEGORY);
			return categoryIndex.getNumTerms();
		}
		else if(this.type == IndexType.PLACE)
		{
			if( placeIndex == null )
				placeIndex = readIndex(IndexType.PLACE);
			return placeIndex.getNumTerms();
		}
		else return -1;
	}
	
	/**
	 * Get total number of terms from the "value" dictionary associated with this 
	 * index. A postings list is always created with the "value" dictionary
	 * @return The total number of terms
	 */
	public int getTotalValueTerms() {
		//TODO: YOU MUST IMPLEMENT THIS
		if(dictionary == null)
			dictionary = readDictionary();
		if(this.type == IndexType.TERM || this.type == IndexType.AUTHOR || this.type == IndexType.PLACE || this.type == IndexType.CATEGORY)
			return dictionary.dict.size();
		else return -1;
	}
	
	public Map<String, Map <String , ArrayList<Integer>>> getPostingsWithPosIndexes(String term) {
		//TODO:YOU MUST IMPLEMENT THIS
		if(dictionary == null)
			dictionary = readDictionary();
		Map<String , ArrayList<Integer>> result = new HashMap<String, ArrayList<Integer>>();
		if(this.type == IndexType.TERM)
		{
			if(termIndex == null)
				termIndex = readIndex(IndexType.TERM);
			result = termIndex.get(term,dictionary);
		}
		else if (this.type == IndexType.AUTHOR)
		{
			if(authorIndex == null)
				authorIndex = readIndex(IndexType.AUTHOR);
			result = authorIndex.get(term,dictionary);
		}
		else if(this.type == IndexType.CATEGORY)
		{
			if(categoryIndex == null)
				categoryIndex = readIndex(IndexType.CATEGORY);
			result =  categoryIndex.get(term,dictionary);
		}
		else if(this.type == IndexType.PLACE)
		{
			if(placeIndex == null)
				placeIndex = readIndex(IndexType.PLACE);
			result = placeIndex.get(term,dictionary);
		}

		Map<String, Map<String , ArrayList<Integer>>> finaResult = new HashMap<String, Map<String,ArrayList<Integer>>>();
		if(result != null)
		{
			for(Map.Entry<String , ArrayList<Integer>> entry : result.entrySet())
			{
				String key = entry.getKey();
				Map<String , ArrayList<Integer>> docResult = new HashMap<String, ArrayList<Integer>>();
				docResult.put(term, entry.getValue());
				finaResult.put(key, docResult);
			}
		}
		else
		{
			finaResult = null;
		}
		
		return finaResult;
		
	}
	
	/*
	*//**
	 * Method to get the postings for a given term. You can assume that
	 * the raw string that is used to query would be passed through the same
	 * Analyzer as the original field would have been.
	 * @param term : The "analyzed" term to get postings for
	 * @return A Map containing the corresponding fileid as the key and the 
	 * number of occurrences as values if the given term was found, null otherwise.
	 *//*
	public Map<String, Integer> getPostings(String term) {
		//TODO:YOU MUST IMPLEMENT THIS
		 dictionary = readDictionary();
		Map<String, Map <Integer , ArrayList<Integer>>> result = new HashMap<String, Map<Integer,ArrayList<Integer>>>();
		if(this.type == IndexType.TERM)
		{
			termIndex = readIndex(IndexType.TERM);
			result = getPostingsWithPosIndexes(term);
		}
		else if (this.type == IndexType.AUTHOR)
		{
			authorIndex = readIndex(IndexType.AUTHOR);
			result = getPostingsWithPosIndexes(term);
		}
		else if(this.type == IndexType.CATEGORY)
		{
			categoryIndex = readIndex(IndexType.CATEGORY);
			result =  getPostingsWithPosIndexes(term);
		}
		else if(this.type == IndexType.PLACE)
		{
			placeIndex = readIndex(IndexType.PLACE);
			result = getPostingsWithPosIndexes(term);
		}

		Map <String, Integer > finalResult = new HashMap<String, Integer>();
		
		for(Map.Entry<String, Map<Integer,ArrayList<Integer>>> temp1Entry : result.entrySet() )
		{
			String fileID = temp1Entry.getKey();
			Integer numOccurences = -1;
			Map<Integer,ArrayList<Integer>> value = temp1Entry.getValue();
			for(Map.Entry<Integer, ArrayList<Integer>> valueEntry : value.entrySet())
			{
				numOccurences = valueEntry.getKey();
			}
			finalResult.put(fileID, numOccurences);
		}
			return finalResult;
		
	}
	*/
	/**
	 * Method to get the top k terms from the index in terms of the total number
	 * of occurrences.
	 * @param k : The number of terms to fetch
	 * @return : An ordered list of results. Must be <=k fr valid k values
	 * null for invalid k values
	 */
	public List<String> getTopK(int k) {
		//TODO YOU MUST IMPLEMENT THIS
		if(this.type == IndexType.TERM)
		{
			if(termIndex == null)
				termIndex = readIndex(IndexType.TERM);
			return termIndex.getTopK(k);
		}
		else if (this.type == IndexType.AUTHOR)
		{
			if(authorIndex == null)
				authorIndex = readIndex(IndexType.AUTHOR);
			return authorIndex.getTopK(k);
		}
		else if(this.type == IndexType.CATEGORY)
		{
			if(categoryIndex == null)
				categoryIndex = readIndex(IndexType.CATEGORY);
			return categoryIndex.getTopK(k);
		}
		else if(this.type == IndexType.PLACE)
		{
			if(placeIndex == null)
				placeIndex = readIndex(IndexType.PLACE);
			return placeIndex.getTopK(k);
		}
			else return null;	
		}
	
	
	public Map<String, Map<String ,  ArrayList<Integer>>>  intersectPostings (Map<String, Map<String ,  ArrayList<Integer>>> temp1 ,Map<String, Map<String ,  ArrayList<Integer>>> temp2)
	{
		Map<String, Map<String ,  ArrayList<Integer>>> tempResult = new HashMap<String, Map<String,ArrayList<Integer>>>();

		if(temp1 != null && temp2 != null)
		{
		
		for (Map.Entry<String, Map<String ,  ArrayList<Integer>>> temp1Entry : temp1.entrySet()) {
			for (Map.Entry<String, Map<String ,  ArrayList<Integer>>> temp2Entry : temp2.entrySet()) {
				if (temp1Entry.getKey().equals(temp2Entry.getKey())) {
					
					Map <String , ArrayList<Integer>> termDetails = new HashMap<String, ArrayList<Integer>>();
					
					termDetails.putAll(temp1Entry.getValue());
					termDetails.putAll(temp2Entry.getValue());
					tempResult.put(temp1Entry.getKey(), termDetails);
				}
			}
		}
		}
		return tempResult;

	}
	
	public Map<String,  Map<String ,  ArrayList<Integer>>>  unionPostings (Map<String,  Map<String ,  ArrayList<Integer>>> temp1 ,Map<String,  Map<String ,  ArrayList<Integer>>> temp2)
	{
		Map <String,  Map<String ,  ArrayList<Integer>>> tempResult = new HashMap<String,  Map<String ,  ArrayList<Integer>>>();
		
		if(temp1 != null && temp2 != null && temp1.size() > 0 && temp2.size() > 0 )
		{
		
			for (Map.Entry<String,  Map<String ,  ArrayList<Integer>>> temp1Entry : temp1.entrySet()) {
				for (Map.Entry<String,  Map<String ,  ArrayList<Integer>>> temp2Entry : temp2.entrySet()) {
					if (temp1Entry.getKey().equals(temp2Entry.getKey())) {
						Map <String , ArrayList<Integer>> termDetails = new HashMap<String, ArrayList<Integer>>();
						termDetails.putAll(temp1Entry.getValue());
						termDetails.putAll(temp2Entry.getValue());
						tempResult.put(temp1Entry.getKey(), termDetails);
					}
				}
			}
		}
		
		if(temp1 != null && temp1.size() > 0)
		{
			for (Map.Entry<String, Map<String ,  ArrayList<Integer>>> temp1Entry : temp1.entrySet()) {
					if (!tempResult.containsKey(temp1Entry.getKey())) {
						tempResult.put(temp1Entry.getKey(),
								temp1Entry.getValue());
					}
				
			}
		}
		
		if( temp2 != null && temp2.size() > 0)
		{
		
		for (Map.Entry<String, Map<String ,  ArrayList<Integer>>> temp2Entry : temp2.entrySet()) {
			if (!tempResult.containsKey(temp2Entry.getKey())) {
				
					tempResult.put(temp2Entry.getKey(),
							temp2Entry.getValue());
				}
			
		}
		}
		
		return tempResult;

	}
	

	public Map<String,  Map<String ,  ArrayList<Integer>>>  notPostings (Map<String,  Map<String ,  ArrayList<Integer>>> temp1 ,Map<String,  Map<String ,  ArrayList<Integer>>> temp2)
	{
		Map <String,  Map<String ,  ArrayList<Integer>>> tempResult = new HashMap<String,  Map<String ,  ArrayList<Integer>>>();		
		for (Map.Entry<String,  Map<String ,  ArrayList<Integer>>> temp1Entry : temp1.entrySet()) {
			for (Map.Entry<String,  Map<String ,  ArrayList<Integer>>> temp2Entry : temp2.entrySet()) {
				if (!temp1Entry.getKey().equals(temp2Entry.getKey())) {
					tempResult.put(temp1Entry.getKey(),
							temp1Entry.getValue() );
				}
			}
		}
		
		return tempResult;

	}
	
	
	/**
	 * Method to implement a simple boolean AND query on the given index
	 * @param terms The ordered set of terms to AND, similar to getPostings()
	 * the terms would be passed through the necessary Analyzer.
	 * @return A Map (if all terms are found) containing FileId as the key 
	 * and number of occurrences as the value, the number of occurrences 
	 * would be the sum of occurrences for each participating term. return null
	 * if the given term list returns no results
	 * BONUS ONLY
	 */
	public Map<String, Map<String , ArrayList<Integer> >> query(String...terms ) 
	{
		//TODO : BONUS ONLY
		Map<String,  Map<String , ArrayList<Integer> >> result = new HashMap<String, Map<String,ArrayList<Integer>>>();
		
		ArrayList<Map<String, Map<String , ArrayList<Integer> >>> termsPostingListsArray = new ArrayList<Map<String,Map<String,ArrayList<Integer>>>>();
		for (int i = 0; i < terms.length; i++) {
			termsPostingListsArray.add(getPostingsWithPosIndexes(terms[i]));
		}
		
		if(terms.length == 1)
			return termsPostingListsArray.get(0);
		else if(terms.length  > 1)
		{
			 result = intersectPostings(
					termsPostingListsArray.get(0), termsPostingListsArray.get(1));
			for (int i = 2; i < terms.length; i++) {
				result = intersectPostings(result, termsPostingListsArray.get(i));
			}
			
			
		}
		return result;
	}
	
	public Map<String,  Map<String , ArrayList<Integer> >> orQuery(String...terms ) 
	{
		//TODO : BONUS ONLY
		Map<String,  Map<String , ArrayList<Integer> >> result = new HashMap<String, Map<String,ArrayList<Integer>>>();
		
		ArrayList<Map<String, Map<String , ArrayList<Integer> >>> termsPostingListsArray = new ArrayList<Map<String,Map<String,ArrayList<Integer>>>>();
		for (int i = 0; i < terms.length; i++) {
			termsPostingListsArray.add(getPostingsWithPosIndexes(terms[i]));
		}
		
		if(terms.length == 1)
			return termsPostingListsArray.get(0);
		else if(terms.length  > 1)
		{
			 result = unionPostings(
					termsPostingListsArray.get(0), termsPostingListsArray.get(1));
			for (int i = 2; i < terms.length; i++) {
				result = unionPostings(result, termsPostingListsArray.get(i));
			}
			
			
			
		}
		return result;
	}
}
