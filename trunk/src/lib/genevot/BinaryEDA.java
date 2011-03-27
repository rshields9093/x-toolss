package lib.genevot;

import java.util.Random;
import java.util.Arrays;


/** This class represents a binary-coded EDA. Each generation, children are created from the probability distribution function of the set of parents. See Larranaga, P. and Lozano, J. A., Estimation of Distribution Algorithms: A New Tool for Evolutionary Computation, Kluwer Academic Publishers, 2002, for a detailed treatment.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class BinaryEDA implements ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection {
	
	/** This field represents the population of individuals.
	 */
	protected Population population;
	/** This field represents the number of dimensions for an individual (i.e., the number of genes).
	 */
	protected int numDimensions;
	/** This is the random number generator used for the instance. It is initialized in the constructor.
	 */
	protected Random random;
	/** The termination criteria determines when to stop the evolution towards a solution.
	 */
	protected TerminationCriteria terminationCriteria;
	/** The EC evaluator is used to report information about the ECs progress.
	 */
	protected ECMonitor ecEvaluator;
	/** This field stores the number of elite individuals that should be retained each generation.
	 */
	protected int numElites;

	/** The constructor takes the population size, the number of genes (i.e., dimensionality, which implies resolution), the number of elite individuals, the evaluation function, the termination criteria, and the EC evaluator. No migration is used.
	 * 
	 * @since 1.0
	 * @param populationSize The size of the population
	 * @param numGenes The dimensionality of an individual, which, in this 
	 *     case, implies the binary resolution
	 * @param numElites The number of elite individuals that are retained 
	 *     every generation
	 * @param ef The evaluation function to be optimized
	 * @param tc The termination criteria that determines when the EC should 
	 *     stop. This is often measure in function evaluations.
	 * @param ecEval The EC evaluator
	 */
	public BinaryEDA(int populationSize, int numGenes, int numElites, EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecEval) {
		terminationCriteria = tc;
		ecEvaluator = ecEval;
		random = new Random();
		numDimensions = numGenes;
		this.numElites = numElites;
		Interval[] bounds = new Interval[numDimensions];
		for(int i = 0; i < bounds.length; i++) {
			bounds[i] = new Interval(Interval.Type.BOOLEAN, new Boolean(false), new Boolean(true));
		}
		
		NoMigrationOperator noMig = new NoMigrationOperator();
		
		//		         Population(int           , Interval[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(populationSize, bounds, ef, this, this, this, this, noMig);
	}

	/** The constructor takes the population size, the number of genes (i.e., dimensionality, which implies resolution), the number of elite individuals, the evaluation function, the termination criteria, the EC evaluator, and the migration operator. 
	 * 
	 * @since 1.0
	 * @param populationSize The size of the population
	 * @param numGenes The dimensionality of an individual, which, in this 
	 *     case, implies the binary resolution
	 * @param numElites The number of elite individuals that are retained 
	 *     every generation
	 * @param ef The evaluation function to be optimized
	 * @param tc The termination criteria that determines when the EC should 
	 *     stop. This is often measure in function evaluations.
	 * @param ecEval The EC evaluator
	 * @param migOp The migration operator
	 */
	public BinaryEDA(int populationSize, int numGenes, int numElites, EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecEval, MigrationOperator migOp) {
		terminationCriteria = tc;
		ecEvaluator = ecEval;
		random = new Random();
		numDimensions = numGenes;
		this.numElites = numElites;
		Interval[] bounds = new Interval[numDimensions];
		for(int i = 0; i < bounds.length; i++) {
			bounds[i] = new Interval(Interval.Type.BOOLEAN, new Boolean(false), new Boolean(true));
		}
		
		//		         Population(int           , Interval[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(populationSize, bounds, ef, this, this, this, this, migOp);
	}
	
	/** This method implements the ParentSelection interface. Here, we simply sort the population and select the best half of the individuals.
	 * 
	 * @since 1.0
	 * @param population The current population
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
	 * @return An array of individuals representing the children
	 */
	public Individual[] recombine(Population population, Individual[] parent) {
		return parent;
	}
	
	/** This method implements the MutationOperator interface. Here, we create an array of individuals (the children) of the same length as the population size. We use the probability distribution of the incoming individuals to select each gene for each child.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param individual The current set of parents
	 * @return An array of individuals representing the mutated versions of the 
	 *     incoming individuals
	 */
	public Individual[] mutate(Population population, Individual[] individual) {
		Individual[] mutant = new Individual[population.getSize() - numElites];
		for(int i = 0; i < mutant.length; i++) {
			mutant[i] = new Individual(new Chromosome(individual[0].getChromosome().getBounds()));
			for(int j = 0; j < mutant[i].getChromosome().getSize(); j++) {
				boolean geneValue = ((Boolean)individual[random.nextInt(individual.length)].getChromosome().getGene(j)).booleanValue();
				mutant[i].getChromosome().setGene(j, new Boolean(geneValue));
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
		Individual[] survivors = new Individual[population.getSize()];
		for(int i = 0; i < children.length; i++) {
			survivors[numElites + i] = children[i];
		}
		for(int i = 0; i < numElites; i++) {
			survivors[i] = (Individual)currentPopulation[i].clone();	
		}
		return survivors;		
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
