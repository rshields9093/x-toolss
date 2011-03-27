

package lib.genevot;


/** This class represents a particle swarm optimization. For a detailed description of this algorithm, see Kennedy, J. (1997), “The Particle Swarm: Social Adaptation of Knowledge”, Proceedings of the 1997 International Conference on Evolutionary Computation, pp. 303-308, IEEE Press.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class ParticleSwarmOptimization implements ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection {
	/** This constant is used to denote using synchronous update for the PSO.
	 */
	public static final int SYNCHRONOUS_UPDATE = 0;
	/** This constant is used to denote using asynchronous update for the PSO.
	 */
	public static final int ASYNCHRONOUS_UPDATE = 1;
	/** This field represents the current population of particles.
	 */
	private Population population;
	/** This field represents the neighborhood size.
	 */
	private int neighborhoodSize;
	/** This field represents the number of neighborhoods in the swarm.
	 */
	private int numNeighborhoods;
	/** This field represents the swarm update type. The possible values for this field are ASYNCHRONOUS_UPDATE and SYNCHRONOUS_UPDATE.
	 */
	private int updateType;
	/** This field represents whether or not Clerc's constriction coeffient should be used. For more information, see Clerc, M. (1999) The swarm and the queen: towards a deterministic and adaptive particle swarm optimization. Proceedings, 1999 ICEC, Washington, DC, pp 1951-1957.
	 */
	private boolean constrictionCoefficient;
	/** This field represents the cognition rate for the swarm.
	 */
	private double cognitionRate;
	/** This field represents the social rate for the swarm.
	 */
	private double socialRate;
	/** This field holds the array of chromosomes representing the neighborhood best vectors. It is of size numNeighborhoods.
	 */
	private Chromosome[] neighborhoodBest;
	/** This field holds the termination criteria for the PSO.
	 */
	private TerminationCriteria terminationCriteria;
	/** This field holds the EC evaluator for the PSO.
	 */
	private ECMonitor ecEvaluator;

		
	/** The constructor creates a new PSO with the specified number of particles, the neighborhood size, the array of minimum bounds for each particle, the array of maximum bounds for each particle, the cognition rate, the social rate, whether or not the constriction coefficient should be used, the update type, the evaluation function to be optimized, the termination criteria, and the EC evaluator. If the neighborhood size is equal to the number of particles, the PSO uses a star topology. Otherwise, the PSO uses a standard ring topology of the specified neighborhood size. No migration is used.
	 * 
	 * @since 1.0
	 * @param numParticles The number of particles in the swarm
	 * @param neighborhoodSize The size of the neighborhood for a particle
	 * @param min The array of minimum bounds for each element of the 
	 *     particle's vectors
	 * @param max The array of maximum bounds for each element of the 
	 *     particle's vectors
	 * @param cogRate The cognition rate
	 * @param socRate The social rate
	 * @param constCoeff TRUE if the constriction coefficient should be used 
	 *     and FALSE otherwise
	 * @param updateType The update type used (asynchronous or synchronous)
	 * @param ef The evaluation function
	 * @param tc The termination criteria
	 * @param ecEval The EC evaluator
	 */
	public ParticleSwarmOptimization(int numParticles, int neighborhoodSize, double[] min, double[] max, double cogRate, double socRate, boolean constCoeff, int updateType, EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecEval) {
		terminationCriteria = tc;
		ecEvaluator = ecEval;
		int size = numParticles;
		this.neighborhoodSize = neighborhoodSize;
		this.updateType = updateType;
		constrictionCoefficient = constCoeff;
		cognitionRate = cogRate;
		socialRate = socRate;
		Particle[] particle = new Particle[size];
		if(neighborhoodSize == size) {
			numNeighborhoods = 1;
		}
		else {
			numNeighborhoods = size;
		}
		neighborhoodBest = new Chromosome[numNeighborhoods];
		int neighborhoodNum = 0;
		Interval[] bounds = new Interval[min.length];
		for(int i = 0; i < bounds.length; i++) {
			bounds[i] = new Interval(Interval.Type.DOUBLE, new Double(min[i]), new Double(max[i]));	
		}
		for(int i = 0; i < particle.length; i++) {
			particle[i] = new Particle(new Chromosome(bounds));
		}
		
		NoMigrationOperator noMig = new NoMigrationOperator();
				
		//		         Population(Individual[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(particle, ef, this, this, this, this, noMig);
	}

	/** The constructor creates a new PSO with the specified number of particles, the neighborhood size, the array of minimum bounds for each particle, the array of maximum bounds for each particle, the cognition rate, the social rate, whether or not the constriction coefficient should be used, the update type, the evaluation function to be optimized, the termination criteria, the EC evaluator, and the migration operator. If the neighborhood size is equal to the number of particles, the PSO uses a star topology. Otherwise, the PSO uses a standard ring topology of the specified neighborhood size.
	 * 
	 * @since 1.0
	 * @param numParticles The number of particles in the swarm
	 * @param neighborhoodSize The size of the neighborhood for a particle
	 * @param min The array of minimum bounds for each element of the 
	 *     particle's vectors
	 * @param max The array of maximum bounds for each element of the 
	 *     particle's vectors
	 * @param cogRate The cognition rate
	 * @param socRate The social rate
	 * @param constCoeff TRUE if the constriction coefficient should be used 
	 *     and FALSE otherwise
	 * @param updateType The update type used (asynchronous or synchronous)
	 * @param ef The evaluation function
	 * @param tc The termination criteria
	 * @param ecEval The EC evaluator
	 * @param migOp The migration operator
	 */
	public ParticleSwarmOptimization(int numParticles, int neighborhoodSize, double[] min, double[] max, double cogRate, double socRate, boolean constCoeff, int updateType, EvaluationFunction ef, TerminationCriteria tc, ECMonitor ecEval, MigrationOperator migOp) {
		terminationCriteria = tc;
		ecEvaluator = ecEval;
		int size = numParticles;
		this.neighborhoodSize = neighborhoodSize;
		this.updateType = updateType;
		constrictionCoefficient = constCoeff;
		cognitionRate = cogRate;
		socialRate = socRate;
		Particle[] particle = new Particle[size];
		if(neighborhoodSize == size) {
			numNeighborhoods = 1;
		}
		else {
			numNeighborhoods = size;
		}
		neighborhoodBest = new Chromosome[numNeighborhoods];
		int neighborhoodNum = 0;
		Interval[] bounds = new Interval[min.length];
		for(int i = 0; i < bounds.length; i++) {
			bounds[i] = new Interval(Interval.Type.DOUBLE, new Double(min[i]), new Double(max[i]));	
		}
		for(int i = 0; i < particle.length; i++) {
			particle[i] = new Particle(new Chromosome(bounds));
		}
		
		//		         Population(Individual[], EvaluationFunction, ParentSelection, RecombinationOperator, MutationOperator, SurvivorSelection, MigrationOperator)
		population = new Population(particle, ef, this, this, this, this, migOp);
	}

	/** This method finds the best individual in the neighborhood beginning at the specified start index and containing neighborhoodSize particles. The population array is treated as a circular array in the event of an out-of-bounds index.
	 * 
	 * @since 1.0
	 * @param population The population of particles
	 * @param startIndex The starting index of the neighborhood
	 * @return The p vector of the best individual in the neighborhood
	 */
	private Chromosome findBest(Individual[] population, int startIndex) {
		int bestIndex = startIndex;
		Individual best = population[bestIndex];
		int i = startIndex;
		int count = 0;
		while(count < neighborhoodSize) {
			if(population[i].compareTo(best) < 0) {
				best = population[i];
				bestIndex = i;	
			}
			i = (i + 1) % population.length;
			count++;	
		}
		Chromosome bestChromosome = (Chromosome)population[bestIndex].getChromosome().clone();
		return bestChromosome;
	}
	
	/** This method moves the specified individual according to the PSO method.
	 * 
	 * @since 1.0
	 * @param neighborhoodIndex The starting index for the specified particle's 
	 *     neighborhood
	 * @param individual The particle to be moved
	 */
	private void move(int neighborhoodIndex, Individual individual) {
		Particle particle = (Particle)individual;
		double delta;
		double K = 1.0;
		if(constrictionCoefficient) {
			double phi = cognitionRate + socialRate;
			K = 2.0 / Math.abs(2.0 - phi - Math.sqrt((phi * phi) - (4.0 * phi)));
		}
		int numDimensions = individual.getChromosome().getSize();
		for(int i = 0; i < numDimensions; i++) {
			double r1 = Math.random();
			double r2 = Math.random();
			double vi, pi, xi, nbi, min, max;
			vi = ((Double)particle.getV().getGene(i)).doubleValue();
			pi = ((Double)particle.getChromosome().getGene(i)).doubleValue();
			xi = ((Double)particle.getX().getGene(i)).doubleValue();
			nbi = ((Double)neighborhoodBest[neighborhoodIndex].getGene(i)).doubleValue();
			vi = K * (vi + cognitionRate * r1 * (pi - xi) + socialRate * r2 * (nbi - xi));	
			xi = xi + vi;
			min = ((Double)particle.getX().getBounds(i).getMin()).doubleValue();
			max = ((Double)particle.getX().getBounds(i).getMax()).doubleValue();
			if(xi < min || xi > max) {
				xi = Math.min(max, Math.max(xi, min));
				vi = 0.0;
			}
			
			particle.getV().setGene(i, new Double(vi));
			particle.getX().setGene(i, new Double(xi));
		}
	}
	
	/** This method implements the ParentSelection interface. It first updates each particle's p vector and p fitness if the particle's current location is the best that it has encountered. Then it updates the array of neighborhood best particles. Finally, it returns the set of particles for position updating.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param individual The array of particles from which parents should be chosen
	 * @return The array of particles to be moved
	 */
	public Individual[] selectParents(Population population, Individual[] individual) {
		int size = population.getSize();
		for(int i = 0; i < individual.length; i++) {
			Particle p = (Particle)individual[i];
			if(population.getNumGenerations() <= 1) {
				p.setP((Chromosome)p.getX().clone());
				p.setPFitness(p.getXFitness());	
			}
			else if(p.getXFitness() < p.getPFitness()) {
				p.setP((Chromosome)p.getX().clone());
				p.setPFitness(p.getXFitness());	
			}	
		}

		int neighborhoodNum = 0;
		if(neighborhoodSize < size) {
			for(int i = 0; i < individual.length; i++) {
				int startIndex = (i < neighborhoodSize / 2)? (size - neighborhoodSize / 2 + i) : (i - neighborhoodSize / 2);
				neighborhoodNum = i;
				neighborhoodBest[neighborhoodNum] = findBest(individual, startIndex);
			}
		}
		else {
			neighborhoodBest[neighborhoodNum] = findBest(individual, 0);
		}

		return individual;
	}
	
	/** This method implements the RecombinationOperator interface. It simply returns the array of parents.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param parents The current array of parents
	 * @return The array of parents
	 */
	public Individual[] recombine(Population population, Individual[] parents) {
		return parents;
	}
	
	/** This method implements the MutationOperator interface. It iterates through all individuals and updates their positions based on the PSO algorithm.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param individual The current array of individuals
	 * @return The array of individuals after position updating
	 */
	public Individual[] mutate(Population population, Individual[] individual) {
		int size = population.getSize();
		Individual[] mutant = new Individual[individual.length];
		int neighborhoodNum = 0;
		for(int i = 0; i < mutant.length; i++) {	
			mutant[i] = (Individual)individual[i].clone();
			int n = (neighborhoodSize < size)? i : 0;
			move(n, mutant[i]);
			if(updateType == SYNCHRONOUS_UPDATE) {
				if(neighborhoodSize < size) {
					neighborhoodNum = i;
				}
				int startIndex = (i < neighborhoodSize / 2)? (size - neighborhoodSize / 2 + i) : (i - neighborhoodSize / 2);
				neighborhoodBest[neighborhoodNum] = findBest(individual, startIndex);
			}	
			else {
				if(neighborhoodSize < size) {
					neighborhoodNum = i;
				}
			}
		}
		if(updateType == ASYNCHRONOUS_UPDATE) {
			int startIndex = size - (neighborhoodSize / 2);
			for(int i = 0; i < numNeighborhoods; i++) {
				neighborhoodBest[i] = findBest(individual, startIndex);
				startIndex = (startIndex + 1) % size;
			}
		}				
		return mutant;
	}
	
	public void setMutationRate(double rate) {}
	
	public double getMutationRate() {
		return 0.0;
	}
	
	/** This method implements the SurvivorSelection interface. It simply returns the array of children (since they are the particles that have been moved).
	 * 
	 * @since 1.0
	 * @param population The current population of particles
	 * @param parents The current set of parents
	 * @param children The array of updated particles
	 * @return The array of updated particles
	 */
	public Individual[] selectSurvivors(Population population, Individual[] parents, Individual[] children) {
		return children;
	}
	
	/** This method simply calls the evolve() method of the population with the specified termination criteria and EC evaluator.
	 * 
	 * @since 1.0
	 * @return The EC result as returned by the EC evaluator
	 */
	public ECResult evolve() {
		return population.evolve(terminationCriteria, ecEvaluator);	
	}
	
	public Population getPopulation(){
		return population;
	}
	
	/** This method resets the population to a random initialization.
	 * 
	 * @since 1.0
	 */
	public void reset() {
		Particle[] particle = new Particle[population.getSize()];
		for(int i = 0; i < particle.length; i++) {
			particle[i] = new Particle(new Chromosome(population.getIndividual(i).getChromosome().getBounds()));
		}
		population.initialize(particle);
	}	
}
