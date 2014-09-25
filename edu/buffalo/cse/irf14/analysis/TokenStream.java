/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * @author nikhillo
 * Class that represents a stream of Tokens. All {@link Analyzer} and
 * {@link TokenFilter} instances operate on this to implement their
 * behavior
 */

public class TokenStream implements Iterator<Token>{

	private List<Token> tokens;
	private  ListIterator<Token> litr;
    private int tokenIndex ;
    private  boolean canCallRemove ;
    private boolean removeCalled;
    private boolean endReached ;
    
    public TokenStream()
    {
    	tokens = new ArrayList<Token>();
    	tokenIndex = 0;
    	canCallRemove = false;
    	endReached = false;
    }

    
    public int getTokenStreamSize() {
    	return tokens.size();
    }
    
    
    
    public void setIterator()
    {
    	litr = tokens.listIterator();
    }
    /**
	 * Method that checks if there is any Token left in the stream
	 * with regards to the current pointer.
	 * DOES NOT ADVANCE THE POINTER
	 * @return true if at least one Token exists, false otherwise
	 */
 
    
	@Override
	public boolean hasNext() {
		// TODO YOU MUST IMPLEMENT THIS
		
		return litr.hasNext();
       /* if(this.tokens.size() == this.tokenIndex) {
        	return false;
		}
        else {
        	return true;
        }*/
	}
	
	public boolean hasPrevious()
	{
		return litr.hasPrevious();
	}

	public Token previous()
	{
		if(litr.hasPrevious())
		{
			return litr.previous();
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Method to return the next Token in the stream. If a previous
	 * hasNext() call returned true, this method must return a non-null
	 * Token.
	 * If for any reason, it is called at the end of the stream, when all
	 * tokens have already been iterated, return null
	 */
	@Override
	public Token next() {
		// TODO YOU MUST IMPLEMENT THIS
		
		if(litr.hasNext())
		{
			canCallRemove = true;
			return litr.next();
		}
		else
		{
			return null;
		}
		/*if (this.hasNext() == false) {
			tokenIndex = 0;
			return null;
		}
		else {
			canCallRemove = true;
			return tokens.get(tokenIndex++);
		}*/
	}
	
	/**
	 * Method to remove the current Token from the stream.
	 * Note that "current" token refers to the Token just returned
	 * by the next method. 
	 * Must thus be NO-OP when at the beginning of the stream or at the end
	 */
	@Override
	public void remove() {
		// TODO YOU MUST IMPLEMENT THIS
		
	
			if(canCallRemove == true)
			{
				litr.remove();
				canCallRemove = false;
				removeCalled = true;
			}
			else
			{
				//System.out.println("--Do Nothing--");
			}
		

	}
	
	/**
	 * Method to reset the stream to bring the iterator back to the beginning
	 * of the stream. Unless the stream has no tokens, hasNext() after calling
	 * reset() must always return true.
	 */
	public void reset() {
		//TODO : YOU MUST IMPLEMENT THIS
		
		while(litr.hasPrevious() )
		{
			litr.previous();
		}
		
		endReached = false;
	}
	
	/**
	 * Method to append the given TokenStream to the end of the current stream
	 * The append must always occur at the end irrespective of where the iterator
	 * currently stands. After appending, the iterator position must be unchanged
	 * Of course this means if the iterator was at the end of the stream and a 
	 * new stream was appended, the iterator hasn't moved but that is no longer
	 * the end of the stream.
	 * @param stream : The stream to be appended
	 */
	public void append(TokenStream stream) {
		//TODO : YOU MUST IMPLEMENT THIS
		int iteratorPos = litr.nextIndex();
		
		if(stream != null)
		{
			for (int i = 0; i < stream.getTokenStreamSize(); i++) {
				this.tokens.add(stream.tokens.get(i));
			}
		}
		
		 this.setIterator();
		 for(int i =0 ; i < iteratorPos;i++)
		 {
			 litr.next();
		 }
		 
		
		
	}
	
	public void add(Token token) {
		this.tokens.add(token);
		this.setIterator();
	}
	
	/**
	 * Method to get the current Token from the stream without iteration.
	 * The only difference between this method and {@link TokenStream#next()} is that
	 * the latter moves the stream forward, this one does not.
	 * Calling this method multiple times would not alter the return value of {@link TokenStream#hasNext()}
	 * @return The current {@link Token} if one exists, null if end of stream
	 * has been reached or the current Token was removed
	 */
	public Token getCurrent() {
		//TODO: YOU MUST IMPLEMENT THIS
		Token currToken = null;
		int currElementPos = -1; 
		if(litr.nextIndex() > 0 && litr.nextIndex() <= tokens.size() && !endReached  )
		{
			if(!removeCalled )
			{
				removeCalled = false;
				if(litr.nextIndex() == tokens.size())
					endReached = true;
				currElementPos = litr.nextIndex()-1;
				currToken = tokens.get(currElementPos);
				
				
				
			}
		}
		
		return currToken;			
	}
	
}
