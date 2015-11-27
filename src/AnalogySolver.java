import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class AnalogySolver {
	
	private static String pathToVectors = null;
	private static String pathToQuestions = null;
	private static String model = "cosAdd";  //default model is '3CosAdd'
	
	private static int dimension = 0;
	private static RepresentationModel represModel;
	
	private static boolean printAnalogies = false;  //print results
	
	public static void main(String[] args) throws IOException {
		
		readArguments(args);
		
		readVectors();
		findAnalogies();
	}
	
	public static void findAnalogies(){
		
		AnalogyModel analogyModel;
		if(model.equals("cosAdd")){
			analogyModel = new CosAdd();     		//3CosAdd analogy model
		}else if(model.equals("pairDirection")){
			analogyModel = new PairDirection();			//PairDirection analogy model
		}else{
			analogyModel = new CosMul();     //3CosMul analogy model
		}
		
		try (BufferedReader br = new BufferedReader(new FileReader(pathToQuestions))) {
			
			String category = null;
		    String line;
		    while ((line = br.readLine()) != null) {
		    	
		    	String[] tokens = line.split("\\s");
		    	if(tokens[0].equals(":")){				//new category
		    		category = tokens[1];
		    		analogyModel.addNewCategoryForEvaluation(category);  //add new category for evaluation
		    		if(printAnalogies){
		    			System.out.println("Category: " + category);
		    			System.out.println("---------");
		    		}
		    		
		    	}
		    	else{
		    		
		    		AnalogyQuestion currentQuestion = new AnalogyQuestion(tokens[0], tokens[1], tokens[2]);
		    		String correctWord = tokens[3];
		    		
		    		String prediction = analogyModel.getAnalogy(currentQuestion, represModel);
		    		if ((prediction.toLowerCase()).equals(correctWord.toLowerCase())){       //correct
		    			analogyModel.correctExample(category);
		    			if(printAnalogies){
		    				System.out.println("\n"+tokens[0] + " : " + tokens[1] + " = " + tokens[2] + " : ?");
		    				System.out.println("Predicted: " + prediction);
		    				System.out.println("Correct:   " +  correctWord);
		    			}
		    		}
		    		else{     //incorrect
		    			analogyModel.incorrectExample(category);
		    			if(printAnalogies){
		    				System.out.println("\n"+tokens[0] + " : " + tokens[1] + " = " + tokens[2] + " : ?");
		    				System.out.println("Predicted: " + prediction);
		    				System.out.println("Correct:   " +  correctWord);
		    			}
		    		}		    				    																	
		    	}
		         	
		    }
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		analogyModel.printEvaluation();
		
	}
	
	public static void addWordEmbeddingToModel(String line){
		
		String[] tokens = line.split("\\s");
		if(dimension==0){
			dimension = tokens.length - 1;
		}
		
		//create new word emdedding
		WordEmbedding we = new WordEmbedding(tokens[0], dimension);
		for(int i=0; i<dimension; i++){

			we.addValueToVector(i, Double.parseDouble(tokens[i+1]));  //construct the vector
		}
		
		represModel.addWordEmbedding(we);	//add this embedding to the representation model	
	}
	
	public static void readVectors() throws IOException{
				
		try (BufferedReader br = new BufferedReader(new FileReader(pathToVectors))) {
			
			represModel = new RepresentationModel();
			
		    String line;
		    
		    while ((line = br.readLine()) != null) {  //for each line
		         	
		    	addWordEmbeddingToModel(line);	//add this embedding to the representation model	   	 	

		    }
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void readArguments(String[] args){
		
		int i = 0;
		while (i < args.length) {
			String arg = args[i];
			if (arg.equals("-pathToVectors")) {
				pathToVectors = args[i+1];
			}
			else if(arg.equals("-pathToQuestions")){
				pathToQuestions = args[i+1];
			}
			else if(arg.equals("-analogyModel")){
				if (!args[i+1].equals("cosAdd") && !args[i+1].equals("pairDirection") && !args[i+1].equals("cosMul")){
					System.err.println("Invalid option for the analogy model. Supported options: cosAdd, pairDirection, cosMul");
					System.exit(1);
				}
				else{
					model=args[i+1];
				}
			}
			else if(arg.equals("-printAnalogies")){
				printAnalogies = true;
			}
			
			i += 2;
		}
		
		if(pathToVectors==null || pathToQuestions ==null){
			System.err.println("Wrong usage. Try again as follows:");
			System.err.println("java AnalogySolver -pathToVectors pathToVectors -pathToQuestions pathToQuestions");
			System.exit(0);
		}
	}
	
	
}
