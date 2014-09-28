/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
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
	static Index termIndex;
	static Index categoryIndex;
	static Index authorIndex;
	static Index placeIndex;
	String indexDir;
	/**
	 * Default constructor
	 * @param indexDir : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		//TODO : YOU MUST IMPLEMENT THIS
		this.indexDir = indexDir;
		stream = null;
		content = null;
		tokenizer = null;
		factory = null;
		analyzer = null;
		this.termIndex = new Index(IndexType.TERM);
		this.categoryIndex = new Index(IndexType.CATEGORY);
		this.authorIndex = new Index(IndexType.AUTHOR);
		this.placeIndex = new Index(IndexType.PLACE);
		
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
			
			int docID = 0;
			docID = FieldDictionary.insert(d.getField(FieldNames.FILEID)[0]);
			
			
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
						
						
							while(stream.hasNext())
							{
								Token token = stream.next();
								

								if(name == FieldNames.FILEID || name == FieldNames.AUTHORORG || name == FieldNames.CONTENT || name == FieldNames.NEWSDATE || name == FieldNames.TITLE )
								{
									termIndex.put(token.toString(), docID);
								}
								else if(name == FieldNames.CATEGORY)
								{
									categoryIndex.put(token.toString(),docID );
								}
								else if(name == FieldNames.AUTHOR)
								{
									authorIndex.put(token.toString(), docID);
								}
								else if(name == FieldNames.PLACE)
								{
									placeIndex.put(token.toString(), docID);
								}
								
							
							}
						
						
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
		
		this.termIndex.sortAndAggregate();
		this.placeIndex.sortAndAggregate();
		this.authorIndex.sortAndAggregate();
		this.categoryIndex.sortAndAggregate();
		this.termIndex.dumpIntoDisk(this.indexDir);
		this.placeIndex.dumpIntoDisk(this.indexDir);
		this.categoryIndex.dumpIntoDisk(this.indexDir);
		this.authorIndex.dumpIntoDisk(this.indexDir);

		
	}
}
