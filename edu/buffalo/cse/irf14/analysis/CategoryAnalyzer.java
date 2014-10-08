package edu.buffalo.cse.irf14.analysis;

import edu.buffalo.cse.irf14.document.FieldNames;


public class CategoryAnalyzer implements Analyzer {
	
	TokenStream stream;
	TokenFilterFactory tokenFilterFactory;
	TokenFilter filter ;
	
	

	
	 public CategoryAnalyzer(TokenStream stream) {
		 this.stream = stream;
		 this.tokenFilterFactory = TokenFilterFactory.getInstance();
		 this.filter = null;
		 
	}

	@Override
	public boolean increment() throws TokenizerException {
		
		

		return false;
	}

	@Override
	public TokenStream getStream() {
		
		return this.stream;
	}
	
	
	

}
