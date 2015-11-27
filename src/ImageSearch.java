import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class ImageSearch {

	private static int NUM_SENTENCES = 5;   //5 sentences per image
	private static String pathToSentences = null;   //file with image ids and sentences (Flickr8k.lemma.token.tx)
	private static String pathToTestFile = null;  //file with image ids for testing (Flickr_8k.testImages.txt )
	private static String IRModel = "ulm";     //default value ulm (Unigram Language Model)
	private static String pathToTrainFile = null;  //training set (Flickr_8k.trainImages.txt)
	private static String pathToDevFile = null;   //development set (Flickr_8k.devImages.txt)
	
	private static double lamda_ulm = 0.5;  //lamda used in unigram language model
	private static double lamda_cmb = 0.5;  //lamda used in compined model
	
	private static String pathToVectors = null;   //vectors for vector based models
	
	private static String aggregationMethod = "single" ;   //default value
	private static int numCandidates = 1;       //default: 1 candidate image
	
	private static ArrayList<Image> images;     //set with all images
	private static ArrayList<Query> queries;     //set with all sentences picked as queries 
	
	private static Set<String> testImageIds;  //set of image ids in the test set
	private static Set<String> restImageIds;    //set of image ids in the training (and/or) development set
	
	private static Map<Query, String> queryToImageId;     //maps a query to the correct image id
	private static Map<Query, Set<String>> results;       //maps a query to a set of top candidate image ids 
	
	public static void main(String[] args) throws IOException {
		
		readArguments(args);
		
		images = new ArrayList<Image>();
		queries = new ArrayList<Query>();
		
		testImageIds = new HashSet<String>();
		restImageIds = new HashSet<String>();
		queryToImageId = new HashMap<Query,String>();
		results = new HashMap<Query, Set<String>>();
		
		readTestImageIds();          //read image ids in the testing set
		
		if(pathToTrainFile!=null){
			readRestImageIds(pathToTrainFile);   //read image ids in the training set
		}
		if(pathToDevFile!=null){
			readRestImageIds(pathToDevFile); //read image ids in the developement set
		}
		
		readSentences();      //read the file with the sentences
				
		if(!IRModel.equals("all")){   
			findImages(IRModel);     //run one model
		}
		else{						//run all the models
			findImages("ulm");       //unigram language model
			initializeRankings();
			findImages("vbma");   //vector based model additive
			initializeRankings();
			findImages("vbmm");      //vector based model multiplicative
			initializeRankings();
			findImages("cmb");		//compined model
		}
		
		
	}
	
	private static void findImages(String model){
		
		computeScoresForImages(model);        //computes the scores for each image based on the 'model'
		findBestScoringImageForEachQuery();     //applies the selected aggregation heuristic
		evaluation();                       //calls the functions for computing Recall@N and MRR
	}
	
	private static void evaluation(){
		
		IRModelEvaluation eval = new IRModelEvaluation();
		eval.computeRecall(queryToImageId, results);
		eval.computeMRR(queries, queryToImageId);
	}
	
	private static void single_AggregationHeurictic(){   //calls the single aggregation heuristic
		
		System.out.println("Single aggregation method.");
		
		for(int i=0; i<queries.size(); i++){
			results.put(queries.get(i), queries.get(i).aggregationHeurictic_Single(numCandidates));
		}
	}
	
	private static void average_AggregationHeurictic(){     //calls the average aggregation heuristic
		
		System.out.println("Average aggregation method.");
		
		for(int i=0; i<queries.size(); i++){
			results.put(queries.get(i), queries.get(i).aggregationHeurictic_Average(numCandidates));
		}
	}
	
	private static void findBestScoringImageForEachQuery(){
		
		if(aggregationMethod.equals("single")){
			single_AggregationHeurictic();
		}
		else if(aggregationMethod.equals("average")){
			average_AggregationHeurictic();
		}
			
	}
	
	private static void computeScoresForImages(String IRModel){
		
		IRModel model;
		
		//creates the selected model
		if(IRModel.equals("ulm")){
			System.out.println("\nUnigram Language Model");
			System.out.println("----------------------");
			model = new UnigramLanguageModel();
			((UnigramLanguageModel) model).setLamda(lamda_ulm);
		}
		else if(IRModel.equals("vbma")){
			System.out.println("\nAdditive Vector Based Model");
			System.out.println("---------------------------");
			model = new AdditiveVectorBasedModel();
			try {
				((AdditiveVectorBasedModel) model).readVectors(pathToVectors);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(IRModel.equals("vbmm")){
			System.out.println("\nMultiplicative Vector Based Model");
			System.out.println("---------------------------------");
			model = new MultiplicativeVectorBasedModel();
			try {
				((MultiplicativeVectorBasedModel) model).readVectors(pathToVectors);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			System.out.println("\nCombined Model");
			System.out.println("--------------");
			model = new CombinedModel();
			((CombinedModel) model).setLamda(lamda_cmb);
			((CombinedModel) model).setLamdaForUnigramModel(lamda_ulm);
			((CombinedModel) model).setPathToVectors(pathToVectors);
		}
		
		model.computeScoresForImages(queries, images);   //computes the scores
	}
	
	private static void initializeRankings(){
		
		for(int i=0; i<queries.size(); i++){
			queries.get(i).newRankings();       //removes previous rankings
		}
	}
	
	
	private static void readRestImageIds(String pathToFile){  //adds all the image ids to the set 'restImageIds'
		
		try (BufferedReader br = new BufferedReader(new FileReader(pathToFile))) {
						
		    String line;
		    
		    while ((line = br.readLine()) != null) {
		    	restImageIds.add(line);
		    }
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void readTestImageIds(){  //adds all the image ids to the set 'testImageIds'
		
		try (BufferedReader br = new BufferedReader(new FileReader(pathToTestFile))) {
						
		    String line;
		    
		    while ((line = br.readLine()) != null) {
		    	testImageIds.add(line);
		    }
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void addQuery(String query, String imageId){
		Query q = new Query(query);        //new query
		queries.add(q);
		
		queryToImageId.put(q, imageId);    //maps the query to the correct image id
	}
	
	private static void readMoreSentences(BufferedReader br, Image image, int random) throws IOException{
		
		String line;
		for(int i=1; i<NUM_SENTENCES; i++){  //read rest sentences
    		
    		line = br.readLine();
    		if(line==null){
    			System.err.println("ERROR: Unexpected end of file.");
    			System.exit(1);
    		}
    		
    		String[] tokens = line.split("\t");
    		
    		String sentence = removeSymbols(tokens[1]);
	    	
	    	if (random == i){        //the random number shows the number of the sentence will be picked as query
	    		addQuery(sentence, image.getId());     //add query sentence
	    	}
	    	else{
	    		image.addSentence(sentence);    //add simple sentence
	    	}  		
    	}
	}
	
	private static void readMoreSentences(BufferedReader br, Image image) throws IOException{
	
		String line;
		for(int i=1; i<NUM_SENTENCES; i++){  //read rest sentences
    		
    		line = br.readLine();
    		if(line==null){
    			System.err.println("ERROR: Unexpected end of file.");
    			System.exit(1);
    		}
    		
    		String[] tokens = line.split("\t");
	    	
    		String sentence = removeSymbols(tokens[1]);
    		
    		image.addSentence(sentence);	//add all sentences as simple sentences (not queries)
    	}
		
	}
	
	private static String removeSymbols(String s){
		String sentence = s.replace(".", " ");
    	sentence = sentence.replace(",", " ");
    	sentence = sentence.replace("#", " ");
    	sentence = sentence.replace("?", " ");
    	sentence = sentence.replace("'", " ");
    	sentence = sentence.replace("-", " ");
    	sentence = sentence.replace(" the ", " ");
    	sentence = sentence.replace("The ", " ");
    	sentence = sentence.replace("A ", " ");
    	sentence = sentence.replace(" a ", " ");
    	sentence = sentence.replace(" in ", " ");
    	sentence = sentence.replace(" on ", " ");
    	sentence = sentence.replace(" of ", " ");
    	return sentence;
	}
	
	private static void readSentences(){
		
		Random randomGenerator = new Random();
		
		try (BufferedReader br = new BufferedReader(new FileReader(pathToSentences))) {
						
		    String line;
		    
		    while ((line = br.readLine()) != null) {    
		    	
		    	String[] tokens = line.split("\t");  //split the line to get imageId#n  and sentence 
		    	String[] subtokens = tokens[0].split("#");  //split imageId#n to imageId and n
		    		    	
		    	Image image = new Image(subtokens[0]);  //new image, first sentence
		    	String sentence = removeSymbols(tokens[1]);
		    	int random;
		    	if(testImageIds.contains(subtokens[0])){  //if this image belongs to test set
		    		random = randomGenerator.nextInt(NUM_SENTENCES);     //pick a random sentence from this image
		    	
			    	if (random == 0){    //the sentence I just read
			    		addQuery(sentence, image.getId());  //add this sentence to the queries
			    		readMoreSentences(br,image);    //read rest 4 sentences for this image
			    	}
			    	else{
			    		image.addSentence(sentence);      //add this sentence to the set of sentences
			    		readMoreSentences(br,image,random);  //read rest 4 sentences for this image
			    	}
		    	}
		    	else if(restImageIds.contains(subtokens[0])){ //if this image is not in the test set
		    		image.addSentence(sentence);         //add this sentence to the set of sentences
		    		readMoreSentences(br,image);         //read rest 4 sentences for this image
		    	}
		    	
		    	images.add(image);       //add this image to the set of images
		    }
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void readArguments(String[] args){
		
		int i = 0;
		while (i < args.length) {
			String arg = args[i];
			if(arg.equals("-help")){
				System.out.println("\nUsage: java ImageSearch -pathToTestFile pathToTestFile -pathToSentences pathToSentences");
				System.out.println("\nOther possible parameters:");
				System.out.println("-IRModel         ulm / vbma / vbmm / cm / all (defalult = ulm)");
				System.out.println("-pathToVectors   (not for 'ulm' model)");
				System.out.println("-lamba_cmb           (double value [0..1])");
				System.out.println("-lamba_uml           (double value [0..1])");
				System.out.println("-aggregationMethod    single / average  (default = single)");
				System.out.println("-numCandidates    (integer value, default = 1)");
				System.exit(0);
				
			}
			if (arg.equals("-pathToVectors")) {
				pathToVectors = args[i+1];
			}
			else if(arg.equals("-pathToTestFile")){
				pathToTestFile = args[i+1];
			}
			else if(arg.equals("-pathToSentences")){
				pathToSentences = args[i+1];
			}
			else if(arg.equals("-pathToTrainFile")){
				pathToTrainFile = args[i+1];
			}
			else if(arg.equals("-pathToDevFile")){
				pathToDevFile = args[i+1];
			}
			else if(arg.equals("-numCandidates")){
				
				numCandidates = Integer.parseInt(args[i+1]);
			}
			else if(arg.equals("-lamda_ulm")){
				
				lamda_ulm = Double.parseDouble(args[i+1]);
				if (lamda_ulm<0 || lamda_ulm >1){
					System.err.println("ERROR: Invalid value for 'lamda_ulm'.\n'lamda_ulm' must be in the range [0..1]");
					System.exit(1);
				}
			}
			else if(arg.equals("-lamda_cmb")){
				
				lamda_cmb = Double.parseDouble(args[i+1]);
				if (lamda_cmb<0 || lamda_cmb >1){
					System.err.println("ERROR: Invalid value for 'lamda_cmb'.\n'lamda_cmb' must be in the range [0..1]");
					System.exit(1);
				}
			}
			else if(arg.equals("-IRModel")){
				if (!args[i+1].equals("ulm") && !args[i+1].equals("vbma") && !args[i+1].equals("vbmm") && !args[i+1].equals("cmb") && !args[i+1].equals("all")){
					System.err.println("ERROR: Invalid option for 'IRmodel'. \n\nSupported options:\nulm (Unigram Language Model)\n" +
							"vbma (Vector Based Model Additive)\nvbmm (Vector Based Model Multiplicative)\ncmb (Combined Model)\nall (all the previous models)");							
					System.exit(1);
				}
				else{
					IRModel=args[i+1];
				}
			}
			else if(arg.equals("-aggregationMethod")){
				if (!args[i+1].equals("single") && !args[i+1].equals("average")){
					System.err.println("ERROR: Invalid option for 'aggregationMethod'. \n\nSupported options:\nsingle (image with the best single scoring sentence)\n" +
							"average (image with the best average scroring of sentences)");							
					System.exit(1);
				}
				else{
					aggregationMethod=args[i+1];
				}
			}
			
			i += 2;
		}
		
		if(pathToTestFile==null || pathToSentences==null){
			System.err.println("ERROR: The path to testFile/sentences was not given.");
			System.err.println("\nUsage: java ImageSearch -pathToTestFile pathToTestFile -pathToSentences pathToSentences");
			System.err.println("\nOther possible parameters:");
			System.err.println("-IRModel         ulm / vbma / vbmm / cmb / all (defalult = ulm)");
			System.err.println("-pathToVectors   (not for 'ulm' model)");
			System.err.println("-lamba_cmb           (double value [0..1], default = 0.5)");
			System.err.println("-lamba_uml           (double value [0..1], default = 0.5)");
			System.err.println("-aggregationMethod    single / average  (default = single)");
			System.err.println("-numCandidates    (integer value, default = 1)");
			System.exit(1);
		}
		
		if(!IRModel.equals("ulm") && pathToVectors==null){
			System.err.println("ERROR: IRModel '"+IRModel + "' needs a vector represenation. Please specify the path to vectors.");
			System.err.println("\nUsage: java ImageSearch -pathToFile pathToFile -IRModel irModel -pathToVectors pathToVectors");
			System.exit(1);
		}
	}

}
