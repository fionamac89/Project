package jsonparser;

import java.util.ArrayList;
import java.util.List;

public class Movie {
	private List<String> genres = null;
	private List<String> keywords = null;
	private String title = "";
	private long id = 0;
	private String overview = "";
	private String origTitle = "";
	
	public Movie() {
		this.genres = new ArrayList<String>();
		this.keywords = new ArrayList<String>();
	}

	public List<String> getGenres() {
		return genres;
	}

	public void setGenres(String genre) {
		this.genres.add(genre);
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(String keyword) {
		this.keywords.add(keyword);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}
	
	public String getOrigTitle() {
		return origTitle;
	}

	public void setOrigTitle(String origTitle) {
		this.origTitle = origTitle;
	}

	public String listToString(List<String> list) {
	    StringBuilder result = new StringBuilder();
	    for (String value : list) {
	      result.append(value+"\t");
	    } 
	    return result.toString();
	}
	
}
