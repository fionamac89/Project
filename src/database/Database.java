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
	private String user = "";
	private String password = "";

	public Database(String url) {
		this.url = url;
		this.user = "fdb11130";
		this.password = "rigulatn";
		dbConnect();
	}

	public void dbConnect() {
		/*
		 * test this connection on a uni pc
		 */
		try {

			this.con = DriverManager.getConnection(this.url, this.user,
					this.password);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

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

			System.out.println("Added entry");
		}
	}

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

	public void dbCreateEvalTable(String name) {
		PreparedStatement pst = null;
		try {
			String sql = "CREATE TABLE "
					+ name
					+ "(Classification VARCHAR(64) NOT NULL, Type VARCHAR(12) NOT NULL, Score DOUBLE(6,4) NOT NULL);";
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

	public void dbCreateEvalGenreTable(String name) {
		PreparedStatement pst = null;
		try {
			String sql = "CREATE TABLE "
					+ name
					+ "(GenreID INT(6) NOT NULL, Type VARCHAR(12) NOT NULL, Score DOUBLE(6,4) NOT NULL, FOREIGN KEY (GenreID) REFERENCES GenreTest(ID));";
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

	public void dbAddEval(String name, String className, String type,
			double score) {
		PreparedStatement pst = null;
		try {
			String sql = "INSERT IGNORE INTO " + name
					+ "(Classification, Type, Score) VALUES (?,?,?)";
			System.out.println(sql);
			pst = con.prepareStatement(sql);

			pst.setString(1, className);
			pst.setString(2, type);
			pst.setDouble(3, score);
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

	public void dbAddEvalGenre(String name, int genreid, String type,
			double score) {
		PreparedStatement pst = null;
		try {
			String sql = "INSERT IGNORE INTO " + name
					+ "(GenreID, Type, Score) VALUES (?,?,?)";
			pst = con.prepareStatement(sql);

			pst.setInt(1, genreid);
			pst.setString(2, type);
			pst.setDouble(3, score);
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

	/*
	 * TODO: add in getTest and getClassified by genre.
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

	public double dbGetEvalScore(String table, String type, int genreid) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double score = 0;
		try {
			pst = con.prepareStatement("SELECT Score FROM " + table + " WHERE GenreID=? AND Type=?");
			pst.setInt(1, genreid);
			pst.setString(2, type);
			rs = pst.executeQuery();
			while (rs.next()) {
				score=rs.getDouble("Score");
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
		return score;
	}
	
}