package jsonparser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Parser {

	private JSONParser parser = null;
	private Object obj = null;
	private JSONObject jsonObj = null;

	public Parser() {
		parser = new JSONParser();
		jsonObj = new JSONObject();
	}

	/*
	 * http://stackoverflow.com/questions/12938442/how-to-parse-from-json-to-map-
	 * with-json-simple-and-retain-key-order Can use this code to browse the
	 * JSON content before putting it into the DB String json = aceptaDefault();
	 * JSONParser parser = new JSONParser(); Object obj = parser.parse(json);
	 * Map map = (Map)obj;
	 */
	public Movie parseMovie(String line) {

		Movie m = new Movie();

		try {

			this.obj = parser.parse(line);

			this.jsonObj = (JSONObject) obj;

			/*
			 * status_code - first field if invalid result genres - list
			 * homepage id imdb_id original_title overview - if this is
			 * null/empty don't use the object..? title
			 */

			if (jsonObj.containsKey("status_code")) {
				m = null;
			} else {

				long id = (Long) jsonObj.get("id");
				String overview = (String) jsonObj.get("overview");
				String title = (String) jsonObj.get("title");
				String origTitle = (String) jsonObj.get("original_title");

				System.out.println("ID: " + id);
				m.setId(id);
				System.out.println("Orig. Title: " + origTitle);
				m.setOrigTitle(origTitle);
				System.out.println("Title: " + title);
				m.setTitle(title);
				System.out.println("Overview: " + overview);
				if ((overview == null) || (overview.length() < 1)
						|| (overview.toLowerCase().contains("no overview"))) {
					overview = "";
				}
				m.setOverview(overview);

				// loop array
				JSONArray genres = (JSONArray) jsonObj.get("genres");
				Iterator<JSONObject> giterator = genres.iterator();
				while (giterator.hasNext()) {
					String genre = (String) giterator.next().get("name");
					System.out.println(genre);
					m.setGenres(genre);
				}

				/*
				 * if (jsonObj.containsKey("keywords")) { JSONObject keywords =
				 * (JSONObject) jsonObj .get("keywords"); JSONArray kwords =
				 * (JSONArray) keywords.get("keywords"); Iterator<JSONObject>
				 * kiterator = kwords.iterator(); while (kiterator.hasNext()) {
				 * JSONObject keyword = (JSONObject) kiterator.next()
				 * .get("name"); System.out.println(keyword);
				 * m.setKeywords((String) keyword.get("keywords")); } }
				 */

				return m;
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return m;

	}

	public Map<Long, String> parseGenre(String line) {
		JSONObject thing = null;
		Map<Long, String> genreMap = new HashMap<Long, String>();
		try {
			this.obj = parser.parse(line);

			this.jsonObj = (JSONObject) obj;
			JSONArray genres = (JSONArray) jsonObj.get("genres");
			Iterator<JSONObject> giterator = genres.iterator();

			while (giterator.hasNext()) {
				thing = giterator.next();
				String genre = (String) thing.get("name");
				long id = (Long) thing.get("id");
				genreMap.put(id, genre);
				System.out.println(id + ": " + genre);
			}

		} catch (ParseException e) {

			e.printStackTrace();
		}

		return genreMap;
	}

}
