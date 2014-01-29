package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    public Database(String filepath) {
        String url = "jdbc:mysql://localhost:3306/fdb11130";
        String user = "fdb11130";
        String password = "rigulatn";
        try {
			this.con = DriverManager.getConnection(this.url, this.user, this.password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        input = new Parser(filepath);
    }

    public static void main(String[] args) {
//    	Database db = new Database("/Users/Fiona/Dropbox/Strath Uni/Year 4/Project/Script Test/output3.txt");
//
//        try {
//        	db.dbConnect();
//        	db.input.parseMovie();
//        } catch (SQLException ex) {
//            Logger lgr = Logger.getLogger(Database.class.getName());
//            lgr.log(Level.SEVERE, ex.getMessage(), ex);
//
//        } finally {
//            try {
//                if (db.rs != null) {
//                    db.rs.close();
//                }
//                if (db.st != null) {
//                    db.st.close();
//                }
//                if (db.con != null) {
//                    db.con.close();
//                }
//
//            } catch (SQLException ex) {
//                Logger lgr = Logger.getLogger(Database.class.getName());
//                lgr.log(Level.WARNING, ex.getMessage(), ex);
//            }
//        }
        
        /**
         * Parser for Genres and insert into DB
         */
    	Database db = new Database("/Users/Fiona/Dropbox/Strath Uni/Year 4/Project/Script Test/genres.txt");

        try {
        	db.dbConnect();
        	db.genreMap = db.input.parseGenre();
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Database.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            try {
                if (db.rs != null) {
                    db.rs.close();
                }
                if (db.st != null) {
                    db.st.close();
                }
                if (db.con != null) {
                    db.con.close();
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
        this.st = this.con.createStatement();
        
        //INSERT statement to insert Movie object into DB
        this.rs = this.st.executeQuery("SELECT VERSION()");
    }
    
    private void dbInsertGenre() throws SQLException {
        this.st = this.con.createStatement();
        
        //INSERT statement to insert Movie object into DB
        this.rs = this.st.executeQuery("SELECT VERSION()");
    }
}