package edu.buffalo.cse.irf14.index;

import java.io.Serializable;
import java.util.HashMap;

class FieldDictionary implements Serializable {
	 int counter = 0;
	 HashMap<Integer, String> dict = new HashMap<Integer, String>();
	
	 int insert (String value) {
		int key = ++counter;
		dict.put(key, value);
		return key;
	}

	 void insert (Integer key, String value) {
		dict.put(key, value);
	}

	 boolean containsKey(Integer key) {
		return dict.containsKey(key);
	}
	
	 String get (Integer key) {
		return dict.get(key);
	}
}
