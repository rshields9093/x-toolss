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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.geom.Point2D;
import java.awt.Color;

import javax.swing.JFrame;

import lib.genevot.*;

/*
 * The Population object of the genetic algorithm runs this every time it completes
 * a function evaluation.  This is where you update the GUI and log files.
 */
public class XTOOLSECMonitor implements ECMonitor{
	private ECResult ecResult;
	private XTOOLSResultsFrame frame;
	private OptimizationPanel optPanel;
	private PrintWriter logfile;
	private PrintWriter outfile;
	private int logInterval;
	private int numIntervals;
	private int numFunEvals;
	private int maxFunEvals;
	private int lastPopFunEvals;
	private boolean newBest = true;
	private ThreadTerminator threadTerminator;
	
	
	
	public XTOOLSECMonitor(OptimizationPanel op, boolean showFrame, int logInterval, int maxFunEvals, ThreadTerminator tt, String logFilename, String outFilename) {
		ecResult = new ECResult();
		optPanel = op;
		if(showFrame) {
			frame = new XTOOLSResultsFrame();
		}
		else {
			frame = null;
		}
		logfile = null;
		outfile = null;
		if(logFilename != null) {
			try {
				logfile = new PrintWriter(new FileOutputStream(logFilename));
			}
			catch(IOException e) {
				System.out.println(e);
			}
		}
		if(outFilename != null) {
			try {
				outfile = new PrintWriter(new FileOutputStream(outFilename));
			}
			catch(IOException e) {
				System.out.println(e);
			}
		}	
		this.logInterval = logInterval;
		numIntervals = 0;
		numFunEvals = 0;
		lastPopFunEvals = 0;
		this.maxFunEvals = maxFunEvals;
		threadTerminator = tt;
		if(frame != null) {
			frame.setLocationRelativeTo(null);
		}	
	}
	
