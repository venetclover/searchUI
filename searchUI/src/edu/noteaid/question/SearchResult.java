package edu.noteaid.question;

public class SearchResult {
	String filename;
	String url;
	String question;
	String ans;
	
	public SearchResult(String f, String u, String q, String a){
		this.filename = f;
		this.url = u;
		this.question = q;
		this.ans = a;
	}
	
	public String getQuestion(){
		return this.question;
	}
	
	public String getAns(){
		return this.ans;
	}
	
	public String getUrl(){
		return this.url;
	}
}
