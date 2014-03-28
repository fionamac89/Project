package main;

import view.CommandLine;

/**
 * Main method class for running the system.
 * 
 * @author Fiona MacIsaac
 *
 */
public class Run {

	public static void main(String[] args) {		
		CommandLine gui = new CommandLine();
		gui.menu();
	}

}
