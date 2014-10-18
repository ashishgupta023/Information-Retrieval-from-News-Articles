/**
 * 
 */
package edu.buffalo.cse.irf14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.index.IndexWriter;

/**
 * @author nikhillo
 *
 */
public class Runner {

	/**
	 * 
	 */
	public Runner() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String ipDir = args[0];
		String indexDir = args[1];
		//more? idk!
		
		File ipDirectory = new File(ipDir);
		String[] catDirectories = ipDirectory.list();
		
		String[] files;
		File dir;
		
		Document d = null;
		IndexWriter writer = new IndexWriter(indexDir);
		long time = System.currentTimeMillis();
		PrintStream fileOutputStream  = null; 
		try {
			 fileOutputStream = new PrintStream(new File(indexDir + File.separator + "output"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SearchRunner testSearch = new SearchRunner(indexDir, indexDir + File.separator + "snippets", 'Q', System.out);
		
		testSearch.query("place:washington AND government", ScoringModel.OKAPI);

		/*while(true)
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        System.out.print("Enter Query : ");
	        try {
				String s = br.readLine();
				testSearch.query(s, ScoringModel.TFIDF);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        System.out.println();
		}
		*/
		
		//File file = new File(indexDir + File.separator + "input.txt");
		//testSearch.query(file);
		//Query query = QueryParser.parse("Author:\"Patti Domm\" AND american express", "OR");
		//System.out.println(query.getEvalOrder());
		//parser.parse("(black OR blue) AND bruises", "OR");
		//parser.parse("Category:War AND Author:Dutt AND Place:Baghdad AND prisoners detainees rebels", "OR");
		//parser.parse("(Love NOT War) AND Category:(movies NOT crime)", "OR");
		//parser.parse("(hello world you) OR (this is test)", "OR");
		/*IndexReader reader = new IndexReader(indexDir, IndexType.TERM);
		//Map<String,Integer> res =  reader.orQuery("analyst","capital");
		
		for (String key : res.keySet()) {
		    System.out.println(key + " : " + res.get(key));
		}
		*//* try {
			for (String cat : catDirectories) {
				dir = new File(ipDir+ File.separator+ cat);
				files = dir.list();
				
				if (files == null)
					continue;
				
				for (String f : files) {
					try {
						d = Parser.parse(dir.getAbsolutePath() + File.separator + f);
						writer.addDocument(d);
						//break cut;
					} catch (ParserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
			}
			
			writer.close();
			long res = System.currentTimeMillis() - time;
			System.out.println(Long.toString(res));
		} catch (IndexerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
	}  

}
