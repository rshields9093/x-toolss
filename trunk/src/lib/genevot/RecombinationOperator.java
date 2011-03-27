
package lib.genevot;


/** This interface provides the specification for a recombination operator. The operator should take an array of individuals representing the parents and return an array of individuals representing the children.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public interface RecombinationOperator {
	/** This method must be implemented by all classes implementing this interface. It should recombine the array of parents and return the array of newly created children. It is advised that these children be based on cloned (i.e., deep) copies of the parents.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param parent The current set of parents
	 * @return An array of individuals representing the children
	 */
	public Individual[] recombine(Population population, Individual[] parent);
}
