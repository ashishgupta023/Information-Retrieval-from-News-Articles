package edu.buffalo.cse.irf14.analysis;

import edu.buffalo.cse.irf14.document.FieldNames;


public class AuthorAnalyzer implements Analyzer {
	
	TokenStream stream;
	TokenFilterFactory tokenFilterFactory;
	TokenFilter filter ;
	
	

	
	 public AuthorAnalyzer(TokenStream stream) {
		 this.stream = stream;
		 this.tokenFilterFactory = TokenFilterFactory.getInstance();
		 this.filter = null;
		 
	}

	@Override
	public boolean increment() throws TokenizerException {
		
		 this.filter = this.tokenFilterFactory.getFilterByType(TokenFilterType.ACCENT, this.stream);

		if(this.stream != null)
		{
			while(this.filter.increment())
			{
				
			}
		}

		 this.stream.reset();

		
		 this.filter = this.tokenFilterFactory.getFilterByType(TokenFilterType.CAPITALIZATION, this.stream);
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
