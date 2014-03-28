package evaluation;

import java.util.Map;
/**
 * This class is used as the evaluation tool for the system.
 * Evaluation is performed on a per genre basis for each classification experiment.
 * First of all the true positive, false negative and false positive numbers are
 * counted and then the equations for Precision, Recall and F-Measure are calculated.
 * 
 * @author Fiona MacIsaac
 *
 */

public class Evaluation {

	private double truepos = 0;
	private double falsepos = 0;
	private double falseneg = 0;

	/**
	 * Initialise these items as 0 so that if a calculation is not possible,
	 * a zero entry can be added to the database for reference.
	 */
	private double precision = 0;
	private double recall = 0;
	private double fmeasure = 0;

	public Evaluation() {

	}

	/**
	 * Take in the pre-determined list of films that have been
	 * tagged with a specific genre and compare this to the list
	 * of classified films for the same genre. The tp, fp and fn can be
	 * counted for the genre. From this the precision, recall and 
	 * f-measure can be calulcated.
	 * @param gold
	 * @param classified
	 */
	public void runEvaluation(Map<Integer, Integer> gold,
			Map<Integer, Integer> classified) {
		for (Integer filmid : gold.keySet()) {
			if (classified.containsKey(filmid)) {
				truepos++;
			} else {
				falseneg++;
			}
		}

		for (Integer filmid : classified.keySet()) {
			if (!gold.containsKey(filmid)) {
				falsepos++;
			}
		}

		setPrecision();
		setRecall();
		setFmeasure();
	}

	/**
	 * Perform the precision calculation as long as one of the variables
	 * is not 0. If both variables are 0 then there is no point in the
	 * calculation being performed.
	 */
	private void setPrecision() {
		if (this.truepos > 0 || this.falsepos > 0) {
			this.precision = this.truepos / (this.truepos + this.falsepos);
		}
	}

	/**
	 * Perform the recall calculation as long as one of the variables
	 * is not 0. If both variables are 0 then there is no point in the
	 * calculation being performed.
	 */
	private void setRecall() {
		if (this.truepos > 0 || this.falseneg > 0) {
			this.recall = this.truepos / (this.truepos + this.falseneg);
		}
	}

	/**
	 * Perform the F-Measure calculation as long as both of the variables
	 * are positive. If either is 0 then the result is irrelevant.
	 */
	private void setFmeasure() {
		if (this.precision > 0 && this.recall > 0) {
			this.fmeasure = 2 * ((this.precision * this.recall) / (this.precision + this.recall));
		}
	}

	/**
	 * Return the precision.
	 * 
	 * @return
	 */
	public double getPrecision() {
		return this.precision;
	}

	/**
	 * Return the recall.
	 * 
	 * @return
	 */
	public double getRecall() {
		return this.recall;
	}

	/**
	 * Return the F-Measure.
	 * 
	 * @return
	 */
	public double getFmeasure() {
		return this.fmeasure;
	}

}
