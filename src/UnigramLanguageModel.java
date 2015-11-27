import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UnigramLanguageModel implements IRModel {
	
	private double lamda;
	private Map<String, Integer> termsToFrequencies; //maps a word to its frequency in the whole sentences (corpus)
	private int totalTerms;  //total number of terms contained in the corpus (sentences)
	
	public UnigramLanguageModel(){
		termsToFrequencies = new HashMap<String, Integer>();
		totalTerms = 0;
	}
	
	public void setLamda(double l){
		lamda = l;
	}
	
	public void computeScoresForImages(ArrayList<Query> queries, ArrayList<Image> images){
		
		initializeModel(queries, images);
		
		for(int i=0; i<queries.size(); i++){
			computeScoresForImages(queries.get(i), images);
		}
		
	}
	
	//computes rankings of each image for query q
	public void computeScoresForImages(Query q, ArrayList<Image> images){
		
		double a=0.001;
		for (int i=0; i<images.size(); i++){   //for each image
						
			for(int s=0; s<images.get(i).getNumOfSentences(); s++){   //for each sentence of the image
				
				Ranking r = new Ranking();         //create a ranking
				r.setId(images.get(i).getId());    //set as id the image id
				double ranking = 1.0;
			
				Sentence sentence = images.get(i).getSentence(s);  //get the sentence i
				sentence.findNumOfOccurences();    //compute the frequency for each term in the sentence
				
				for(int t=0; t<q.getLength(); t++){  //for each term
					
					int occurences = sentence.getOccurencesOfTerm(q.getTerm(t));  //frequency of the term in the sentence
					// ranking *= (1-lambda) P(t|Mc) + lamda P(t|Md) + a
					// P(t|Mc) = probability of occurence of term t in the corpus
					// P(t|Md) = probability of occurence of term t in the document (sentence)
					// a = to avoid zero values
					ranking *= (1-lamda) * ((double) termsToFrequencies.get(q.getTerm(t).toLowerCase())/totalTerms) + lamda * ((double) occurences/sentence.getLength()) + a;
				}
				
				r.setValue(ranking);
				q.addRanking(r);   //add ranking for this image
			}
		}
		
	}
	
	//initializes the total frequencies for the terms of the queries
	public void initializeModel(ArrayList<Query> queries, ArrayList<Image> images){
		
		for(int i=0; i<queries.size(); i++){
			
			String[] terms = queries.get(i).getTerms();		
			
			//for all the terms of the queries
			for(int j=0; j<terms.length; j++){
				termsToFrequencies.put(terms[j].toLowerCase(), 0);							//initialize all to zero
			}		
		}
		
		for(int i=0; i<images.size(); i++){        //for all images
			
			ArrayList<Sentence> sentences = images.get(i).getSentences();
			
			for(int j=0; j<sentences.size(); j++){			//for each sentence of the image
				
				String[] terms = sentences.get(j).getTerms(); 
				
				totalTerms += terms.length;    //increase the size of the corpus
				
				for(int k=0; k<terms.length; k++){
					
					Object f = termsToFrequencies.get(terms[k].toLowerCase());
					if(f!=null){    //if this term is contained in the queries
						termsToFrequencies.put(terms[k].toLowerCase(), ((int) f + 1));
					}
					
				}
				
			}
			
		}
	
		/*for(String key: termsToFrequencies.keySet()){
			System.out.println("Term: " + key + " Frequency: " + termsToFrequencies.get(key));
		}*/
		
	}

}
