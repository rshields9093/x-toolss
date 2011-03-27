
package lib.genevot;

import java.util.Vector;

/** This class represents an individual in a population. The individual is composed of a chromosome and a fitness.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class Individual implements Cloneable, Comparable {
	/** This field holds the chromosome representation of the individual.
	 */
	protected Chromosome chromosome;
	/** This field holds the fitness of the individual.
	 */
	protected Vector<Double> fitness;
	
	/** The constructor takes the chromosome for this individual, creates a cloned copy of it for this instance, and sets the fitness to 0.0.
	 * 
	 * @since 1.0
	 * @param c The chromosome for this individual
	 */
	public Individual(Chromosome c) {
		chromosome = (Chromosome)c.clone();
		fitness = new Vector<Double>();
	}
	
	/** This method creates and returns a deep copy of the individual.
	 * 
	 * @since 1.0
	 * @return A deep copy of the individual
	 */
	public Object clone() {
		try {
			Individual ind = (Individual)super.clone();
			ind.chromosome = (Chromosome)chromosome.clone();
			ind.fitness = (Vector<Double>)fitness.clone();
			return ind;
		}
		catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}
	
	/** This method returns the chromosome for this individual.
	 * 
	 * @since 1.0
	 * @return The chromosome for the individual
	 */
	public Chromosome getChromosome() {
		return chromosome;
	}
	
	/** This method sets the chromosome for the individual to the specified parameter.
	 * 
	 * @since 1.0
	 * @param c The new chromosome
	 */
	public void setChromosome(Chromosome c) {
		chromosome = (Chromosome)c.clone();
	}
	
	/** This method returns the fitness for the individual.
	 * 
	 * @since 1.0
	 * @return The individual's fitness
	 */
	public double getFitness() {
		return getFitness(0);
	}

	public double getFitness(int index) {
		if(index >= 0 && index < fitness.size()) {
			return fitness.elementAt(index);
		}	
		else {
			return Double.POSITIVE_INFINITY;
		}
	}
	
	public int getNumFitnessValues() {
		return fitness.size();
	}
	
	/** This method sets the fitness for the individual to the specified parameter.
	 * 
	 * @since 1.0
	 * @param fit The new fitness value
	 */
	 
	public void setFitness(double[] fit) {
		fitness.clear();
		for(int i = 0; i < fit.length; i++) {
			fitness.add(fit[i]);
		}	
	}
	
	public void setFitness(double fit) {
		setFitness(0, fit);
	}

	public void setFitness(int index, double fit) {
		if(fitness.size() > index) {
			fitness.setElementAt(fit, index);
		}
		else {
			fitness.addElement(fit);
		}
	}
	
	public void addFitness(double fit) {
		fitness.addElement(fit);
	}
	
	public String getPrintableFitness() {
		String s = "";
		for(int i = 0; i < fitness.size(); i++) {
			s += fitness.elementAt(i) + " ";
		}
		return s;
	}
	
	/** This method compares the individual to the individual (in Object form) passed in. If the individual's fitness is less than the object's fitness, it returns a negative number. If the individual's fitness is greater than the object's fitness, it returns a negative number. If the fitness values are equal, it returns a zero.
	 * 
	 * @since 1.0
	 * @param o The individual to compare
	 * @return An integer value of -1 for "less than", 1 for "greater than", and 
	 *      0 for "equal to"
	 */
	public int compareTo(Object o) {
		Individual ind = (Individual)o;
		int min = Math.min(ind.fitness.size(), fitness.size());
		for(int i = 0; i < min; i++) {
			if(fitness.elementAt(i) < ind.fitness.elementAt(i)) {
				return -1;	
			}	
			else if(fitness.elementAt(i) > ind.fitness.elementAt(i)) {
				return 1;
			}
		}	
		return 0;
	}
}


