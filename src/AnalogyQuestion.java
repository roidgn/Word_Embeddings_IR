
public class AnalogyQuestion {  //contains the three words of the analogy question
	
	private String word_a;
	private String word_b;
	private String word_c;

	public AnalogyQuestion(String a, String b, String c) {
		word_a = a;
		word_b = b;
		word_c = c;
	}
	
	public String getWord_a(){
		return word_a;
	}
	
	public String getWord_b(){
		return word_b;
	}
	
	public String getWord_c(){
		return word_c;
	}

}
