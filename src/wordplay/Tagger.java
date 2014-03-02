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
	private CharArraySet stopSet = null;

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
				System.out.println(word);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		stopSet = StopFilter.makeStopSet(Version.LUCENE_46, stopwords);

	}

	public void setStopWordFilter(String content) {
		tokenStream = new StandardTokenizer(Version.LUCENE_46,
				new StringReader(content));
		if (stopSet != null) {
			tokenStream = new StopFilter(Version.LUCENE_46, tokenStream,
					stopSet);
		} else {
			tokenStream = new StopFilter(Version.LUCENE_46, tokenStream,
					StandardAnalyzer.STOP_WORDS_SET);
		}
	}

	public void setStemStopFilter(String content) {
		tokenStream = new StandardTokenizer(Version.LUCENE_46,
				new StringReader(content));
		if (stopSet != null) {
			tokenStream = new StopFilter(Version.LUCENE_46, tokenStream,
					stopSet);
		} else {
			tokenStream = new StopFilter(Version.LUCENE_46, tokenStream,
					StandardAnalyzer.STOP_WORDS_SET);
		}
		tokenStream = new PorterStemFilter(tokenStream);
	}

	public void setStemFilter(String content) {
		tokenStream = new StandardTokenizer(Version.LUCENE_46,
				new StringReader(content));
		tokenStream = new PorterStemFilter(tokenStream);
	}

	public void setNoFilter(String content) {
		tokenStream = new StandardTokenizer(Version.LUCENE_46,
				new StringReader(content));
	}

	public void applyFilter() {
		CharTermAttribute token = tokenStream
				.addAttribute(CharTermAttribute.class);
		try {
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				output = token.toString();
				// Add numeric check here?
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

	public void termOccurrence(String text) {
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
	public String stemFilter(String content) {
		StringBuilder sb = new StringBuilder();
		tokenStream = new StandardTokenizer(Version.LUCENE_46,
				new StringReader(content));
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
	
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);
	    int i = Integer.parseInt(str);
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
}
