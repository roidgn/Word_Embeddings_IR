import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class IRModelEvaluation {
	
	/**
	 * Computes Recall@N
	 * @param queryToImageId: maps a query to the correct image id
	 * @param results: maps a query to a set of N candidates (Set<String>)
	 */
	public void computeRecall(Map<Query, String> queryToImageId, Map<Query, Set<String>> results){
		
		int correctExamples = 0;
		for(Query query: results.keySet()){   //for each query
			
			//if the N candidates contain the true image id
			if(results.get(query).contains(queryToImageId.get(query))){
				correctExamples++;     //increase the number of correct examples
			}
		}
		
		System.out.println("Recall: " + ((double) correctExamples/results.size()));
	}
	
	/**
	 * Computes Mean Reciprocal Rank (MRR)
	 * @param queries: all the queries
	 * @param queryToImageId: maps a query to the correct image id
	 */
	public void computeMRR(ArrayList<Query> queries, Map<Query, String> queryToImageId){
		
		int numOfQueries = queries.size();
		double sum = 0.0;
		for(int i=0; i<numOfQueries; i++){ //for each query
		
			// sum += 1/rank position of the true image id
			sum += (double) 1/queries.get(i).getRankPositionOfImage(queryToImageId.get(queries.get(i)));
		}
		
		double mrr = sum/numOfQueries;
		System.out.println("MRR: " + mrr);
	}

}
