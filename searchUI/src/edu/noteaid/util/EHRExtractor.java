package edu.noteaid.util;

/**
 * 
 */

import gov.nih.nlm.nls.metamap.Ev;
import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.File;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

/**
 * This class uses the umls metamap utility and tags the input text
 * @author Balaji
 */
public class EHRExtractor {
	
	private final String host ="10.1.1.3";
	private MetaMapApi api = new MetaMapApiImpl(host);
	private HashMap<String, ArrayList> type2term = new HashMap<String, ArrayList>();
	private HashMap<String, ArrayList> term2type = new HashMap<String, ArrayList>();
	private static final Logger LOGGER = Logger.getLogger(EHRExtractor.class);

	public void close() {
		
	}

	public void init(){
		List<String> theOptions = new ArrayList<String>();
		theOptions.add("-y");  // turn on Word Sense Disambiguation
		if (theOptions.size() > 0) {
	  		api.setOptions(theOptions);
		}
	}

	public String extract_note(String path) {
		//Parse the xml file
		EHRXMLReader reader = new EHRXMLReader();
		reader.initParsing(path);
	//	System.out.println(reader.note);
		return reader.note;

		/*
		File fp = new File(path);
		String fn = fp.getName();
		String tmp_filename = fn.replace(".xml", ".txt");
		
		//save text as file, return temp filename
		try {
			System.out.println(reader.note);
			PrintWriter out = new PrintWriter(tmp_filename, "UTF-8");
			out.print(reader.note);
			out.close();
			return tmp_filename;
		} catch(Exception e){
			e.printStackTrace();
			System.out.println("Fail to save temp file");
			return null;
		}
		*/
	
	}

	public void removeTmpFile(String tmp_filename) {
		File fp = new File(tmp_filename);
		fp.delete();

	}
        
	public void persistMap(HashMap map, String s1, String s2){

		System.out.println(map.toString() +" "+  s1 +" "+ s2);
		ArrayList values = (ArrayList) map.get(s1);
		if (values != null) {
			System.out.println(values.toString());
			values.add(s2);
		} else {
			ArrayList newval = new ArrayList<String>();
			map.put(s1, newval.add(s2));
		}
		
	}

	/**
	 * For test purpose ONLY. for development please use getSemanticTypes.
	 * @param path
	 * @throws Exception
	 */
	public void printSemanticTypes(String text) throws Exception {
		String finalText = "";
		String phraseText = "";
		List<Result> resultList = api.processCitationsFromString(text);
		Result result = resultList.get(0);        
//		System.out.println("****\nResult:\t");
        for(Utterance utterance: result.getUtteranceList()){
//			System.out.println("Utterance:");
//			System.out.println("  Id: " + utterance.getId());
//			System.out.println("  Utterance text: " + utterance.getString());
//			System.out.println("  Position: " + utterance.getPosition());
			List<PCM> pcms = utterance.getPCMList();
			for (PCM pcm : pcms) {
				phraseText = pcm.getPhrase().getPhraseText();
//				System.out.println("    Phrase: " + phraseText);
				List<Mapping> mappings = pcm.getMappingList();
				for (Mapping mapping : mappings) {
//					System.out.println("      Mapping Score: " + mapping.getScore());
					List<Ev> evs = mapping.getEvList();
					for (Ev ev : evs) {
						String cui = ev.getConceptId();
//						System.out.println("        CUID: " + cui);
//						System.out.println("        Name: " + ev.getConceptName());
						System.out.println("        Preferred Name: " + ev.getPreferredName());
						List<String> semanticTypes = ev.getSemanticTypes();
						for (String semanticType : semanticTypes) {
							System.out.println("          Semantic type: " + semanticType);
							persistMap(term2type, ev.getPreferredName(), semanticType);
							persistMap(type2term, semanticType, ev.getPreferredName());
                        }
						List<String> words = ev.getMatchedWords();
						for(String word : words) {
//							System.out.println("          Matched Word : " + word);
						}
//						phraseText = "<" + cui + ">" + phraseText + "</" + cui + ">";
					}
                        
				}
//				finalText = finalText + " " +phraseText;
			}
		}
//		System.out.println("The Final Text is : " + finalText);
	}

	public void checkMapSize(){
		System.out.println(term2type.size()+ " "+ type2term.size());
		Set keys = type2term.keySet();
		for (Object key: keys){
			System.out.println(type2term.get(key).toString());
		}
	
	}

  	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String path = "t_records/report4244.xml";
		EHRExtractor metamap = new EHRExtractor();
		metamap.init();

		EHRExtractor extractor = new EHRExtractor();
		try {
			String note = extractor.extract_note(path);
			String[] notelines = note.split("\n");
			for (String line : notelines){
				System.out.println("line: " + line);
				if (!line.trim().equals(""))
					metamap.printSemanticTypes(line);
			}
			metamap.checkMapSize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
