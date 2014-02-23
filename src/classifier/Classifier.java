package classifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datumbox.opensource.classifiers.NaiveBayes;
import com.datumbox.opensource.dataobjects.NaiveBayesKnowledgeBase;

public class Classifier {

	private NaiveBayes nb = null;
	private Map<String, String[]> trainingDataset = null;
	private Map<Integer, Integer> classified = null;
	private NaiveBayesKnowledgeBase knowledgeBase = null;

	public Classifier() {
		nb = new NaiveBayes();
		trainingDataset = new HashMap<>();
		classified = new HashMap<>();
	}
	
	//Method to take genre words and convert them into string array.
	public String[] readLines(List<String> words) {
		return words.toArray(new String[words.size()]);
	}
	
	
	/*
	 * Process
	 */
	
	//Create Datasets & Load examples into memory
		/*
		 * -Get list of genres
		 * -For each genre, get all words from thesaurus and convert these to string array
		 * -Put all this info into map<String, String[]>
		 */
	
	public void addToDataset(String genre, String[] words) {
		trainingDataset.put(genre, words);
	}

	
	//Train classifier (feature selection?)
		/* NaiveBayes nb = new NaiveBayes();
         * nb.setChisquareCriticalValue(6.63); //May need to alter NB class to remove this feature selection being used for now
         * nb.train(trainingExamples);
         */
	public void trainClassifier() {
		nb.train(trainingDataset);
	}
	//Get the knowledge base for the trained classifier
		/*
		 * NaiveBayesKnowledgeBase knowledgeBase = nb.getKnowledgeBase();
		 */
	public void setKnowledgeBase() {
		knowledgeBase = nb.getKnowledgeBase();
	}
	//Reset classifier and training set
		/*
		 * nb = null;
		 * trainingExamples = null;
		 */
	public void resetClassifier() {
		 nb = null;
		 trainingDataset = null;
	}
	
	
	//Use the classifier by giving it the trained knowledge base
		/*
		 * nb = new NaiveBayes(knowledgeBase);
		 * String output = nb.predict(String)
		 */
	public void prepClassifier() {
		nb = new NaiveBayes(knowledgeBase);
	}
	
	public String classifyData(String overview) {
		String output = nb.predict(overview);
		return output;
	}
	
	public void setClassified(int filmid, int genreid) {
		classified.put(filmid, genreid);
	}
	
	public Map<Integer, Integer> getClassifiedData() {
		return classified;
	}
	
}
