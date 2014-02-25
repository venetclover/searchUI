package edu.noteaid.question;

import java.util.ArrayList;

public class QuestionAnsPair {
	String question = "";
	String ans = "";
	String URL = "";
	ArrayList<String> keywords = new ArrayList<String>();
	public QuestionAnsPair(String question, String ans){
		this.question = question;
		this.ans = ans;
	}
	
	public void set_question(String question){
		this.question = question;
	}
	
	public void set_url(String url){
		this.URL = url;
	}
	
	public void set_keywords(String kw){
		this.keywords.add(kw);
	}
}
