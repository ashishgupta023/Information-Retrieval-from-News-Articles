package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.corba.se.spi.ior.Writeable;


class Index implements Serializable {
	private HashMap<String, PostingList> termMap = new HashMap<String, PostingList>();
	public IndexType indexType;
	
	public Index(IndexType indexType) {
		this.indexType = indexType;
	}


	class keyCollectFrq {
		int collectionFrq;
		String key;
	}

	public int getNumTerms() {
		return termMap.size();
	}
	
	public List<String> getTopK(int k) {
		List <String> topKList =null ;
		
		
		if(k>0)
		{
		topKList = new ArrayList<String>();
		
		/* Implementing Comparator */
		class keyCollectFrqComparable implements Comparator<keyCollectFrq>{
		     @Override
		    public int compare(keyCollectFrq list1, keyCollectFrq list2) {
		        return (list1.collectionFrq>list2.collectionFrq ? -1 : (list1.collectionFrq==list2.collectionFrq ? 0 : 1));
		    }
		} 

		List<keyCollectFrq> listKeyCollectionFrq= new ArrayList<keyCollectFrq>();
		
		//List<MyObject> list = new ArrayList<MyObject>();
		for(String key : termMap.keySet()) {
			PostingList list = termMap.get(key);
			keyCollectFrq keyCollectFrqObj = new keyCollectFrq();
			keyCollectFrqObj.collectionFrq = list.getCollectionFrq();
			keyCollectFrqObj.key = key;
			listKeyCollectionFrq.add(keyCollectFrqObj);
		}
		Collections.sort(listKeyCollectionFrq, new keyCollectFrqComparable());
		if(k> listKeyCollectionFrq.size() )
			k=listKeyCollectionFrq.size();
		for(int i = 0; i<k; i++) {
			topKList.add(listKeyCollectionFrq.get(i).key);
			}
		}
		
		return topKList;

	}

	
	
	public boolean put(String term, Integer docId) {
		/* Checking if term is previously present or not. */
		

		if(!this.termMap.containsKey(term)) {
			PostingList postingValue = new PostingList();
			postingValue.insert(docId);
			put(term, postingValue);	// inserting Key if it is not present		
			return true;
		}
		PostingList storedValue = this.termMap.get(term);	// Appending Into a Posting List
		storedValue.insert(docId);
		return true;
	}
	
	private boolean put(String term, PostingList value) {
		/* Checking if term is previously present or not. */
		if(!this.termMap.containsKey(term)) {
			termMap.put(term, value);	// inserting Key if it is not present
			return true;
		}
		else return false;
	}

	public Map<String, Integer> get(String term, FieldDictionary dict) {
		/* Returns Value if Key exists in our HashMap else it returns null. */
		if(!this.termMap.containsKey(term)){
			return null;
		}
		
		PostingList value = termMap.get(term);
		return value.getPostingList(dict);
	}
	
	public Map<Integer, Integer> getFileIds(String term) {
		// Returns Value if Key exists in our HashMap else it returns null. 
		if(!this.termMap.containsKey(term)){
			return null;
		}
		
		PostingList value = termMap.get(term);
		Map<Integer , Integer> map = value.getPostingListWithdocIDs();
		return map;
	}
	
	

	public int sortAndAggregate() {
		for (String key : this.termMap.keySet()) {

			sortAndAggregate(key);
		}
		return 0;
	}
	
	private int sortAndAggregate(String term) {
		this.sortPostingList(term);
		int length = this.aggregatePostingList(term);
		return length;
	}
	
	private boolean sortPostingList(String term) {
		if(!this.termMap.containsKey(term)){
			return false;
		}
		PostingList storedValue = termMap.get(term);
		storedValue.sortPostingList();
		return true;
	}
	
	private int aggregatePostingList(String term) {
		if(!this.termMap.containsKey(term)){
			return 0;
		}
		PostingList storedValue = termMap.get(term);
		int length = storedValue.aggregatePostingList();
		return length;
	}
	
	
	}
	
	
