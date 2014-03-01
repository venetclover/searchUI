package edu.noteaid.util;

import edu.noteaid.question.TermTypeHashFile;
import gov.nih.nlm.nls.metamap.*;
import gov.nih.nlm.uts.webservice.content.DefinitionDTO;
import gov.nih.nlm.uts.webservice.content.Psf;
import gov.nih.nlm.uts.webservice.content.TermStringDTO;
import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsFault_Exception;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class uses the umls metamap utility and tags the input text
 * 
 * @author Aurora
 */
public class EHRUMLSExtractor {

	private String username = "uwmbiodlp";
	private String password = "Uwmpword2011";

	private String serviceName = "http://umlsks.nlm.nih.gov";

	UtsWsContentController utsContentService;
	UtsWsSecurityController utsSecurityService;
	String ticketGrantingTicket;

	String input_path;
	String out_extend_path;

	private String getProxyTicket() {
		try {
			return utsSecurityService.getProxyTicket(ticketGrantingTicket,
					serviceName);
		} catch (Exception e) {
			System.err.println("Error getting ticket.");
			return "";
		}
	}

	public String getDefinition(String version, String cui) {
		String ticket = getProxyTicket();

		Psf psf = new Psf();
		ArrayList<DefinitionDTO> definitionObj = null;
		try {
			definitionObj = (ArrayList<DefinitionDTO>) utsContentService
					.getConceptDefinitions(ticket, version, cui, psf);
		} catch (gov.nih.nlm.uts.webservice.content.UtsFault_Exception e) {
			e.printStackTrace();
			return null;
		}

		StringBuilder sb = new StringBuilder();
		/*
		 * for (int i = 0; i < definitionObj.size(); i++) {
		 * gov.nih.nlm.uts.webservice.content.DefinitionDTO myDefDTO =
		 * definitionObj
		 * .get(i);
		 * sb.append(myDefDTO.getValue());
		 * System.out.println(myDefDTO.getValue());
		 * }
		 */
		if (definitionObj.size() > 0) {
			gov.nih.nlm.uts.webservice.content.DefinitionDTO myDefDTO = definitionObj
					.get(0);
			sb.append(myDefDTO.getValue());
		}
		return sb.toString();
	}
	public void initialize() {
		utsContentService = (new UtsWsContentControllerImplService())
				.getUtsWsContentControllerImplPort();
		utsSecurityService = (new UtsWsSecurityControllerImplService())
				.getUtsWsSecurityControllerImplPort();
		try {
			ticketGrantingTicket = utsSecurityService.getProxyGrantTicket(
					username, password);
		} catch (UtsFault_Exception e) {
			e.printStackTrace();
		}
	}

	public void extract_note(String path) {
		initialize();
		System.out.println(getDefinition("2011AB", "C0221138"));
	}

	/***
	 * 
	 * extractTermbyPathName is a method that input text file path and extract
	 * the UMLS
	 * 
	 * There are two doc types: questions, notes
	 * 
	 */
	String getDefinitionAppended(String filename) {
		File input = new File(filename);

		try {
			Document doc = Jsoup.parse(input, "UTF-8");
			Elements cuis = doc.select("cui");

			for (Element cuiE : cuis) {
				String cui = cuiE.attr("value");

				System.out.println("O: " + cuiE.text());

				String def = getDefinition("2011AB", cui);
				if (def.equals("") || def.equals(null))
					continue;
				else
					cuiE.append(" " + getDefinition("2011AB", cui));
				System.out.println("U: " + cuiE.text());
			}

			return doc.text();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	void extractCUIDocByPath(String path, String docType) {
		File pf = new File(path);
		if (pf.isDirectory()) {
			for (File afile : pf.listFiles())
				extractCUIDocByPath(afile.getAbsolutePath(), docType);
		} else {

			Path pn = Paths.get(path);
			String new_fn = out_extend_path + pn.getFileName().toString();
			try (PrintWriter out = new PrintWriter(new_fn)) {
				out.write(getDefinitionAppended(path));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	void test_ExtractUMLS4Docs() {
		initialize();
		String root;
		String inputType = "notes";

		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)
			root = "C:/Users/aurora/git/searchUI/searchUI/";
		else
			root = "/home/auroral/q_generation/";
		input_path = root + "raw_cui/notes/";
		out_extend_path = root + "raw_extend/notes/";

		extractCUIDocByPath(input_path, inputType);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EHRUMLSExtractor extractor = new EHRUMLSExtractor();
		// extractor.test_ExtractingNotes();
		extractor.test_ExtractUMLS4Docs();
	}
}
