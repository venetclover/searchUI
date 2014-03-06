package edu.noteaid.question;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import edu.noteaid.util.EHRMetaMapExtractor;
import edu.noteaid.util.ObjSerializer;
import edu.noteaid.util.SimilarityCalculator;

public class QuestionFinderByGroups {

	static ObjSerializer serializer;

	public QuestionFinderByGroups() {
		serializer = new ObjSerializer();
	}

	/***
	 * While becoming part of website, the user will input the notes from
	 * textFeild, so there will be preprocessing for the note
	 */

	public void startFindingbyNoteText(String noteTxt) {
		EHRMetaMapExtractor extractor = new EHRMetaMapExtractor();
		String notePath = extractor.extractTermbyText(noteTxt);
		startFindingbyNotePath(notePath);
	}

	public void startFindingbyNotePath(String notePath) {

		String JSONFilename = "persist/notes/"
				+ FilenameUtils.getBaseName(notePath);
		TermTypeHashFile doc = (TermTypeHashFile) serializer.deserialize(
				JSONFilename, TermTypeHashFile.class);
		TermHashDoc ttDoc = new TermHashDoc();
		HashMap<String, Integer> termSems = (HashMap<String, Integer>) ttDoc.termSemMap;
		if (termSems == null) {
			ttDoc.processTermSem(doc.term2type);
			termSems = (HashMap<String, Integer>) ttDoc.getTermSemMap();
		}

		SimilarityCalculator calculator = new SimilarityCalculator();
		calculator.toRealVector(termSems);
		// Order

		// Generate Results
	}

	public static void main(String[] args) {
		QuestionFinderByGroups qfg = new QuestionFinderByGroups();
		qfg.startFindingbyNotePath("raw_text/notes/report4244");

	}

}
