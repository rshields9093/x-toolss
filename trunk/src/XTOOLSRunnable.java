//package edu.auburn.eng.aci.xtoolss;


import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JOptionPane;
import edu.auburn.eng.aci.genevot.*;

public class XTOOLSRunnable extends Thread {
	private Population population;
	private ParticleSwarmOptimization pso;
	private TerminationCriteria termCrit;
	private ECMonitor ecMonitor;
	private OnlineAdaptation onlineAdapt;
	private int maxNumRuns;
	private ThreadTerminator threadTerminator;
	private PrintWriter statfile;
	private String statFilename;
	private boolean isDisplayed;
	
	public XTOOLSRunnable(Population p, TerminationCriteria tc, ECMonitor ecMon, int maxRuns, ThreadTerminator tt, String statFilename, boolean isDisplayed, OnlineAdaptation oa) {
		super("X-TOOLSS Thread");
		population = p;
		termCrit = tc;
		ecMonitor = ecMon;
		onlineAdapt = oa;
		maxNumRuns = maxRuns;
		threadTerminator = tt;
		statfile = null;
		pso = null;
		this.statFilename = statFilename;
		this.isDisplayed = isDisplayed;
	}

	public XTOOLSRunnable(ParticleSwarmOptimization pso, int maxRuns, ThreadTerminator tt, String statFilename, boolean isDisplayed) {
		maxNumRuns = maxRuns;
		threadTerminator = tt;
		statfile = null;
		this.pso = pso;
		this.statFilename = statFilename;
		this.isDisplayed = isDisplayed;
		onlineAdapt = null;
	}
	
	public void run() {
		int numRuns = 0;
		if(statFilename != null) {
			try {
				statfile = new PrintWriter(new FileOutputStream(statFilename));
			}
			catch(IOException e) {
				System.out.println(e);
			}
		}
		else {
			try {
				statfile = new PrintWriter(new FileOutputStream("xtoolss.stat"));
			}
			catch(IOException e) {
				System.out.println(e);
			}		
		}
		while(!threadTerminator.killThread && numRuns < maxNumRuns) {
			ECResult result = null;
			if(pso == null) {
				ecMonitor.initialize();
				population.initialize();
				result = population.evolve(termCrit, ecMonitor, onlineAdapt);
			}
			else {
				pso.reset();
				result = pso.evolve();
			}
				
			if(statfile != null) {
				statfile.println(result.bestIndividual.getPrintableFitness());
				statfile.flush();
			}
			numRuns++;
		}
		if(statfile != null) {
			statfile.close();
		}
		String message = null;
		if(threadTerminator.killThread) {
			message = "X-TOOLSS was terminated during run " + numRuns + "."; 		
		}
		else if(numRuns == maxNumRuns) {
			message = "X-TOOLSS has finished all " + maxNumRuns + " runs."; 
		}
		else {
			message = "X-TOOLSS was terminated during run " + numRuns + "."; 
		}
		if(isDisplayed) {
			JOptionPane.showMessageDialog(null, message);
		}	
		threadTerminator.killThread = true;
	}
}

