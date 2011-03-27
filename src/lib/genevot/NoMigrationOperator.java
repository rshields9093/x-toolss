
package lib.genevot;


/** This class implements the migration operator to produce a "null" migration, where no individuals migrate.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class NoMigrationOperator implements MigrationOperator {

	/** This constructor creates a new instance of this class. It does nothing.
	 * 
	 * @since 1.0
	 */
	public NoMigrationOperator() {}

	/** This method implements the MigrationOperator interface. It simply returns the current population since we are not using any migration.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param individual The current set of individuals in the population
	 * @return An array of individuals representing the population after 
	 *     migration
	 */
	public Individual[] migrate(Population population, Individual[] individual) {
		return individual;
	}
}
