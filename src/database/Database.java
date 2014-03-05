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

public class Database {

	private Connection con = null;
	private String url = "";

	public Database(String url) {
		this.url = url;
		dbConnect();
	}

	public void dbConnect() {
		/*
		 * test this connection on a uni pc
		 */
		String user = "fdb11130";
		String password = "rigulatn";

		try {

			this.con = DriverManager.getConnection(this.url, user, password);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void dbInsertMovie(Movie movie) {
		PreparedStatement pst = null;
		long key = movie.getId();
		try {
			pst = con
					.prepareStatement("INSERT IGNORE INTO FilmList(ID, Title, Overview) VALUES(?,?,?)");

			for (String genre : movie.getGenres()) {
				// TODO finish getting genre ID and adding movie into DB.
				pst.setLong(1, key);
				if (movie.getTitle().length() < 1) {
					pst.setString(2, movie.getOrigTitle());
				} else {
					pst.setString(2, movie.getTitle());
				}
				if (movie.getOverview().length() < 1) {
					pst.setString(3, null);
				} else {
					pst.setString(3, movie.getOverview());
				}

				pst.executeUpdate();
				dbCreateFGLink(movie, genre);
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

			System.out.println("Added entry");
		}
	}

	public void dbCreateFGLink(Movie movie, String genre) {
		PreparedStatement pst = null;
		try {
			pst = con
					.prepareStatement("INSERT IGNORE INTO FGLink(FilmID, GenreID) VALUES(?,?)");
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
				if(rs != null) {
					rs.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}

		return genres;
	}

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

	/*
	 * TODO: db query to extract all films with specific genre ID
	 */
	public List<Integer> dbGetMoviesForGenre(int genre) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<Integer> movies = new ArrayList<Integer>();
		try {
			pst = con
					.prepareStatement("SELECT FilmID FROM FGLink WHERE GenreID=?");
			pst.setInt(1, genre);
			rs = pst.executeQuery();
			while (rs.next()) {
				movies.add(rs.getInt("FilmID"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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

	public List<Integer> dbGetMoviesForGenreTrainSet(int genre, String suffix) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<Integer> movies = new ArrayList<Integer>();
		String sql = "";
		try {
			sql = "SELECT FilmID FROM TrainingSet" + suffix
					+ " WHERE GenreID=?";
			pst = con.prepareStatement(sql);
			pst.setInt(1, genre);
			rs = pst.executeQuery();
			while (rs.next()) {
				movies.add(rs.getInt("FilmID"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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

	/*
	 * TODO: db query to extract the overview of all films given their IDs
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
			// TODO Auto-generated catch block
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

	/*
	 * TODO: db query to populate TrainingSet table
	 */
	public void dbPopulateTrainingSet(List<Integer> training, int id,
			String suffix) {
		PreparedStatement pst = null;
		try {
			String sql = "INSERT IGNORE INTO TrainingSet" + suffix
					+ "(FilmID, GenreID) VALUES(?,?)";
			pst = con.prepareStatement(sql);
			for (Integer filmid : training) {
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

	/*
	 * TODO: db query to populate TestSet table
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
			// TODO Auto-generated catch block
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

	public Map<Integer, Integer> dbGetTestSet(String suffix) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<Integer, Integer> test = new HashMap<Integer, Integer>();
		try {
			pst = con.prepareStatement("SELECT * FROM TestSet" + suffix);
			rs = pst.executeQuery();
			while (rs.next()) {
				test.put(rs.getInt("FilmID"), rs.getInt("GenreID"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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

		return test;
	}

	/*
	 * TODO: populate thesaurus
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

	/*
	 * TODO: Get thesaurus? Get thesaurus for Genre?
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
			// TODO Auto-generated catch block
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

	/*
	 * TODO: populate classified table
	 */
	public void dbPopulateClassified(Map<Integer, Integer> classified) {
		PreparedStatement pst = null;
		try {
			pst = con
					.prepareStatement("INSERT IGNORE INTO Classified_SW(FilmID, GenreID) VALUES (?,?)");
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

	public void dbPopulateClassifiedSpecific(String name,
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

	public void dbDeleteMovies() {
		Statement st = null;
		try {
			st = con.createStatement();
			st.execute("DELETE FROM FilmList");
			System.out.println("FilmList content Deleted.");
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

	public void dbDisconnect() {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean tableExists(String name) {
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
}