package view;

import java.util.List;

import main.ISystem;
import main.ProjectSystem;

/**
 * This class is used to provide the interface functionality for the program.
 * 
 * @author Fiona MacIsaac
 * 
 */
public class CommandLine {

	private ISystem system = null;
	private UserIO io = null;
	private String thesName = "";
	private String className = "";
	private String suffix = "";
	private String evalName = "";
	private boolean newList = false;
	private int size = -1;
	private String filter;
	private boolean newAlg = false;

	public CommandLine() {
		io = new UserIO();
		system = new ProjectSystem();
	}

	/**
	 * The main display and menu mechanism for the user interface. This method
	 * is called to 'start' the program.
	 */
	public void menu() {
		System.out
				.println("Welcome to the Thesaurus Creation and Classification System (TCCS)\n");

		while (!io.getExit()) {

			System.out.println("1. Create Training and Test sets\n"
					+ "2. Create Thesaurus\n" + "3. Perform Classification\n"
					+ "4. Perform Evaluation\n" + "5. List tables\n");
			System.out.println("Press q to exit.");
			String input = io
					.getString("Please enter the task number you would like to complete: ");

			switch (input) {
			/*
			 * Take in the size of training set to be created and the suffix to
			 * identify the training and test set pair. If the table with that
			 * suffix does not exist, then create them and populate the tables.
			 */
			case "1":

				this.size = io
						.getIntRange(
								"Please enter the size of the training set you wish to create: ",
								10, 4000);
				do {
					this.suffix = io
							.getString("Please enter the suffix to define the training/test pair: ");
				} while (tableExists("TestSet" + this.suffix));

				system.createTrainingTable(this.suffix);
				system.createTestTable(this.suffix);
				system.createTrainingSet(this.size, this.suffix, "FGLink_2");
				break;
			/*
			 * Take in the suffix to identify the thesaurus, the suffix for the
			 * training set to be used and the filter to be used. If
			 * the thesaurus table with that suffix exists, then ask the user
			 * for further input. If the training set with that suffix does not
			 * exist, then ask the user for further input.
			 */
			case "2":

				do {
					this.thesName = "Thesaurus"
							+ io.getString("Please enter the suffix to identify the Thesaurus: ");
				} while (tableExists(this.thesName));

				do {
					this.suffix = io
							.getString("Please enter the suffix of the training set to be used: ");
				} while (!tableExists("TrainingSet" + this.suffix));

				do {
					this.filter = io
							.getFilter("Please choose the filter you would like to implement: (none, s, sw or ssw)");

				} while (filter.length() < 1);
				system.createThesaurus(this.thesName);

				if (this.filter.equals("sw") || this.filter.equals("ssw")) {
					this.newList = io
							.getAlg("Would you like to use the new stop word list?: ");
					if (this.newList) {
						system.setStopWords("./lib/stopwords.txt");
					}
				}
				System.out.println("Thesaurus population begun.");
				system.populateThesaurus(this.filter, this.thesName,
						this.suffix);
				break;
			/*
			 * Take in the suffix to be used to identify the classification
			 * table. Take in the suffix that identifies the test set to be used
			 * for classification. Classify the data. Add it to the database
			 */
			case "3":
				do {
					this.className = "Classified"
							+ io.getString("Please enter the suffix to identify the Classification table: ");
				} while (tableExists(this.className));
				system.createClassified(this.className);

				do {
					this.suffix = io
							.getString("Please enter the suffix of the test set to be used: ");
				} while (!tableExists("TestSet" + this.suffix));
				system.trainClassifier(this.thesName);
				system.classifyTestData(this.suffix);
				system.archiveClassified(this.className);
				break;
			/*
			 * Take in the suffix to identify the evaluation table with. Take in
			 * the suffixes of the test set and classified tables to be compared
			 * during evaluation and perform the evaluation on these tables.
			 */
			case "4":
				do {
					this.evalName = "Eval"
							+ io.getString("Please enter the suffix to identify the Evaluation table: ");
				} while (tableExists(this.evalName));
				system.createEvalGenre(this.evalName);

				do {
					this.suffix = io
							.getString("Please enter the suffix of the test set to be used during evaluation: ");
				} while (!tableExists("TestSet" + this.suffix));

				do {
					this.className = "Classified"
							+ io.getString("Please enter the suffix of the Classification table to use: ");
				} while (!tableExists(this.className));

				system.runEvalPerGenre(this.evalName, this.suffix,
						this.className);
				break;
			/*
			 * Display the list of tables in the database.
			 */
			case "5":
				display(system.getTables());
				break;
			/*
			 * Allow the user to quit the system.
			 */
			case "q":
				io.checkExit();
				break;
			default:
				System.out.println("Invalid input, please try again.");
			}
		}

		System.out.println("Goodbye!");
	}

	/**
	 * Check if the given name is already present in the database as a table.
	 * 
	 * @param name
	 * @return
	 */
	private boolean tableExists(String name) {
		if (system.tableExists(name)) {
			System.out.printf("Table %s exists. \n", name);
			return true;
		}
		System.out.printf("Table %s did not exist. \n", name);
		return false;
	}

	/**
	 * Format the list of tables to display appropriately.
	 * 
	 * @param list
	 */
	private void display(List<String> list) {
		for (String l : list) {
			System.out.println(l);
		}
	}

}
