package edu.noteaid.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class SimilarityCalculator {

	private final Set<String> terms = new HashSet<>();
	
	Map<String, Integer> getTermFrequencies(HashMap<String, Integer> doc) throws IOException {
        
        Map<String, Integer> frequencies = new HashMap<>();
        for (Map.Entry<String, Integer> pair: doc.entrySet()) {
            String term = pair.getKey();
            int freq = pair.getValue();
            frequencies.put(term, freq);
            terms.add(term);
        }
        
        return frequencies;
    }
	
	double getCosineSimilarity(RealVector v1, RealVector v2) {
        return (v1.dotProduct(v2)) / (v1.getNorm() * v2.getNorm());
    }
	
    public RealVector toRealVector(Map<String, Integer> map) {
        RealVector vector = new ArrayRealVector(terms.size());
        int i = 0;
        for (String term : terms) {
            int value = map.containsKey(term) ? map.get(term) : 0;
            vector.setEntry(i++, value);
        }
        return (RealVector) vector.mapDivide(vector.getL1Norm());
    }
	
	public void setTermsHashMap(ArrayList<HashMap<String, Integer>> docs){
		for (HashMap<String, Integer> doc: docs){
			try {
				getTermFrequencies(doc);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}


}
