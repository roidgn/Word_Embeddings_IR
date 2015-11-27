import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PairDirection implements AnalogyModel{

	private AnalogyModelEvaluation totalEvaluation; //recall for all the examples
	private Map<String, AnalogyModelEvaluation> categoryToEvaluation;  //recall for each category
	
	public PairDirection() {
		totalEvaluation = new AnalogyModelEvaluation("All");
		categoryToEvaluation = new HashMap<String, AnalogyModelEvaluation>();
	}
	
	@Override
	public String getAnalogy(AnalogyQuestion question, RepresentationModel model) {
		
		if(question.getWord_a().equals(question.getWord_c())){   //if a==c , return b
			return question.getWord_b();
		}
		if(question.getWord_a().equals(question.getWord_b())){   //if a==b, return c
			return question.getWord_c();
		}
		
		//get the vectors for each word 
		double[] vector_a = model.getVectorFor(question.getWord_a());
		double[] vector_b = model.getVectorFor(question.getWord_b());
		double[] vector_c = model.getVectorFor(question.getWord_c());
		if(vector_a==null || vector_b==null || vector_c==null){
			return null;
		}
		
		String similarWord=new String();
		double maxSimilarity = 0.0;
		ArrayList<WordEmbedding> wordEmbeddings = model.getWordEmbeddings();  //get all possible words
		int totalEmbeddings = wordEmbeddings.size();
		double[] diff_a = computeDiffVector(vector_b, vector_a);  // vector b-a
		for (int i=0; i<totalEmbeddings; i++){
			
			WordEmbedding we = wordEmbeddings.get(i);
			double[] diff_b = computeDiffVector(we.getWordVector(), vector_c);  //word_vector - c
			
			double sim = CosineSimilarity.computeSimilarity(diff_b, diff_a);  //cos similarity between these vectors
			
			//if similarity is higher and this word is not one of the a,b,c
			if (sim > maxSimilarity && notTheSameWord(question, we.getWord())){
				similarWord = we.getWord();  //save the word
				maxSimilarity = sim;
			}
		}
		
		return similarWord;
	}

	
	public double[] computeDiffVector(double[] b, double[] a){
		double[] vector = new double[a.length];
		
		for(int i=0; i<vector.length; i++){
			vector[i] = b[i] - a[i];
		}
		
		return vector;
	}
	
	//checks that d!=a and d!=b and d!=c (we have already checked that a!=c and a!=b, so d must be a different word)
	private boolean notTheSameWord(AnalogyQuestion question, String word){
		
		String lc_word = word.toLowerCase();
		String lc_a = question.getWord_a().toLowerCase();
		String lc_b = question.getWord_b().toLowerCase();
		String lc_c = question.getWord_c().toLowerCase();
		
		if(!lc_word.equals(lc_a) && !lc_word.equals(lc_b) && !lc_word.equals(lc_c)){
			return true;
		}
		
		return false;
	}

	@Override
	public void addNewCategoryForEvaluation(String category) {
		AnalogyModelEvaluation eval = new AnalogyModelEvaluation(category);
		categoryToEvaluation.put(category, eval);
	}
	
	@Override
	public void correctExample(String category) {
		//System.out.println("Correct");
		if (category!=null){
			categoryToEvaluation.get(category).increaseCorrectExamples();
			categoryToEvaluation.get(category).increaseTotalExamples();
		}
		totalEvaluation.increaseCorrectExamples();
		totalEvaluation.increaseTotalExamples();
	}
	
	@Override
	public void incorrectExample(String category) {
		//System.out.println("Incorrect");
		if (category!=null){
			categoryToEvaluation.get(category).increaseTotalExamples();
		}
		totalEvaluation.increaseTotalExamples();
	}
	
	@Override
	public void printEvaluation(){
		System.out.println("Evaluation for analogy model: PairDirection");
		totalEvaluation.printRecall();  //prints total recall
		System.out.println();
		for(AnalogyModelEvaluation eval : categoryToEvaluation.values()){
			eval.printRecall();     //prints recall for each category
			System.out.println();
		}	
	}

}
