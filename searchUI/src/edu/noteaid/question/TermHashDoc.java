package edu.noteaid.question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/***
 * 
 * A class record the mapping of terms and semantic types
 * after processed from TermTypeHashFile
 * 
 * @author aurora
 *
 */
public class TermHashDoc {

	Map<String, Integer> termSemMap = null;
	Map<String, Integer> semTermMap = null;
	

	public TermHashDoc() {
		termSemMap = new HashMap<String, Integer>();
		semTermMap = new HashMap<String, Integer>();
	}

	public void processTermSem(HashMap<String, ArrayList<String>> termTypeMap) {
		for (Map.Entry<String, ArrayList<String>> pair : termTypeMap.entrySet()) {
			String term = pair.getKey();
			ArrayList<String> types = pair.getValue();
			for (String type : types){
				termSemMap.put(term+"_"+type, 1);
			}	
		}
	}
	
	public Map<String, Integer> getTermSemMap(){
		if (termSemMap != null)
			return termSemMap;
		else
			return null;
	}
	
	public void processSemTerm(HashMap<String, ArrayList<String>> termTypeMap) {
		for (Map.Entry<String, ArrayList<String>> pair : termTypeMap.entrySet()) {
			String term = pair.getKey();
			ArrayList<String> types = pair.getValue();
			for (String type : types){
				semTermMap.put(type+"_"+term, 1);
			}	
		}
	}
	
	public Map<String, Integer> getSemTermMap(){
		if (semTermMap != null)
			return semTermMap;
		else
			return null;
	}

}
