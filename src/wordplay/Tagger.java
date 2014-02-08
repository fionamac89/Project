package wordplay;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsonparser.Movie;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class Tagger {
	
	private TokenStream tokenStream = null;
	private String output = "";
	private Map<String, Integer> importantText = null;
	
	public Tagger() {
		importantText = new HashMap<String, Integer>();
	}
	
	/*
	 * Stop word analysis
	 */
	
	public void removeStopWords(String content) {
        StringBuilder sb = new StringBuilder();
		tokenStream = new StandardTokenizer(Version.LUCENE_46, new StringReader(content)); //Add String Reader?
        tokenStream = new StopFilter(Version.LUCENE_46, tokenStream, StandardAnalyzer.STOP_WORDS_SET);
        CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);
        try {
			while (tokenStream.incrementToken()) 
			{
				output = token.toString().toString();
		        termOccurrence(output);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void termOccurrence(String text) {
		if(importantText.containsKey(text)) {
			int temp = importantText.get(text);
			temp+=1;
			importantText.remove(text);
			importantText.put(text, temp);
		} else {
			importantText.put(text, 1);
		}
		
	}
	
	public Map<String, Integer> getWords() {
		return importantText;
	}
}
