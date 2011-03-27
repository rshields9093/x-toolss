
package lib.genevot;


import java.util.Random;


/** This class represents a standard EP. In this algorithm, each gene's mutation rate is proportional to the individual's fitness. At each generation, each individual in the population creates a child. (mu + lambda) tournament selection is used as the replacement strategy. For a detailed description of Evolutionary Programming, see Fogel, David B. and Chellapilla, K., "Revisiting Evolutionary Programming," AeroSense'98: Aerospace/Defense Sensing and Controls, Orlando, Apr. 1998.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class StandardEP implements ParentSelection, RecombinationOperator {
	
	/** This field holds the current population.
	 */
	protected Population population;
	/** This field holds the number of dimensions for an individual (i.e., the number of genes).
	 */
	protected int numDimensions;
	/** This field holds the EC evaluator for this algorithm.
	 */
	protected ECMonitor ecEvaluator;
	/** This field holds the termination criteria for this algorithm.
	 */
	protected TerminationCriteria terminationCriteria; 
	/** This field holds the random number generator for this class. It is initialized in the constructor.
	 */
	protected Random random;

	/** This constructor creates an instance of the standard EP. It takes a population size, the tournament size for the survivor selection, the minimum and maximum bounds for each gene, the evaluation function, the termination criteria, and the EC evaluator. No migration is used.
	 * 
	 * @since 1.0
	 * @param populationSize The size of the population
	 * @param min The array of minimum bounds for the genes
	 * @param max The array of maximum bounds for the genes
	 * @param ef The evaluation function
	 * @param tc The termination criteria
	 * @param ecEval The EC evaluator
	 */
	public StandardEP(int populationSize, double[] min, double[] max, double mutRate, double mutRange,
						EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecEval) {
		terminationCriteria = tc;
		ecEvaluator = ecEval;
		random = new Random();
		numDimensions = min.length;
		Interval[] bounds = new Interval[numDimensions];
		for(int i = 0; i < bounds.length; i++) {
			bounds[i] = new Interval(Interval.Type.DOUBLE, new Double(min[i]), new Double(max[i]));
		}
		
		MuPlusLambdaSelection mplSelection = new MuPlusLambdaSelection();
		GaussianMutationOperator mutOp = new GaussianMutationOperator(1.0, mutRate, mutRange);
		
		NoMigrationOperator noMig = new NoMigrationOperator();
		
		//		         Population(int           , Interval[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(populationSize, bounds, ef, this, this, mutOp, mplSelection, noMig); 
	}

	/** This constructor creates an instance of the standard EP. It takes a population size, the tournament size for the survivor selection, the minimum and maximum bounds for each gene, the evaluation function, the termination criteria, the EC evaluator, and the migration operator.
	 * 
	 * @since 1.0
	 * @param populationSize The size of the population
	 * @param min The array of minimum bounds for the genes
	 * @param max The array of maximum bounds for the genes
	 * @param ef The evaluation function
	 * @param tc The termination criteria
	 * @param ecEval The EC evaluator
	 * @param migOp The migration operator
	 */
	public StandardEP(int populationSize, double[] min, double[] max, double mutRate, double mutRange,
	EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecEval, MigrationOperator migOp) {
		terminationCriteria = tc;
		ecEvaluator = ecEval;
		random = new Random();
		numDimensions = min.length;
		Interval[] bounds = new Interval[numDimensions];
		for(int i = 0; i < bounds.length; i++) {
			bounds[i] = new Interval(Interval.Type.DOUBLE, new Double(min[i]), new Double(max[i]));
		}
		
		MuPlusLambdaSelection mplSelection = new MuPlusLambdaSelection();
		GaussianMutationOperator mutOp = new GaussianMutationOperator(1.0, mutRate, mutRange);
		
		//		         Population(int           , Interval[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(populationSize, bounds, ef, this, this, mutOp, mplSelection, migOp); 
	}

	/** This method implements the ParentSelection interface. It returns the entire population.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param individual The set of individuals from which parents should be chosen
	 * @return An array of individuals representing the parents
	 */
	public Individual[] selectParents(Population population, Individual[] individual) {
		return individual;
	}
	
	/** This method implements the RecombinationOperator interface. It simply returns the entire array of individuals.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param parent The current set of parents
	 * @return An array of individuals representing the children
	 */
	public Individual[] recombine(Population population, Individual[] parent) {
		return parent;
	}
	
	/** This method simply calls the <code>evolve</code> method of the population using the specified termination criteria and EC evaluator.
	 * 
	 * @since 1.0
	 * @return The EC result that is determined by the EC evaluator
	 */
	public ECResult evolve() {
		return population.evolve(terminationCriteria, ecEvaluator);
	}
	
	/** This method resets the population to a random initialization.
	 * 
	 * @since 1.0
	 */
	public void reset() {
		population.initialize();
	}
}
