package movelist.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {

	String[] banned = { "hot", "pussy", "tits", "titties", "cock", "sex" };

	public Parser() {

	}

	public static void main(String args[]) throws IOException {
		Parser parser = new Parser(); //
		System.out.println("I'm about to parse things");
		try {
			parser.parseDelimitedFile("/Users/Fiona/Documents/ProjectLists/movies.list");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void parseDelimitedFile(String filePath) throws Exception {
		File file = new File("movies.txt");
		File in = new File(filePath);
		FileWriter writer = new FileWriter(file);
		FileReader fr = new FileReader(in);
		BufferedWriter bw = new BufferedWriter(writer);
		BufferedReader br = new BufferedReader(fr);
		String currentRecord;
		String oldRecord = "";
		String record = "";
		System.out.println("I'm here");
		currentRecord = br.readLine();
		while ((currentRecord = br.readLine()) != null) {
			currentRecord = readToken(currentRecord);
			if (currentRecord.length() == 0) {
				;
			} else {
				if (currentRecord.equalsIgnoreCase(oldRecord)) {
					;
				} else {
					System.out.println(currentRecord);
					record = currentRecord + "\n";
					bw.write(record);
				}

				oldRecord = currentRecord;
			}
		}
		br.close();
		bw.close();
	}

	private String readToken(String token) {
		String[] array = token.split("[(]");
		String finished = "";
		for (int i = 0; i < banned.length; i++) {
			
			//str.replaceAll("[^\\dA-Za-z ]", "").replaceAll("\\s+", "+");
			if (array[0].startsWith("[^\\dA-Za-z ]")) {
				;
			} else {
				
				finished = array[0].replaceAll("[^\\dA-Za-z ]", "");
			}
		}
		return finished;

	}
}
