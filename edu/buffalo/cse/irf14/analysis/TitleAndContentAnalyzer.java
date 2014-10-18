package edu.buffalo.cse.irf14.analysis;

import edu.buffalo.cse.irf14.document.FieldNames;


public class TitleAndContentAnalyzer implements Analyzer {
	
	TokenStream stream;
	TokenFilterFactory tokenFilterFactory;
	TokenFilter filter ;
	

	
	 public TitleAndContentAnalyzer(TokenStream stream) {
		 this.stream = stream;
		 this.tokenFilterFactory = TokenFilterFactory.getInstance();
		 
		 
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
		
		 this.filter = this.tokenFilterFactory.getFilterByType(TokenFilterType.STOPWORD, this.stream);
		 if(this.stream != null)
			{
				while(this.filter.increment())
				{
					
				}
			}
		 this.stream.reset();
		 
		 this.filter = this.tokenFilterFactory.getFilterByType(TokenFilterType.DATE, this.stream);
		 if(this.stream != null)
			{
				while(this.filter.increment())
				{
					
				}
			}
		 this.stream.reset();
		 
		 this.filter = this.tokenFilterFactory.getFilterByType(TokenFilterType.NUMERIC, this.stream);
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
		 
		 this.filter = this.tokenFilterFactory.getFilterByType(TokenFilterType.STEMMER, this.stream);
		 if(this.stream != null)
			{
				while(this.filter.increment())
				{
					
				}
			}
		 this.stream.reset();
		 
		
		 
		
		 this.filter = this.tokenFilterFactory.getFilterByType(TokenFilterType.SYMBOL, this.stream);

		if(this.stream != null)
		{
			while(this.filter.increment())
			{
				
			}
		}
		
		 this.stream.reset();


		 this.filter = this.tokenFilterFactory.getFilterByType(TokenFilterType.SPECIALCHARS, this.stream);
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
