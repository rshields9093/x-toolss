//package edu.auburn.eng.aci.xtoolss;


import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.geom.Point2D;
import java.awt.Color;
import edu.auburn.eng.aci.genevot.*;


public class XTOOLSECMonitor implements ECMonitor, PropertyChangeListener {
	private ECResult ecResult;
	private XTOOLSResultsFrame frame;
	private PrintWriter logfile;
	private PrintWriter outfile;
	private int logInterval;
	private int numFunEvals;
	private int maxFunEvals;
	private int lastPopFunEvals;
	private ThreadTerminator threadTerminator;
	
	public XTOOLSECMonitor(boolean showFrame, int logInterval, int maxFunEvals, ThreadTerminator tt, String logFilename, String outFilename) {
		ecResult = new ECResult();
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
		numFunEvals = 0;
		lastPopFunEvals = 0;
		this.maxFunEvals = maxFunEvals;
		threadTerminator = tt;
		if(frame != null) {
			frame.addPropertyChangeListener(this);
		}	
	}
	
	public ECResult getResults(Population population, Individual[] parents, Individual[] children) {
		Individual best = population.getIndividual(0);
		double avgFit = 0.0;
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
		avgFit /= (double)population.getSize();
		numFunEvals += (population.getNumFunctionEvaluations() - lastPopFunEvals);
		lastPopFunEvals = population.getNumFunctionEvaluations();
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
		if(frame != null && (numFunEvals >= logInterval || population.getNumFunctionEvaluations() >= maxFunEvals)) {
			if(best instanceof Particle) {
				if(children != null) {
					frame.setBestIndividualInfo(frame.getBestIndividualInfo() + "Generation " + population.getNumGenerations() + " Best: " + ((Particle)best).getPFitness() + " : " + ((Particle)best).getP() + "\n");
					String tempString = "";
					tempString += "Evaluations: " + population.getNumFunctionEvaluations() + "      Total: " + maxFunEvals + "\n";
					tempString += "Best: " + ((Particle)best).getP() + " fit: " + ((Particle)best).getPFitness() + "\n";
					tempString += "Average Fitness: " + avgFit + "\n";
					for(int i = 0; i < population.getSize(); i++) {
						tempString += "Ind " + (i + 1) + ": " + ((Particle)population.getIndividual(i)).getP() + " fit: " + ((Particle)population.getIndividual(i)).getPFitness() + "\n";
					}
					frame.setCurrentPopulationInfo(tempString);
					frame.addPoint(new Point2D.Double(population.getNumGenerations(), avgFit), Color.blue);
					frame.addPoint(new Point2D.Double(population.getNumGenerations(), ((Particle)best).getPFitness()), Color.red);
				}
				else {
					frame.setBestIndividualInfo(frame.getBestIndividualInfo() + "Generation " + population.getNumGenerations() + " Best: " + ((Particle)best).getFitness() + " : " + ((Particle)best).getChromosome() + "\n");
					String tempString = "";
					tempString += "Evaluations: " + population.getNumFunctionEvaluations() + "      Total: " + maxFunEvals + "\n";
					tempString += "Best: " + ((Particle)best).getChromosome() + " fit: " + ((Particle)best).getFitness() + "\n";
					tempString += "Average Fitness: " + avgFit + "\n";
					for(int i = 0; i < population.getSize(); i++) {
						tempString += "Ind " + (i + 1) + ": " + ((Particle)population.getIndividual(i)).getChromosome() + " fit: " + ((Particle)population.getIndividual(i)).getFitness() + "\n";
					}
					frame.setCurrentPopulationInfo(tempString);
					frame.addPoint(new Point2D.Double(population.getNumGenerations(), avgFit), Color.blue);
					frame.addPoint(new Point2D.Double(population.getNumGenerations(), ((Particle)best).getFitness()), Color.red);
				}
			}
			else {
				frame.setBestIndividualInfo(frame.getBestIndividualInfo() + "Generation " + population.getNumGenerations() + " Best: " + best.getPrintableFitness() + " : " + best.getChromosome() + "\n");
				String tempString = "";
				tempString += "Evaluations: " + population.getNumFunctionEvaluations() + "      Total: " + maxFunEvals + "\n";
				tempString += "Best: " + best.getChromosome() + " fit: " + best.getPrintableFitness() + "\n";
				tempString += "Average Fitness: " + avgFit + "\n";
				for(int i = 0; i < population.getSize(); i++) {
					tempString += "Ind " + (i + 1) + ": " + population.getIndividual(i).getChromosome() + " fit: " + population.getIndividual(i).getPrintableFitness() + "\n";
				}
				frame.setCurrentPopulationInfo(tempString);
				frame.addPoint(new Point2D.Double(population.getNumGenerations(), avgFit), Color.blue);
				frame.addPoint(new Point2D.Double(population.getNumGenerations(), best.getFitness()), Color.red);
			}
		}
		if(numFunEvals >= logInterval) {
			numFunEvals = 0;
		}
		
//		System.out.println("Mutation Rate (" + population.getNumGenerations() + "): " + population.getMutationOperator().getMutationRate());
		return ecResult;
	}

	public boolean isDisplayed() {
		return (frame != null);
	}
	
	public void initialize() {
		ecResult = new ECResult();
		numFunEvals = 0;
		lastPopFunEvals = 0;
		frame.clearPoints();
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		if(event.getPropertyName().equals("WindowClosed")) {
			threadTerminator.killThread = true;
			if(logfile != null) {
				logfile.close();
			}
			if(outfile != null) {
				outfile.close();
			}
		}
	}
}
