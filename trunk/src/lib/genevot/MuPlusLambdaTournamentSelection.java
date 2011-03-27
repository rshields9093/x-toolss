
package lib.genevot;


import java.util.Random;


/** This class represents (mu + lambda) survivor selection where the "best" mu are determined by tournament selection.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class MuPlusLambdaTournamentSelection implements SurvivorSelection {
	/** This field represents the tournament size used in the tournament selection.
	 */
	private int tournamentSize;
	/** This field holds the random number generator for this class. It is initialized at compile time.
	 */
	private static final Random random = new Random();
	
	/** The constructor takes the size of the tournament used in this (mu + lambda) selection.
	 * 
	 * @since 1.0
	 * @param tournSize The tournament size
	 */
	public MuPlusLambdaTournamentSelection(int tournSize) {
		tournamentSize = tournSize;
	}
	
	/** This method randomly selects the participants for each tournament from the given population.
	 * 
	 * @since 1.0
	 * @param population The population of possible tournament participants
	 * @return An array of individuals representing the participants in a 
	 *     tournament
	 */
	private Individual[] selectParticipants(Individual[] population) {
		Individual[] participant = new Individual[tournamentSize];
		for(int i = 0; i < participant.length; i++) {
			participant[i] = population[random.nextInt(population.length)];
		}
		return participant;
	}
	
	/** This method performs the tournament selection of the competitors.
	 * 
	 * @since 1.0
	 * @param competitor The array of individuals representing the competitors in
	 *      the tournament
	 * @return The individual that wins the tournament
	 */
	private Individual runTournament(Individual[] competitor) {
		Individual best = competitor[0];
		for(int i = 1; i < competitor.length; i++) {
			if(competitor[i].compareTo(best) < 0) {
				best = competitor[i];
			}
		}
		return best;
	}
	
	/** This method implements the SurvivorSelection interface. It groups all current individuals and all children into a single array and returns the best mu individuals from this array, as determined by the tournament selection. Here, mu is determined by the size of the current population array and lambda is determined by the size of the children array.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param parents The current array of parents
	 * @param children The current array of children
	 * @return An array of individuals representing the survivors
	 */
	// Rank them and take the top half instead of tournament selection.
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
		Individual[] survivor = new Individual[population.getSize()];
		for(int i = 0; i < survivor.length; i++) {
			survivor[i] = runTournament(selectParticipants(fullPopulation));
		}
		return survivor;
	}		
}



