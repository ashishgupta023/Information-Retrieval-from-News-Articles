/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author nikhillo
 * Class that emulates reading data back from a written index
 */
public class IndexReader {
	
	IndexType type;
	String indexDir;
	
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
	}
	
	/**
	 * Get total number of terms from the "key" dictionary associated with this 
	 * index. A postings list is always created against the "key" dictionary
	 * @return The total number of terms
	 */
	public int getTotalKeyTerms() {
		//TODO : YOU MUST IMPLEMENT THIS
		if(this.type == IndexType.TERM)
			return IndexWriter.termIndex.getNumTerms();
		else if (this.type == IndexType.AUTHOR)
			return IndexWriter.authorIndex.getNumTerms();
		else if(this.type == IndexType.CATEGORY)
			return IndexWriter.authorIndex.getNumTerms();
		else if(this.type == IndexType.PLACE)
			return IndexWriter.placeIndex.getNumTerms();
		else return -1;
	}
	
	/**
	 * Get total number of terms from the "value" dictionary associated with this 
	 * index. A postings list is always created with the "value" dictionary
	 * @return The total number of terms
	 */
	public int getTotalValueTerms() {
		//TODO: YOU MUST IMPLEMENT THIS
		
		if(this.type == IndexType.TERM || this.type == IndexType.AUTHOR || this.type == IndexType.PLACE || this.type == IndexType.CATEGORY)
			return FieldDictionary.dict.size();
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
		
		if (this.type == IndexType.AUTHOR)
				return IndexWriter.authorIndex.get(term);
		else if (this.type == IndexType.CATEGORY)
				return IndexWriter.categoryIndex.get(term);
		else if (this.type == IndexType.PLACE)
				return IndexWriter.placeIndex.get(term);
		else if (this.type == IndexType.TERM)
			return IndexWriter.termIndex.get(term);
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
		
		if (this.type == IndexType.AUTHOR)
			return IndexWriter.authorIndex.getTopK(k);
		else if (this.type == IndexType.CATEGORY)
				return IndexWriter.categoryIndex.getTopK(k);
		else if (this.type == IndexType.PLACE)
				return IndexWriter.placeIndex.getTopK(k);
		else if (this.type == IndexType.TERM)
			return IndexWriter.termIndex.getTopK(k);
		else return null;	
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
	public Map<String, Integer> query(String...terms) 
	{
		//TODO : BONUS ONLY
		Map <Integer, Integer> result = new HashMap<Integer, Integer>();
		
 		if (this.type == IndexType.AUTHOR)
		{
			
				//sorted docID & term frequency
				Map<Integer, Integer> temp1 = IndexWriter.authorIndex.getFileIds(terms[0]);
				Map<Integer, Integer> temp2 = IndexWriter.authorIndex.getFileIds(terms[1]);
				Set<Integer> temp1Keys = temp1.keySet();
				Set<Integer> temp2Keys = temp2.keySet();
				Iterator itr1 = temp1Keys.iterator();
				Iterator itr2 = temp2Keys.iterator();
				if(temp1Keys.size() > temp2Keys.size())
				{
					while(itr2.hasNext())
					{
						Integer val1 = (Integer) itr1.next();
						Integer val2 = (Integer) itr2.next();
						if(val1 == val2)
						{
							result.put(val2, temp2.get(val2));
						}
					}
				}
				else if(temp1Keys.size() < temp2Keys.size())
				{
					while(itr1.hasNext())
					{
						Integer val1 = (Integer) itr1.next();
						Integer val2 = (Integer) itr2.next();
						if(val1 == val2)
						{
							result.put(val2, temp2.get(val2));
						}	
					}
				}
				
			
				
		}
		/*else if (this.type == IndexType.CATEGORY)
			return IndexWriter.categoryIndex.get(term);
		else if (this.type == IndexType.PLACE)
			return IndexWriter.placeIndex.get(term);
		else if (this.type == IndexType.TERM)
		return IndexWriter.termIndex.get(term);
			else return null;*/
		return null;
	}
}
