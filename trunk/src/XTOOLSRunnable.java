/*
 * Copyright 2005 Mike Tinker, Gerry Dozier, Aaron Gerrett, Lauren Goff, 
 * Mike SanSoucie, and Patrick Hull
 * Copyright 2011 Joshua Adams
 * 
 * This file is part of X-TOOLSS.
 *
 * X-TOOLSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * X-TOOLSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with X-TOOLSS.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JOptionPane;
import lib.genevot.*;

public class XTOOLSRunnable extends Thread {
	private Population population;
	private ParticleSwarmOptimization pso;
	private TerminationCriteria termCrit;
	private ECMonitor ecMonitor;
	private OnlineAdaptation onlineAdapt;
	private int maxNumRuns;
	private int numRuns = 0;
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
	public int getNumRuns(){
		return numRuns;
	}
	
	public int getMaxNumRuns(){
		return maxNumRuns;
	}
	public void run() {
		numRuns = 0;
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
//CSA				ecMonitor.initialize();
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

