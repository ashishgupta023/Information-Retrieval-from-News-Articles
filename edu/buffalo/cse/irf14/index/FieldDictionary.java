package edu.buffalo.cse.irf14.index;

import java.util.HashMap;

class FieldDictionary {
	static int counter = 0;
	static HashMap<Integer, String> dict = new HashMap<Integer, String>();
	
	static int insert (String value) {
		int key = ++counter;
		dict.put(key, value);
		return key;
	}

	static void insert (Integer key, String value) {
		dict.put(key, value);
	}

	static boolean containsKey(Integer key) {
		return dict.containsKey(key);
	}
	
	static String get (Integer key) {
		return dict.get(key);
	}
}
