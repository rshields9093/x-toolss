//package edu.auburn.eng.aci.xtoolss;


import edu.auburn.eng.aci.genevot.TerminationCriteria;
import edu.auburn.eng.aci.genevot.Population;


public class MaxFunctionEvalTermination implements TerminationCriteria {
	private int numFunctionEvals;
	private ThreadTerminator threadTerminator;
	
	public MaxFunctionEvalTermination(int numFE, ThreadTerminator tt) {
		numFunctionEvals = numFE;
		threadTerminator = tt;
	}
	
	public boolean terminate(Population population) {
		return (threadTerminator.killThread || (population.getNumFunctionEvaluations() >= numFunctionEvals));
	}
}
