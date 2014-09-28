package edu.buffalo.cse.irf14.analysis;

import edu.buffalo.cse.irf14.document.FieldNames;


public class NewsDateAnalyzer implements Analyzer {
	
	TokenStream stream;
	TokenFilterFactory tokenFilterFactory;
	TokenFilter filter ;
	
	

	
	 public NewsDateAnalyzer(TokenStream stream) {
		 this.stream = stream;
		 this.tokenFilterFactory = TokenFilterFactory.getInstance();
		 this.filter = null;
		 
	}

	@Override
	public boolean increment() throws TokenizerException {
		
		 this.filter = this.tokenFilterFactory.getFilterByType(TokenFilterType.DATE, this.stream);

		if(this.stream != null)
		{
			while(this.filter.increment())
			{
				
			}
		}

		 this.stream.reset();

		
		return false;
	}

	@Override
	public TokenStream getStream() {
		
		return this.stream;
	}
	
	
	

}
