import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public abstract class VectorBasedModel implements IRModel {  //reads the vectors

	RepresentationModel model;  //contains the word embeddings (word and vectors)
	int dimension;
	
	public VectorBasedModel(){
		model = new RepresentationModel();
		dimension = 0;
	}
	
	@Override  
	public void computeScoresForImages(ArrayList<Query> queries, ArrayList<Image> images) {

	}
				
	
	public  void readVectors(String pathToVectors) throws IOException{
		
		try (BufferedReader br = new BufferedReader(new FileReader(pathToVectors))) {
						
		    String line;
		    
		    while ((line = br.readLine()) != null) {
		         	
		    	String[] tokens = line.split("\\s+");
				if(dimension==0){
					dimension = tokens.length - 1;
				}
				
				WordEmbedding we = new WordEmbedding(tokens[0], dimension);  //new word
				for(int i=0; i<dimension; i++){
					we.addValueToVector(i, Double.parseDouble(tokens[i+1]));  //construct vector
				}
				
				model.addWordEmbedding(we); //add it to the model	   	 	

		    }
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
