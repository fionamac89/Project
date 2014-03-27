package view;

import java.util.List;

import main.ISystem;
import main.ProjectSystem;

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
	private boolean connected = false;

	public CommandLine() {
		io = new UserIO();
	}

	public void menu() {
		System.out
				.println("Welcome to the Thesaurus Creation and Classification System (TCCS)\n");
		connType("Are you logged in to a device connected to the university network? (y/n): ");

		if (this.connected) {
			while (!io.getExit()) {

				System.out.println("1. Create Training and Test sets\n"
						+ "2. Create Thesaurus\n"
						+ "3. Perform Classification\n"
						+ "4. Perform Evaluation\n" + "5. List tables\n");
				System.out.println("Press q to exit.");
				String input = io
						.getString("Please enter the task number you would like to complete: ");

				switch (input) {
				case "1":
					/*
					 * 1 - take in size of training set greater than 0 and less
					 * than a constant 2 - take in the suffix to be used to
					 * define the training/test pair 3 - check if the training
					 * table exists. if yes, return to this menu
					 */

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
				case "2":
					/*
					 * 1 - take in thesaurus name 2 - check if that table
					 * already exists if yes, return to this menu 3 - populate
					 * thesaurus
					 */
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
					this.newAlg = io
							.getAlg("Would you like to use the new algorithm?: ");
					System.out.println("Thesaurus population begun.");
					if (newAlg) {
						system.populateThesaurus2(this.filter, this.thesName,
								this.suffix);
					} else {
						system.populateThesaurus(this.filter, this.thesName,
								this.suffix);
					}
					break;
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

					system.classifyTestData(this.suffix);
					break;
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
								+ io.getString("Please enter the suffix to identify the Classification table: ");
					} while (!tableExists(this.className));
					
					system.runEvalPerGenre(this.evalName, this.suffix,
							this.className);
					break;
				case "5":
					display(system.getTables());
					break;
				case "q":
					io.checkExit();
					break;
				default:
					System.out.println("Invalid input, please try again.");
				}
			}
		}

		System.out.println("Goodbye!");
	}

	private boolean tableExists(String name) {
		if (system.tableExists(name)) {
			System.out.printf("Table %s exists. \n", name);
			return true;
		}
		System.out.printf("Table %s created. \n", name);
		return false;
	}

	private void display(List<String> list) {
		for (String l : list) {
			System.out.println(l);
		}
	}

	private void connType(String text) {
		if (io.onUniNetwork(text)) {
			system = new ProjectSystem(true);
			this.connected = true;
		} else {
			system = new ProjectSystem(false);
			this.connected = true;
		}

	}

}
