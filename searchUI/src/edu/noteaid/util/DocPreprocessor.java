package edu.noteaid.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import edu.stanford.nlp.process.DocumentPreprocessor;

public class DocPreprocessor {

	void test() {
		String fn = "raw_txt/notes/report4295";
		extractTextFromDoc(fn);

	}

	void extractTextFromDoc(String path) {
		File fp = new File(path);
		try {
			BufferedReader br = new BufferedReader(new FileReader(fp));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.trim().equals(""))
					sb.append("\n");
				else
					sb.append(line + " ");
			}
			br.close();

			String text = sb.toString();
			// System.out.println(text);
			PrintWriter pw = new PrintWriter(path);
			pw.print(text);
			pw.close();

			DocumentPreprocessor dp = new DocumentPreprocessor(path);
			StringBuilder sentBuilder;
			StringBuilder docBuilder = new StringBuilder();
			for (List<?> sentence : dp) {
				sentBuilder = new StringBuilder();
				for (Object e : sentence) {
					String es = e.toString();
					if (es.equals("-LRB-") || es.equals("-RRB-")
							|| es.equals("-LSB-") || es.equals("-RSB-"))
						continue;
					else
						sentBuilder.append(e + " ");
				}
				docBuilder.append((sentBuilder.toString().trim()) + "\n");
			}

			pw = new PrintWriter(path);
			pw.print(docBuilder.toString());
			pw.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		DocPreprocessor processor = new DocPreprocessor();
		processor.test();
	}

}
