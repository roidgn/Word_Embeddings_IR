interface AnalogyModel {     //interface for the analogy models
	
    public String getAnalogy(AnalogyQuestion question, RepresentationModel model);
    
    public void addNewCategoryForEvaluation(String category);
    public void correctExample(String category);
    public void incorrectExample(String category);
	public void printEvaluation();
}