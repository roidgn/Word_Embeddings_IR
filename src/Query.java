import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class Query extends Sentence{

	ArrayList<Ranking> rankingsPerSentence;  // a ranking for each sentence ('single' aggregation technique)
	ArrayList<Ranking> rankingsPerImage;   //a ranking per image ('average' aggregation technique)
											
	
	public Query(String sentence) {
		super(sentence);
		rankingsPerSentence = new ArrayList<Ranking>();
		rankingsPerImage = new ArrayList<Ranking>();
	}
	
	public Query(String[] terms) {
		super(terms);
		rankingsPerSentence = new ArrayList<Ranking>();
		rankingsPerImage = new ArrayList<Ranking>();
	}
	
	public void addRanking(Ranking r){
		rankingsPerSentence.add(r);
	}
	
	public void newRankings(){
		rankingsPerSentence = new ArrayList<Ranking>();
		rankingsPerImage = new ArrayList<Ranking>();
	}
	
	public ArrayList<Ranking> getRankings(){
	
		return rankingsPerSentence;
	}
	
	/**
	 * 
	 * @param numCandidates: the number of best scoring images I want to retrieve as result for each query
	 * @return a set with the image id(s) (as many as the 'numCandidates')
	 */
	public HashSet<String> aggregationHeurictic_Single(int numCandidates){
		
		//sort in reverse order according to the ranking score
		Collections.sort(rankingsPerSentence, Collections.reverseOrder(new RankingComparator()));
		
		HashSet<String> set = new HashSet<String>();
		for(int i=0; i<numCandidates; i++){     
			set.add(rankingsPerSentence.get(i).getId()); //add to the set the top 'numCandidates' image ids
		}
		return set; 
	}
	
	
	/**
	 * 
	 * @param numCandidates: the number of best scoring images I want to retrieve as result for each query
	 * @return a set with the image id(s) (as many as the 'numCandidates')
	 */
	public HashSet<String> aggregationHeurictic_Average(int numCandidates){
		
		Map<String, Double> imageIdToRankings = new HashMap<String, Double>();
		Map<String, Integer> imageIdToCount = new HashMap<String, Integer>();
		
		for(int i=0; i< rankingsPerSentence.size(); i++){    
			imageIdToRankings.put(rankingsPerSentence.get(i).getId(), 0.0);  //initialize to 0
			imageIdToCount.put(rankingsPerSentence.get(i).getId(), 0);
		}
		
		for(int i=0; i< rankingsPerSentence.size(); i++){
			
			String imageId = rankingsPerSentence.get(i).getId();
			//add the ranking score for this image 
			imageIdToRankings.put(imageId , (imageIdToRankings.get(imageId)+rankingsPerSentence.get(i).getValue()));
			//increase the number of rankings for this image
			imageIdToCount.put(imageId , (imageIdToCount.get(imageId)+1));
		}
		
		for(String imageId: imageIdToRankings.keySet()){
			Ranking r = new Ranking();
			r.setId(imageId);   //set image id
			//find the average ranking score
			r.setValue(imageIdToRankings.get(imageId)/imageIdToCount.get(imageId));
			rankingsPerImage.add(r);
		}
		
		//sort in reverse order according to the ranking score
		Collections.sort(rankingsPerImage, Collections.reverseOrder(new RankingComparator()));
		
		HashSet<String> set = new HashSet<String>();
		for(int i=0; i<numCandidates; i++){
			set.add(rankingsPerImage.get(i).getId());  //add to the set the top 'numCandidates' image ids
		}
		return set;	
	}
	
	public int getRankPositionOfImage(String imageId){
		int rank = 0;
		
		ArrayList<Ranking> rankings;
		if(rankingsPerImage.size()==0){    //single aggregation
			rankings = rankingsPerSentence;
		}
		else{                              //average aggregation
			rankings = rankingsPerImage;
		}
		
		for(int i=0; i<rankings.size(); i++){
			
			if(rankings.get(i).getId().equals(imageId)){
				return i+1;
			}
		}
		
		if(rank==0){
			System.err.println("ERROR: Image with id " + imageId + " is not in the list with possible documents.");
			System.exit(1);
		}
		return rank;
	}
	
}
