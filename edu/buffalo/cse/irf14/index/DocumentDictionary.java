package edu.buffalo.cse.irf14.index;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

class DocumentDictionary implements Serializable {
	 int counter = 0;
	 //HashMap<Integer, String> dict = new HashMap<Integer, String>();
	 // Doc ID to FILE ID mappings <DocID , < FILEID ,  document_length > >
	 HashMap<Integer, HashMap<String, Integer>>  dict  = new HashMap<Integer, HashMap<String, Integer>> ();
	
	 int insert (String fileID ) {
		int key = ++counter;
		HashMap<String, Integer> doc = new HashMap<String , Integer>();
		doc.put(fileID , 0);
		dict.put(key, doc);
		return key;
	}

	 void insert (Integer docID,  Integer length ) {
		 HashMap<String, Integer> doc = dict.get(docID);
		 doc.put(get(docID), length);
		 dict.put(docID, doc);
	 }

	 boolean containsKey(Integer key) {
		return dict.containsKey(key);
	}
	
	 String get (Integer key) {
		 HashMap<String, Integer> doc =  dict.get(key);
		 for (String fileID : doc.keySet())
		 {
			 return fileID;
		 }
		 return null;
	}
	 
	
}
