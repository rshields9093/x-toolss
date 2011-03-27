
package lib.genevot;


/** This class represents a real-coded steady-state GA. This GA uses binary tournament selection, steady-state replacement, BLX-alpha crossover, and Gaussian mutation. For a detailed treatment of GAs, see G. Dozier, A. Homaifar, E. Tunstel,  and D. Battle, "An Introduction  to Evolutionary Computation" (Chapter 17), Intelligent Control Systems Using Soft Computing Methodologies, A. Zilouchian & M. Jamshidi (Eds.), pp. 365-380, CRC press.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class RealSteadyStateGA {
	
	/** This field holds the current population.
	 */
	protected Population population;
	/** This field holds the number of dimensions (i.e., number of genes) of an individual.
	 */
	protected int numDimensions;
	/** This field holds the EC evaluator for the GA.
	 */
	protected ECMonitor ecEvaluator;
	/** This field holds the termination criteria for the GA.
	 */
	protected TerminationCriteria terminationCriteria; 

	/** This constructor creates an instance of the GA based on the population size, the array of minimum bounds for the genes, the array of maximum bounds for the genes, the crossover usage rate, the alpha value for BLX crossover, the mutation usage rate, mutation rate, the mutation range, evaluation function, termination criteria, and EC evaluator. No migration is used.
	 * 
	 * @since 1.0
	 * @param populationSize The size of the population
	 * @param min The array of minimum bounds for the genes
	 * @param max The array of maximum bounds for the genes
	 * @param crossoverUsageRate The crossover usage rate
	 * @param blxAlpha The alpha value for BLX crossover
	 * @param mutationUsageRate The mutation usage rate
	 * @param mutationRate The mutation rate of the Gaussian mutation
	 * @param mutationRange The mutation range of the Gaussian mutation 
	 *     (i.e., the percentage of the effective range that will be used for the
	 *      standard deviation)
	 * @param ef The evaluation function
	 * @param tc The termination criteria
	 * @param ecEval The EC evaluator
	 */
	public RealSteadyStateGA(int populationSize, double[] min, double[] max, double crossoverUsageRate, double blxAlpha, double mutationUsageRate, double mutationRate, double mutationRange, EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecEval) {
		terminationCriteria = tc;
		ecEvaluator = ecEval;
		numDimensions = min.length;
		Interval[] bounds = new Interval[numDimensions];
		for(int i = 0; i < bounds.length; i++) {
			bounds[i] = new Interval(Interval.Type.DOUBLE, new Double(min[i]), new Double(max[i]));
		}
		
		TournamentSelection tournSelection = new TournamentSelection(2, 2);
		SteadyStateSelection stdyStSelection = new SteadyStateSelection();
		BLXCrossoverOperator blxRecombination = new BLXCrossoverOperator(crossoverUsageRate, blxAlpha);
		GaussianMutationOperator gaussMutOp = new GaussianMutationOperator(mutationUsageRate, mutationRate, mutationRange);
		NoMigrationOperator noMig = new NoMigrationOperator();
		
		//		         Population(int           , Interval[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(populationSize, bounds, ef, tournSelection, blxRecombination, gaussMutOp, stdyStSelection, noMig); 
	}
	
	/** This constructor creates an instance of the GA based on the population size, the array of minimum bounds for the genes, the array of maximum bounds for the genes, the crossover usage rate, the alpha value for BLX crossover, the mutation usage rate, mutation rate, the mutation range, evaluation function, termination criteria, EC evaluator, and migration operator.
	 * 
	 * @since 1.0
	 * @param populationSize The size of the population
	 * @param min The array of minimum bounds for the genes
	 * @param max The array of maximum bounds for the genes
	 * @param crossoverUsageRate The crossover usage rate
	 * @param blxAlpha The alpha value for BLX crossover
	 * @param mutationUsageRate The mutation usage rate
	 * @param mutationRate The mutation rate of the Gaussian mutation
	 * @param mutationRange The mutation range of the Gaussian mutation 
	 *     (i.e., the percentage of the effective range that will be used for the
	 *      standard deviation)
	 * @param ef The evaluation function
	 * @param tc The termination criteria
	 * @param ecEval The EC evaluator
	 * @param migOp The migration operator
	 */
	public RealSteadyStateGA(int populationSize, double[] min, double[] max, double crossoverUsageRate, double blxAlpha, double mutationUsageRate, double mutationRate, double mutationRange, EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecEval, MigrationOperator migOp) {
		terminationCriteria = tc;
		ecEvaluator = ecEval;
		numDimensions = min.length;
		Interval[] bounds = new Interval[numDimensions];
		for(int i = 0; i < bounds.length; i++) {
			bounds[i] = new Interval(Interval.Type.DOUBLE, new Double(min[i]), new Double(max[i]));
		}
		
		TournamentSelection tournSelection = new TournamentSelection(2, 2);
		SteadyStateSelection stdyStSelection = new SteadyStateSelection();
		BLXCrossoverOperator blxRecombination = new BLXCrossoverOperator(crossoverUsageRate, blxAlpha);
		GaussianMutationOperator gaussMutOp = new GaussianMutationOperator(mutationUsageRate, mutationRate, mutationRange);
		
		//		         Population(int           , Interval[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(populationSize, bounds, ef, tournSelection, blxRecombination, gaussMutOp, stdyStSelection, migOp); 
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
