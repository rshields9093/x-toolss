
package lib.genevot;


import java.util.Random;


/** This class implements the MutationOperator interface to create a Gaussian mutation operator. If the chromosome has any float or double genes, the mutation will randomly augment the value according to a Gaussian distribution with zero mean and with standard deviation equal to a percentage of the range of the gene. This percentage is specified as the mutation range parameter.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class GaussianMutationOperator implements MutationOperator {
	/** This field represents the mutation usage rate. This is the probability with which a particular individual is mutated at all.
	 */
	private double mutationUsageRate;
	/** This field represents the mutation rate. This is the probability with which a particular gene is mutated. Note that a gene cannot be mutated unless the individual has begun to be mutated. In other words, the individual must have passed through the mutation usage rate probability before this probability is applied.
	 */
	private double mutationRate;
	/** This field represents the mutation range of the Gaussian mutation. In other words, it represents the percentage of the gene's range that will become the standard deviation for the Gaussian distribution.
	 */
	private double mutationRange;
	/** This is the random number generator for this class. It is initialized in the constructor.
	 */
	private Random random;
	
	private int lowIndex;
	
	private int highIndex;
	
	/** The constructor requires the usage rate, mutation rate, and mutation range for the Gaussian mutation.
	 * 
	 * @since 1.0
	 * @param usageRate The mutation usage rate
	 * @param rate The mutation rate
	 * @param range The mutation range
	 */
	public GaussianMutationOperator(double usageRate, double rate, double range) {
		this(usageRate, rate, range, 0, Integer.MAX_VALUE);
	}

	public GaussianMutationOperator(double usageRate, double rate, double range, int lowIndex, int highIndex) {
		mutationUsageRate = usageRate;
		mutationRate = rate;
		mutationRange = range;
		random = new Random();
		this.lowIndex = Math.max(0, lowIndex);
		this.highIndex = Math.max(highIndex, this.lowIndex);
	}
	
	/** This method implements the mutate() method specified in the MutationOperator interface. It performs a Gaussian mutation on each gene that is either of type double or float.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param individual The current set of individuals to be mutated
	 * @return An array of the mutated individuals
	 */
	public Individual[] mutate(Population population, Individual[] individual) {
		Individual[] mutant = new Individual[individual.length];
		for(int i = 0; i < mutant.length; i++) {
			mutant[i] = new Individual(new Chromosome(individual[0].getChromosome().getBounds()));
			if(Math.random() <= mutationUsageRate) {
				int lowSlice = lowIndex;
				int highSlice = Math.min(mutant[i].getChromosome().getSize() - 1, highIndex);
				for(int j = lowSlice; j <= highSlice; j++) {
					if(Math.random() <= mutationRate) {
						if(individual[i].getChromosome().getBounds(j).getType() == Interval.Type.DOUBLE) {
							double geneValue, min, max;
							geneValue = ((Double)individual[i].getChromosome().getGene(j)).doubleValue();
							min = ((Double)individual[i].getChromosome().getBounds(j).getMin()).doubleValue();
							max = ((Double)individual[i].getChromosome().getBounds(j).getMax()).doubleValue();
							geneValue = geneValue + mutationRange * (max - min) * random.nextGaussian();
							geneValue = Math.max(min, Math.min(max, geneValue));
							mutant[i].getChromosome().setGene(j, new Double(geneValue));
						}
						else if(individual[i].getChromosome().getBounds(j).getType() == Interval.Type.FLOAT) {
							float geneValue, min, max;
							geneValue = ((Float)individual[i].getChromosome().getGene(j)).floatValue();
							min = ((Float)individual[i].getChromosome().getBounds(j).getMin()).floatValue();
							max = ((Float)individual[i].getChromosome().getBounds(j).getMax()).floatValue();
							geneValue = geneValue + (float)mutationRange * (max - min) * (float)random.nextGaussian();
							geneValue = Math.max(min, Math.min(max, geneValue));
							mutant[i].getChromosome().setGene(j, new Float(geneValue));
						}
						else {
							mutant[i].getChromosome().setGene(j, individual[i].getChromosome().getGene(j));
						}
					}
					else {
						mutant[i].getChromosome().setGene(j, individual[i].getChromosome().getGene(j));
					}					
				}
			}
			else {
				mutant[i] = (Individual)individual[i].clone();	
			}	
		}
		return mutant;
	}
	
	public void setMutationRate(double rate) {
		this.mutationRate = rate;
	}
	
	public double getMutationRate() {
		return this.mutationRate;
	}

}
	
