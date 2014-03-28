package jsonparser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 * Parser is used to convert the JSON formatted content that was retrieved from 
 * TheMovieDatabse.org into a movie object so that addition to the database
 * can be done in a more simplistic format.
 * 
 * The other component of the parser is to parse the genre list which is also
 * retrieved from TMDB in JSON format. This is returned as a map for simple addition
 * to the database.
 * 
 * @author Fiona MacIsaac
 *
 */
public class Parser {

	private JSONParser parser = null;
	private Object obj = null;
	private JSONObject jsonObj = null;

	public Parser() {
		parser = new JSONParser();
		jsonObj = new JSONObject();
	}


	/**
	 * Take in a line of JSON that represents the information for a movie and
	 * parse the relevant information such as id, title, overview and genres
	 * and add them to a Movie object.
	 * 
	 * @param line
	 * @return
	 */
	public Movie parseMovie(String line) {

		Movie m = new Movie();

		try {

			this.obj = parser.parse(line);

			this.jsonObj = (JSONObject) obj;

			/*
			 * When a query was made to the data source and the ID didn't not yet
			 * have content attached to it, a status_code is returned as the first field.
			 * This is caught here so that null movies are not added to the database.
			 */
			if (jsonObj.containsKey("status_code")) {
				m = null;
			} else {
				long id = (Long) jsonObj.get("id");
				String overview = (String) jsonObj.get("overview");
				String title = (String) jsonObj.get("title");
				m.setId(id);
				m.setTitle(title);
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
					m.setGenres(genre);
				}

				return m;
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return m;

	}

	/**
	 * Takes in the line of JSON which contains the list of genre information for
	 * TMDB. Extracts the genre id and the genre string and adds this to the map
	 * which is returned to be used to add the content to the database.
	 * 
	 * @param line
	 * @return
	 */
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
			}

		} catch (ParseException e) {

			e.printStackTrace();
		}

		return genreMap;
	}

}
