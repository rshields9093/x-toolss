
package lib.genevot;


import java.util.Arrays;


/** This class implements the SurvivorSelection interface to provide steady-state selection. This means that the worst individuals in the current population are replaced by the children produced during that generation.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class SteadyStateSelection implements SurvivorSelection {

	/** This constructor creates an instance of the class.
	 * 
	 * @since 1.0
	 */
	public SteadyStateSelection() {}
	
	/** This method implements the SurvivorSelection interface to provide steady-state selection. This means that the worst individuals in the current population are replaced by the children produced during that generation.
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
		int index = nextPopulation.length - 1;
		for(int i = 0; i < children.length; i++) {
			nextPopulation[index] = (Individual)children[i].clone();
			index--;
		}
		
		return nextPopulation;
	} 
}

