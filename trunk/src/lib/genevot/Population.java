
package lib.genevot;


import java.util.Random;


/** This class represents a population of individuals used in an EC. It is made up of an array of individuals, an evaluation function, a parent selection scheme, a survivor selection scheme, a recombination operator, a mutation operator, and a migration operator.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */

public class Population implements Cloneable {
	/** This field holds the random number generator for this class. It is initialized at compile time.
	 */
	protected static final Random random = new Random();
	/** This field holds the array of individuals belonging to the population.
	 */
	protected Individual[] individual;
	/** This field holds the array of intervals corresponding to the gene bounds for each gene of an individual.
	 */
	protected Interval[] geneBounds;
	/** This field holds the evaluation function to be optimized.
	 */
	protected EvaluationFunction evaluationFunction;
	/** This field holds the parent selection scheme for this population.
	 */
	protected ParentSelection parentSelection;
	/** This field holds the recombination operator for this population.
	 */
	protected RecombinationOperator recombinationOperator;
	/** This field holds the mutation operator for this population.
	 */
	protected MutationOperator mutationOperator;
	/** This field holds the survivor selection scheme for this population.
	 */
	protected SurvivorSelection survivorSelection;
	/** This field holds the migration operator for this population.
	 */
	protected MigrationOperator migrationOperator;
	/** This field holds the number of generations that have currently been processed by this population.
	 */
	protected int numGenerations;
	/** This field holds the number of function evaluations that have currently been executed by this population.
	 */
	protected int numFunctionEvaluations;
	/** This field controls if the evolve function pauses or not.
	 */
	protected boolean paused = false;
	
	
	/** The constructor creates a new population given the specified parameters. The population is initialized with the array of individuals specified.
	 * 
	 * @since 1.0
	 * @param individual An array of individuals representing the initial 
	 *     population
	 * @param ef The evaluation function to be optimized
	 * @param ps The parent selection scheme
	 * @param ro The recombination operator
	 * @param mo The mutation operator
	 * @param ss The survivor selection scheme
	 * @param mig The migration operator
	 */
	public Population(Individual[] individual, EvaluationFunction ef, ParentSelection ps, RecombinationOperator ro, MutationOperator mo, SurvivorSelection ss, MigrationOperator mig) {
		evaluationFunction = ef;
		parentSelection = ps;
		recombinationOperator = ro;
		mutationOperator = mo;
		survivorSelection = ss;
		migrationOperator = mig;
		
		this.individual = individual;
		this.geneBounds = individual[0].getChromosome().getBounds();
		
		numGenerations = 0;
		numFunctionEvaluations = 0;
	}
	
	/** The constructor creates a new population given the specified parameters. The population is randomly initialized based on the specified intervals for each gene.
	 * 
	 * @since 1.0
	 * @param size The size of the population
	 * @param geneBounds The interval array representing the bounds of each 
	 *     gene of an individual
	 * @param ef The evaluation function to be optimized
	 * @param ps The parent selection scheme
	 * @param ro The recombination operator
	 * @param mo The mutation operator
	 * @param ss The survivor selection scheme
	 * @param mig The migration operator
	 */
	public Population(int size, Interval[] geneBounds, EvaluationFunction ef, ParentSelection ps, RecombinationOperator ro, MutationOperator mo, SurvivorSelection ss, MigrationOperator mig) {
		evaluationFunction = ef;
		parentSelection = ps;
		recombinationOperator = ro;
		mutationOperator = mo;
		survivorSelection = ss;
		migrationOperator = mig;
		this.geneBounds = new Interval[geneBounds.length];
		for(int i = 0; i < this.geneBounds.length; i++) {
			this.geneBounds[i] = (Interval)geneBounds[i].clone();
		}
		
		individual = new Individual[size];
		for(int i = 0; i < individual.length; i++) {
			individual[i] = new Individual(new Chromosome(this.geneBounds));
		}	
		
		numGenerations = 0;
		numFunctionEvaluations = 0;
	}

	/** This method returns a deep copy of the population.
	 * 
	 * @since 1.0
	 * @return A deep copy of the population
	 */
	public Object clone() {
		try {
			Population pop = (Population)super.clone();
			return pop;
		}
		catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}
	
	/** This method randomly initializes the existing population.
	 * 
	 * @since 1.0
	 */
	public void initialize() {
		for(int i = 0; i < individual.length; i++) {
			individual[i] = new Individual(new Chromosome(this.geneBounds));
		}			
		numGenerations = 0;
		numFunctionEvaluations = 0;		
	}
	
	/** This method initializes the population with the given individuals.
	 * 
	 * @since 1.0
	 * @param individual The array of individuals that will initialize the population
	 */
	public void initialize(Individual[] individual) {
		this.individual = individual;
		numGenerations = 0;
		numFunctionEvaluations = 0;		
	}
	
	/** This method returns the size (i.e., number of individuals) of the population.
	 * 
	 * @since 1.0
	 * @return The population size
	 */
	public int getSize() {
		return individual.length;	
	}
	
