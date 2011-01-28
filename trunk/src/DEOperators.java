
//package edu.auburn.eng.aci.xtoolss;

import edu.auburn.eng.aci.genevot.*;

public class DEOperators implements RecombinationOperator {
	private boolean generational;
	private double phi;

	public DEOperators(boolean isGenerational, double phi) {
		generational = isGenerational;
		this.phi = phi;
	}

	public Individual[] recombine(Population population, Individual[] parent) {
		int numDimensions = parent[0].getChromosome().getSize();
		if(generational) {
			Individual[] child = new Individual[parent.length / 2];
			for(int i = 0; i < parent.length/2; i++) {
				child[i] = new Individual(new Chromosome(parent[2 * i].getChromosome().getBounds()));
				if(parent[2 * i].compareTo(parent[2 * i + 1]) < 0) {
					for(int j = 0; j < numDimensions; j++) {
						if(parent[2*i].getChromosome().getType(j) == Interval.Type.DOUBLE) {
							double a = ((Double)parent[2 * i].getChromosome().getGene(j)).doubleValue();
							double b = ((Double)parent[2 * i + 1].getChromosome().getGene(j)).doubleValue();;
							double value = b + phi * Math.random() * (a - b);
							value = Math.max(((Double)child[i].getChromosome().getBounds(j).getMin()).doubleValue(), Math.min(((Double)child[i].getChromosome().getBounds(j).getMax()).doubleValue(), value));
							child[i].getChromosome().setGene(j, new Double(value));
						}
						else if(parent[2*i].getChromosome().getType(j) == Interval.Type.FLOAT) {
							float a = ((Float)parent[2 * i].getChromosome().getGene(j)).floatValue();
							float b = ((Float)parent[2 * i + 1].getChromosome().getGene(j)).floatValue();;
							double value = b + phi * Math.random() * (a - b);
							value = Math.max(((Float)child[i].getChromosome().getBounds(j).getMin()).floatValue(), Math.min(((Float)child[i].getChromosome().getBounds(j).getMax()).floatValue(), value));
							child[i].getChromosome().setGene(j, new Float(value));						
						}
						else {
							if(Math.random() > 0.5) {
								child[i].getChromosome().setGene(j, parent[2*i].getChromosome().getGene(j));
							}
							else {
								child[i].getChromosome().setGene(j, parent[2*i+1].getChromosome().getGene(j));							
                            }
						}
					}
				}
				else {
					for(int j = 0; j < numDimensions; j++) {
						if(parent[2*i].getChromosome().getType(j) == Interval.Type.DOUBLE) {
							double a = ((Double)parent[2 * i + 1].getChromosome().getGene(j)).doubleValue();
							double b = ((Double)parent[2 * i].getChromosome().getGene(j)).doubleValue();;
							double value = b + phi * Math.random() * (a - b);
							value = Math.max(((Double)child[i].getChromosome().getBounds(j).getMin()).doubleValue(), Math.min(((Double)child[i].getChromosome().getBounds(j).getMax()).doubleValue(), value));
							child[i].getChromosome().setGene(j, new Double(value));
						}
						else if(parent[2*i].getChromosome().getType(j) == Interval.Type.FLOAT) {
							float a = ((Float)parent[2 * i + 1].getChromosome().getGene(j)).floatValue();
							float b = ((Float)parent[2 * i].getChromosome().getGene(j)).floatValue();;
							double value = b + phi * Math.random() * (a - b);
							value = Math.max(((Float)child[i].getChromosome().getBounds(j).getMin()).floatValue(), Math.min(((Float)child[i].getChromosome().getBounds(j).getMax()).floatValue(), value));
							child[i].getChromosome().setGene(j, new Float(value));						
						}
						else {
							if(Math.random() > 0.5) {
								child[i].getChromosome().setGene(j, parent[2*i].getChromosome().getGene(j));
							}
							else {
								child[i].getChromosome().setGene(j, parent[2*i+1].getChromosome().getGene(j));							
							}
						}
					}			
				}
			}
			return child;
		}
		else {
			Individual[] child = new Individual[1];
			child[0] = new Individual(new Chromosome(parent[0].getChromosome().getBounds()));
			if(parent[0].compareTo(parent[1]) < 0) {
				for(int i = 0; i < numDimensions; i++) {
					if(parent[0].getChromosome().getType(i) == Interval.Type.DOUBLE) {
						double a = ((Double)parent[0].getChromosome().getGene(i)).doubleValue();
						double b = ((Double)parent[1].getChromosome().getGene(i)).doubleValue();;
						double value = b + phi * Math.random() * (a - b);
						value = Math.max(((Double)child[0].getChromosome().getBounds(i).getMin()).doubleValue(), Math.min(((Double)child[0].getChromosome().getBounds(i).getMax()).doubleValue(), value));
						child[0].getChromosome().setGene(i, new Double(value));
					}
					else if(parent[0].getChromosome().getType(i) == Interval.Type.FLOAT) {
						float a = ((Float)parent[0].getChromosome().getGene(i)).floatValue();
						float b = ((Float)parent[1].getChromosome().getGene(i)).floatValue();;
						double value = b + phi * Math.random() * (a - b);
						value = Math.max(((Float)child[0].getChromosome().getBounds(i).getMin()).floatValue(), Math.min(((Float)child[0].getChromosome().getBounds(i).getMax()).floatValue(), value));
						child[0].getChromosome().setGene(i, new Float(value));						
					}
					else {
						if(Math.random() > 0.5) {
							child[0].getChromosome().setGene(i, parent[0].getChromosome().getGene(i));
						}
						else {
							child[i].getChromosome().setGene(i, parent[1].getChromosome().getGene(i));							
						}
					}
				}
			}
			else {
				for(int i = 0; i < numDimensions; i++) {
					if(parent[0].getChromosome().getType(i) == Interval.Type.DOUBLE) {
						double a = ((Double)parent[1].getChromosome().getGene(i)).doubleValue();
						double b = ((Double)parent[0].getChromosome().getGene(i)).doubleValue();;
						double value = b + phi * Math.random() * (a - b);
						value = Math.max(((Double)child[0].getChromosome().getBounds(i).getMin()).doubleValue(), Math.min(((Double)child[0].getChromosome().getBounds(i).getMax()).doubleValue(), value));
						child[0].getChromosome().setGene(i, new Double(value));
					}
					else if(parent[0].getChromosome().getType(i) == Interval.Type.FLOAT) {
						float a = ((Float)parent[1].getChromosome().getGene(i)).floatValue();
						float b = ((Float)parent[0].getChromosome().getGene(i)).floatValue();;
						double value = b + phi * Math.random() * (a - b);
						value = Math.max(((Float)child[0].getChromosome().getBounds(i).getMin()).floatValue(), Math.min(((Float)child[0].getChromosome().getBounds(i).getMax()).floatValue(), value));
						child[0].getChromosome().setGene(i, new Float(value));						
					}
					else {
						if(Math.random() > 0.5) {
							child[0].getChromosome().setGene(i, parent[0].getChromosome().getGene(i));
						}
						else {
							child[i].getChromosome().setGene(i, parent[1].getChromosome().getGene(i));							
						}
					}
				}			
			}
			return child;
		}
	}
}