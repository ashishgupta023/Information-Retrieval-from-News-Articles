package edu.buffalo.cse.irf14.analysis;

import edu.buffalo.cse.irf14.document.FieldNames;


public class TitleAnalyzer implements Analyzer {
	
	TokenStream stream;
	TokenFilterFactory tokenFilterFactory;
	TokenFilter filter ;
	
	

	
	 public TitleAnalyzer(TokenStream stream) {
		 this.stream = stream;
		 this.tokenFilterFactory = TokenFilterFactory.getInstance();
		 
		 
	}

	@Override
	public boolean increment() throws TokenizerException {
		
		 this.filter = this.tokenFilterFactory.getFilterByType(TokenFilterType.SYMBOL, this.stream);

		if(this.stream != null)
		{
			while(this.filter.increment())
			{
				
			}
		}
		
		 this.filter = this.tokenFilterFactory.getFilterByType(TokenFilterType.SPECIALCHARS, this.stream);
		 this.stream.reset();
		 if(this.stream != null)
			{
				while(this.filter.increment())
				{
					
				}
			}

		return false;
	}

	@Override
	public TokenStream getStream() {
		
		return this.stream;
	}
	
	
	

}
