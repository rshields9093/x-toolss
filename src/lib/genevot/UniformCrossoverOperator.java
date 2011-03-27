
package lib.genevot;


/** This class implements the RecombinationOperator interface to perform uniform crossover. In this type of crossover, two parents produce a child whose genes are randomly chosen from either the first or second parent.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class UniformCrossoverOperator implements RecombinationOperator {
	/** This field holds the usage rate for the crossover operator. This rate is applied per individual.
	 */
	private double crossoverUsageRate;

	/** The constructor takes the crossover usage rate and creates an instance of the uniform crossover operator.
	 * 
	 * @since 1.0
	 * @param xoverUsageRate The crossover usage rate
	 */
	public UniformCrossoverOperator(double xoverUsageRate) {
		crossoverUsageRate = xoverUsageRate;
	}
	
	/** This method implements the RecombinationOperator interface to perform uniform crossover. It returns a set of children of size equal to half the size of the parent array that is passed in.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param parent The current set of parents
	 * @return An array of individuals representing the children
	 */
	public Individual[] recombine(Population population, Individual[] parent) {
		Individual[] child = new Individual[parent.length / 2];
		int numDimensions = parent[0].getChromosome().getSize();
		int count = 0;
		for(int i = 0; i < parent.length; i+=2) {
			child[count] = new Individual(new Chromosome(parent[0].getChromosome().getBounds()));
			if(Math.random() <= crossoverUsageRate) {
				for(int j = 0; j < numDimensions; j++) {
					if(Math.random() <= 0.5) {
						child[count].getChromosome().setGene(j, parent[i].getChromosome().getGene(j));
					}
					else {
						child[count].getChromosome().setGene(j, parent[i + 1].getChromosome().getGene(j));
					}
				}
			}
			else {
				int parentIndex = (Math.random() >= 0.5)? i : i + 1;
				for(int j = 0; j < numDimensions; j++) {
					child[count].getChromosome().setGene(j, parent[parentIndex].getChromosome().getGene(j));
				}
			}
			count++;
		}
		return child;	
	}	
}
