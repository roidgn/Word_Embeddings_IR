
public class WordEmbedding {   //contains a word and its vector

	String word;      
	double[] wordVector; 

	public WordEmbedding(String text, int dimension) {
		
		word = new String(text);
		wordVector = new double[dimension];
	}
	
	public String getWord() {
		return word;
	}
	
	public double[] getWordVector() {
		return wordVector;
	}
	
	public void addValueToVector(int index, double value){
	
		wordVector[index]=value;
	}

}
