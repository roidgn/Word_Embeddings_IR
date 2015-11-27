import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RepresentationModel {

	private Map<String, double[]> textToVectors;  //maps words to their vectors
	ArrayList<WordEmbedding> embeddings;    //an array of word embeddings
	
	public Map<String, double[]> getTextToVectors() {
		return textToVectors;
	}

	public RepresentationModel() {
		
		embeddings = new ArrayList<WordEmbedding>();
		textToVectors = new HashMap<String, double[]>();
	}
	
	public void addWordEmbedding(WordEmbedding we){
	
		embeddings.add(we);
		textToVectors.put(we.getWord(), we.getWordVector());
	}
	
	public ArrayList<WordEmbedding> getWordEmbeddings(){
		return embeddings;
	}
	
	public double[] getVectorFor(String word){
		
		double[] vector = textToVectors.get(word);  //get the vector
		if(vector==null){   //if there is no vector for this word
			vector = textToVectors.get(word.toLowerCase());  //check the lower case
			if(vector==null){
				//System.out.println("No vector found for word '" + word + "'");
				return null;
			}
			
			return vector;
		}
		return vector;
	}
			
}
