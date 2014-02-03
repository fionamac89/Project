package wordplay;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
	private List<String> genreWords = null;
	private Movie movie = null;
	
	public Tagger() {
		genreWords = new ArrayList<String>();
	}
	
	/*
	 * Stop word analysis
	 */
	
	public void removeStopWords(String content) {
        tokenStream = new StandardTokenizer(Version.LUCENE_46, new StringReader(content)); //Add String Reader?
        StringBuilder sb = new StringBuilder();
        tokenStream = new StopFilter(Version.LUCENE_46, tokenStream, StandardAnalyzer.STOP_WORDS_SET);
        CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);
        try {
			while (tokenStream.incrementToken()) 
			{
				output = token.toString().toString();
		        genreWords.add(output);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<String> getWords() {
		return genreWords;
	}
}
