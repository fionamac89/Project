package jsonparser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Parser {

	private JSONParser parser = null;
	private FileReader reader = null;
	private Object obj = null;
	private JSONObject jsonObj = null;
	private BufferedReader br = null;

	public Parser(String filepath) {
		parser = new JSONParser();
		try {
			reader = new FileReader(filepath);
			br = new BufferedReader(reader);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * http://stackoverflow.com/questions/12938442/how-to-parse-from-json-to-map-
	 * with-json-simple-and-retain-key-order Can use this code to browse the
	 * JSON content before putting it into the DB String json = aceptaDefault();
	 * JSONParser parser = new JSONParser(); Object obj = parser.parse(json);
	 * Map map = (Map)obj;
	 */
	public Map<Long, Movie> parseMovie() {
		
		Movie m = new Movie();
		Map<Long, Movie> movieMap = new HashMap<Long, Movie>();
		
		try {

			String line = "";

			while ((line = br.readLine()) != null) {
				this.obj = parser.parse(line);

				this.jsonObj = (JSONObject) obj;

				/*
				 * status_code - first field if invalid result genres - list
				 * homepage id imdb_id original_title overview - if this is
				 * null/empty don't use the object..? title
				 */

				if (jsonObj.containsKey("status_code")) {
					;
				} else {

					long id = (Long) jsonObj.get("id");
					String overview = (String) jsonObj.get("overview");
					String title = (String) jsonObj.get("title");

					System.out.println("ID: "+id);
					m.setId(id);
					System.out.println("Title: " + title);
					m.setTitle(title);
					System.out.println("Overview: " + overview);
					m.setOverview(overview);

					// loop array
					JSONArray genres = (JSONArray) jsonObj.get("genres");
					Iterator<JSONObject> giterator = genres.iterator();
					while (giterator.hasNext()) {
						String genre = (String) giterator.next().get("name");
						System.out.println(genre);
						m.setGenres(genre);
					}

					if (jsonObj.containsKey("keywords")) {
						JSONObject keywords = (JSONObject) jsonObj
								.get("keywords");
						JSONArray kwords = (JSONArray) keywords.get("keywords");
						Iterator<JSONObject> kiterator = kwords.iterator();
						while (kiterator.hasNext()) {
							JSONObject keyword = (JSONObject) kiterator.next()
									.get("name");
							System.out.println(keyword);
							m.setKeywords((String) keyword.get("keywords")); 
						}
					}
					
					movieMap.put(m.getId(), m);
				}
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return movieMap;
	}

	public Map<Long, String> parseGenre() {
		JSONObject thing = null;
		Map<Long, String> genreMap = new HashMap<Long, String>();
		try {
			String line = br.readLine();
			this.obj = parser.parse(line);
			this.jsonObj = (JSONObject) obj;
			JSONArray genres = (JSONArray) jsonObj.get("genres");
			Iterator<JSONObject> giterator = genres.iterator();

			while (giterator.hasNext()) {
				thing = giterator.next();
				String genre = (String) thing.get("name");
				long id = (Long) thing.get("id");
				genreMap.put(id, genre);
				System.out.println(id+": "+genre);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return genreMap;
	}

}
