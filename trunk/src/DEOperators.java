
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

import lib.genevot.*;

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