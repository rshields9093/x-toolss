
//package edu.auburn.eng.aci.xtoolss;

import edu.auburn.eng.aci.genevot.*;
import java.util.Random;
import java.util.Arrays;

public class EDAOperators implements ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection {
	private class Statistics {
		public double mean;
		public double stdev;
		public Statistics() {
			mean = 0.0;
			stdev = 0.0;	
		}	
	}

	protected static final double MIN_SIGMA = 0.001;
	private int numElites;
	private Random random;
	
	public EDAOperators(int numElites) {
		this.numElites = numElites;
		random = new Random();
	}	

	private double mean(double[] array) {
		double average = 0.0;
		for(int i = 0; i < array.length; i++) {
			average += array[i];	
		}
		average /= (double)array.length;
		return average;	
	}
	
	private double stdev(double[] array, double average) {
		double sum = 0.0;
		for(int i = 0; i < array.length; i++) {
			sum += Math.pow((array[i] - average), 2);
		}
		double s = Math.sqrt(sum / ((double)array.length - 1.0));
		return s;
	}

	private double[] getGeneSample(Individual[] parent, int geneNum) {
		double[] gp = new double[parent.length];
		for(int i = 0; i < parent.length; i++) {
			if(parent[i].getChromosome().getType(geneNum) == Interval.Type.DOUBLE) {
				gp[i] = ((Double)parent[i].getChromosome().getGene(geneNum)).doubleValue();	
			}
			else if(parent[i].getChromosome().getType(geneNum) == Interval.Type.FLOAT) {
				gp[i] = (double)((Float)parent[i].getChromosome().getGene(geneNum)).floatValue();	
			}
			else {
				gp[i] = 0.0;
			}
		}
		return gp;
	}
	
	private Statistics[] getStatistics(Individual[] parent) {
		int numDimensions = parent[0].getChromosome().getSize();
		Statistics[] stats = new Statistics[numDimensions];
		for(int i = 0; i < numDimensions; i++) {
			stats[i] = new Statistics();
			double[] sample = getGeneSample(parent, i);
			stats[i].mean = mean(sample);
			stats[i].stdev = stdev(sample, stats[i].mean);	
		}	
		return stats;
	}

	public Individual[] selectParents(Population population, Individual[] individual) {
		Individual[] parent = new Individual[individual.length / 2];
		Arrays.sort(individual, 0, individual.length);
		for(int i = 0; i < parent.length; i++) {
			parent[i] = (Individual)individual[i].clone();	
		}
		return parent;
	}
	
	public Individual[] recombine(Population population, Individual[] parent) {
		return parent;
	}
	
    public double getMutationRate() { return 0.0; }
    public void setMutationRate(double rate) {}
    
	public Individual[] mutate(Population population, Individual[] individual) {
		Statistics[] stats = getStatistics(individual);
		Individual[] mutant = new Individual[population.getSize()];
		for(int i = 0; i < mutant.length; i++) {
			mutant[i] = new Individual(new Chromosome(individual[0].getChromosome().getBounds()));
			for(int j = 0; j < mutant[i].getChromosome().getSize(); j++) {
				if(mutant[i].getChromosome().getType(j) == Interval.Type.BOOLEAN || mutant[i].getChromosome().getType(j) == Interval.Type.INTEGER) {
					int randIndex = random.nextInt(individual.length);
					mutant[i].getChromosome().setGene(j, individual[randIndex].getChromosome().getGene(j));
				}
				else if(mutant[i].getChromosome().getType(j) == Interval.Type.FLOAT) {
					double geneValue = stats[j].mean + Math.max(stats[j].stdev, MIN_SIGMA) * random.nextGaussian();
					geneValue = Math.min(((Float)mutant[i].getChromosome().getBounds(j).getMax()).floatValue(), Math.max(geneValue, ((Float)mutant[i].getChromosome().getBounds(j).getMin()).floatValue()));					
					mutant[i].getChromosome().setGene(j, new Float(geneValue));	
				}	
				else {
					double geneValue = stats[j].mean + Math.max(stats[j].stdev, MIN_SIGMA) * random.nextGaussian();
					geneValue = Math.min(((Double)mutant[i].getChromosome().getBounds(j).getMax()).doubleValue(), Math.max(geneValue, ((Double)mutant[i].getChromosome().getBounds(j).getMin()).doubleValue()));
					mutant[i].getChromosome().setGene(j, new Double(geneValue));	
				}
			}	
		}
		return mutant;
	}
	
	public Individual[] selectSurvivors(Population population, Individual[] parents, Individual[] children) {
		Individual[] currentPopulation = new Individual[population.getSize()];
		for(int i = 0; i < currentPopulation.length; i++) {
			currentPopulation[i] = population.getIndividual(i);
		}		
		Arrays.sort(currentPopulation, 0, currentPopulation.length);
		for(int i = 0; i < numElites; i++) {
			children[i] = (Individual)currentPopulation[i].clone();	
		}
		return children;		
	}
	
}	
