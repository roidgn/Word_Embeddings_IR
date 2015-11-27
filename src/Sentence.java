import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Sentence {
	
	private String[] terms;    //all the terms of the sentence
	private Map<String, Integer> termToFrequency;   //maps a term to its frequency in the sentence
	
	public Sentence(String sentence){
		terms = sentence.split("\\s+");
		
		if(terms[0].equals("")){
			String[] new_terms = Arrays.copyOfRange(terms, 1, terms.length);
			terms = new_terms;
		}
		
		termToFrequency = new HashMap<String, Integer>();
	}
	
	public Sentence(String[] t){
		terms = t;		
		termToFrequency = new HashMap<String, Integer>();
	}
	
	public String[] getTerms(){
		return terms;
	}
	
	public int getLength(){
		return terms.length;
	}
	
	public String getTerm(int i){
		return terms[i];
	}
	
	
	//computes the frequency of each term in the sentence
	public void findNumOfOccurences(){
		
		for(int i=0; i<terms.length; i++){
			
			termToFrequency.put(terms[i], 0);
		}
		
		for(int i=0; i<terms.length; i++){
			
			termToFrequency.put(terms[i], (termToFrequency.get(terms[i]) + 1));
		}
	}
	
	public int getOccurencesOfTerm(String term){
		
		for(int i=0; i<terms.length; i++){
			if(terms[i].equals(term))
				return termToFrequency.get(terms[i]);
		}
		
		return 0;
	}

}
