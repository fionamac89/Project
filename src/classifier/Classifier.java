package classifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datumbox.opensource.classifiers.NaiveBayes;
import com.datumbox.opensource.dataobjects.NaiveBayesKnowledgeBase;
/**
 * The purpose of this class is to interface with the Datumbox Naive Bayes classification packages
 * and to make the necessary functionality available to the main system that has been developed.
 * 
 * This class allows for the creation of a 'dataset' in the format used by the Datumbox classifier,
 * creation and training of the classifier as well as performing classification and the ability to 
 * return the classified data to the main system for entry into the database.
 * 
 * @author Fiona MacIsaac
 *
 */
public class Classifier {

	private NaiveBayes nb = null;
	private Map<String, String[]> trainingDataset = null;
	private Map<Integer, Integer> classified = null;
	private NaiveBayesKnowledgeBase knowledgeBase = null;

	public Classifier() {
		nb = new NaiveBayes();
		trainingDataset = new HashMap<String, String[]>();
		classified = new HashMap<Integer, Integer>();
	}
	
	/**
	 * Taken from com.datumbox.opensource.features TextTokenizer.java
	 * 
	 * Puts the input of a list of strings into a string array for use by
	 * the classifier.
	 * 
	 * @param words
	 * @return
	 */
	public String[] readLines(List<String> words) {
		return words.toArray(new String[words.size()]);
	}

	/**
	 * Adds the appropriate string array of words to the dataset map
	 * with the respective genre as the key.
	 * 
	 * @param genre
	 * @param words
	 */
	public void addToDataset(String genre, String[] words) {
		trainingDataset.put(genre, words);
	}

	/**
	 * Used to train the classifier using the dataset.
	 */
	public void trainClassifier() {
		nb.train(trainingDataset);
	}

	/**
	 * Retains the knowledgebase that was created during training
	 */
	public void setKnowledgeBase() {
		knowledgeBase = nb.getKnowledgeBase();
	}

	/**
	 * Resets the classifier and training set so that the classifier can now
	 * be used on test data.
	 */
	public void resetClassifier() {
		 nb = null;
		 trainingDataset = null;
	}
	
	/**
	 * Sets up the classifier with the trained knowledge base.
	 */
	public void prepClassifier() {
		nb = new NaiveBayes(knowledgeBase);
	}
	
	/**
	 * Predict the genre of the overview parameter.
	 * 
	 * @param overview
	 * @return
	 */
	public String classifyData(String overview) {
		return nb.predict(overview);	
	}
	
	/**
	 * For each film overview that has been classified, save the filmid
	 * and genreid in a map so the data can be entered into the relevant
	 * table in the database.
	 * 
	 * @param filmid
	 * @param genreid
	 */
	public void setClassified(int filmid, int genreid) {
		classified.put(filmid, genreid);
	}
	
	/**
	 * Return the classified data.
	 * 
	 * @return
	 */
	public Map<Integer, Integer> getClassifiedData() {
		return classified;
	}
	
}
