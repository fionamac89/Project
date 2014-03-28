package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsonparser.Movie;
/**
 * This class is used to handle all communication and connectivity between the
 * program and the MySQL database.
 * 
 * @author Fiona MacIsaac
 *
 */
public class Database {

	private Connection con = null;
	private String url = "";
	private String user = "";
	private String password = "";

	/**
	 * Constructor used to initialize the login parameters for the database
	 * and then connect to said database.
	 * @param url
	 */
	public Database(String url) {
		this.url = url;
		this.user = "fdb11130";
		this.password = "rigulatn";
		dbConnect();
	}

	/**
	 * Make the connection to the database using the class variables.
	 * Return an error message if the connection is not made successfully.
	 */
	public void dbConnect() {
		
		try {

			this.con = DriverManager.getConnection(this.url, this.user,
					this.password);

		} catch (SQLException e) {
			System.out.println("** Database Connection issue. **");
		}
	}

	/**
	 * Create the table that will hold the film data of id, title and overview
	 * using the name parameter as the name of the table.
	 * 
	 * @param name
	 */
	public void dbCreateFilmList(String name) {
		PreparedStatement pst = null;
		try {
			String sql = "CREATE TABLE "
					+ name
					+ "(ID INT(6) NOT NULL, Title TEXT NOT NULL, Overview TEXT NOT NULL, PRIMARY KEY(ID));";
			pst = con.prepareStatement(sql);
			pst.executeUpdate();
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Create the table that will contain the linking data to connect a film with its
	 * respective genres
	 * 
	 * @param name
	 */
	public void dbCreateFGLink(String name) {
		PreparedStatement pst = null;
		try {
			String sql = "CREATE TABLE "
					+ name
					+ "(FilmID INT(6) NOT NULL, GenreID INT(6) NOT NULL, FOREIGN KEY (FilmID) REFERENCES FilmList(ID) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (GenreID) REFERENCES GenreTest(ID) ON UPDATE CASCADE ON DELETE CASCADE);";
			pst = con.prepareStatement(sql);
			pst.executeUpdate();
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Insert a movie that has been parsed from a file into the database.
	 * This include creating the linker entries required for each film to
	 * genre mapping.
	 * 
	 * @param movie
	 * @param list
	 * @param fg
	 */
	public void dbInsertMovie(Movie movie, String list, String fg) {
		PreparedStatement pst = null;
		long key = movie.getId();
		try {
			pst = con.prepareStatement("INSERT IGNORE INTO " + list
					+ "(ID, Title, Overview) VALUES(?,?,?)");

			for (String genre : movie.getGenres()) {
				pst.setLong(1, key);
				pst.setString(2, movie.getTitle());
				if (movie.getOverview().length() < 1) {
					pst.setString(3, null);
				} else {
					pst.setString(3, movie.getOverview());
				}

				pst.executeUpdate();
				dbCreateFGLink(fg, movie, genre);
			}
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
			ex.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}

		}
	}

	/**
	 * Private method called by the dbInsertMovie used to add a linker record
	 * for each film to genre mapping.
	 * 
	 * @param fg
	 * @param movie
	 * @param genre
	 */
	private void dbCreateFGLink(String fg, Movie movie, String genre) {
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("INSERT IGNORE INTO " + fg
					+ "(FilmID, GenreID) VALUES(?,?)");
			pst.setLong(1, movie.getId());
			int genreID = dbGetGenreID(genre);
			pst.setInt(2, genreID);
			pst.executeUpdate();
			System.out.println("Link created for " + genre);
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
			ex.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}

	}

	/**
	 * Used to return the string name of a genre when given the id for a genre.
	 * 
	 * @param genre
	 * @return
	 */
	public int dbGetGenreID(String genre) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int num = -1;
		try {
			pst = con
					.prepareStatement("SELECT ID FROM GenreTest WHERE Genre=(?)");
			pst.setString(1, genre);
			rs = pst.executeQuery();
			while (rs.next()) {
				num = rs.getInt(1);
			}
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {

				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}

		return num;
	}

	/**
	 * Return the full list of genre ids from the genre table.
	 * 
	 * @return
	 */
	public List<Integer> dbGetGenreList() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<Integer> genres = new ArrayList<Integer>();
		try {
			pst = con.prepareStatement("SELECT ID FROM GenreTest");
			rs = pst.executeQuery();
			while (rs.next()) {
				genres.add(rs.getInt("ID"));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}

		return genres;
	}

	/**
	 * Return a map of all the genres in the table represented by their
	 * id and string.
	 * 
	 * @return
	 */
	public Map<Integer, String> dbGetGenreListMap() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<Integer, String> genres = new HashMap<Integer, String>();
		try {
			pst = con.prepareStatement("SELECT * FROM GenreTest");
			rs = pst.executeQuery();
			while (rs.next()) {
				genres.put(rs.getInt("ID"), rs.getString("Genre"));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}

		return genres;
	}

	/**
	 * Insert the list of genre id and strings from file to the database.
	 * 
	 * @param genreMap
	 */
	public void dbInsertGenre(Map<Long, String> genreMap) {
		PreparedStatement pst = null;
		try {
			pst = con
					.prepareStatement("INSERT IGNORE INTO GenreTest(ID, Genre) VALUES(?,?)");
			for (Entry<Long, String> e : genreMap.entrySet()) {
				long key = e.getKey();
				String value = e.getValue();
				pst.setLong(1, key);
				pst.setString(2, value);
				pst.executeUpdate();
				System.out.println("Added entry");
			}
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Return the map of film and genre id pairs for a specific genre id.
	 * The table parameter is used so that this type of data extraction can be
	 * used on training, test or classified data tables as they all have the same format.
	 * 
	 * @param genre
	 * @param table
	 * @return
	 */
	public Map<Integer, Integer> dbGetMoviesForGenreMap(int genre, String table) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = "";
		Map<Integer, Integer> movies = new HashMap<Integer, Integer>();
		try {
			sql = "SELECT * FROM " + table + " WHERE GenreID=?";
			pst = con.prepareStatement(sql);
			pst.setInt(1, genre);
			rs = pst.executeQuery();
			while (rs.next()) {
				movies.put(rs.getInt("FilmID"), rs.getInt("GenreID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}

		return movies;
	}

	/**
	 * Return the list of film ids for a specific genre. The table parameter is used so that 
	 * this type of data extraction can be used on training, test or classified data tables 
	 * as they all have the same format.
	 * 
	 * @param genre
	 * @param name
	 * @return
	 */
	public List<Integer> dbGetMoviesForGenreList(int genre, String name) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<Integer> movies = new ArrayList<Integer>();
		String sql = "";
		try {
			sql = "SELECT FilmID FROM " + name + " WHERE GenreID=?";
			pst = con.prepareStatement(sql);
			pst.setInt(1, genre);
			rs = pst.executeQuery();
			while (rs.next()) {
				movies.add(rs.getInt("FilmID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}

		return movies;
	}

	/**
	 * Return the overview for the film id given.
	 * 
	 * @param filmid
	 * @return
	 */
	public String dbGetOverview(int filmid) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String overview = "";
		try {
			pst = con
					.prepareStatement("SELECT Overview FROM FilmList WHERE ID=?");
			pst.setInt(1, filmid);
			rs = pst.executeQuery();
			while (rs.next()) {
				overview = rs.getString("Overview");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		return overview;
	}

	/**
	 * Create the training set table with the given suffix.
	 * This helps to identify the training and test sets which are
	 * complements of each other within the database.
	 * 
	 * @param suffix
	 */
	public void dbCreateTrainingSet(String suffix) {
		PreparedStatement pst = null;
		try {
			String sql = "CREATE TABLE "
					+ "TrainingSet"
					+ suffix
					+ "(FilmID INT(6) NOT NULL, GenreID INT(6) NOT NULL, FOREIGN KEY (FilmID) REFERENCES FilmList(ID), FOREIGN KEY (GenreID) REFERENCES GenreTest(ID));";
			pst = con.prepareStatement(sql);
			pst.executeUpdate();
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Create the test set with the given suffix. This suffix is the
	 * same as what is used to create the training set table.
	 * 
	 * @param suffix
	 */
	public void dbCreateTestSet(String suffix) {
		PreparedStatement pst = null;
		try {
			String sql = "CREATE TABLE "
					+ "TestSet"
					+ suffix
					+ "(FilmID INT(6) NOT NULL, GenreID INT(6) NOT NULL, FOREIGN KEY (FilmID) REFERENCES FilmList(ID), FOREIGN KEY (GenreID) REFERENCES GenreTest(ID));";
			pst = con.prepareStatement(sql);
			pst.executeUpdate();
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Populate the training set on a per genre basis. Parameters are a genre id
	 * and the list of films that are associated with that genre.
	 * 
	 * @param training
	 * @param genreid
	 * @param suffix
	 */
	public void dbPopulateTrainingSet(List<Integer> training, int genreid,
			String suffix) {
		PreparedStatement pst = null;
		try {
			String sql = "INSERT IGNORE INTO TrainingSet" + suffix
					+ "(FilmID, GenreID) VALUES(?,?)";
			pst = con.prepareStatement(sql);
			for (Integer filmid : training) {
				pst.setInt(1, filmid);
				pst.setInt(2, genreid);
				pst.executeUpdate();
			}
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
			ex.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Populate the test set on a per genre basis. Parameters are a genre id
	 * and the list of films that are associated with that genre.
	 * 
	 * @param test
	 * @param id
	 * @param suffix
	 */
	public void dbPopulateTestSet(List<Integer> test, int id, String suffix) {
		PreparedStatement pst = null;
		try {
			String sql = "INSERT IGNORE INTO TestSet" + suffix
					+ "(FilmID, GenreID) VALUES(?,?)";
			pst = con.prepareStatement(sql);
			for (Integer filmid : test) {
				pst.setInt(1, filmid);
				pst.setInt(2, id);
				pst.executeUpdate();
			}
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
			ex.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Return the training set in map format so that film id and genre id pairs are
	 * returned.
	 * 
	 * @param suffix
	 * @return
	 */
	public Map<Integer, Integer> dbGetTrainingSet(String suffix) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<Integer, Integer> training = new HashMap<Integer, Integer>();
		try {
			pst = con.prepareStatement("SELECT * FROM TrainingSet" + suffix);
			rs = pst.executeQuery();
			while (rs.next()) {
				training.put(rs.getInt("FilmID"), rs.getInt("GenreID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}

		return training;
	}

	/**
	 * Return the test set in map format so that film id and genre id pairs are
	 * returned.
	 * 
	 * @param suffix
	 * @return
	 */
	public Map<Integer, Integer> dbGetTestSet(String suffix) {
		Statement pst = null;
		ResultSet rs = null;
		String sql = "";
		Map<Integer, Integer> test = new HashMap<Integer, Integer>();
		try {
			sql = "SELECT * FROM TestSet" + suffix;
			pst = con.createStatement();
			rs = pst.executeQuery(sql);
			while (rs.next()) {
				test.put(rs.getInt("FilmID"), rs.getInt("GenreID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		System.out.println("Test Set size: " + test.size());
		return test;
	}
	
	/**
	 * Populate the thesaurus table on a per genre basis - add the words and their
	 * frequencies for the given genre id.
	 * 
	 * @param thes
	 * @param genreid
	 * @param name
	 */
	public void dbPopulateThesaurus(Map<String, Integer> thes, int genreid,
			String name) {
		PreparedStatement pst = null;
		String sql = "";
		try {
			sql = "INSERT IGNORE INTO " + name
					+ "(GenreID, Word, Frequency) VALUES (?,?,?)";
			pst = con.prepareStatement(sql);
			for (Entry<String, Integer> e : thes.entrySet()) {
				pst.setInt(1, genreid);
				pst.setString(2, e.getKey());
				pst.setInt(3, e.getValue());
				pst.executeUpdate();
			}

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Return the list of words from the thesaurus for a specific genre id.
	 * 
	 * @param genreid
	 * @param name
	 * @return
	 */
	public List<String> dbGetThesaurus(int genreid, String name) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> entries = new ArrayList<String>();
		String sql = "";
		try {
			sql = "SELECT Word FROM " + name + " WHERE GenreID=?";
			pst = con.prepareStatement(sql);
			pst.setInt(1, genreid);
			rs = pst.executeQuery();
			while (rs.next()) {
				entries.add(rs.getString("Word"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}

		return entries;
	}

	/**
	 * Populate the classified table with the given map of film id and genre
	 * id pairs.
	 * 
	 * @param name
	 * @param classified
	 */
	public void dbPopulateClassified(String name,
			Map<Integer, Integer> classified) {
		PreparedStatement pst = null;
		try {
			String sql = "INSERT IGNORE INTO " + name
					+ "(FilmID, GenreID) VALUES (?,?)";
			System.out.println(sql);
			pst = con.prepareStatement(sql);
			for (Entry<Integer, Integer> e : classified.entrySet()) {
				pst.setInt(1, e.getKey());
				pst.setInt(2, e.getValue());
				pst.executeUpdate();
			}

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Create a classification table with the given name.
	 * 
	 * @param name
	 */
	public void dbCreateClassifiedTable(String name) {
		PreparedStatement pst = null;
		try {
			String sql = "CREATE TABLE "
					+ name
					+ "(FilmID INT(6) NOT NULL, GenreID INT(6) NOT NULL, FOREIGN KEY (FilmID) REFERENCES FilmList(ID), FOREIGN KEY (GenreID) REFERENCES GenreTest(ID));";
			pst = con.prepareStatement(sql);
			pst.executeUpdate();
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Create a thesaurus table with the given name.
	 * 
	 * @param name
	 */
	public void dbCreateThesaurus(String name) {
		PreparedStatement pst = null;
		try {
			String sql = "CREATE TABLE "
					+ name
					+ "(GenreID INT(6) NOT NULL, Word VARCHAR(64) NOT NULL, Frequency INT(6) NOT NULL, FOREIGN KEY (GenreID) REFERENCES GenreTest(ID));";
			pst = con.prepareStatement(sql);
			pst.executeUpdate();
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Create the table to hold the overall evaluation scores for all
	 * the experiments that have been run.
	 * 
	 * @param name
	 */
	public void dbCreateEvalTable(String name) {
		PreparedStatement pst = null;
		try {
			String sql = "CREATE TABLE "
					+ name
					+ "(Classification VARCHAR(64) NOT NULL, PrecisionScore DOUBLE(6,4) NOT NULL, RecallScore DOUBLE(6,4) NOT NULL, FmeasureScore DOUBLE(6,4) NOT NULL, PRIMARY KEY (Classification));";
			pst = con.prepareStatement(sql);
			pst.executeUpdate();
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Used to apply thresholds for the improved algorithm on the thesaurus.
	 * This means that when classification is done, this method is used to
	 * pull the entries from the thesaurus that are between the thresholds have
	 * been set.
	 * 
	 * @param name
	 * @param genreid
	 * @param upper
	 * @param lower
	 * @return
	 */
	public List<String> dbApplyThreshold(String name, int genreid, int upper, int lower) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> words = new ArrayList<String>();
		String sql = "";
		try {
			sql = "SELECT * FROM " + name + " WHERE GenreID=? AND Frequency < ? AND Frequency > ?";
			pst = con.prepareStatement(sql);
			pst.setInt(1, genreid);
			pst.setInt(2, upper);
			pst.setInt(3, lower);
			rs = pst.executeQuery();
			while (rs.next()) {
				words.add(rs.getString("Word"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		return words;
	}
	
	/**
	 * Create a table with a given name to hold the evaluation of classification
	 * success on a per genre basis.
	 * 
	 * @param name
	 */
	public void dbCreateEvalGenreTable(String name) {
		PreparedStatement pst = null;
		try {
			String sql = "CREATE TABLE "
					+ name
					+ "(GenreID INT(6) NOT NULL, PrecisionScore DOUBLE(6,4) NOT NULL, RecallScore DOUBLE(6,4) NOT NULL, FmeasureScore DOUBLE(6,4) NOT NULL, FOREIGN KEY (GenreID) REFERENCES GenreTest(ID));";
			pst = con.prepareStatement(sql);
			pst.executeUpdate();
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Add a total evaluation for an algorithm. This is the precision, recall and f-measure
	 * that is calculated as an average of the results from the per genre scores of a 
	 * classification.
	 * 
	 * @param name
	 * @param className
	 * @param precision
	 * @param recall
	 * @param fmeasure
	 */
	public void dbAddEval(String name, String className, double precision, double recall, double fmeasure) {
		PreparedStatement pst = null;
		try {
			String sql = "INSERT IGNORE INTO " + name
					+ "(Classification, PrecisionScore, RecallScore, FmeasureScore) VALUES (?,?,?,?)";
			System.out.println(sql);
			pst = con.prepareStatement(sql);

			pst.setString(1, className);
			pst.setDouble(2, precision);
			pst.setDouble(3, recall);
			pst.setDouble(4, fmeasure);
			pst.executeUpdate();

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Add an evaluation entry for a genre to a classification specific evaluation table.
	 * 
	 * @param name
	 * @param genreid
	 * @param precision
	 * @param recall
	 * @param fmeasure
	 */
	public void dbAddEvalGenre(String name, int genreid, double precision, double recall, double fmeasure) {
		PreparedStatement pst = null;
		try {
			String sql = "INSERT IGNORE INTO " + name
					+ "(GenreID, PrecisionScore, RecallScore, FmeasureScore) VALUES (?,?,?,?)";
			pst = con.prepareStatement(sql);

			pst.setInt(1, genreid);
			pst.setDouble(2, precision);
			pst.setDouble(3, recall);
			pst.setDouble(4, fmeasure);
			pst.executeUpdate();

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Was used during development. Allows for a quick and simple way to empty
	 * a table of entries.
	 * 
	 * @param name
	 */
	public void dbDeleteFromTable(String name) {
		Statement st = null;
		try {
			st = con.createStatement();
			st.execute("DELETE FROM " + name);
			System.out.println(name + " content deleted.");
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Disconnect from the database.
	 */
	public void dbDisconnect() {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return true if a table name is already in use in the database,
	 * return false if the table name is not in use.
	 * 
	 * @param name
	 * @return
	 */
	public boolean dbTableExists(String name) {
		boolean exists = false;
		DatabaseMetaData dbm;
		ResultSet tables = null;
		try {
			dbm = con.getMetaData();
			tables = dbm.getTables(null, null, name, null);
			if (tables.next()) {
				exists = true;
			}

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (tables != null) {
					tables.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		return exists;
	}
	
	/**
	 * Return the list of all tables within the database.
	 * 
	 * @return
	 */
	public List<String> dbListTables() {
		DatabaseMetaData dbm;
		List<String> tableList = new ArrayList<String>();
		ResultSet tables = null;
		try {
			dbm = con.getMetaData();
			tables = dbm.getTables(null, null, "%", null);
		
			while(tables.next()){
				tableList.add(tables.getString("TABLE_NAME"));
			}
			

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (tables != null) {
					tables.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		return tableList;
	}
	
}