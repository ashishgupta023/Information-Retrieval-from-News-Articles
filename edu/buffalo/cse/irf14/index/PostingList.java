package edu.buffalo.cse.irf14.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostingList {
	private Boolean diskDumped;
	private Integer collectionFrq;
	private Integer documentFrq;
	private String fileToDump;
	private List<Posting> postingList = new ArrayList<Posting>();
	private List<Long> FileMarkers = new ArrayList<Long>();
	
	/**
	 * @return the fileMarkers
	 */
	public List<Long> getFileMarkers() {
		return FileMarkers;
	}

	/**
	 * @param filemarker the filemarker to set
	 */
	public void setFileMarkers(Long filemarker) {
		FileMarkers.add(filemarker);
	}

	PostingList() {
		diskDumped = false;
		collectionFrq = 0;
		documentFrq = 0;
		fileToDump = null;
	}
	
	void insert(Integer docId) {
		// appends Posting to end of Postings List..
		Posting post = new Posting();
		post.setDocId(docId);;
		postingList.add(post);
		collectionFrq++;
		}
	
	Map<String, Integer> getPostingList() {
		int numItems = postingList.size();
		Map<String, Integer> posts = new HashMap<String, Integer>();

		for(int i = 0; i < numItems; i++) {
			Posting post = postingList.get(i);
			// docId to fileId lookup
			String fileId = FieldDictionary.get(post.getDocId());
			posts.put(fileId, post.getTermFrq());
		}
		return posts;
	}
	
	int getPostingListSize() {
		return postingList.size();
	}
	
	
	boolean sortPostingList() {
		Collections.sort(postingList, new PostDocIdComparable());
		return true;
	}
	
	int aggregatePostingList() {
		if (postingList.size() < 2)
			return postingList.size();
	 
		int j = 0;
		int i = 1;
		
		while (i < postingList.size()) {
			if (postingList.get(i).getDocId() == postingList.get(j).getDocId()) {
				postingList.get(j).incrTermFrq();
				i++;
			} else {
				j++;
				postingList.set(j, postingList.get(i));
				i++;
			}
		}
		int length = j + 1;
		postingList = postingList.subList(0, length);
		this.documentFrq = length;
		return length;
	}
	
	public String postingListToString() {
		String postingListString = "";
		String docIdTermFrqDelim = "^";
		String postDelim = "\t";
		String listDelim = "\n";
		
		for (int i = 0; i < postingList.size(); i++) {
			String docString = postingList.get(i).getDocId().toString();
			String termFrqString = postingList.get(i).getTermFrq().toString();
			postingListString = postingListString + docString + docIdTermFrqDelim + termFrqString + postDelim;
		}
		postingListString = postingListString.trim();
		postingListString = postingListString + listDelim;
		return postingListString;
	}
	
	int getCollectionFrq() {
		return collectionFrq;
	}
	
	boolean isDskedDumped () {
		return diskDumped;
	}
}
