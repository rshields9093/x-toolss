package lib.genevot;


/** This class represents a binary-coded steady-state GA. This GA uses binary tournament selection, steady-state replacement, uniform crossover, and bit-flip mutation. For a detailed treatment of GAs, see G. Dozier, A. Homaifar, E. Tunstel,  and D. Battle, "An Introduction  to Evolutionary Computation" (Chapter 17), Intelligent Control Systems Using Soft Computing Methodologies, A. Zilouchian & M. Jamshidi (Eds.), pp. 365-380, CRC press.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class BinarySteadyStateGA {

	/** This field holds the termination criteria for the GA.
	 */
	protected TerminationCriteria terminationCriteria; 	
	/** This field holds the EC evaluator for the GA.
	 */
	protected ECMonitor ecEvaluator; 
	/** This field holds the current population.
	 */
	protected Population population;
	/** This field holds the number of dimensions (i.e., number of genes, which also implicitly determines the resolution) of an individual.
	 */
	protected int numDimensions;

	/** This constructor creates an instance of the GA based on the population size, number of genes, number of elite individuals, crossover usage rate, mutation usage rate, mutation rate, evaluation function, termination criteria, and EC evaluator. No migration is used.
	 * 
	 * @since 1.0
	 * @param populationSize The size of the population
	 * @param numGenes The dimensionality of an individual in the population
	 * @param crossoverUsageRate The crossover usage rate
	 * @param mutationUsageRate The mutation usage rate
	 * @param mutationRate The mutation rate
	 * @param ef The evaluation function that this GA will optimize
	 * @param tc The termination criteria for the GA
	 * @param ecEval The EC evaluator
	 */
	public BinarySteadyStateGA(int populationSize, int numGenes, double crossoverUsageRate, double mutationUsageRate, double mutationRate, EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecEval) {
		terminationCriteria = tc;
		ecEvaluator = ecEval;
		numDimensions = numGenes;
		Interval[] bounds = new Interval[numDimensions];
		for(int i = 0; i < bounds.length; i++) {
			bounds[i] = new Interval(Interval.Type.BOOLEAN, new Boolean(false), new Boolean(true));	
		}
		TournamentSelection tournSelection = new TournamentSelection(2, 2);
		SteadyStateSelection ssSelection = new SteadyStateSelection();
		UniformCrossoverOperator uniRecombination = new UniformCrossoverOperator(crossoverUsageRate);
		BitFlipMutationOperator bfMutOp = new BitFlipMutationOperator(mutationUsageRate, mutationRate);
		NoMigrationOperator noMig = new NoMigrationOperator();
		
		//		         Population(int           , Interval[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(populationSize, bounds, ef, tournSelection, uniRecombination, bfMutOp, ssSelection, noMig);
	}

	/** This constructor creates an instance of the GA based on the population size, number of genes, number of elite individuals, crossover usage rate, mutation usage rate, mutation rate, evaluation function, termination criteria, EC evaluator, and migration operator.
	 * 
	 * @since 1.0
	 * @param populationSize The size of the population
	 * @param numGenes The dimensionality of an individual in the population
	 * @param crossoverUsageRate The crossover usage rate
	 * @param mutationUsageRate The mutation usage rate
	 * @param mutationRate The mutation rate
	 * @param ef The evaluation function that this GA will optimize
	 * @param tc The termination criteria for the GA
	 * @param ecEval The EC evaluator
	 * @param migOp The migration operator
	 */
	public BinarySteadyStateGA(int populationSize, int numGenes, double crossoverUsageRate, double mutationUsageRate, double mutationRate, EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecEval, MigrationOperator migOp) {
		terminationCriteria = tc;
		ecEvaluator = ecEval;
		numDimensions = numGenes;
		Interval[] bounds = new Interval[numDimensions];
		for(int i = 0; i < bounds.length; i++) {
			bounds[i] = new Interval(Interval.Type.BOOLEAN, new Boolean(false), new Boolean(true));	
		}
		TournamentSelection tournSelection = new TournamentSelection(2, 2);
		SteadyStateSelection ssSelection = new SteadyStateSelection();
		UniformCrossoverOperator uniRecombination = new UniformCrossoverOperator(crossoverUsageRate);
		BitFlipMutationOperator bfMutOp = new BitFlipMutationOperator(mutationUsageRate, mutationRate);
		
		//		         Population(int           , Interval[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(populationSize, bounds, ef, tournSelection, uniRecombination, bfMutOp, ssSelection, migOp);
	}

	/** This method just calls the <code>evolve</code> method of the population with the specified termination criteria and EC evaluator.
	 * 
	 * @since 1.0
	 * @return The EC result as determined by the EC evaluator
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
