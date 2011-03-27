
package lib.genevot;


import java.util.Arrays;


/** This class represents (mu + lambda) survivor selection.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class MuPlusLambdaSelection implements SurvivorSelection {
	
	/** The constructor simply creates an instance of this class.
	 */
	public MuPlusLambdaSelection() {}
	
	/** This method implements the SurvivorSelection interface. It groups all current individuals and all children into a single array and returns the best mu individuals from this array. Here, mu is determined by the size of the current population array and lambda is determined by the size of the children array.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param parents The current array of parents
	 * @param children The current array of children
	 * @return An array of individuals representing the survivors
	 */
	public Individual[] selectSurvivors(Population population, Individual[] parents, Individual[] children) {
		Individual[] fullPopulation = new Individual[population.getSize() + children.length];
		int count = 0;
		for(int i = 0; i < population.getSize(); i++) {
			fullPopulation[count] = (Individual)population.getIndividual(i).clone();
			count++;	
		}
		for(int i = 0; i < children.length; i++) {
			fullPopulation[count] = (Individual)children[i].clone();
			count++;	
		}
		Arrays.sort(fullPopulation, 0, fullPopulation.length);
		Individual[] survivor = new Individual[population.getSize()];
		for(int i = 0; i < survivor.length; i++) {
			survivor[i] = fullPopulation[i];
		}
		return survivor;
	}		
}



