package database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsonparser.Movie;
import jsonparser.Parser;

public class Database {

	private Connection con = null;
	private String url = "jdbc:mysql://localhost:3306/fdb11130";
	private String user = "fdb11130";
	private String password = "rigulatn";
	private Statement st = null;
	private ResultSet rs = null;
	private Parser input = null;
	private Map<Long, String> genreMap = null;
	private Movie movie = null;
	private PreparedStatement pst = null;
	private PreparedStatement mpst = null;
	private BufferedReader br = null;
	private FileReader reader = null;

	public Database(String filepath) {
		String url = "jdbc:mysql://localhost:3306/fdb11130";
		String user = "fdb11130";
		String password = "rigulatn";
		try {
			this.con = DriverManager.getConnection(this.url, this.user,
					this.password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			reader = new FileReader(filepath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		br = new BufferedReader(reader);
		input = new Parser();
		movie = null;
	}

	public void run() {

		try {
			dbConnect();
			String line = "";

			while ((line = br.readLine()) != null) {
				movie = input.parseMovie(line);
				if (movie != null) {
					dbInsertMovie();
				}
			}
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	public void runGenre() {
		/**
		 * Parser for Genres and insert into DB
		 */

		try {
			dbConnect();
			genreMap = input.parseGenre();
			dbInsertGenre();
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}

	}

	private void dbConnect() throws SQLException {
		this.st = this.con.createStatement();
		this.rs = this.st.executeQuery("SELECT VERSION()");

		if (this.rs.next()) {
			System.out.println(this.rs.getString(1));
		}
	}

	private void dbInsertMovie() throws SQLException {
		long key = movie.getId();
		//System.out.println(movie.toString());
		int genreID = -1;
		mpst = con
				.prepareStatement("INSERT INTO MoviesTest(MovieID, Title, Overview, Keywords, GenreID) VALUES(?,?,?,?,?)");
		for (String genre : movie.getGenres()) {			
			// TODO finish getting genre ID and adding movie into DB.
			mpst.setLong(1, key);
			mpst.setString(2, movie.getTitle());
			mpst.setString(3, movie.getOverview());
			if (movie.getKeywords().size() < 1) {
				mpst.setString(4, null);
			} else {
				mpst.setString(4, movie.listToString(movie.getKeywords()));
			}

			genreID = dbGetGenreID(genre);
			System.out.println(genreID);
			mpst.setInt(5, genreID);
			mpst.executeUpdate();
		}

		System.out.println("Added entry");
	}

	private int dbGetGenreID(String genre) throws SQLException {
		pst = con.prepareStatement("SELECT ID FROM GenreTest WHERE Genre=(?)");
		pst.setString(1, genre);
		rs = pst.executeQuery();
		int num = -1;
		while (rs.next()) {
			num = rs.getInt(1);
		}

		return num;
	}

	private void dbInsertGenre() throws SQLException {
		pst = con
				.prepareStatement("INSERT INTO GenreTest(ID, Genre) VALUES(?,?)");
		for (Entry<Long, String> e : genreMap.entrySet()) {
			long key = e.getKey();
			String value = e.getValue();
			pst.setLong(1, key);
			pst.setString(2, value);
			pst.executeUpdate();
			System.out.println("Added entry");
		}
	}
}