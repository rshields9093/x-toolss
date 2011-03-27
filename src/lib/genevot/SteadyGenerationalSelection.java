
package lib.genevot;


import java.util.Arrays;
import java.util.Random;


/** This class implements the SurvivorSelection interface to provide steady-generational selection. This means that random individuals are chosen (with elitist consideration) to be replaced by the children produced during that generation.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class SteadyGenerationalSelection implements SurvivorSelection {

	/** This field represents the number of elite individuals that should be allowed to survive into the next generation.
	 */
	private int numElites;

	/** This field represents the random number generator used in this class. It is initialized in the constructor.
	 */
	private Random random;
	
	/** This constructor creates a steady-generational selection with no elitism.
	 * 
	 * @since 1.0
	 */
	public SteadyGenerationalSelection() {
		this(0);
	}
	
	/** This constructor creates a steady-generational selection with the specified number of elite individuals.
	 * 
	 * @since 1.0
	 * @param numElites The number of elite individuals that should survive
	 */
	public SteadyGenerationalSelection(int numElites) {
		random = new Random();
		this.numElites = numElites;	
	}
	
	/** This method implements the SurvivorSelection interface to provide steady-generational selection. This means that random individuals are chosen (with elitist consideration) to be replaced by the children produced during that generation.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param parents The current set of parents
	 * @param children The current set of children
	 * @return An array of individuals representing the survivors of the current 
	 *     generation
	 */
	public Individual[] selectSurvivors(Population population, Individual[] parents, Individual[] children) {
		Individual[] nextPopulation = new Individual[population.getSize()];
		for(int i = 0; i < nextPopulation.length; i++) {
			nextPopulation[i] = (Individual)population.getIndividual(i).clone();
		}
		Arrays.sort(nextPopulation, 0, nextPopulation.length);
		for(int i = 0; i < children.length; i++) {
			int index = numElites + random.nextInt(nextPopulation.length - numElites);
			nextPopulation[index] = (Individual)children[i].clone();
		}
		
		return nextPopulation;
	} 
}

