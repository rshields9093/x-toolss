
package lib.genevot;


import java.util.Random;


/** This class implements the ParentSelection interface. It allows parents to be selected via tournament selection, where each parent is the winner of a tournament among possible parents.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class TournamentSelection implements ParentSelection {
	/** This field holds the size of the tournament.
	 */
	private int tournamentSize;
	/** This field holds the number of parents that should be produced.
	 */
	private int numParents;
	/** This field holds the random number generation for this class. It is initialized at compile time.
	 */
	private static final Random random = new Random();
	
	/** The constructor takes the tournament size and the number of parents to be produced by this selection scheme.
	 * 
	 * @since 1.0
	 * @param tsize The tournament size
	 * @param numParents The number of parents to be produced
	 */
	public TournamentSelection(int tsize, int numParents) {
		tournamentSize = tsize;
		this.numParents = numParents;
	}
	
	/** This method randomly selects the participants for a tournament.
	 * 
	 * @since 1.0
	 * @param population The array of possible participants
	 * @return An array of individuals that will participate in a tournament
	 */
	private Individual[] selectParticipants(Individual[] population) {
		Individual[] participant = new Individual[tournamentSize];
		for(int i = 0; i < participant.length; i++) {
			participant[i] = population[random.nextInt(population.length)];
		}
		return participant;
	}
	
	/** This method runs the tournament on the set of competitors and returns the best among them.
	 * 
	 * @since 1.0
	 * @param competitor The set of individuals to participate
	 * @return The winning individual of the tournament
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
	
	/** This method implements the ParentSelection interface. It runs <code>numParents</code> tournaments and returns the array of winners.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param individual The current set of inidviduals from which the parents should be chosen
	 * @return An array of parents
	 */
	public Individual[] selectParents(Population population, Individual[] individual) {
		Individual[] parent = new Individual[numParents];
		for(int i = 0; i < parent.length; i++) {
			parent[i] = (Individual)(runTournament(selectParticipants(individual))).clone();
		}
		return parent;
	}		
}


