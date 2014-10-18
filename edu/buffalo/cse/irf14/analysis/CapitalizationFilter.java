
package edu.buffalo.cse.irf14.analysis;

public class CapitalizationFilter extends TokenFilter {
	CapitalizationFilter(TokenStream stream) {
		super(stream);
		}
	
	
	
	@Override
	public boolean increment() throws TokenizerException {
		
		try
		{
			if(filterStream.hasNext())
				currToken = filterStream.next();
			boolean tokenCaps = currToken.getTermText().matches("[A-Z]+");
			
			
			if(tokenCaps)
			{
	
				if (filterStream.hasNext())
				{
					currToken = filterStream.next();
					if(currToken.getTermText().matches("[A-Z]+"))
					{
						
								currToken.setTermText(currToken.getTermText().toLowerCase());
								filterStream.previous();
								if (filterStream.hasPrevious())
									currToken = filterStream.previous();
								currToken.setTermText(currToken.getTermText().toLowerCase());
								
							
					}
					else
					{
						if(filterStream.hasPrevious())
							currToken = filterStream.previous();
						if (filterStream.hasPrevious())
							currToken = filterStream.previous();
						
					}
					
				}
				else
				{
					currToken.setTermText(currToken.getTermText().toLowerCase());

				}
				
				
				
				if (filterStream.hasNext())
					filterStream.next();
				if (filterStream.hasNext())
					filterStream.next();
				
							

				
			}
			else
			{
				currToken.setTermText(currToken.getTermText().toLowerCase());

			}

		
		}
		catch(Exception e)
		{
		}
		return filterStream.hasNext();

	}
	
	@Override
	public TokenStream getStream() {
		return filterStream;
	}
}



 
 

