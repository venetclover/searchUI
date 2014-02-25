package edu.noteaid.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class ObjSerializer<V, K> {
	/***
	 * 
	 * Serialize object generated by project in server, as well as deserialize
	 * the file to the object
	 */
	Logger logger;

	public ObjSerializer() {
		logger = LogManager.getLogger("Sericalizer");
	}

	public void serialize(Object o, String filename) {
		logger.debug("Serialize " + o.toString() + " to " + filename);

		Gson gson = new Gson();
		String json = gson.toJson(o);

		try {
			PrintWriter out = new PrintWriter(filename);
			out.print(json);
			out.close();
		} catch (FileNotFoundException e) {

		}
		System.out.println(json.toString());
	}

	public Object deserialize(String filename, Class objClass) {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			StringBuilder sb = new StringBuilder();

			String line = "";
			while ((line = br.readLine()) != null)
				sb.append(line);

			String text = sb.toString();
			Gson gson = new Gson();
			Object rObj = gson.fromJson(text, objClass);

			return rObj;

		} catch (IOException e) {
			return new IOException();
		}

	}

	public void test() {
		testObj x = new testObj();
		String filename = "persist/obj.json";
		serialize(x, filename);
		testObj t = (testObj) deserialize(filename, testObj.class);
		System.out.println(t.onlyStr);
	}

	public static void main(String[] args) {
		ObjSerializer<Object, Object> serializer = new ObjSerializer<Object, Object>();
		serializer.test();

	}

	class testObj {
		Map<String, String> map;
		String onlyStr = "test primitive";

		public testObj() {
			map = new HashMap<String, String>();
			map.put("a", "hello");
			map.put("b", "gson");
		}
	}

}
