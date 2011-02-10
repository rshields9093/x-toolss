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

import edu.auburn.eng.aci.genevot.*;
import java.util.Random;


public class EPOperators implements ParentSelection, RecombinationOperator, MutationOperator {
	private boolean continuous;
	private boolean meta;
	private double eta;
	private Random random;
	
	public EPOperators(boolean isContinuous, boolean isMeta, double eta) {
		continuous = isContinuous;
		meta = isMeta;
		this.eta = eta;
		random = new Random();
	}
	
	public Individual[] selectParents(Population population, Individual[] individual) {
		return individual;
	}
	
	public Individual[] recombine(Population population, Individual[] parent) {
		if(continuous) {
			Individual[] child = new Individual[1];
			child[0] = parent[random.nextInt(parent.length)];
			return child;		
		}
		else {
			return parent;
		}
	}
	
    public double getMutationRate() { return 0.0; }
    public void setMutationRate(double rate) {}

	public Individual[] mutate(Population population, Individual[] individual) {
		int numDimensions = individual[0].getChromosome().getSize();
		if(meta) {
			numDimensions /= 2;
			Individual[] mutant = new Individual[individual.length];
			for(int i = 0; i < individual.length; i++) {
				mutant[i] = new Individual(new Chromosome(individual[i].getChromosome().getBounds()));
				for(int j = 0; j < numDimensions; j++) {
					if(mutant[i].getChromosome().getType(j) == Interval.Type.DOUBLE) {
						double geneValue = ((Double)individual[i].getChromosome().getGene(j)).doubleValue();
						double sigma = ((Double)individual[i].getChromosome().getGene(numDimensions + j)).doubleValue();
						double min = ((Double)mutant[i].getChromosome().getBounds(j).getMin()).doubleValue();
						double max = ((Double)mutant[i].getChromosome().getBounds(j).getMax()).doubleValue();
						geneValue = geneValue + sigma * (max - min) * random.nextGaussian();
						geneValue = Math.max(min, Math.min(max, geneValue));
						mutant[i].getChromosome().setGene(j, new Double(geneValue));
						
						geneValue = ((Double)individual[i].getChromosome().getGene(numDimensions + j)).doubleValue();
						geneValue = geneValue + geneValue * eta * random.nextGaussian();
						min = ((Double)mutant[i].getChromosome().getBounds(numDimensions + j).getMin()).doubleValue();
						max = ((Double)mutant[i].getChromosome().getBounds(numDimensions + j).getMax()).doubleValue();
						geneValue = Math.max(min, Math.min(max, geneValue));
						mutant[i].getChromosome().setGene(numDimensions + j, new Double(geneValue));
					}
					else if(mutant[i].getChromosome().getType(j) == Interval.Type.FLOAT) {
						double geneValue = ((Float)individual[i].getChromosome().getGene(j)).floatValue();
						float sigma = ((Float)individual[i].getChromosome().getGene(numDimensions + j)).floatValue();
						float min = ((Float)mutant[i].getChromosome().getBounds(j).getMin()).floatValue();
						float max = ((Float)mutant[i].getChromosome().getBounds(j).getMax()).floatValue();
						geneValue = geneValue + sigma * (max - min) * random.nextGaussian();
						geneValue = Math.max(min, Math.min(max, geneValue));
						mutant[i].getChromosome().setGene(j, new Float(geneValue));
						
						geneValue = ((Float)individual[i].getChromosome().getGene(numDimensions + j)).floatValue();
						geneValue = geneValue + geneValue * eta * random.nextGaussian();
						min = ((Float)mutant[i].getChromosome().getBounds(numDimensions + j).getMin()).floatValue();
						max = ((Float)mutant[i].getChromosome().getBounds(numDimensions + j).getMax()).floatValue();
						geneValue = Math.max(min, Math.min(max, geneValue));
						mutant[i].getChromosome().setGene(numDimensions + j, new Float(geneValue));
					}
					else {
						mutant[i].getChromosome().setGene(j, individual[i].getChromosome().getGene(j));
						mutant[i].getChromosome().setGene(numDimensions + j, individual[i].getChromosome().getGene(numDimensions + j));						
					}
				} 
			}
			return mutant;
		}
		else {
			return individual;
		}
	}
}	
