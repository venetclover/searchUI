package edu.noteaid.question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/***
 * 
 * Object that holds information of document. The information is MetaMap
 * information: Semantic types. Since server already has MetaMap, we use JSON
 * and this class to keep information so that it can be transferred between
 * different machines.
 * 
 * @author aurora
 * 
 */

public class TermTypeHashFile {

	HashMap<String, ArrayList<String>> type2term = new HashMap<String, ArrayList<String>>();
	HashMap<String, ArrayList<String>> term2type = new HashMap<String, ArrayList<String>>();

	public TermTypeHashFile(Map<String, ArrayList<String>> m1,
			Map<String, ArrayList<String>> m2) {
		type2term = (HashMap<String, ArrayList<String>>) m1;
		term2type = (HashMap<String, ArrayList<String>>) m2;
	}
}
