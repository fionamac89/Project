package view;

import java.io.File;
import java.util.Scanner;
/**
 * This class is used to facilitate and validate the user input which is
 * requested by the CommandLine class.
 * 
 * @author Fiona MacIsaac
 *
 */
public class UserIO {
	private Scanner inputReader = null;
	private boolean exit = false;

	public UserIO() {
		this.inputReader = new Scanner(System.in);
	}

	/**
	 * Display appropriate text to the user requesting input then
	 * validate this input to check it is reasonable text.
	 * 
	 * @param text
	 * @return
	 */
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

	/**
	 * Use this method to get a size from the user that falls within a
	 * predetermined range.
	 * 
	 * @param text
	 * @param min
	 * @param max
	 * @return
	 */
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

	/**
	 * Use this method to make certain that the user wishes to exit the program.
	 * 
	 * @return
	 */
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

	/**
	 * Return if the user has chosen to exit or not.
	 * 
	 * @return
	 */
	public boolean getExit() {
		return exit;
	}

	/**
	 * Validate that the user has entered a proper filter. Ignore all other input.
	 * 
	 * @param text
	 * @return
	 */
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

	/**
	 * Return whether the user wants to use the new or old algorithm.
	 * 
	 * @param text
	 * @return
	 */
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
