
package lib.genevot;


/** This interface provides the specification for the termination criteria of an EC. This criteria can be anything the implementer desires, such as some number of function evaluations being reached, some threshold for the best fitness, etc.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public interface TerminationCriteria {
	/** This method takes the current state of the population and returns a boolean value that determines whether the EC should terminate or not.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @return A boolean value that is TRUE if the EC should terminate and FALSE 
	 *     otherwise
	 */
	public boolean terminate(Population population); 
}
