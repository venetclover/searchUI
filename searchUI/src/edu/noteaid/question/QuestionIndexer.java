package edu.noteaid.question;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class QuestionIndexer {

	public void indexQuestions(ArrayList<QuestionAnsPair> pairs,
			DocumentInfo docInfo) {
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
		// Store the index in memory:
		Directory directory;
		IndexWriter iwriter;
		try {
			directory = FSDirectory.open(new File("WebIndex/QAIndex"));

			IndexWriterConfig config = new IndexWriterConfig(
					Version.LUCENE_CURRENT, analyzer);
			config.setOpenMode(OpenMode.CREATE_OR_APPEND);

			iwriter = new IndexWriter(directory, config);
			for (QuestionAnsPair pair : pairs) {
				Document doc = new Document();
				String text = docInfo.text;
				String filename = docInfo.filename;
				String url = docInfo.url;
				doc.add((IndexableField) new Field("text", text,
						TextField.TYPE_STORED));
				doc.add((IndexableField) new Field("filename", filename,
						TextField.TYPE_STORED));
				doc.add((IndexableField) new Field("url", url,
						TextField.TYPE_STORED));

				doc.add((IndexableField) new Field("question", pair.question,
						TextField.TYPE_STORED));
				doc.add((IndexableField) new Field("answer", pair.ans,
						TextField.TYPE_STORED));

				for (String kws : pair.keywords)
					doc.add((IndexableField) new Field("keywords", kws,
							TextField.TYPE_STORED));

				iwriter.addDocument(doc);
				
			}
			iwriter.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	public static void main(String[] args) {
		QuestionIndexer ind = new QuestionIndexer();
		// ind.indexQuestions(args[1]);
	}

}
