
package lib.genevot;


/** This class represents a real-coded differential EA. This GA uses binary tournament selection, generational replacement, differential recombination, and Gaussian mutation. The differential recombination
 *    is accomplished by taking two parents, A and B where A.fitness < B.fitness. The child, C, is created by the following formula: C_k = PHI * Random(0,1) * (A_k - B_k).
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class GenerationalDEA implements RecombinationOperator {
	
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

	/** This constructor creates an instance of the differential EA based on the population size, the array of minimum bounds for the genes, the array of maximum bounds for the genes, the number of elite individuals, the phi value for recombination, the mutation usage rate, mutation rate, the mutation range, evaluation function, termination criteria, and EC evaluator. No migration is used.
	 * 
	 * @since 1.0
	 * @param populationSize The size of the population
	 * @param min The array of minimum bounds for the genes
	 * @param max The array of maximum bounds for the genes
	 * @param numElites The number of elite individuals that are allowed to  
	 *     survive each generation
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
	public GenerationalDEA(int populationSize, double[] min, double[] max, int numElites, double phi, double mutationUsageRate, double mutationRate, double mutationRange, EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecMon) {
		terminationCriteria = tc;
		ecMonitor = ecMon;
		numDimensions = min.length;
		Interval[] bounds = new Interval[numDimensions];
		for(int i = 0; i < bounds.length; i++) {
			bounds[i] = new Interval(Interval.Type.DOUBLE, new Double(min[i]), new Double(max[i]));
		}
		
		this.phi = phi;
		
		TournamentSelection tournSelection = new TournamentSelection(2, (populationSize - numElites) * 2);
		GenerationalSelection genSelection = new GenerationalSelection(numElites);
		GaussianMutationOperator gaussMutOp = new GaussianMutationOperator(mutationUsageRate, mutationRate, mutationRange);
		NoMigrationOperator noMig = new NoMigrationOperator();
		
		//		         Population(int           , Interval[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(populationSize, bounds, ef, tournSelection, this, gaussMutOp, genSelection, noMig); 
	}
	
	/** This constructor creates an instance of the differential EA based on the population size, the array of minimum bounds for the genes, the array of maximum bounds for the genes, the number of elite individuals, the phi value for recombination, the mutation usage rate, mutation rate, the mutation range, evaluation function, termination criteria, EC evaluator, and migration operator.
	 * 
	 * @since 1.0
	 * @param populationSize The size of the population
	 * @param min The array of minimum bounds for the genes
	 * @param max The array of maximum bounds for the genes
	 * @param numElites The number of elite individuals that are allowed to  
	 *     survive each generation
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
	public GenerationalDEA(int populationSize, double[] min, double[] max, int numElites, double phi, double mutationUsageRate, double mutationRate, double mutationRange, EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecMon, MigrationOperator migOp) {
		terminationCriteria = tc;
		ecMonitor = ecMon;
		numDimensions = min.length;
		Interval[] bounds = new Interval[numDimensions];
		for(int i = 0; i < bounds.length; i++) {
			bounds[i] = new Interval(Interval.Type.DOUBLE, new Double(min[i]), new Double(max[i]));
		}
		
		this.phi = phi;
		
		TournamentSelection tournSelection = new TournamentSelection(2, (populationSize - numElites) * 2);
		GenerationalSelection genSelection = new GenerationalSelection(numElites);
		GaussianMutationOperator gaussMutOp = new GaussianMutationOperator(mutationUsageRate, mutationRate, mutationRange);
		
		//		         Population(int           , Interval[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(populationSize, bounds, ef, tournSelection, this, gaussMutOp, genSelection, migOp); 
	}
	
	/** This method implements the differential recombination. The operation is accomplished by taking two parents, A and B where A.fitness < B.fitness. The child, C, is created by the following formula: C_k = PHI * Random(0,1) * (A_k - B_k). The parent array should be of size 2 * popSize, and the child array that is returned should be of size popSize.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param parent The current set of parents
	 * @return An array of individuals representing the children
	 */
	public Individual[] recombine(Population population, Individual[] parent) {
		Individual[] child = new Individual[population.getSize()];
		for(int i = 0; i < child.length; i++) {
			child[i] = new Individual(new Chromosome(parent[2 * i].getChromosome().getBounds()));
			if(parent[2 * i].compareTo(parent[2 * i + 1]) < 0) {
				for(int j = 0; j < numDimensions; j++) {
					double a = ((Double)parent[2 * i].getChromosome().getGene(j)).doubleValue();
					double b = ((Double)parent[2 * i + 1].getChromosome().getGene(j)).doubleValue();;
					double value = b + phi * Math.random() * (a - b);
					value = Math.max(((Double)child[i].getChromosome().getBounds(j).getMin()).doubleValue(), Math.min(((Double)child[i].getChromosome().getBounds(j).getMax()).doubleValue(), value));
					child[i].getChromosome().setGene(j, new Double(value));
				}
			}
			else {
				for(int j = 0; j < numDimensions; j++) {
					double a = ((Double)parent[2 * i + 1].getChromosome().getGene(j)).doubleValue();
					double b = ((Double)parent[2 * i].getChromosome().getGene(j)).doubleValue();;
					double value = b + phi * Math.random() * (a - b);
					value = Math.max(((Double)child[i].getChromosome().getBounds(j).getMin()).doubleValue(), Math.min(((Double)child[i].getChromosome().getBounds(j).getMax()).doubleValue(), value));
					child[i].getChromosome().setGene(j, new Double(value));
				}			
			}
		}
		return child;
	}
	
	/** This method just calls the <code>evolve</code> method of the population with the specified termination criteria and EC evaluator.
	 * 
	 * @since 1.0
	 * @return The EC result as determined by the EC evaluator
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
