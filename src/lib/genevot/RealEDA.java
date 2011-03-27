
package lib.genevot;


import java.util.Random;
import java.util.Arrays;


/** This class represents a real-coded EDA. Each generation, children are created from the probability density function of the set of parents. See Larranaga, P. and Lozano, J. A., Estimation of Distribution Algorithms: A New Tool for Evolutionary Computation, Kluwer Academic Publishers, 2002, for a detailed treatment.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class RealEDA implements ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection {
	private class Statistics {
		public double mean;
		public double stdev;
		public Statistics() {
			mean = 0.0;
			stdev = 0.0;	
		}	
	}
	
	/** This constant defines the minimum value that the standard deviation of the probability density function can attain.
	 */
	protected static final double MIN_SIGMA = 0.001;
	/** The EC evaluator is used to report information about the ECs progress.
	 */
	protected ECMonitor ecEvaluator;
	/** The termination criteria determines when to stop the evolution towards a solution.
	 */
	protected TerminationCriteria terminationCriteria;
	/** This field represents the population of individuals.
	 */
	protected Population population;
	/** This field represents the number of dimensions for an individual (i.e., the number of genes).
	 */
	protected int numDimensions;
	/** This is the random number generator used for the instance. It is initialized in the constructor.
	 */
	protected Random random;
	/** This field stores the number of elite individuals that should be retained each generation.
	 */
	protected int numElites;

	/** The constructor takes the population size, the array of minimum bounds for the genes, the array of maximum bounds for the genes, the number of elite individuals, the evaluation function, the termination criteria, and the EC evaluator. No migration is used.
	 * 
	 * @since 1.0
	 * @param populationSize The size of the population
	 * @param min The array of minimum bounds for the genes of an individual
	 * @param max The array of maximum bounds for the genes of an individual
	 * @param numElites The number of elite individuals that are retained 
	 *     every generation
	 * @param ef The evaluation function
	 * @param tc The termination criteria
	 * @param ecEval The EC evaluator
	 */
	public RealEDA(int populationSize, double[] min, double[] max, int numElites, EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecEval) {
		terminationCriteria = tc;
		ecEvaluator = ecEval;
		random = new Random();
		numDimensions = min.length;
		this.numElites = numElites;
		Interval[] bounds = new Interval[numDimensions];
		for(int i = 0; i < bounds.length; i++) {
			bounds[i] = new Interval(Interval.Type.DOUBLE, new Double(min[i]), new Double(max[i]));	
		}
		
		NoMigrationOperator noMig = new NoMigrationOperator();
		
		//		         Population(int           , Interval[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(populationSize, bounds, ef, this, this, this, this, noMig);
	}
	
	/** The constructor takes the population size, the array of minimum bounds for the genes, the array of maximum bounds for the genes, the number of elite individuals, the evaluation function, the termination criteria, the EC evaluator, and the migration operator. 
	 * 
	 * @since 1.0
	 * @param populationSize The size of the population
	 * @param min The array of minimum bounds for the genes of an individual
	 * @param max The array of maximum bounds for the genes of an individual
	 * @param numElites The number of elite individuals that are retained 
	 *     every generation
	 * @param ef The evaluation function
	 * @param tc The termination criteria
	 * @param ecEval The EC evaluator
	 * @param migOp The migration operator
	 */
	public RealEDA(int populationSize, double[] min, double[] max, int numElites, EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecEval, MigrationOperator migOp) {
		terminationCriteria = tc;
		ecEvaluator = ecEval;
		random = new Random();
		numDimensions = min.length;
		this.numElites = numElites;
		Interval[] bounds = new Interval[numDimensions];
		for(int i = 0; i < bounds.length; i++) {
			bounds[i] = new Interval(Interval.Type.DOUBLE, new Double(min[i]), new Double(max[i]));	
		}
		
		//		         Population(int           , Interval[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(populationSize, bounds, ef, this, this, this, this, migOp);
	}
	
	/** This method calculates the mean of an array of values.
	 * 
	 * @since 1.0
	 * @param array The array to be calculated
	 * @return The mean value of the array
	 */
	private double mean(double[] array) {
		double average = 0.0;
		for(int i = 0; i < array.length; i++) {
			average += array[i];	
		}
		average /= (double)array.length;
		return average;	
	}
	
	/** This method calculates the unbiased standard deviation of an array.
	 * 
	 * @since 1.0
	 * @param array The array to be calculated
	 * @param average The mean of the array
	 * @return The standard deviation of the array
	 */
	private double stdev(double[] array, double average) {
		double sum = 0.0;
		for(int i = 0; i < array.length; i++) {
			sum += Math.pow((array[i] - average), 2);
		}
		double s = Math.sqrt(sum / ((double)array.length - 1.0));
		return s;
	}
	
	/** This method collects all the gene values along a particular dimension for the set of individuals.
	 * 
	 * @since 1.0
	 * @param parent The array of individuals
	 * @param geneNum The dimension of interest
	 * @return The set of gene values along the specified dimension
	 */
	private double[] getGeneSample(Individual[] parent, int geneNum) {
		double[] gp = new double[parent.length];
		for(int i = 0; i < parent.length; i++) {
			gp[i] = ((Double)parent[i].getChromosome().getGene(geneNum)).doubleValue();	
		}
		return gp;
	}
	
	/** This method returns the statistics (i.e., mean and standard deviation) along each dimension for a set of individuals.
	 * 
	 * @since 1.0
	 * @param parent The set of individuals of interest
	 * @return The array of statistics for each dimension
	 */
	private Statistics[] getStatistics(Individual[] parent) {
		Statistics[] stats = new Statistics[numDimensions];
		for(int i = 0; i < numDimensions; i++) {
			stats[i] = new Statistics();
			double[] sample = getGeneSample(parent, i);
			stats[i].mean = mean(sample);
			stats[i].stdev = stdev(sample, stats[i].mean);	
		}	
		return stats;
	}

	/** This method implements the ParentSelection interface. Here, we simply sort the population and select the best half of the individuals.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param individual The set of individuals from which parents should be chosen
	 * @return An array of individuals representing the parents
	 */
	public Individual[] selectParents(Population population, Individual[] individual) {
		Individual[] parent = new Individual[individual.length / 2];
		Arrays.sort(individual, 0, individual.length);
		for(int i = 0; i < parent.length; i++) {
			parent[i] = (Individual)individual[i].clone();	
		}
		return parent;
	}
	
	/** This method implements the RecombinationOperator interface. Here, we simply return the array of parents (because there is no recombination step).
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param parent The current set of parents
	 * @return An array of individuals representing the children produced from 
	 *     the recombination
	 */
	public Individual[] recombine(Population population, Individual[] parent) {
		return parent;
	}
	
	/** This method implements the MutationOperator interface. Here, we create an array of individuals (the children) of the same length as the population size. We use the probability density function of the incoming individuals to select each gene for each child.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param individual The current set of parents
	 * @return An array of individuals representing the mutated versions of the 
	 *     incoming individuals
	 */
	public Individual[] mutate(Population population, Individual[] individual) {
		Statistics[] stats = getStatistics(individual);
		Individual[] mutant = new Individual[population.getSize()];
		for(int i = 0; i < mutant.length; i++) {
			mutant[i] = new Individual(new Chromosome(individual[0].getChromosome().getBounds()));
			for(int j = 0; j < mutant[i].getChromosome().getSize(); j++) {
				double geneValue = stats[j].mean + Math.max(stats[j].stdev, MIN_SIGMA) * random.nextGaussian();
				geneValue = Math.min(((Double)mutant[i].getChromosome().getBounds(j).getMax()).doubleValue(), Math.max(geneValue, ((Double)mutant[i].getChromosome().getBounds(j).getMin()).doubleValue()));
				mutant[i].getChromosome().setGene(j, new Double(geneValue));	
			}	
		}
		return mutant;
	}
	
	public void setMutationRate(double rate) {}
	
	public double getMutationRate() {
		return 0.0;
	}
	
	/** This method implements the SurvivorSelection interface. Here, we return the children, with the inclusion of the best n individuals fromt the current population, where n is the number of elite individuals as specified in numElites.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param parents The current set of parents
	 * @param children The current set of children
	 * @return The array of individuals representing the survivors
	 */
	public Individual[] selectSurvivors(Population population, Individual[] parents, Individual[] children) {
		Individual[] currentPopulation = new Individual[population.getSize()];
		for(int i = 0; i < currentPopulation.length; i++) {
			currentPopulation[i] = population.getIndividual(i);
		}		
		Arrays.sort(currentPopulation, 0, currentPopulation.length);
		for(int i = 0; i < numElites; i++) {
			children[i] = (Individual)currentPopulation[i].clone();	
		}
		return children;		
	}
	
	/** This method simply calls the <code>evolve</code> method of the Population with the termination criteria and EC evaluator.
	 * 
	 * @since 1.0
	 * @return An ECResult corresponding to the output of the EC evaluator
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
