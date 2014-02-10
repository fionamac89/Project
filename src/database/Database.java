package database;

import java.sql.Connection;
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
	private Statement st = null;
	private ResultSet rs = null;
	private PreparedStatement pst = null;
	private PreparedStatement mpst = null;
	private PreparedStatement fgpst = null;

	public Database() {
		// dbConnect();
	}

	public void dbConnect() {
		/*
		 * Read this data in from (encrypted?) file for security?
		 */
		String url = "jdbc:mysql://localhost:3306/fdb11130";
		String user = "fdb11130";
		String password = "rigulatn";

		try {

			this.con = DriverManager.getConnection(url, user, password);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void dbInsertMovie(Movie movie) {
		long key = movie.getId();
		int genreID = -1;
		try {
			mpst = con
					.prepareStatement("INSERT IGNORE INTO FilmList(ID, Title, Overview) VALUES(?,?,?)");

			for (String genre : movie.getGenres()) {
				// TODO finish getting genre ID and adding movie into DB.
				mpst.setLong(1, key);
				if (movie.getTitle().length() < 1) {
					mpst.setString(2, movie.getOrigTitle());
				} else {
					mpst.setString(2, movie.getTitle());
				}
				if (movie.getOverview().length() < 1) {
					mpst.setString(3, null);
				} else {
					mpst.setString(3, movie.getOverview());
				}

				mpst.executeUpdate();
				dbCreateFGLink(movie, genre);
			}
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
			ex.printStackTrace();
		} finally {
			try {
				if (mpst != null) {
					mpst.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}

			System.out.println("Added entry");
		}
	}

	public void dbCreateFGLink(Movie movie, String genre) {
		try {
			fgpst = con
					.prepareStatement("INSERT IGNORE INTO FGLink(FilmID, GenreID) VALUES(?,?)");
			fgpst.setLong(1, movie.getId());
			int genreID = dbGetGenreID(genre);
			fgpst.setInt(2, genreID);
			fgpst.executeUpdate();
			System.out.println("Link created for " + genre);
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
			ex.printStackTrace();
		} finally {
			try {
				if (fgpst != null) {
					fgpst.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}

	}

	public int dbGetGenreID(String genre) {
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
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}

		return genres;
	}

	public void dbInsertGenre(Map<Long, String> genreMap) {
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
		List<Integer> movies = new ArrayList<Integer>();
		try {
			pst = con
					.prepareStatement("SELECT FilmID FROM FGLink WHERE GenreID==(?)");
			pst.setInt(genre, 1);
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
	public Map<Integer, String> dbGetOverview(List<Integer> movies) {
		Map<Integer, String> overviews = new HashMap<Integer, String>();
		try {
			pst = con
					.prepareStatement("SELECT Overview FROM FilmList WHERE FilmID==(?)");
			for (Integer filmid : movies) {
				pst.setInt(filmid, 1);
				rs = pst.executeQuery();
				while (rs.next()) {
					overviews.put(filmid, rs.getString("Overview"));
				}
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
		return overviews;
	}

	/*
	 * TODO: db query to create a new TrainingSet table
	 */

	/*
	 * TODO: db query to create a new TestSet table
	 */

	/*
	 * TODO: db query to populate TrainingSet table
	 */
	public void dbPopulateTrainingSet(List<Integer> training, int id) {
		try {
			fgpst = con
					.prepareStatement("INSERT IGNORE INTO TrainingSet(FilmID, GenreID) VALUES(?,?)");
			for (Integer filmid : training) {
				fgpst.setInt(1, filmid);
				fgpst.setInt(2, id);
				fgpst.executeUpdate();
			}
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
			ex.printStackTrace();
		} finally {
			try {
				if (fgpst != null) {
					fgpst.close();
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
	public void dbPopulateTestSet(List<Integer> test, int id) {
		try {
			pst = con
					.prepareStatement("INSERT IGNORE INTO TrainingSet(FilmID, GenreID) VALUES(?,?)");
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

	public Map<Integer, Integer> dbGetTrainingSet() {
		Map<Integer, Integer> training = new HashMap<Integer, Integer>();
		try {
			pst = con.prepareStatement("SELECT * FROM TrainingSet");
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

	public Map<Integer, Integer> dbGetTestSet() {
		Map<Integer, Integer> test = new HashMap<Integer, Integer>();
		try {
			pst = con.prepareStatement("SELECT * FROM TestSet");
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

	public void dbDeleteMovies() {
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
}