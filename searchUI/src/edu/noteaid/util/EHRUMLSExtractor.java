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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.log4j.Logger;

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
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < definitionObj.size(); i++) {
			gov.nih.nlm.uts.webservice.content.DefinitionDTO myDefDTO = definitionObj
					.get(i);
			sb.append(myDefDTO.getValue());
			System.out.println(myDefDTO.getValue());
		}
		return sb.toString();
	}

	public void initialize() {
		utsContentService = (new UtsWsContentControllerImplService())
				.getUtsWsContentControllerImplPort();
		utsSecurityService = (new UtsWsSecurityControllerImplService())
				.getUtsWsSecurityControllerImplPort();
		try {
			ticketGrantingTicket = utsSecurityService.getProxyGrantTicket(username, password);
		} catch (UtsFault_Exception e) {
			e.printStackTrace();
		}
	}

	public void extract_note(String path) {
		initialize();
		System.out.println(getDefinition("2011AB", "C0085096"));
	}

	/***
	 * 
	 * extractTermbyPathName is a method that input text file path and extract
	 * the UMLS
	 * 
	 * There are two doc types: questions, notes
	 * 
	 */


	void test_ExtractUMLS4Docs() {
		initialize();
		System.out.println(getDefinition("2011AB", "C0085096"));

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
