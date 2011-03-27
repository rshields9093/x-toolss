
package lib.genevot;


/** This interface specifies an EC monitor. The job of the EC monitor is to collect and return information on the state of the EC. For instance, the EC monitor may keep up with the number of function evaluations to find the best solution.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public interface ECMonitor {
	/** This method must be implemented by all implementing classes. It takes the current population, set of parents, and set of children and returns the EC result.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param parents The current set of parents for this generation
	 * @param children The current set of children created for this generation
	 * @return The EC result that was determined from the current state of the 
	 *     population
	 */
	public ECResult getResults(Population population, Individual[] parents, Individual[] children);
	
	/** This method must be implemented by all implementing classes. It should initialize the state of the EC monitor. 
	 * 
	 * @since 1.0
	 */
	public void initialize();	
}
