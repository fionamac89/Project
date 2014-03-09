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

	/*
	 * Stop word analysis
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

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
			// setStemStopFilter();
			setStopWordFilter();
			setStemFilter();
			break;
		case "none":
		default:
			;
		}
	}

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
			// setStemStopFilter();
			setStopWordFilter();
			setStemFilter();
			break;
		case "none":
		default:
			;
		}
	}

	private void setStopWordFilter() {

		if (defaultList) {
			tokenStream = new StopFilter(luceneVersion, tokenStream,
					StandardAnalyzer.STOP_WORDS_SET);
		} else {
			tokenStream = new StopFilter(luceneVersion, tokenStream,
					StopFilter.makeStopSet(luceneVersion, stopwords));
		}
	}

	// public void setStemStopFilter(String content) {
	//
	// if (defaultList) {
	// tokenStream = new StopFilter(luceneVersion, tokenStream,
	// StandardAnalyzer.STOP_WORDS_SET);
	// } else {
	// tokenStream = new StopFilter(luceneVersion, tokenStream,
	// StopFilter.makeStopSet(luceneVersion, stopwords));
	// }
	//
	// tokenStream = new PorterStemFilter(tokenStream);
	// }

	private void setStemFilter() {

		tokenStream = new PorterStemFilter(tokenStream);
		stemFilter = true;

	}

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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

	// Apply stemming to text for when it is used for prediction
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
				// Add numeric check here?
				if (output != null) {
					sb.append(output + " ");
				}
			}
			tokenStream.end();
			tokenStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	public Map<String, Integer> getWords() {
		return importantText;
	}

	public void clearWords() {
		importantText.clear();
	}

	public static boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
			int i = Integer.parseInt(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public boolean getStemFilterStatus() {
		return stemFilter;
	}
}
