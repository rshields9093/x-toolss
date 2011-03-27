
package lib.genevot;


/** This interface provides the specification for a mutation operator. The operator should take an array of individuals to be mutated and return an array of mutated individuals.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public interface MutationOperator {
	/** This method must be implemented by all classes implementing this interface. It should mutate the array of individuals and return the array of newly mutated individuals.
	 *  It is advised that these individuals be based on cloned (i.e., deep) copies of the individuals being passed in.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param individual The set of individuals to be mutated
	 * @return An array of mutated individuals
	 */
	public Individual[] mutate(Population population, Individual[] individual);
	public void setMutationRate(double rate);
	public double getMutationRate();
}
