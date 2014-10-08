/**
 * 
 */
package edu.buffalo.cse.irf14.index;

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
	private static FieldDictionary  dict;
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
		
		
	}
	
	private Index readIndex(IndexType type)
	{
		try {
			fileInputStream = new FileInputStream(this.indexDir+ File.separator + type.toString() + File.separator + type.toString() );
			objectInputStream = new ObjectInputStream(fileInputStream);
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
	
	private FieldDictionary readDictionary()
	{
		try {
			fileInputStream = new FileInputStream(this.indexDir+ File.separator + "dictionary" );
			objectInputStream = new ObjectInputStream(fileInputStream);
			FieldDictionary tempDict = (FieldDictionary)objectInputStream.readObject();
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
	
	/**
	 * Get total number of terms from the "key" dictionary associated with this 
	 * index. A postings list is always created against the "key" dictionary
	 * @return The total number of terms
	 */
	public int getTotalKeyTerms() {
		//TODO : YOU MUST IMPLEMENT THIS
		
		
		if(this.type == IndexType.TERM)
		{
			termIndex = readIndex(IndexType.TERM);
			return termIndex.getNumTerms();
		}
		else if (this.type == IndexType.AUTHOR)
		{
			authorIndex = readIndex(IndexType.AUTHOR);
			return authorIndex.getNumTerms();
		}
		else if(this.type == IndexType.CATEGORY)
		{
			categoryIndex = readIndex(IndexType.CATEGORY);
			return categoryIndex.getNumTerms();
		}
		else if(this.type == IndexType.PLACE)
		{
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
		FieldDictionary dictionary = readDictionary();
		if(this.type == IndexType.TERM || this.type == IndexType.AUTHOR || this.type == IndexType.PLACE || this.type == IndexType.CATEGORY)
			return dictionary.dict.size();
		else return -1;
	}
	
	/**
	 * Method to get the postings for a given term. You can assume that
	 * the raw string that is used to query would be passed through the same
	 * Analyzer as the original field would have been.
	 * @param term : The "analyzed" term to get postings for
	 * @return A Map containing the corresponding fileid as the key and the 
	 * number of occurrences as values if the given term was found, null otherwise.
	 */
	public Map<String, Integer> getPostings(String term) {
		//TODO:YOU MUST IMPLEMENT THIS
		FieldDictionary dictionary = readDictionary();

		if(this.type == IndexType.TERM)
		{
			termIndex = readIndex(IndexType.TERM);
			return termIndex.get(term,dictionary);
		}
		else if (this.type == IndexType.AUTHOR)
		{
			authorIndex = readIndex(IndexType.AUTHOR);
			return authorIndex.get(term,dictionary);
		}
		else if(this.type == IndexType.CATEGORY)
		{
			categoryIndex = readIndex(IndexType.CATEGORY);
			return categoryIndex.get(term,dictionary);
		}
		else if(this.type == IndexType.PLACE)
		{
			placeIndex = readIndex(IndexType.PLACE);
			return placeIndex.get(term,dictionary);
		}
		else return null;
		
	}
	
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
			termIndex = readIndex(IndexType.TERM);
			return termIndex.getTopK(k);
		}
		else if (this.type == IndexType.AUTHOR)
		{
			authorIndex = readIndex(IndexType.AUTHOR);
			return authorIndex.getTopK(k);
		}
		else if(this.type == IndexType.CATEGORY)
		{
			categoryIndex = readIndex(IndexType.CATEGORY);
			return categoryIndex.getTopK(k);
		}
		else if(this.type == IndexType.PLACE)
		{
			placeIndex = readIndex(IndexType.PLACE);
			return placeIndex.getTopK(k);
		}
			else return null;	
		}
	
	
	Map<String, Integer>  intersectPostings (Map<String, Integer> temp1 ,Map<String, Integer> temp2)
	{
		Map <String, Integer> tempResult = new HashMap<String, Integer>();
		
		for (Map.Entry<String, Integer> temp1Entry : temp1.entrySet()) {
			for (Map.Entry<String, Integer> temp2Entry : temp2.entrySet()) {
				if (temp1Entry.getKey().equals(temp2Entry.getKey())) {
					tempResult.put(temp1Entry.getKey(),
							temp1Entry.getValue() + temp2Entry.getValue());
				}
			}
		}
		return tempResult;

	}
	
	Map<String, Integer>  unionPostings (Map<String, Integer> temp1 ,Map<String, Integer> temp2)
	{
		Map <String, Integer> tempResult = new HashMap<String, Integer>();
		
		
		for (Map.Entry<String, Integer> temp1Entry : temp1.entrySet()) {
			for (Map.Entry<String, Integer> temp2Entry : temp2.entrySet()) {
				if (temp1Entry.getKey().equals(temp2Entry.getKey())) {
					tempResult.put(temp1Entry.getKey(),
							temp1Entry.getValue() + temp2Entry.getValue());
				}
			}
		}
		
		for (Map.Entry<String, Integer> temp1Entry : temp1.entrySet()) {
				if (!tempResult.containsKey(temp1Entry.getKey())) {
					tempResult.put(temp1Entry.getKey(),
							temp1Entry.getValue());
				}
			
		}
		
		for (Map.Entry<String, Integer> temp2Entry : temp2.entrySet()) {
			if (!tempResult.containsKey(temp2Entry.getKey())) {
				if (!temp2Entry.getKey().equals(temp2Entry.getKey())) {
					tempResult.put(temp2Entry.getKey(),
							temp2Entry.getValue());
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
	public Map<String, Integer> query(String...terms ) 
	{
		//TODO : BONUS ONLY
		HashMap<String, Integer> result = new LinkedHashMap<String, Integer>();
		
		ArrayList<Map<String, Integer>> termsPostingListsArray = new ArrayList<Map<String, Integer>>();
		for (int i = 0; i < terms.length; i++) {
			termsPostingListsArray.add(getPostings(terms[i]));
		}
		
		if(terms.length == 1)
			return termsPostingListsArray.get(0);
		else if(terms.length  > 1)
		{
			Map<String, Integer> tempMap = intersectPostings(
					termsPostingListsArray.get(0), termsPostingListsArray.get(1));
			for (int i = 2; i < terms.length; i++) {
				tempMap = intersectPostings(tempMap, termsPostingListsArray.get(i));
			}
			
			List<Entry<String, Integer>> unsortedEntries = new LinkedList<Map.Entry<String, Integer>>(
					tempMap.entrySet());
			
			Collections.sort(unsortedEntries, new Comparator<Entry<String, Integer>>() {

				@Override
				public int compare(Entry<String, Integer> val1,
						Entry<String, Integer> val2) {
					return val2.getValue().compareTo(val1.getValue());
				}
			});

			for (Map.Entry<String, Integer> entry : unsortedEntries) {
				result.put(entry.getKey(), entry.getValue());
			}
			
		}
		return result;
	}
	
	public Map<String, Integer> orQuery(String...terms ) 
	{
		//TODO : BONUS ONLY
		HashMap <String, Integer> result = new LinkedHashMap<String, Integer>();
		
		ArrayList<Map<String, Integer>> termsPostingListsArray = new ArrayList<Map<String, Integer>>();
		for (int i = 0; i < terms.length; i++) {
			termsPostingListsArray.add(getPostings(terms[i]));
		}
		
		if(terms.length == 1)
			return termsPostingListsArray.get(0);
		else if(terms.length  > 1)
		{
			Map<String, Integer> tempMap = unionPostings(
					termsPostingListsArray.get(0), termsPostingListsArray.get(1));
			for (int i = 2; i < terms.length; i++) {
				tempMap = unionPostings(tempMap, termsPostingListsArray.get(i));
			}
			
			List<Entry<String, Integer>> unsortedEntries = new LinkedList<Map.Entry<String, Integer>>(
					tempMap.entrySet());
			
			 Collections.sort(unsortedEntries, new Comparator<Entry<String, Integer>>() {

				@Override
				public int compare(Entry<String, Integer> val1,
						Entry<String, Integer> val2) {
					return val2.getValue().compareTo(val1.getValue());
				}
			});

			for (Map.Entry<String, Integer> entry : unsortedEntries) {
				result.put(entry.getKey(), entry.getValue());
			}
			
		}
		return result;
	}
}
