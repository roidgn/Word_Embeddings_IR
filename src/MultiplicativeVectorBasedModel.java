import java.util.ArrayList;


public class MultiplicativeVectorBasedModel extends VectorBasedModel{

	@Override   //additive model
	public void computeScoresForImages(ArrayList<Query> queries, ArrayList<Image> images) {

		for(int i=0; i<queries.size(); i++){
			computeScoresForImages(queries.get(i), images);
		}
	}
	
	public void computeScoresForImages(Query q, ArrayList<Image> images){
		
		double[] query_vector = new double[dimension];
		for(int i=0; i<dimension; i++){
			query_vector[i] = 1;      //initialization
		}
		
		//compute the vector for the query
		for(int t=0; t<q.getLength(); t++){       //for each term of the query
			double[] term_vector = model.getVectorFor(q.getTerm(t));
			if(term_vector==null){
				continue;        //ignore the words with no pre-trained vector
			}
			for(int i=0; i<dimension; i++){
				query_vector[i] *= term_vector[i];
			}
		}
		
		for (int i=0; i<images.size(); i++){          //for each image
			for(int s=0; s<images.get(i).getNumOfSentences(); s++){     //for each sentence of the image
			
				Sentence sentence = images.get(i).getSentence(s);
				double[] sentence_vector = new double[dimension];
				for(int d=0; d<dimension; d++){
					sentence_vector[d] = 1;     //initialization
				}
				
				for(int t=0; t<sentence.getLength(); t++){    //for each term of the sentence
					double[] term_vector = model.getVectorFor(sentence.getTerm(t));
					if(term_vector==null){
						continue;
					}
					for(int d=0; d<dimension; d++){
						sentence_vector[d] *= term_vector[d];
					}
				}
				
				Ranking r = new Ranking();
				r.setId(images.get(i).getId());
				double similarity = CosineSimilarity.computeSimilarity(query_vector, sentence_vector);
				//the ranking for this sentence is the cosine similarity between the query vector and sentence vector
				r.setValue(similarity);
				q.addRanking(r);
			}
		}
	}
}
