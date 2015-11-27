
public class AnalogyModelEvaluation {

	private String category;  //the name of the category
	private int total_examples;
	private int correct_examples;
	
	public AnalogyModelEvaluation(String cat) {
		
		category = new String(cat);
		total_examples = 0;
		correct_examples = 0;
	}
	
	public void increaseTotalExamples(){
		total_examples++;
	}
	
	public void increaseCorrectExamples(){
		correct_examples++;
	}
	
	public void printRecall(){
		System.out.println("Category: " + category);
		System.out.println("-------------------");
		System.out.println("Recall: " +  ((double) correct_examples/total_examples));
	}

}
