package edu.noteaid.util;

import edu.noteaid.question.TermTypeHashFile;
import gov.nih.nlm.nls.metamap.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

/**
 * This class uses the umls metamap utility and tags the input text
 * 
 * @author Aurora
 */
public class EHRMetaMapExtractor {

	private final String host = "10.1.1.3";
	private MetaMapApi api = new MetaMapApiImpl(host);
	private HashMap<String, ArrayList<String>> type2term = new HashMap<String, ArrayList<String>>();
	private HashMap<String, ArrayList<String>> term2type = new HashMap<String, ArrayList<String>>();
	private static final Logger LOGGER = Logger
			.getLogger(EHRMetaMapExtractor.class);

	public void close() {

	}

	public void init() {
		List<String> theOptions = new ArrayList<String>();
		theOptions.add("-y"); // turn on Word Sense Disambiguation
		if (theOptions.size() > 0) {
			api.setOptions(theOptions);
		}
	}

	public String extract_note(String path) {
		// Parse the xml file
		EHRXMLReader reader = new EHRXMLReader();
		reader.initParsing(path);
		// System.out.println(reader.note);
		return reader.note;

		/*
		 * File fp = new File(path); String fn = fp.getName(); String
		 * tmp_filename = fn.replace(".xml", ".txt");
		 * 
		 * //save text as file, return temp filename try {
		 * System.out.println(reader.note); PrintWriter out = new
		 * PrintWriter(tmp_filename, "UTF-8"); out.print(reader.note);
		 * out.close(); return tmp_filename; } catch(Exception e){
		 * e.printStackTrace(); System.out.println("Fail to save temp file");
		 * return null; }
		 */

	}
	
	public String extractTermbyText(String noteTxt){
		try {
			
			String tmpPath = "raw_text/temp/" +
					String.valueOf(System.currentTimeMillis());
			PrintWriter pw = new PrintWriter(tmpPath);
			pw.print(noteTxt);
			pw.close();
			
			extractTermbyPathName(tmpPath, "notes");
			
			return tmpPath;
		} catch (FileNotFoundException e) {
			
			return null;
		}
	}

	/***
	 * 
	 * extractTermbyPathName is a method that input text file path and extract
	 * the UMLS
	 * 
	 * There are two doc types: questions, notes
	 * 
	 */
	public void extractTermbyPathName(String path, String docType) {
		File pf = new File(path);
		if (pf.isDirectory()) {
			for (File afile : pf.listFiles())
				extractTermbyPathName(afile.getAbsolutePath(), docType);
		} else {
			type2term = new HashMap<String, ArrayList<String>>();
			term2type = new HashMap<String, ArrayList<String>>();

			try (BufferedReader br = new BufferedReader(new FileReader(pf))) {

				String line = "";
				while ((line = br.readLine()) != null) {
					printSemanticTypes(line);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			TermTypeHashFile ttmap = new TermTypeHashFile(type2term, term2type);
			ObjSerializer<Object, Object> serializer = new ObjSerializer<Object, Object>();
			serializer.serialize(ttmap,
					"persist/" + docType + "/" + pf.getPath());
		}
	}

	public void extractTermbyPathName(String path) {
		extractTermbyPathName(path, "question");
	}

	public void removeTmpFile(String tmp_filename) {
		File fp = new File(tmp_filename);
		fp.delete();

	}

	public void persistMap(HashMap<String, ArrayList<String>> map, String s1,
			String s2) {

		// System.out.println(map.toString() +" "+ s1 +" "+ s2);
		ArrayList<String> values = map.get(s1);
		if (values != null) {
			// System.out.println(values.toString());
			values.add(s2);
		} else {
			ArrayList<String> newval = new ArrayList<String>();
			newval.add(s2);
			map.put(s1, newval);
		}

	}

	/**
	 * For test purpose ONLY. for development please use getSemanticTypes.
	 * 
	 * @param path
	 * @throws Exception
	 */
	public void printSemanticTypes(String text) throws Exception {
		String finalText = "";
		String phraseText = "";
		List<Result> resultList = api.processCitationsFromString(text);
		Result result = resultList.get(0);
		// System.out.println("****\nResult:\t");
		for (Utterance utterance : result.getUtteranceList()) {
			// System.out.println("Utterance:");
			// System.out.println("  Id: " + utterance.getId());
			// System.out.println("  Utterance text: " + utterance.getString());
			// System.out.println("  Position: " + utterance.getPosition());
			List<PCM> pcms = utterance.getPCMList();
			for (PCM pcm : pcms) {
				phraseText = pcm.getPhrase().getPhraseText();
				// System.out.println("    Phrase: " + phraseText);
				List<Mapping> mappings = pcm.getMappingList();
				for (Mapping mapping : mappings) {
					// System.out.println("      Mapping Score: " +
					// mapping.getScore());
					List<Ev> evs = mapping.getEvList();
					for (Ev ev : evs) {
						String cui = ev.getConceptId();
						System.out.println("        CUID: " + cui);
						// System.out.println("        Name: " +
						// ev.getConceptName());
						System.out.println("        Preferred Name: "
								+ ev.getPreferredName());
						List<String> semanticTypes = ev.getSemanticTypes();
						for (String semanticType : semanticTypes) {
							System.out.println("          Semantic type: "
									+ semanticType);
							persistMap(term2type, ev.getPreferredName(),
									semanticType);
							persistMap(type2term, semanticType,
									ev.getPreferredName());
						}
						// List<String> words = ev.getMatchedWords();
						// for(String word : words) {
						// System.out.println("          Matched Word : " +
						// word);
						// }
						// phraseText = "<" + cui + ">" + phraseText + "</" +
						// cui + ">";
					}

				}
				// finalText = finalText + " " +phraseText;
			}
		}
		// System.out.println("The Final Text is : " + finalText);
	}

	public void checkMapSize() {
		System.out.println(term2type.size() + " " + type2term.size());
		Set<String> keys = type2term.keySet();
		for (Object key : keys) {
			System.out.println(type2term.get(key).toString());
		}

	}

	void test_ExtractingNotes() {
		String path = "t_records/report4244.xml";
		try {
			String note = extract_note(path);
			String[] notelines = note.split("\n");
			for (String line : notelines) {
				System.out.println("line: " + line);
				if (!line.trim().equals(""))
					printSemanticTypes(line);
			}
			// metamap.checkMapSize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void test_ExtractUMLS4Docs() {
		String path = "/home/auroral/q_generation/raw_txt/questions/qa_text/";
		String type = "questions";
		extractTermbyPathName(path, type);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EHRMetaMapExtractor extractor = new EHRMetaMapExtractor();
		// extractor.test_ExtractingNotes();
		extractor.test_ExtractUMLS4Docs();
	}
}
