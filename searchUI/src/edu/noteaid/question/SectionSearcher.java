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

public class SectionSearcher {
	String filename = "WebSource/";
	ArrayList<QuestionAnsPair> pairs = new ArrayList<QuestionAnsPair>();

	public SectionSearcher() {

	}

	public SectionSearcher(String path) {
		this.filename = path;
	}

	public void startSearch() {
		File path = new File(this.filename);
		if (path.isDirectory()) {
			File[] files = path.listFiles();
			for(File f: files)
				search(f);
			
		} else {
			search(path);
		}
	}

	public void constructQuestion(String sent) {
		String REGEX = "Q[\\.|:]\\s.\\s(.*?)(\\sA[\\.|:].*?)([A-Z].*)";
		Pattern pattern = Pattern.compile(REGEX);
		Matcher matcher = pattern.matcher(sent);
		if (matcher.find()) {
			QuestionAnsPair qa_obj = new QuestionAnsPair(matcher.group(1),
					matcher.group(3));
			pairs.add(qa_obj);
		}
	}

	public void printAllQuestions() {
		for (QuestionAnsPair qa : pairs) {
			System.out.println("--Question Set:");
			System.out.println(qa.question);
			System.out.println(qa.ans);
		}
	}

	public void search(File input) {
		try {
			Document doc = Jsoup.parse(input, "UTF-8");
			Elements ps = doc.getElementsByTag("p");
	//		StringBuilder sb = new StringBuilder();
			for (Iterator<Element> iterator = ps.iterator(); iterator.hasNext();) {
				Element p = iterator.next();
				String sentence = p.text();
				if (sentence.startsWith("Q")) {
					constructQuestion(sentence);
				}
			}

			printAllQuestions();
		} catch (IOException e) {
			System.out.println("Cannot find the file");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SectionSearcher qsearch = new SectionSearcher();
		qsearch.startSearch();
	}
}
