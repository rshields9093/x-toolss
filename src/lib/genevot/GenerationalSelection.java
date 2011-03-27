
package lib.genevot;


import java.util.Arrays;


/** This class implements the SurvivorSelection interface. It selects the entire set of children to survive to the next generation. If elitism is desired, it will also allow the best N individuals to survive.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class GenerationalSelection implements SurvivorSelection {
	/** This field represents the number of elite individuals that should be allowed to survive into the next generation.
	 */
	private int numElites;
	
	/** This constructor creates a generational selection with no elitism.
	 * 
	 * @since 1.0
	 */
	public GenerationalSelection() {
		this(0);	
	}
	
	/** This constructor creates a generational selection with the specified number of elite individuals.
	 * 
	 * @since 1.0
	 * @param numElites The number of elite individuals that should survive
	 */
	public GenerationalSelection(int numElites) {
		this.numElites = numElites;	
	}
	
	/** This method implements the selectSurvivors() method of the SurvivorSelection interface. It performs generational selection with the specified number of elite individuals (if any).
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param parents The array of parents
	 * @param children The array of children
	 * @return An array of surviving individuals
	 */
	public Individual[] selectSurvivors(Population population, Individual[] parents, Individual[] children) {
		Individual[] currentPopulation = new Individual[population.getSize()];
		for(int i = 0; i < currentPopulation.length; i++) {
			currentPopulation[i] = population.getIndividual(i);
		}
		Individual[] survivors = new Individual[population.getSize()];
		for(int i = 0; i < children.length; i++) {
			survivors[i + numElites] = children[i];
		}
		Arrays.sort(currentPopulation, 0, currentPopulation.length);
		for(int i = 0; i < numElites; i++) {
			survivors[i] = (Individual)currentPopulation[i].clone();
		}
		return survivors;
	} 
}


