package lib.genevot;


import java.util.Random;


/** This class implements the MutationOperator interface to represent the bit-flip mutation. 
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class BitFlipMutationOperator implements MutationOperator {
	/** This is the rate at which mutation is used. Essentially, this represents the probability of a particular individual to have any mutation applied.
	 */
	private double mutationUsageRate;
	/** This is the rate at which mutation is applied to a gene. Essentially, this represents the probability of a particular gene to have its value flipped.
	 */
	private double mutationRate;
	/** This is the random number generator used by this class. It is initialized in the constructor.
	 */
	private Random random;
	
	private int lowIndex;
	
	private int highIndex;
	
	/** This constructor creates a new bit-flip mutation operator. It takes the mutation usage rate and the mutation rate.
	 * 
	 * @since 1.0
	 * @param usageRate The mutation usage rate
	 * @param rate The mutation rate
	 */
	public BitFlipMutationOperator(double usageRate, double rate) {
		this(usageRate, rate, 0, Integer.MAX_VALUE); 
	}

	public BitFlipMutationOperator(double usageRate, double rate, int lowIndex, int highIndex) {
		mutationUsageRate = usageRate;
		mutationRate = rate;
		random = new Random();
		this.lowIndex = Math.max(0, lowIndex);
		this.highIndex = Math.max(highIndex, this.lowIndex);
	}

	/** This method implements the mutate() method of the MutationOperator interface. It loops through all individuals and uses mutation on a particular individual at the specified mutation usage rate. If an individual is allowed to be mutated, it loops through each gene applying mutation at the specified mutation rate.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param individual The current set of individuals to be mutated
	 * @return An array of individuals representing the mutated individuals
	 */
	public Individual[] mutate(Population population, Individual[] individual) {
		Individual[] mutant = new Individual[individual.length];
		for(int i = 0; i < mutant.length; i++) {
			mutant[i] = new Individual(new Chromosome(individual[0].getChromosome().getBounds()));
			if(Math.random() < mutationUsageRate) {
				int lowSlice = lowIndex;
				int highSlice = Math.min(mutant[i].getChromosome().getSize() - 1, highIndex);
				for(int j = lowSlice; j <= highSlice; j++) {
					mutant[i].getChromosome().setGene(j, individual[i].getChromosome().getGene(j));
					if(Math.random() < mutationRate) {
						if(individual[i].getChromosome().getBounds(j).getType() == Interval.Type.BOOLEAN) {
							boolean geneValue, min, max;
							geneValue = ((Boolean)individual[i].getChromosome().getGene(j)).booleanValue();
							min = ((Boolean)individual[i].getChromosome().getBounds(j).getMin()).booleanValue();
							max = ((Boolean)individual[i].getChromosome().getBounds(j).getMax()).booleanValue();
							geneValue = !geneValue;
							geneValue = (max == min)? max : geneValue;
							mutant[i].getChromosome().setGene(j, new Boolean(geneValue));
						}
						else if(individual[i].getChromosome().getBounds(j).getType() == Interval.Type.INTEGER) {
							int geneValue, min, max;
							geneValue = ((Integer)individual[i].getChromosome().getGene(j)).intValue();
							min = ((Integer)individual[i].getChromosome().getBounds(j).getMin()).intValue();
							max = ((Integer)individual[i].getChromosome().getBounds(j).getMax()).intValue();
							boolean found = false;
							int temp = 0;
							while(!found) {
								temp = min + random.nextInt(max - min + 1);
								found = (temp != geneValue);
							}
							geneValue = temp;
							geneValue = Math.max(min, Math.min(max, geneValue));
							mutant[i].getChromosome().setGene(j, new Integer(geneValue));
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
	
