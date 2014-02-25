package edu.noteaid.question;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HAQuestionSearcher {
	String filename = "WebSource/ha/";
	QuestionIndexer indexer;

	public HAQuestionSearcher() {
		indexer = new QuestionIndexer();
	}

	public HAQuestionSearcher(String path) {
		this.filename = path;
	}

	public void startSearch() {
		File path = new File(this.filename);
		if (path.isDirectory()) {
			File[] files = path.listFiles();
			for(File f: files){
				System.out.print(f.getName());
				ArrayList<QuestionAnsPair> q_pairs = search(f);
				DocumentInfo docInfo = new DocumentInfo(f.getName(), "", "");
				indexer.indexQuestions(q_pairs, docInfo);
			}
		} else {
			ArrayList<QuestionAnsPair> q_pairs = search(path);
			DocumentInfo docInfo = new DocumentInfo(path.getName(), "", "");
			indexer.indexQuestions(q_pairs, docInfo);
		}
	}

	public QuestionAnsPair constructQuestion(String sent) {
		String REGEX = "Q[\\.|:]\\s.\\s(.*?)(\\sA[\\.|:].*?)([A-Z].*)";
		Pattern pattern = Pattern.compile(REGEX);
		Matcher matcher = pattern.matcher(sent);
		if (matcher.find()) {
			QuestionAnsPair qa_obj = new QuestionAnsPair(matcher.group(1),
					matcher.group(3));
			return qa_obj;
		}
		return null;
	}

	public void printAllQuestions(ArrayList<QuestionAnsPair> pairs) {
		for (QuestionAnsPair qa : pairs) {
			System.out.println("--Question Set:");
			System.out.println(qa.question);
			System.out.println(qa.ans);
		}
	}

	public ArrayList<QuestionAnsPair> search(File input) {
		ArrayList<QuestionAnsPair> pairs = new ArrayList<QuestionAnsPair>();
		try {
			Document doc = Jsoup.parse(input, "UTF-8");
			Elements ps = doc.getElementsByTag("p");
	//		StringBuilder sb = new StringBuilder();
			for (Iterator<Element> iterator = ps.iterator(); iterator.hasNext();) {
				Element p = iterator.next();
				String sentence = p.text();
				if (sentence.startsWith("Q")) {
					pairs.add(constructQuestion(sentence));
				}
			}

			printAllQuestions(pairs);
			return pairs;
		} catch (IOException e) {
			System.out.println("Cannot find the file");
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		HAQuestionSearcher qsearch = new HAQuestionSearcher();
		qsearch.startSearch();
	}
}
