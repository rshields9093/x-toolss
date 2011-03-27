
package lib.genevot;


import java.util.Random;


/** This class represents a meta-EP. In this algorithm, each gene also has a mutation rate that is coevolved. At each generation, each individual produces a child. Survivors are selected according to a (mu + lambda) tournament selection. For a detailed description of Evolutionary Programming, see Fogel, David B. and Chellapilla, K., "Revisiting Evolutionary Programming," AeroSense'98: Aerospace/Defense Sensing and Controls, Orlando, Apr. 1998.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class MetaEP implements ParentSelection, RecombinationOperator, MutationOperator {
	
	/** This field holds the current population.
	 */
	protected Population population;
	/** This field holds the number of dimensions (i.e., the number of genes) for an individual.
	 */
	protected int numDimensions;
	/** This field holds the mutation rate of the evolved strategy parameters.
	 */
	protected double eta;
	/** This field holds the EC monitor.
	 */
	protected ECMonitor ecMonitor;
	/** This field holds the termination criteria for this EC.
	 */
	protected TerminationCriteria terminationCriteria; 
	/** This field holds the random number generator for this class. It is initialized in the constructor.
	 */
	protected Random random;

	/** The constructor takes the population size, the tournament size for the survivor selection, an array of minimum bounds for the genes, an array of maximum bounds for the genes, the mutation rate for the evolved strategy parameters, the evaluation function to be optimized, the termination criteria for the EC, and the EC evaluator to be used. No migration is used.
	 * 
	 * @since 1.0
	 * @param populationSize The size of the population
	 * @param survivorTournamentSize The tournament size for survivor 
	 *     selection
	 * @param min The array of minimum bounds for the genes
	 * @param max The array of maximum bounds for the genes
	 * @param eta The mutation rate for the strategy parameters
	 * @param ef The evaluation function
	 * @param tc The termination criteria
	 * @param ecMon The EC monitor
	 */
	public MetaEP(int populationSize, int survivorTournamentSize, double[] min, double[] max, double eta, EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecMon) {
		terminationCriteria = tc;
		ecMonitor = ecMon;
		random = new Random();
		numDimensions = min.length;
		this.eta = eta;
		Interval[] bounds = new Interval[numDimensions * 2];
		for(int i = 0; i < numDimensions; i++) {
			bounds[i] = new Interval(Interval.Type.DOUBLE, new Double(min[i]), new Double(max[i]));
			bounds[numDimensions + i] = new Interval(Interval.Type.DOUBLE, new Double(0.0), new Double(1.0));
		}
		
		MuPlusLambdaTournamentSelection mplSelection = new MuPlusLambdaTournamentSelection(survivorTournamentSize);
		NoMigrationOperator noMig = new NoMigrationOperator();
		
		//		         Population(int           , Interval[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(populationSize, bounds, ef, this, this, this, mplSelection, noMig); 
	}

	/** The constructor takes the population size, the tournament size for the survivor selection, an array of minimum bounds for the genes, an array of maximum bounds for the genes, the mutation rate for the evolved strategy parameters, the evaluation function to be optimized, the termination criteria for the EC, the EC evaluator, and the migration operator.
	 * 
	 * @since 1.0
	 * @param populationSize The size of the population
	 * @param survivorTournamentSize The tournament size for survivor 
	 *     selection
	 * @param min The array of minimum bounds for the genes
	 * @param max The array of maximum bounds for the genes
	 * @param eta The mutation rate for the strategy parameters
	 * @param ef The evaluation function
	 * @param tc The termination criteria
	 * @param ecMon The EC monitor
	 * @param migOp The migration operator
	 */
	public MetaEP(int populationSize, int survivorTournamentSize, double[] min, double[] max, double eta, EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecMon, MigrationOperator migOp) {
		terminationCriteria = tc;
		ecMonitor = ecMon;
		random = new Random();
		numDimensions = min.length;
		this.eta = eta;
		Interval[] bounds = new Interval[numDimensions * 2];
		for(int i = 0; i < numDimensions; i++) {
			bounds[i] = new Interval(Interval.Type.DOUBLE, new Double(min[i]), new Double(max[i]));
			bounds[numDimensions + i] = new Interval(Interval.Type.DOUBLE, new Double(0.0), new Double(1.0));
		}
		
		MuPlusLambdaTournamentSelection mplSelection = new MuPlusLambdaTournamentSelection(survivorTournamentSize);
		
		//		         Population(int           , Interval[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(populationSize, bounds, ef, this, this, this, mplSelection, migOp); 
	}

	/** This method implements the ParentSelection interface. It returns the entire population.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param individual The current set of individuals from which parents should be selected
	 * @return An array of individuals representing the parents
	 */
	public Individual[] selectParents(Population population, Individual[] individual) {
		return individual;	
	}

	/** This method implements the RecombinationOperator interface. It returns the entire array of parents.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param parent The current array of parents
	 * @return An array of individuals representing the children
	 */
	public Individual[] recombine(Population population, Individual[] parent) {
		return parent;
	}
	
	/** This method implements the MutationOperator interface. It mutates each individual with a Gaussian mutation whose range is determined by the evolved strategy parameters.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param individual The array of individuals to be mutated
	 * @return An array of mutated individuals
	 */
	public Individual[] mutate(Population population, Individual[] individual) {
		Individual[] mutant = new Individual[individual.length];
		for(int i = 0; i < individual.length; i++) {
			mutant[i] = new Individual(new Chromosome(individual[i].getChromosome().getBounds()));
			for(int j = 0; j < numDimensions; j++) {
				double geneValue = ((Double)individual[i].getChromosome().getGene(j)).doubleValue();
				double sigma = ((Double)individual[i].getChromosome().getGene(numDimensions + j)).doubleValue();
				double min = ((Double)mutant[i].getChromosome().getBounds(j).getMin()).doubleValue();
				double max = ((Double)mutant[i].getChromosome().getBounds(j).getMax()).doubleValue();
				geneValue = geneValue + sigma * (max - min) * random.nextGaussian();
				geneValue = Math.max(min, Math.min(max, geneValue));
				mutant[i].getChromosome().setGene(j, new Double(geneValue));
				
				geneValue = ((Double)individual[i].getChromosome().getGene(numDimensions + j)).doubleValue();
				geneValue = geneValue + geneValue * eta * random.nextGaussian();
				min = ((Double)mutant[i].getChromosome().getBounds(numDimensions + j).getMin()).doubleValue();
				max = ((Double)mutant[i].getChromosome().getBounds(numDimensions + j).getMax()).doubleValue();
				geneValue = Math.max(min, Math.min(max, geneValue));
				mutant[i].getChromosome().setGene(numDimensions + j, new Double(geneValue));
			} 
		}
		return mutant;
	}
	
	public void setMutationRate(double rate) {}
	
	public double getMutationRate() {
		return 0.0;
	}
	
	/** This method calls the evolve() method of the population with the specified termination criteria and EC evaluator.
	 * 
	 * @since 1.0
	 * @return The EC result as returned by the EC evaluator
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
