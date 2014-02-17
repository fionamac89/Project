package test;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import database.Database;

public class DatabaseTest {

	private static Database db = null;
	private List<Integer> genres = null;
	private List<Integer> movies = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		db = new Database();
		db.dbConnect();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		db.dbDisconnect();
		db = null;
	}

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testDbConnect() {
		// fail("Not yet implemented"); // TODO
	}

	@Test
	public void testDbInsertMovie() {
		// fail("Not yet implemented"); // TODO
	}

	@Test
	public void testDbCreateFGLink() {
		// fail("Not yet implemented"); // TODO
	}

	@Test
	public void testDbGetGenreID() {
		// fail("Not yet implemented"); // TODO
	}

	@Test
	public void testDbGetGenreList() {
		genres = db.dbGetGenreList();
		//System.out.println(genres);
	}

	@Test
	public void testDbInsertGenre() {
		// fail("Not yet implemented"); // TODO
	}

	@Test
	public void testDbGetMoviesForGenre() {
		genres = db.dbGetGenreList();
		for(Integer id : genres) {
			movies = db.dbGetMoviesForGenre(id);
			//System.out.println(movies);
		}

	}

	@Test
	public void testDbGetOverview() {
		//movies = null;
		movies = db.dbGetMoviesForGenre(12);
		Map<Integer, String> overviews = db.dbGetOverview(movies);
		for (Entry<Integer, String> e : overviews.entrySet()) {
			System.out.println(e.getKey() + ": " + e.getValue());
		}
	}

	@Test
	public void testDbPopulateTrainingSet() {
		// fail("Not yet implemented"); // TODO
	}

	@Test
	public void testDbPopulateTestSet() {
		// fail("Not yet implemented"); // TODO
	}

	@Test
	public void testDbGetTrainingSet() {
		// fail("Not yet implemented"); // TODO
	}

	@Test
	public void testDbGetTestSet() {
		// fail("Not yet implemented"); // TODO
	}

	@Test
	public void testDbDeleteMovies() {
		// fail("Not yet implemented"); // TODO
	}

}
