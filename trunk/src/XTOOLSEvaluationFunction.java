//package edu.auburn.eng.aci.xtoolss;


import edu.auburn.eng.aci.genevot.EvaluationFunction;
import edu.auburn.eng.aci.genevot.Chromosome;
import edu.auburn.eng.aci.genevot.Interval;


public class XTOOLSEvaluationFunction implements EvaluationFunction {
	private AppFile application;
	
	public XTOOLSEvaluationFunction(AppFile app) {
		application = app;
	}

	public double[] evaluate(Chromosome c) {
		return application.runWith(c);
	}
}
