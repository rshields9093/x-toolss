
package lib.genevot;


/** This interface provides the specification for survivor selection. The operator should take an array of individuals from which the survivors should be chosen.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public interface SurvivorSelection {
	/** This method must be implemented by all classes implementing this interface. It should select the survivors from the array of individuals and return the array of these survivors. It is advised that these selected individuals be based on cloned (i.e., deep) copies of the individuals being passed in.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param parents The current set of parents that were used during this 
	 *     generation
	 * @param children The current set of children that were created during 
	 *     this generation
	 * @return An array of individuals representing the survivors
	 */
	Individual[] selectSurvivors(Population population, Individual[] parents, Individual[] children);
}
