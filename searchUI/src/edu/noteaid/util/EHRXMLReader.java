package edu.noteaid.util;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class EHRXMLReader extends DefaultHandler { 
	 
	String note = "";
	boolean isnote = false;
	
	public EHRXMLReader() { 
		super();	
	} 

	public void initParsing(String fpath) { 
		try { 
			SAXParserFactory sf = SAXParserFactory.newInstance(); 
			SAXParser sp = sf.newSAXParser();  
//			System.out.println(fpath);
			sp.parse(fpath, this); 
//			System.out.println(this.note);
			
			Path p = Paths.get(fpath);
			String newFilename = "raw_txt/notes/" + p.getFileName().toString().replace(".xml", "");
			PrintWriter out = new PrintWriter(newFilename);
			out.print(note);
			out.close();
			
			DocPreprocessor dp = new DocPreprocessor();
			dp.extractTextFromDoc(newFilename);
		} catch (Exception e) { 
			e.printStackTrace(); 
		} 
	
	}

	public void startElement(String uri, String localName,String qName, Attributes attributes){
		if (qName.equalsIgnoreCase("report_text")) {
			isnote = true;
		}
	}
	
	public void characters(char ch[], int start, int length) throws SAXException {  
		if (isnote) { 
		//	System.out.print("" + new String(ch, start, length)); 
			note = note + new String(ch, start, length);
		} 
	}

	public static void main(String[] args) {
		String testfile = "t_records/report4422.xml";
		EHRXMLReader reader = new EHRXMLReader();
		reader.initParsing(testfile);
	}
}
