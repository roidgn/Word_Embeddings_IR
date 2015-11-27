import java.io.IOException;
import java.util.ArrayList;


public class CombinedModel implements IRModel {  //combines the unigram language model with the additive vector model

	private double lamda;
	private UnigramLanguageModel unigramModel;
	private AdditiveVectorBasedModel vectorModel;
	
	
	public CombinedModel(){
		unigramModel = new UnigramLanguageModel();
		vectorModel = new AdditiveVectorBasedModel();
	}
	
	public void setLamda(double l){
		lamda = l;
	}
	
	public void setLamdaForUnigramModel(double l){
		unigramModel.setLamda(l);
	}
	
	public void setPathToVectors(String path){
		try {
			vectorModel.readVectors(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void computeScoresForImages(ArrayList<Query> queries, ArrayList<Image> images) {
		
		ArrayList<Query> queries_unigram = new ArrayList<Query>();
		ArrayList<Query> queries_vector = new ArrayList<Query>();
		
		for(int i=0; i<queries.size(); i++){   //initialize the queries for the two models
			Query q_unigram = new Query(queries.get(i).getTerms());
			Query q_vector = new Query(queries.get(i).getTerms());
			
			queries_unigram.add(q_unigram);
			queries_vector.add(q_vector);
		}
		
		//compute ranking scores for each model
		unigramModel.computeScoresForImages(queries_unigram, images);
		vectorModel.computeScoresForImages(queries_vector, images);
		
		//normalize the rankings to the interval [0,1]
		for(int i=0; i<queries.size(); i++){
			normalizeRankings(queries_unigram.get(i).getRankings());
			normalizeRankings(queries_vector.get(i).getRankings());
		}
		
		for(int i=0; i<queries.size(); i++){  //for each query
			Query q_unigram = queries_unigram.get(i);
			Query q_vector = queries_vector.get(i);
			
			for(int j=0; j<queries_unigram.get(i).getRankings().size(); j++){		
				Ranking r = new Ranking();
				double ranking_unigram = q_unigram.getRankings().get(j).getValue();
				double ranking_vector = q_vector.getRankings().get(j).getValue();
				double ranking = lamda * ranking_unigram + (1-lamda) * ranking_vector;  //combine the two rankings
				
				r.setId(q_unigram.getRankings().get(j).getId());
				
				//error checking
				if(q_unigram.getRankings().get(j).getId()!=q_vector.getRankings().get(j).getId()){
					System.err.println("ERROR: Compined Model: Different image ids were compined");
					System.exit(1);
				}
				r.setValue(ranking);
				queries.get(i).addRanking(r);  //add the compined ranking for this sentence
			}
		}
	}
	
	//normalize to the interval [0,1]
	//normalized value = (value - min) / (max - min) 
	private void normalizeRankings(ArrayList<Ranking> rankings){
		
		double min=rankings.get(0).getValue();
		double max=rankings.get(0).getValue();
		for(int i=1; i<rankings.size(); i++){
			if(rankings.get(i).getValue() < min){
				min = rankings.get(i).getValue();  //find min
			}
			else if(rankings.get(i).getValue() > max){
				max = rankings.get(i).getValue();       //find max
			}
		}
		
		for(int i=0; i<rankings.size(); i++){
			double normalizedValue = (rankings.get(i).getValue() - min)/(max - min);
			rankings.get(i).setValue(normalizedValue);
		}
		
	}

}