	public ECResult getResults(Population population, Individual[] parents, Individual[] children) {
		Individual best = population.getIndividual(0);
		double avgFit = 0.0;
		
		/*
		 * This section of code calculates the following:
		 *   smallestFit
		 *   avgFit
		 *   ...
		 */
		
		
		if(best instanceof Particle) {
			double smallestFit = Double.POSITIVE_INFINITY;
			if(children != null) {
				smallestFit = ((Particle)best).getPFitness();
				for(int i = 0; i < population.getSize(); i++) {
					avgFit += ((Particle)population.getIndividual(i)).getPFitness();	
					if(((Particle)population.getIndividual(i)).getPFitness() < smallestFit) {
						best = (Particle)population.getIndividual(i);
						smallestFit = ((Particle)best).getPFitness();	
					}
				}
			}
			else {
				smallestFit = ((Particle)best).getPFitness();
				for(int i = 0; i < population.getSize(); i++) {
					avgFit += ((Particle)population.getIndividual(i)).getPFitness();	
					if(((Particle)population.getIndividual(i)).getPFitness() < smallestFit) {
						best = (Particle)population.getIndividual(i);
						smallestFit = ((Particle)best).getPFitness();	
					}
				}
			}
			if(ecResult.bestIndividual == null || (ecResult.bestFitness.size() > 0 && smallestFit < ecResult.bestFitness.elementAt(0))) {
				ecResult.bestIndividual = (Particle)best.clone();
				if(ecResult.bestFitness.size() <= 0) {
					ecResult.bestFitness.add(smallestFit);
				}
				else {
					ecResult.bestFitness.setElementAt(smallestFit, 0);
				}
				ecResult.numFEBest = population.getNumFunctionEvaluations();	
			}
			else if(ecResult.bestFitness.size() <= 0) {
				ecResult.bestIndividual = (Particle)best.clone();
				ecResult.bestFitness.add(smallestFit);
				ecResult.numFEBest = population.getNumFunctionEvaluations();	
			}
		}
		else {
			for(int i = 0; i < population.getSize(); i++) {
				avgFit += population.getIndividual(i).getFitness();
				if(population.getIndividual(i).compareTo(best) < 0) {
					best = population.getIndividual(i);
				}
			}
			ecResult.bestFitness.clear();
			ecResult.bestIndividual = (Individual)best.clone();
			for(int i = 0; i < best.getNumFitnessValues(); i++) {
				ecResult.bestFitness.add(best.getFitness(i));
			}	
			ecResult.numFEBest = population.getNumFunctionEvaluations();	
		}
		
		/*
		 * Calc avgFit, numFunEvals and lastPopFunEvals
		 */
		avgFit /= (double)population.getSize();
		numFunEvals += (population.getNumFunctionEvaluations() - lastPopFunEvals);
		lastPopFunEvals = population.getNumFunctionEvaluations();
		
		
		/*
		 * Write to files (should only take place if log interval is same as numFunEvals)
		 */
		if(logfile != null) {
			if(best instanceof Particle) {
				if(children != null) {
					for(int i = 0; i < children.length; i++) {
						logfile.println(((Particle)children[i]).getP() + " " + ((Particle)children[i]).getPFitness());
					}
				}	
				else {
					for(int i = 0; i < population.getSize(); i++) {
						logfile.println(((Particle)population.getIndividual(i)).getChromosome() + " " + ((Particle)population.getIndividual(i)).getFitness());
					}
				}
			}
			else {
				if(children != null) {
					for(int i = 0; i < children.length; i++) {
						logfile.println(children[i].getChromosome() + " || " + children[i].getPrintableFitness()); 
					}
				}
				else {
					for(int i = 0; i < population.getSize(); i++) {
						logfile.println(population.getIndividual(i).getChromosome() + " || " + population.getIndividual(i).getPrintableFitness());
					}
				}	
			}
			logfile.flush();
		}
		if(outfile != null) {
			if(numFunEvals >= logInterval) {
				if(best instanceof Particle) {
					if(children != null) {
						outfile.println("Evaluations: " + population.getNumFunctionEvaluations() + "          Total: " + maxFunEvals);
						outfile.println("Best: " + ((Particle)best).getP() + " fit: " + ((Particle)best).getPFitness());
						outfile.println("Average Fitness: " + avgFit);
						for(int i = 0; i < population.getSize(); i++) {
							outfile.println("Ind " + (i + 1) + ": " + ((Particle)population.getIndividual(i)).getP() + " fit: " + ((Particle)population.getIndividual(i)).getPFitness());
						}
					}
					else {
						outfile.println("Evaluations: " + population.getNumFunctionEvaluations() + "          Total: " + maxFunEvals);
						outfile.println("Best: " + ((Particle)best).getChromosome() + " fit: " + ((Particle)best).getFitness());
						outfile.println("Average Fitness: " + avgFit);
						for(int i = 0; i < population.getSize(); i++) {
							outfile.println("Ind " + (i + 1) + ": " + ((Particle)population.getIndividual(i)).getChromosome() + " fit: " + ((Particle)population.getIndividual(i)).getFitness());
						}					
					}
				}
				else {
					outfile.println("Evaluations: " + population.getNumFunctionEvaluations() + "          Total: " + maxFunEvals);
					outfile.println("Best: " + best.getChromosome() + " fit: " + best.getPrintableFitness());
					outfile.println("Average Fitness: " + avgFit);
					for(int i = 0; i < population.getSize(); i++) {
						outfile.println("Ind " + (i + 1) + ": " + population.getIndividual(i).getChromosome() + " fit: " + population.getIndividual(i).getPrintableFitness());
					}
				}
				outfile.println();
				outfile.flush();
			}
			else if(population.getNumFunctionEvaluations() >= maxFunEvals) {
				if(best instanceof Particle) {
					outfile.println("Evaluations: " + population.getNumFunctionEvaluations() + "          Total: " + maxFunEvals);
					outfile.println("Best: " + ((Particle)best).getP() + " fit: " + ((Particle)best).getPFitness());
					outfile.println("Average Fitness: " + avgFit);
					for(int i = 0; i < population.getSize(); i++) {
						outfile.println("Ind " + (i + 1) + ": " + ((Particle)population.getIndividual(i)).getP() + " fit: " + ((Particle)population.getIndividual(i)).getPFitness());
					}
				}
				else {
					outfile.println("Evaluations: " + population.getNumFunctionEvaluations() + "          Total: " + maxFunEvals);
					outfile.println("Best: " + best.getChromosome() + " fit: " + best.getPrintableFitness());
					outfile.println("Average Fitness: " + avgFit);
					for(int i = 0; i < population.getSize(); i++) {
						outfile.println("Ind " + (i + 1) + ": " + population.getIndividual(i).getChromosome() + " fit: " + population.getIndividual(i).getPrintableFitness());
					}
				}
				outfile.println();
				outfile.flush();				
			}
		}
		
		String tempString = "";
		double bestFit = 0.0;
		String bestFitStr = "";
		if((frame != null && population.getNumFunctionEvaluations() >= population.getSize())||(population.getNumFunctionEvaluations() >= maxFunEvals)) {
			if(best instanceof Particle) {
				if(children != null) {
					bestFitStr = ((Particle)best).getPFitness() + " : " + ((Particle)best).getP();
					tempString += "Evaluations: " + population.getNumFunctionEvaluations() + "      Total: " + maxFunEvals + "\n";
					tempString += "Best: " + ((Particle)best).getP() + " fit: " + ((Particle)best).getPFitness() + "\n";
					tempString += "Average Fitness: " + avgFit + "\n";
					for(int i = 0; i < population.getSize(); i++) {
						tempString += "Ind " + (i + 1) + ": " + ((Particle)population.getIndividual(i)).getP() + " fit: " + ((Particle)population.getIndividual(i)).getPFitness() + "\n";
					}
					bestFit = ((Particle)best).getPFitness();
				}
				else {
					bestFitStr = ((Particle)best).getFitness() + " : " + ((Particle)best).getChromosome();
					tempString += "Evaluations: " + population.getNumFunctionEvaluations() + "      Total: " + maxFunEvals + "\n";
					tempString += "Best: " + ((Particle)best).getChromosome() + " fit: " + ((Particle)best).getFitness() + "\n";
					tempString += "Average Fitness: " + avgFit + "\n";
					for(int i = 0; i < population.getSize(); i++) {
						tempString += "Ind " + (i + 1) + ": " + ((Particle)population.getIndividual(i)).getChromosome() + " fit: " + ((Particle)population.getIndividual(i)).getFitness() + "\n";
					}
					bestFit = ((Particle)best).getFitness();
				}
			}
			else {
				bestFitStr = best.getPrintableFitness() + " : " + best.getChromosome();
				tempString += "Evaluations: " + population.getNumFunctionEvaluations() + "      Total: " + maxFunEvals + "\n";
				tempString += "Best: " + best.getChromosome() + " fit: " + best.getPrintableFitness() + "\n";
				tempString += "Average Fitness: " + avgFit + "\n";
				for(int i = 0; i < population.getSize(); i++) {
					tempString += "Ind " + (i + 1) + ": " + population.getIndividual(i).getChromosome() + " fit: " + population.getIndividual(i).getPrintableFitness() + "\n";
				}
				bestFit = best.getFitness();
			}
			frame.setBestIndividualInfo(frame.getBestIndividualInfo() + "Generation " + population.getNumGenerations() + " Best: " + bestFitStr + "\n", bestFit);
			frame.setCurrentPopulationInfo(tempString);
			frame.addPoint(new Point2D.Double(population.getNumGenerations(), avgFit), Color.blue);
			frame.addPoint(new Point2D.Double(population.getNumGenerations(), bestFit), Color.red);
			if(optPanel != null){
				optPanel.updateData();
			}
		}
		if(numFunEvals >= logInterval) {
			numFunEvals = 0;
			numIntervals++;
		}
		
//		System.out.println("Mutation Rate (" + population.getNumGenerations() + "): " + population.getMutationOperator().getMutationRate());
		return ecResult;
	}

	public boolean isDisplayed() {
		return (frame != null);
	}
	
	public int getNumFunctEval(){
		return maxFunEvals;
	}
	
	public int getCompFunctEval(){
		return (logInterval*numIntervals)+numFunEvals;
	}
	
	public void initialize() {
		ecResult = new ECResult();
		numFunEvals = 0;
		lastPopFunEvals = 0;
		if(frame != null) frame.clearPoints();
	}
	
	public void endOptimization() {
		threadTerminator.killThread = true;
		if(logfile != null) {
			logfile.close();
		}
		if(outfile != null) {
			outfile.close();
		}
	}
	/*
	//This function is no longer needed, window can't be closed, event never fires.
	public void propertyChange(PropertyChangeEvent event) {
		if(event.getPropertyName().equals("WindowClosed")) {
			endOptimization();
		}
	}
	*/

	public JFrame getFrame() {
		return frame;
	}

	public ECResult getLastResult() {
		return ecResult;
	}
}
