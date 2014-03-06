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

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 * This class uses the umls metamap utility and tags the input text
 * 
 * @author Aurora
 */
public class EHRMetaMapExtractor {

	private final String host = "10.1.1.3";
	private MetaMapApi api = new MetaMapApiImpl(host);
	private EHRUMLSExtractor umlsExtractor = new EHRUMLSExtractor();
	private HashMap<String, ArrayList<String>> type2term = new HashMap<String, ArrayList<String>>();
	private HashMap<String, ArrayList<String>> term2type = new HashMap<String, ArrayList<String>>();
	private static final Logger LOGGER = Logger
			.getLogger(EHRMetaMapExtractor.class);

	String in_path;
	String out_path;
	String out_extend_path;
	String out_cui_path;
	String complete_path;

	StringBuilder tmpDefinitions;
	StringBuilder tmpCUIContext;

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

	public String extractTermbyText(String noteTxt) {
		try {

			String tmpPath = "raw_text/temp/"
					+ String.valueOf(System.currentTimeMillis());
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
			StringBuilder extendedDoc = new StringBuilder();
			StringBuilder CUIInsertedDoc = new StringBuilder();

			boolean somethingWrong = false;
			try {
				BufferedReader br = new BufferedReader(new FileReader(pf));
				String line = "";
				while ((line = br.readLine()) != null) {
					/*
					 * tmpAppendix and tmpCUIContext are a class variable. It
					 * gathers the definitions of terms in printSemanticTypes().
					 */
					if (line.trim().equals(""))
						continue;

					tmpDefinitions = new StringBuilder();
					tmpCUIContext = new StringBuilder();
					try {
						printSemanticTypes(line);
						extendedDoc.append(line);
						extendedDoc.append(tmpDefinitions);
						CUIInsertedDoc.append(tmpCUIContext);
					} catch (Exception e) {
						somethingWrong = true;
						break;
					}

				}

			} catch (Exception e) {
				somethingWrong = true;
				e.printStackTrace();
			}

			if (somethingWrong == true) {

			} else {
				// pf.renameTo(new File(complete_path
				// + pn.getFileName().toString()));
				TermTypeHashFile ttmap = new TermTypeHashFile(type2term,
						term2type);
				ObjSerializer<Object, Object> serializer = new ObjSerializer<Object, Object>();
				serializer.serialize(ttmap,
						out_path + FilenameUtils.getBaseName(path));
				saveExtendedDoc2File(extendedDoc.toString(), "def",
						FilenameUtils.getBaseName(path));
				saveExtendedDoc2File(CUIInsertedDoc.toString(), "cui",
						FilenameUtils.getBaseName(path));
			}
		}
	}

	public void extractTermbyPathName(String path) {
		extractTermbyPathName(path, "notes");
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

	public void saveExtendedDoc2File(String text, String type, String filename) {
		String save_dir;
		if (type.equals("def"))
			save_dir = out_extend_path;
		else
			save_dir = out_cui_path;

		try {
			PrintWriter out = new PrintWriter(save_dir + filename);
			out.write(text);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * For test purpose ONLY. for development please use getSemanticTypes.
	 * 
	 * @param path
	 * @throws Exception
	 */
	public void printSemanticTypes(String text) throws Exception {

		String phraseText = "";
		System.out.println("sentence: " + text + "<<");
		List<Result> resultList = api.processCitationsFromString(text);
		Result result = resultList.get(0);
		// System.out.println("****\nResult:\t");
		for (Utterance utterance : result.getUtteranceList()) {
			// System.out.println("Utterance:");
			// System.out.println("  Id: " + utterance.getId());
			// System.out.println("  Utterance text: " +
			// utterance.getString());
			// System.out.println("  Position: " + utterance.getPosition());
			List<PCM> pcms = utterance.getPCMList();
			for (PCM pcm : pcms) {
				phraseText = pcm.getPhrase().getPhraseText();
				String tmpPhraseText = "";
				// System.out.println("    Phrase: " + phraseText);
				// List<String> cuis = new ArrayList<String>();
				String cui = "";
				List<Mapping> mappings = pcm.getMappingList();
				for (Mapping mapping : mappings) {
					// System.out.println("      Mapping Score: " +
					// mapping.getScore());
					List<Ev> evs = mapping.getEvList();
					for (Ev ev : evs) {
						cui = ev.getConceptId();
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
						// for (String word : words) {
						// System.out.println("          Matched Word : "
						// + word);
						// }

						// cuis.add(cui);
						// phraseText = "<cui value=" + cui + ">" +
						// phraseText
						// + "</cui>";
					}
					// for (String cui : cuis)

				}
				tmpPhraseText = tmpPhraseText + "<cui value=" + cui + ">"
						+ phraseText + "</cui>";
				tmpCUIContext = tmpCUIContext.append(" " + tmpPhraseText);
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

	void test_ExtractMetaMapS4Docs() {
		String root;

		/***
		 * In Windows, the source files are with eclipse project
		 * For notes:
		 * C:/Users/aurora/git/searchUI/searchUI////raw_txt/notes/
		 * C:/Users/aurora/git/searchUI/searchUI////persist/notes/
		 * C:/Users/aurora/git/searchUI/searchUI////raw_extend/notes/
		 * C:/Users/aurora/git/searchUI/searchUI////raw_cui/notes/
		 * 
		 * For questions:
		 * C:/Users/aurora/git/searchUI/searchUI////raw_txt/questions/qa_text/
		 * C:/Users/aurora/git/searchUI/searchUI////persist/questions/qa_json/
		 * C:/Users/aurora/git/searchUI/searchUI////raw_extend/questions/qa_text
		 * /
		 * C:/Users/aurora/git/searchUI/searchUI////raw_cui/questions/qa_text/
		 * 
		 * In Linux, the sourse files are in other directory
		 * 
		 * For notes:
		 * /home/auroral/q_generation////raw_txt/notes/
		 * /home/auroral/q_generation////persist/notes/
		 * /home/auroral/q_generation////raw_extend/notes/
		 * /home/auroral/q_generation////raw_cui/notes/
		 * 
		 * For questions:
		 * /home/auroral/q_generation////raw_txt/questions/qa_text/
		 * /home/auroral/q_generation////persist/questions/qa_json/
		 * /home/auroral/q_generation////raw_extend/questions/qa_text/
		 * /home/auroral/q_generation////raw_cui/questions/qa_text/
		 ***/
		String inputType = "notes";

		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)
			root = "C:/Users/aurora/git/searchUI/searchUI/";
		else
			root = "/home/auroral/q_generation/";

		in_path = root + "raw_txt/notes/";
		out_path = root + "persist/notes/";
		out_extend_path = root + "raw_extend/notes/";
		out_cui_path = root + "raw_cui/notes/";
		complete_path = root + "raw_txt/notes/complete/";

		extractTermbyPathName(in_path, inputType);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EHRMetaMapExtractor extractor = new EHRMetaMapExtractor();
		// extractor.test_ExtractingNotes();
		extractor.test_ExtractMetaMapS4Docs();
	}
}
