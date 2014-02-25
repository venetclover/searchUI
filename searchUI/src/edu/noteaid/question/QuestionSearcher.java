package edu.noteaid.question;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class QuestionSearcher {

	public ArrayList<SearchResult> SearchQuestions(String query_str) {
		ArrayList<SearchResult> result = new ArrayList<SearchResult>();
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
		Directory directory;
		
		try {
			directory = FSDirectory.open(new File("C:\\Users\\aurora\\workspace\\searchUI\\WebIndex\\QAIndex"));

			// Now search the index:
			DirectoryReader ireader;

			ireader = DirectoryReader.open(directory);
			IndexSearcher isearcher = new IndexSearcher(ireader);
			// Parse a simple query that searches for "text":
			QueryParser parser = new QueryParser(Version.LUCENE_46,
					"answer", analyzer);
			Query query = parser.parse(query_str);
//			Query query = parser.parse("swallow");
			ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
			
			
			for (int i = 0; i < hits.length; i++) {
				Document hitDoc = isearcher.doc(hits[i].doc);
				System.out.println("Filename: " + hitDoc.getField("filename").stringValue());
//				System.out.println("URL: " + hitDoc.getField("url"));
				System.out.println("Question: " + hitDoc.getField("question").stringValue());
				System.out.println("Answer: " + hitDoc.getField("answer").stringValue());
				System.out.println("");
				
				String f_str = hitDoc.getField("filename").stringValue();
				String u_str = hitDoc.getField("url").stringValue();
				String q_str = hitDoc.getField("question").stringValue();
				String a_str = hitDoc.getField("answer").stringValue();
				
				SearchResult r = new SearchResult(f_str, u_str, q_str, a_str);
				result.add(r);
			}

			ireader.close();
			directory.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			return result;
		}

	}

	public static void main(String[] args) {
		QuestionSearcher ind = new QuestionSearcher();
		ArrayList<SearchResult> result = ind.SearchQuestions("insurance");
		// ind.indexQuestions(args[1]);
	}

}
