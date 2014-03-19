package evaluation;

import java.util.Map;

public class Evaluation {

	private double truepos = 0;
	private double falsepos = 0;
	private double falseneg = 0;

	private double precision = 0;
	private double recall = 0;
	private double fmeasure = 0;

	public Evaluation() {
		
	}

	public void runEvaluation(Map<Integer, Integer> gold, Map<Integer, Integer> classified) {
		for(Integer filmid : gold.keySet()) {
			if(classified.containsKey(filmid)) {
				truepos++;
			} else {
				falseneg++;
			}
		}
		
		for(Integer filmid : classified.keySet()) {
			if(!gold.containsKey(filmid)) {
				falsepos++;
			}
		}
		
		setPrecision();
		setRecall();
		setFmeasure();
	}

	private void setPrecision() {

		this.precision = this.truepos / (this.truepos + this.falsepos);
	}

	private void setRecall() {
		this.recall = this.truepos / (this.truepos + this.falseneg);
	}

	private void setFmeasure() {
		this.fmeasure = 2 * ((this.precision * this.recall) / (this.precision + this.recall));
	}

	public double getTruepos() {
		return truepos;
	}

	public double getFalsepos() {
		return falsepos;
	}

	public double getFalseneg() {
		return falseneg;
	}

	public double getPrecision() {
		return this.precision;
	}

	public double getRecall() {
		return this.recall;
	}

	public double getFmeasure() {
		return this.fmeasure;
	}

	public void setTP(double tp) {
		this.truepos = tp;
		System.out.println(this.truepos);
	}

	public void setFP(double fp) {
		this.falsepos = fp;
		System.out.println(this.falsepos);
	}

	public void setFN(double fn) {
		this.falseneg = fn;
		System.out.println(this.falseneg);
	}

}
