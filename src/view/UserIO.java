package view;

import java.io.File;
import java.util.Scanner;

public class UserIO {
	private Scanner inputReader = null;
	private boolean exit = false;

	public UserIO() {
		this.inputReader = new Scanner(System.in);
	}

	// Get String
	public String getString(String text) {
		System.out.println(text);
		String temp = "";
		do {
			temp = inputReader.next();
			if (temp.length() < 1) {
				System.out.println("Invalid string input. Try again: ");
				temp = "";
			}
		} while (temp.length() < 1);

		return temp.trim();

	}

	// Get Integer
	public int getIntRange(String text, int min, int max) {

		int number = 0;
		System.out.println(text);
		do {
			while (!inputReader.hasNextInt()) {
				System.out
						.printf("Training Set size must be an integer between %d and %d. Try again:\n",
								min, max);
				inputReader.next();
			}
			number = inputReader.nextInt();
			if (number < min || number > max)
				System.out
						.printf("Training Set size must be an integer between %d and %d. Try again:\n",
								min, max);
		} while (number < min || number > max);

		return number;
	}

	// Check filepath
	public File getFilepath(String text) {
		System.out.println(text);
		File filepath = null;
		while (!inputReader.hasNext("")) {
			filepath = new File(inputReader.next());
			if (!filepath.exists()) {
				System.out.println("Invalid filepath. Try again:");
				filepath = null;
			}
		}

		return filepath;
	}

	// Check connection (DS login or ssh)
	public boolean onUniNetwork(String text) {
		System.out.println(text);
		String temp = "";
		boolean flag = false;
		do {
			temp = inputReader.next();
			if (!temp.equals("y") && !temp.equals("n")) {
				System.out.println("Please use y or n to answer: ");
			}
		} while (!temp.equals("y") && !temp.equals("n"));

//		temp = inputReader.next();
		if (temp.equals("y")) {
			System.out.println("I am connected to uni network");
			flag = true;
		} else if (temp.equals("n")) {
			flag = false;
		}

		return flag;
	}

	public boolean checkExit() {

		System.out.println("Do you really wish to exit? (y/n): ");
		if (inputReader.hasNext("y")) {
			this.exit = true;
		} else if (inputReader.hasNext("n")) {
			this.exit = false;
		}
		inputReader.next();
		return this.exit;
	}

	public boolean getExit() {
		return exit;
	}

	public String getFilter(String text) {
		System.out.println(text);
		String temp = "";
		boolean filter = false;
		do {
			temp = inputReader.next();
			switch (temp) {
			case "none":
			case "s":
			case "sw":
			case "ssw":
				filter = true;
				break;
				default:
					System.out.println("Input must be either none, s, sw or ssw. Try again: ");
					temp = "";
			}
		} while (!filter);

		return temp.trim();
	}

	public boolean getAlg(String text) {
		System.out.println(text);
		String temp = "";
		boolean newAlg = false;
		do {
			if (!inputReader.hasNext("y") && !inputReader.hasNext("n")) {
				System.out.println("Please use y or n to answer: ");
				inputReader.next();
			}
		} while (!inputReader.hasNext("y") && !inputReader.hasNext("n"));

		temp = inputReader.next();
		
		if (temp.equals("y")) {
			newAlg = true;
		} else if (temp.equals("n")) {
			newAlg = false;
		}

		return newAlg;
	}
}
