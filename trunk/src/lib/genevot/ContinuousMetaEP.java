
package lib.genevot;


import java.util.Random;


/** This class represents a continuous meta-EP. In this algorithm, each gene also has a mutation rate that is coevolved. At each generation, one child is created to replace the worst individual in the population. For a detailed description of Evolutionary Programming, see Fogel, David B. and Chellapilla, K., "Revisiting Evolutionary Programming," AeroSense'98: Aerospace/Defense Sensing and Controls, Orlando, Apr. 1998.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class ContinuousMetaEP implements ParentSelection, RecombinationOperator, MutationOperator {
	
	/** This field holds the current population.
	 */
	protected Population population;
	/** This field holds the number of dimensions for an individual (i.e., the number of genes).
	 */
	protected int numDimensions;
	/** This field represents the eta value, which determines the mutation rate for the strategy parameters.
	 */
	protected double eta;
	/** This field holds the EC monitor for this algorithm.
	 */
	protected ECMonitor ecMonitor;
	/** This field holds the termination criteria for this algorithm.
	 */
	protected TerminationCriteria terminationCriteria; 
	/** This field holds the random number generator for this class. It is initialized in the constructor.
	 */
	protected Random random;

	/** This constructor creates an instance of the continuous meta-EP. It takes a population size, the minimum and maximum bounds for each gene, the eta mutation rate, the evaluation function, the termination criteria, and the EC evaluator. No migration is used.
	 * 
	 * @since 1.0
	 * @param populationSize The size of the population
	 * @param min The array of minimum bounds for the genes
	 * @param max The array of maximum bounds for the genes
	 * @param eta The eta rate for modifying the strategy parameters
	 * @param ef The evaluation function
	 * @param tc The termination criteria
	 * @param ecMonitor The EC monitor
	 */
	public ContinuousMetaEP(int populationSize, double[] min, double[] max, double eta, EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecMon) {
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
		
		MuPlusLambdaSelection mplSelection = new MuPlusLambdaSelection();
		NoMigrationOperator noMig = new NoMigrationOperator();
		
		//		         Population(int           , Interval[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(populationSize, bounds, ef, this, this, this, mplSelection, noMig); 
	}
	
	/** This constructor creates an instance of the continuous meta-EP. It takes a population size, the minimum and maximum bounds for each gene, the eta mutation rate, the evaluation function, the termination criteria, the EC evaluator, and the migration operator.
	 * 
	 * @since 1.0
	 * @param populationSize The size of the population
	 * @param min The array of minimum bounds for the genes
	 * @param max The array of maximum bounds for the genes
	 * @param eta The eta rate for modifying the strategy parameters
	 * @param ef The evaluation function
	 * @param tc The termination criteria
	 * @param ecMon The EC monitor
	 * @param migOp The migration operator
	 */
	public ContinuousMetaEP(int populationSize, double[] min, double[] max, double eta, EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecMon, MigrationOperator migOp) {
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
		
		MuPlusLambdaSelection mplSelection = new MuPlusLambdaSelection();
		
		//		         Population(int           , Interval[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(populationSize, bounds, ef, this, this, this, mplSelection, migOp); 
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

	/** This method implements the RecombinationOperator interface. It returns an array of one individual that is one of the parents (randomly selected from the set of parents).
	 * 
	 * @param population The current population
	 * @param parent The current set of parents
	 * @return A one-element array of individuals representing the child
	 */
	public Individual[] recombine(Population population, Individual[] parent) {
		Individual[] child = new Individual[1];
		child[0] = parent[random.nextInt(parent.length)];
		return child;
	}
	
	/** This method implements the MutationOperator interface. It returns an array of individuals to which the Gaussian random mutation is applied. The strategy parameters are also mutated in the same way.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param individual The current set of individuals to be mutated
	 * @return An array of individuals representing the mutated individuals
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

	
	/** This method simply calls the <code>evolve</code> method of the population using the specified termination criteria and EC evaluator.				
	 * 
	 * @since 1.0
	 * @return The EC result that is determined by the EC evaluator
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
