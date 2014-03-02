package test;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import wordplay.Tagger;

public class TaggerTest {

	private static Tagger tagger = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		tagger = new Tagger();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		tagger.createStopList("./stopwords.txt");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRemoveStopWords() {
		//tagger.removeStopWords("The quick fox jumped over the quick brown dog.");
		//System.out.println(tagger.getWords());
	}

	@Test
	public void testTermOccurrence() {
		//tagger.termOccurrence("The lazy fox jumped over the quick brown dog.");
	}

	@Test
	public void testGetWords() {
		tagger.setStemStopFilter("The quick fox jumped over a lazy c d z 123.");
		tagger.applyFilter();
	}
	
	@Test
	public void testStemmer() {
		//System.out.println(tagger.stemFilter("The lazy fox jumped over the quick brown dog."));
	}

}
