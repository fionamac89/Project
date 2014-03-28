package wordplay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
/**
 * Class used to perform the algorithm for thesaurus construction. This involves
 * making the the token stream, applying the appropriate filters and performing
 * the term occurrence algorithm.
 * 
 * @author Fiona MacIsaac
 *
 */
public class Tagger {

	private TokenStream tokenStream = null;
	private String output = "";
	private Map<String, Integer> importantText = null;
	private List<String> stopwords = null;
	private Version luceneVersion = Version.LUCENE_46;
	private boolean defaultList = true;
	private boolean stemFilter = false;

	public Tagger() {
		importantText = new HashMap<String, Integer>();
		stopwords = new ArrayList<String>();
	}

	/**
	 * Create the stop word list from the file given.
	 * 
	 * @param filepath
	 */
	public void createStopList(String filepath) {
		Scanner in;
		try {
			in = new Scanner(new File(filepath));
			String word = "";
			while (in.hasNext()) {
				word = in.next().trim();
				stopwords.add(word);
			}

			defaultList = false;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Create the token stream and apply the relevant filter. This is used
	 * for the first thesaurus algorithm where the lower case enforcement is
	 * not used at this point.
	 * 
	 * @param content
	 * @param filter
	 */
	public void setFilter(String content, String filter) {
		tokenStream = new StandardTokenizer(luceneVersion, new StringReader(
				content));
		switch (filter) {
		case "sw":
			setStopWordFilter();
			break;
		case "s":
			setStemFilter();
			break;
		case "ssw":
			setStopWordFilter();
			setStemFilter();
			break;
		case "none":
		default:
			;
		}
	}

	/**
	 * Create the token stream and apply the relevant filter. This is used
	 * for the second thesaurus algorithm where the lower case enforcement is
	 * used before the text is tokenised/filtered.
	 * 
	 * @param content
	 * @param filter
	 */
	public void setFilter2(String content, String filter) {
		tokenStream = new StandardTokenizer(luceneVersion, new StringReader(
				content.toLowerCase(Locale.UK)));
		switch (filter) {
		case "sw":
			setStopWordFilter();
			break;
		case "s":
			setStemFilter();
			break;
		case "ssw":
			setStopWordFilter();
			setStemFilter();
			break;
		case "none":
		default:
			;
		}
	}

	/**
	 * Apply the stop word filter to the Token Stream.
	 */
	private void setStopWordFilter() {

		if (defaultList) {
			tokenStream = new StopFilter(luceneVersion, tokenStream,
					StandardAnalyzer.STOP_WORDS_SET);
		} else {
			tokenStream = new StopFilter(luceneVersion, tokenStream,
					StopFilter.makeStopSet(luceneVersion, stopwords));
		}
	}

	/**
	 * Apply the Porter Stemmer filter to the Token Stream.
	 */
	private void setStemFilter() {

		tokenStream = new PorterStemFilter(tokenStream);
		stemFilter = true;

	}

	/**
	 * Run the thesaurus construction algorithm. Lower case enforcement is
	 * used as this was not done when the text was passed to the token stream.
	 */
	public void applyFilter() {
		CharTermAttribute token = tokenStream
				.addAttribute(CharTermAttribute.class);
		try {
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				output = token.toString();
				if (output != null && !isNumeric(output)) {
					termOccurrence(output.toLowerCase(Locale.UK).replaceAll(
							"\\p{P}", ""));
				}
			}
			tokenStream.end();
			tokenStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Used for the second thesaurus algorithm. No lower case
	 * enforcement is required as this was done at another stage.
	 */
	public void applyFilter2() {
		CharTermAttribute token = tokenStream
				.addAttribute(CharTermAttribute.class);
		try {
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				output = token.toString();
				if (output != null && !isNumeric(output)) {
					termOccurrence(output.replaceAll("\\p{P}", ""));
				}
			}
			tokenStream.end();
			tokenStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Perform term occurrence on the text and add the relevant
	 * words and their frequencies to the map.
	 * 
	 * @param text
	 */
	private void termOccurrence(String text) {
		if (importantText.containsKey(text)) {
			int temp = importantText.get(text);
			temp += 1;
			importantText.remove(text);
			importantText.put(text, temp);
		} else {
			importantText.put(text, 1);
		}

	}

	/**
	 * Prepare the stem filter to be used so that during classification
	 * the stemmed version of words are compared.
	 * 
	 * @param content
	 * @return
	 */
	public String classifyStemFilter(String content) {
		StringBuilder sb = new StringBuilder();
		tokenStream = new StandardTokenizer(luceneVersion, new StringReader(
				content));
		tokenStream = new PorterStemFilter(tokenStream);

		CharTermAttribute token = tokenStream
				.addAttribute(CharTermAttribute.class);
		try {
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				output = token.toString();
				if (output != null) {
					sb.append(output + " ");
				}
			}
			tokenStream.end();
			tokenStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * Return the list of words to be added to the thesaurus.
	 * 
	 * @return
	 */
	public Map<String, Integer> getWords() {
		return importantText;
	}

	/**
	 * Clear the list of words that is used for adding to the
	 * thesaurus.
	 */
	public void clearWords() {
		importantText.clear();
	}

	/**
	 * Method for checking if a string can be cast to an integer or double.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
			int i = Integer.parseInt(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	/**
	 * Return whether the stem filter has been selected
	 * or not.
	 * 
	 * @return
	 */
	public boolean getStemFilterStatus() {
		return stemFilter;
	}
}
