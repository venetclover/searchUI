package edu.noteaid.question;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;


public class FnUrlMapper {
	FnUrlPairs pairs;
	
	public FnUrlMapper(String filename) {
		this.pairs = (FnUrlPairs) parseJSON(filename);
	}
	
	public String getCorrespondUrl(String filename){
		for (FnUrlPair pair: this.pairs){
			if (pair.filename.matches(filename))
				return pair.url;
		}
		return null;
		
	}
	
	public String getCorrespondFn(String url){
		for (FnUrlPair pair: this.pairs){
			if (pair.filename.matches(url))
				return pair.filename;
		}
		return null;
		
	}

	private String readFile(String filename) {
		BufferedReader br;
		StringBuilder text = new StringBuilder();
		try {
			br = new BufferedReader(new FileReader(filename));
			for (String line = br.readLine(); line != null; line = br
					.readLine())
				text.append(line);
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return text.toString();
	}
	
	private ArrayList<FnUrlPair> parseJSON(String filename) {

		String text = readFile(filename);
		Gson gson = new Gson();
		FnUrlPairs pairList = gson.fromJson(text, FnUrlPairs.class);
		
		return pairList;
		
	}

	public class FnUrlPairs extends ArrayList<FnUrlPair> {
		private static final long serialVersionUID = 1L;
	}

	public class FnUrlPair {
		String filename;
		String url;
	}

}
