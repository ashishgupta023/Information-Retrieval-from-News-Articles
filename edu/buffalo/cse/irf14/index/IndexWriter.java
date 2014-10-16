/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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
	Index termIndex;
	Index categoryIndex;
	Index authorIndex;
	Index placeIndex;
	String indexDir;
	DocumentDictionary  dictionary ; 
	FileOutputStream fileOutputStream ;
	ObjectOutputStream objectOutputStream ;
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
		fileOutputStream = null;
		dictionary = new DocumentDictionary();
		objectOutputStream = null;
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
			int length = 0;
			int posIndex = 0; 
			docID = dictionary.insert(d.getField(FieldNames.FILEID)[0]);
			
			
			// Index all documents
			for(FieldNames name : FieldNames.values())
			{
				
				if(name != FieldNames.CATEGORY && name != FieldNames.FILEID)
				{
					length = length + 1;
				}
				
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
								
								if(token != null && !token.toString().isEmpty()  )
								{
									if(name != FieldNames.CATEGORY && name != FieldNames.FILEID)
									{
										 posIndex = length + token.getPosIndex();
									}
									if(name == FieldNames.FILEID || name == FieldNames.AUTHORORG || name == FieldNames.CONTENT || name == FieldNames.NEWSDATE || name == FieldNames.TITLE )
									{
										termIndex.put(token.toString(), docID , posIndex );
									}
									else if(name == FieldNames.CATEGORY)
									{
										categoryIndex.put(token.toString(),docID  , posIndex);
									}
									else if(name == FieldNames.AUTHOR)
									{
										authorIndex.put(token.toString(), docID , posIndex);
									}
									else if(name == FieldNames.PLACE)
									{
										placeIndex.put(token.toString(), docID , posIndex);
									}
								}
								
							
							}
						
						
					}	
				}
				if(content != null && name != FieldNames.FILEID && name != FieldNames.CATEGORY)
				{
					length = length + content[0].length();
				}
				
			}
			
			// Update Document length in the document dictionary
			dictionary.insert(docID, length);
			
		}
		catch(TokenizerException e)
		{
			
		}
		
	}
	public void dumpIntoDisk(Index index) {		// this dumps entire list to disk
		String BasePath = this.indexDir;
	
		String indexBaseDirPath = BasePath +File.separator + index.indexType.toString();
		File indexBaseDir = new File(indexBaseDirPath);
		
		// create this Directory of not exists..
		if(!indexBaseDir.exists()) {
			indexBaseDir.mkdir();
		}
		
		if(index!=null)
		{
		
		String indexBaseFile = indexBaseDirPath + File.separator + index.indexType.toString();

		
		File filehandle = new File(indexBaseFile );

		try {
			 fileOutputStream =  new FileOutputStream(filehandle);
			 objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(index);
			
			objectOutputStream.close();
			fileOutputStream.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	
	}
	}
	
	public void dumpDictionary() {		
		String BasePath = this.indexDir;
	
		String indexBaseDirPath = BasePath +File.separator;
		File indexBaseDir = new File(indexBaseDirPath);
		
		// create this Directory of not exists..
		if(!indexBaseDir.exists()) {
			indexBaseDir.mkdir();
		}
		
		if(dictionary!=null)
		{
		
		String indexBaseFile = indexBaseDirPath + File.separator + "dictionary";

		
		File filehandle = new File(indexBaseFile );

		try {
			 fileOutputStream =  new FileOutputStream(filehandle);
			 objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(dictionary);
			
			objectOutputStream.close();
			fileOutputStream.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	
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
		dumpDictionary();
		dumpIntoDisk(termIndex);
		dumpIntoDisk(placeIndex);
		dumpIntoDisk(categoryIndex);
		dumpIntoDisk(authorIndex);
		
		
		
	}
}