	/** This method returns the current number of generations that have been processed by the population.
	 * 
	 * @since 1.0
	 * @return The current number of generations
	 */
	public int getNumGenerations() {
		return numGenerations;	
	}
	
	/** This method returns the current number of function evaluations that have been processed by the population.
	 * 
	 * @since 1.0
	 * @return The current number of function evaluations
	 */
	public int getNumFunctionEvaluations() {
		return numFunctionEvaluations;	
	}
	
	/** This method returns the specified individual from the population.
	 * 
	 * @since 1.0
	 * @param i The index of the desired individual
	 * @return The individual at the specified index
	 */
	public Individual getIndividual(int i) {
		return individual[i];	
	}
	
	/** This method uses the evaluation function to evaluate each individual in the array, setting their fitness accordingly. If the termination criteria is reached before all individuals have been evaluated, the remaining individuals' fitness values are set to positive infinity.
	 * 
	 * @since 1.0
	 * @param ind The array of individuals to be evaluated
	 * @param tc The termination criteria
	 * @return The number of evaluations that were actually made
	 */
	private int evaluate(Individual[] ind, TerminationCriteria tc) {
		int i = 0;
		while(!tc.terminate(this) && i < ind.length) {
			ind[i].setFitness(evaluationFunction.evaluate(ind[i].getChromosome()));
			numFunctionEvaluations++;
			i++;
			while(!tc.terminate(this) && paused){
				try{
					Thread.sleep(1000);
				}catch(Exception e){
					System.out.println("Blah");
				}
			}
		}
		for(int j = i; j < ind.length; j++) {
			ind[j].setFitness(Double.POSITIVE_INFINITY);
		}
		return i;
	}
	
	/** This method takes the termination criteria and the EC evaluator and evolves the population until the termination criteria is reached. The operation of this method is as follows:
	 * <p>
	 * <code>
	 * InitializeECEvaluator() <br>
	 * numGenerations = 0 <br>
	 * numFunctionEvaluations = 0 <br>
	 * Evaluate(currentPopulation) <br>
	 * numFunctionEvaluations = currentPopulation.length <br>
	 * PerformECEvaluation(currentPopulation, null, null) <br>
	 * WHILE (!terminate) LOOP <br>
	 * 	&nbsp;&nbsp; parent = SelectParents(currentPopulation) <br>
	 * 	&nbsp;&nbsp; child = Recombine(parent) <br>
	 * 	&nbsp;&nbsp; child = Mutate(child) <br>
	 * 	&nbsp;&nbsp; Evaluate(child) <br>
	 * 	&nbsp;&nbsp; numFunctionEvaluations = numFunctionEvaluations + child.length <br>
	 * 	&nbsp;&nbsp; currentPopulation = SelectSurvivors(currentPopulation, parent, child) <br>
	 * 	&nbsp;&nbsp; currentPopulation = Migrate(currentPopulation) <br>
	 * 	&nbsp;&nbsp; PerformECEvaluation(currentPopulation, parent, child) <br>
	 * 	&nbsp;&nbsp; numGenerations = numGenerations + 1 <br>
	 * END LOOP
	 * </code>
	 * 
	 * @since 1.0
	 * @param terminationCriteria The termination criteria
	 * @param ecMon The EC monitor
	 * @return The EC result as returned by the EC evaluator
	 */
	public ECResult evolve(TerminationCriteria terminationCriteria, ECMonitor ecMon) {
		return evolve(terminationCriteria, ecMon, null);
	}
	 
	public ECResult evolve(TerminationCriteria terminationCriteria, ECMonitor ecMon, OnlineAdaptation oa) {
		ecMon.initialize();
		numGenerations = 0;
		numFunctionEvaluations = 0;
		evaluate(individual, terminationCriteria);
		ECResult result = ecMon.getResults(this, null, null);
		while(!terminationCriteria.terminate(this)) {
			numGenerations++;
			Individual[] parent = parentSelection.selectParents(this, individual);
			Individual[] child = recombinationOperator.recombine(this, parent);
			child = mutationOperator.mutate(this, child);
			int numEvaluations = evaluate(child, terminationCriteria);
			if(oa != null) {
				oa.adaptMutation(this, parent, child);
			}
			//numFunctionEvaluations += numEvaluations;
			individual = survivorSelection.selectSurvivors(this, parent, child);
			individual = migrationOperator.migrate(this, individual);
			result = ecMon.getResults(this, parent, child);
		}
		return result;
	}
	
	public void setPaused(boolean p){
		paused = p;
	}
	
	public boolean getPaused(){
		return paused;
	}
	
	public EvaluationFunction getEvaluationFunction() {
		return evaluationFunction;
	}
	
	public ParentSelection getParentSelection() {
		return parentSelection;
	}

	public RecombinationOperator getRecombinationOperator() {
		return recombinationOperator;
	}

	public MutationOperator getMutationOperator() {
		return mutationOperator;
	}
	
	public SurvivorSelection getSurvivorSelection() {
		return survivorSelection;
	}

	public MigrationOperator getMigrationOperator() {
		return migrationOperator;
	}
	
}


