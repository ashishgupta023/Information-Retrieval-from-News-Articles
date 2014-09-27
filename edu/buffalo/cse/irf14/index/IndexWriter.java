/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo
 * Class responsible for writing indexes to disk
 */
public class IndexWriter {
	
	TokenStream stream ;
	String[] content ;
	Tokenizer tokenizer;
	AnalyzerFactory factory;
	Analyzer analyzer;
	/**
	 * Default constructor
	 * @param indexDir : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		//TODO : YOU MUST IMPLEMENT THIS
		stream = null;
		content = null;
		tokenizer = null;
		factory = null;
		analyzer = null;
	}
	
	/**
	 * Method to add the given Document to the index
	 * This method should take care of reading the filed values, passing
	 * them through corresponding analyzers and then indexing the results
	 * for each indexable field within the document. 
	 * @param d : The Document to be added
	 * @throws IndexerException : In case any error occurs
	 * @throws TokenizerException 
	 */
	public void addDocument(Document d) throws IndexerException {
		//TODO : YOU MUST IMPLEMENT THIS
		try
		{	
			// Index all documents
			for(FieldNames name : FieldNames.values())
			{
				content  = d.getField(name);
				tokenizer = new Tokenizer();
				if(content != null)
				{
					stream  = tokenizer.consume(content[0]);
				
					factory = AnalyzerFactory.getInstance();
				
					analyzer = factory.getAnalyzerForField(name, stream);
					if(analyzer != null)
					{
						while(analyzer.increment())
						{
							
						}
						stream = analyzer.getStream();
						stream.reset();
					}
				}
				
			}
			
		}
		catch(TokenizerException e)
		{
			
		}
		
	}
	
	/**
	 * Method that indicates that all open resources must be closed
	 * and cleaned and that the entire indexing operation has been completed.
	 * @throws IndexerException : In case any error occurs
	 */
	public void close() throws IndexerException {
		//TODO
	}
}
