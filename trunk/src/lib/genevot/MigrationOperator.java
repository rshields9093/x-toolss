
package lib.genevot;


/** This interface allows the definition of a migration operator, which would allow individuals from a population to migrate to other populations.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public interface MigrationOperator {
	/** This method should take the current population and allow migration to other populations.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param individual The current set of individuals in the population
	 * @return An array of individuals representing the population after 
	 *     migration
	 */
	public Individual[] migrate(Population population, Individual[] individual);
}
