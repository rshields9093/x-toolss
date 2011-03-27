
package lib.genevot;


/** This class represents a real-coded differential EA. This GA uses binary tournament selection, steady-state replacement, differential recombination, and Gaussian mutation. The differential recombination
 *    is accomplished by taking two parents, A and B where A.fitness < B.fitness. The child, C, is created by the following formula: C_k = PHI * Random(0,1) * (A_k - B_k).
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class SteadyStateDEA implements RecombinationOperator {
	
	/** This field holds the current population.
	 */
	protected Population population;
	/** This field holds the number of dimensions (i.e., number of genes) of an individual.
	 */
	protected int numDimensions;
	/** This field holds the EC monitor for the GA.
	 */
	protected ECMonitor ecMonitor;
	/** This field holds the termination criteria for the GA.
	 */
	protected TerminationCriteria terminationCriteria; 
	/** This field holds the phi value for the differential recombination.
	 */
	protected double phi;

	/** This constructor creates an instance of the differential EA based on the population size, the array of minimum bounds for the genes, the array of maximum bounds for the genes, the phi value for recombination, the mutation usage rate, mutation rate, the mutation range, evaluation function, termination criteria, and EC evaluator. No migration is used.
	 * 
	 * @since 1.0
	 * @param populationSize The size of the population
	 * @param min The array of minimum bounds for the genes
	 * @param max The array of maximum bounds for the genes
	 * @param phi The phi value for recombination
	 * @param mutationUsageRate The mutation usage rate
	 * @param mutationRate The mutation rate of the Gaussian mutation
	 * @param mutationRange The mutation range of the Gaussian mutation 
	 *     (i.e., the percentage of the effective range that will be used for the
	 *      standard deviation)
	 * @param ef The evaluation function
	 * @param tc The termination criteria
	 * @param ecMon The EC monitor
	 */
	public SteadyStateDEA(int populationSize, double[] min, double[] max, double phi, double mutationUsageRate, double mutationRate, double mutationRange, EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecMon) {
		terminationCriteria = tc;
		ecMonitor = ecMon;
		numDimensions = min.length;
		Interval[] bounds = new Interval[numDimensions];
		for(int i = 0; i < bounds.length; i++) {
			bounds[i] = new Interval(Interval.Type.DOUBLE, new Double(min[i]), new Double(max[i]));
		}
		
		this.phi = phi;
		
		TournamentSelection tournSelection = new TournamentSelection(2, 2);
		SteadyStateSelection stdyStSelection = new SteadyStateSelection();
		GaussianMutationOperator gaussMutOp = new GaussianMutationOperator(mutationUsageRate, mutationRate, mutationRange);
		NoMigrationOperator noMig = new NoMigrationOperator();
		
		//		         Population(int           , Interval[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(populationSize, bounds, ef, tournSelection, this, gaussMutOp, stdyStSelection, noMig); 
	}
	
	/** This constructor creates an instance of the differential EA based on the population size, the array of minimum bounds for the genes, the array of maximum bounds for the genes, the phi value for recombination, the mutation usage rate, mutation rate, the mutation range, evaluation function, termination criteria, EC evaluator, and migration operator.
	 * 
	 * @since 1.0
	 * @param populationSize The size of the population
	 * @param min The array of minimum bounds for the genes
	 * @param max The array of maximum bounds for the genes
	 * @param phi The phi value for recombination
	 * @param mutationUsageRate The mutation usage rate
	 * @param mutationRate The mutation rate of the Gaussian mutation
	 * @param mutationRange The mutation range of the Gaussian mutation 
	 *     (i.e., the percentage of the effective range that will be used for the
	 *      standard deviation)
	 * @param ef The evaluation function
	 * @param tc The termination criteria
	 * @param ecMon The EC monitor
	 * @param migOp The migration operator
	 */
	public SteadyStateDEA(int populationSize, double[] min, double[] max, double phi, double mutationUsageRate, double mutationRate, double mutationRange, EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecMon, MigrationOperator migOp) {
		terminationCriteria = tc;
		ecMonitor = ecMon;
		numDimensions = min.length;
		Interval[] bounds = new Interval[numDimensions];
		for(int i = 0; i < bounds.length; i++) {
			bounds[i] = new Interval(Interval.Type.DOUBLE, new Double(min[i]), new Double(max[i]));
		}
		
		this.phi = phi;
		
		TournamentSelection tournSelection = new TournamentSelection(2, 2);
		SteadyStateSelection stdyStSelection = new SteadyStateSelection();
		GaussianMutationOperator gaussMutOp = new GaussianMutationOperator(mutationUsageRate, mutationRate, mutationRange);
		
		//		         Population(int           , Interval[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(populationSize, bounds, ef, tournSelection, this, gaussMutOp, stdyStSelection, migOp); 
	}
	
	/** This method implements the differential recombination. The operation is accomplished by taking two parents, A and B where A.fitness < B.fitness. The child, C, is created by the following formula: C_k = PHI * Random(0,1) * (A_k - B_k).
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param parent The current set of parents
	 * @return An array of individuals representing the children
	 */
	public Individual[] recombine(Population population, Individual[] parent) {
		Individual[] child = new Individual[1];
		child[0] = new Individual(new Chromosome(parent[0].getChromosome().getBounds()));
		if(parent[0].compareTo(parent[1]) < 0) {
			for(int i = 0; i < numDimensions; i++) {
				double a = ((Double)parent[0].getChromosome().getGene(i)).doubleValue();
				double b = ((Double)parent[1].getChromosome().getGene(i)).doubleValue();;
				double value = b + phi * Math.random() * (a - b);
				value = Math.max(((Double)child[0].getChromosome().getBounds(i).getMin()).doubleValue(), Math.min(((Double)child[0].getChromosome().getBounds(i).getMax()).doubleValue(), value));
				child[0].getChromosome().setGene(i, new Double(value));
			}
		}
		else {
			for(int i = 0; i < numDimensions; i++) {
				double a = ((Double)parent[1].getChromosome().getGene(i)).doubleValue();
				double b = ((Double)parent[0].getChromosome().getGene(i)).doubleValue();;
				double value = b + phi * Math.random() * (a - b);
				value = Math.max(((Double)child[0].getChromosome().getBounds(i).getMin()).doubleValue(), Math.min(((Double)child[0].getChromosome().getBounds(i).getMax()).doubleValue(), value));
				child[0].getChromosome().setGene(i, new Double(value));
			}			
		}
		return child;
	}
	
	/** This method just calls the <code>evolve</code> method of the population with the specified termination criteria and EC monitor.
	 * 
	 * @since 1.0
	 * @return The EC result as determined by the EC monitor
	 */
	public ECResult evolve() {
		return population.evolve(terminationCriteria, ecMonitor);
	}

	/** This method resets the population to a random initialization.
	 * 
	 * @since 1.0
	 */
	public void reset() {
		population.initialize();
	}
}
