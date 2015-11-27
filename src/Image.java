import java.util.ArrayList;


public class Image {

	private String id;      //image id
	private ArrayList<Sentence> sentences;  //list with 4 or 5 sentences
	
	public Image(String s) {
		id = new String(s);
		sentences = new ArrayList<Sentence>();
	}
	
	public String getId(){
		return id;
	}
	
	public void addSentence(String s){
		Sentence sent = new Sentence(s);
		sentences.add(sent);
	}
	
	public ArrayList<Sentence> getSentences(){
		return sentences;
	}
	
	public int getNumOfSentences(){
		return sentences.size();
	}
	
	public Sentence getSentence(int i){
		return sentences.get(i);
	}

}
