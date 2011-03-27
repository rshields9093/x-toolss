
package lib.genevot;


/** This interface provides the specification for parent selection in an EC. The parents are selected from the current population and are returned.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public interface ParentSelection {
	/** This method must be implemented by all classes implementing this interface. It takes a population and returns the set of individuals from that population that will be parents. It is advised that these individuals be cloned (i.e., deep) copies of the individuals being passed in.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param individual The current set of individuals in the population from which parents should be chosen
	 * @return An array of individuals representing the parents
	 */
	public Individual[] selectParents(Population population, Individual[] individual);
}
