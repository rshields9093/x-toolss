
package lib.genevot;


import java.util.Arrays;


/** This class implements the ParentSelection interface. It selects the best N individuals from the population as parents.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class SelectBestIndividuals implements ParentSelection {
	/** This field represents the number of parents that should be selected.
	 */
	private int numParents;
	
	/** The constructor takes the number of parents that should be selected by this operator.
	 * 
	 * @since 1.0
	 * @param np The number of parents to be selected
	 */
	public SelectBestIndividuals(int np) {
		numParents = np;
	}
	
	/** This method selects the best <code>numParents</code> individuals from the population and returns them.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param individual The current set of individuals from which parents should be chosen
	 * @return An array of individuals representing the best 
	 *     <code>numParents</code> individuals
	 */
	public Individual[] selectParents(Population population, Individual[] individual) {
		Individual[] parent = new Individual[numParents];
		Arrays.sort(individual, 0, individual.length);
		for(int i = 0; i < parent.length; i++) {
			parent[i] = (Individual)individual[i].clone();
		}
		return parent;		
	}
}
