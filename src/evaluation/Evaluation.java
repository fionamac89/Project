package evaluation;

import java.util.Map;
import java.util.Map.Entry;

public class Evaluation {

	private int truepos = 0;
	private int falsepos = 0;
	private int falseneg = 0;

	private double precision = 0;
	private double recall = 0;
	private double fmeasure = 0;

	public Evaluation() {

	}

	public void runEvaluation(Map<Integer, Integer> gold,
			Map<Integer, Integer> test) {
		System.out.println("Gold size: " + gold.size());
		System.out.println("Test size: " + test.size());
		for (Entry<Integer, Integer> e : gold.entrySet()) {
			if (test.containsKey(e.getKey())) {
				if (e.getValue() == test.get(e.getKey())) {
					truepos++;
				} else {
					falseneg++;
				}
			}
		}

		for (Entry<Integer, Integer> f : test.entrySet()) {
			if (!gold.containsKey(f.getKey())) {
				falsepos++;
			}
		}

		setPrecision();
		setRecall();
		setFmeasure();
	}

	private void setPrecision() {

		precision = truepos / (truepos + falsepos);
	}

	private void setRecall() {
		recall = truepos / (truepos + falseneg);
	}

	private void setFmeasure() {
		fmeasure = 2 * ((precision * recall) / (precision + recall));
	}

	public double getPrecision() {
		return precision;
	}

	public double getRecall() {
		return recall;
	}

	public double getFmeasure() {
		return fmeasure;
	}

}
