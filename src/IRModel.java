import java.util.ArrayList;


public interface IRModel {  //interface for the various Information Retrieval models
	
	//computes the rankings scores for each sentence
	public void computeScoresForImages(ArrayList<Query> queries, ArrayList<Image> images);
	
}
