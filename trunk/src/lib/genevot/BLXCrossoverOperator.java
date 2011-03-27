package lib.genevot;


/** This class represents a BLX-alpha crossover operator. It implements the RecombinationOperator interface. The BLX crossover operates as follows:
 * Given two individuals, x and y, we iterate over each gene i. Assuming xi < yi, let delta = alpha * (yi - xi). Then, we let mi be equal to a uniform random value in the range [xi - delta, yi + delta].
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class BLXCrossoverOperator implements RecombinationOperator {
	/** This field represents the crossover usage rate. This essentially is the probability with which a child is created through crossover. If the child does is not created through crossover (by falling outside this probability), it is just a copy of one of the two parents (randomly selected) that would have been used in the crossover.
	 */
	private double crossoverUsageRate;
	/** This field represents alpha value used for BLX crossover.
	 */
	private double blxAlpha;

	private int lowIndex;
	
	private int highIndex;

	/** This constructor creates an instance of the BLX crossover operator. It takes the crossover usage rate and the alpha rate.
	 * 
	 * @since 1.0
	 * @param xoverUsageRate The crossover usage rate
	 * @param blxAlpha The alpha rate for BLX crossover
	 */
	public BLXCrossoverOperator(double xoverUsageRate, double blxAlpha) {
		this(xoverUsageRate, blxAlpha, 0, Integer.MAX_VALUE);
	}

	public BLXCrossoverOperator(double xoverUsageRate, double blxAlpha, int lowIndex, int highIndex) {
		crossoverUsageRate = xoverUsageRate;
		this.blxAlpha = blxAlpha;
		this.lowIndex = Math.max(0, lowIndex);
		this.highIndex = Math.max(highIndex, this.lowIndex);
	}
	
	/** This method implements the recombine() method of the RecombinationOperator interface. It loops through all parents, two-by-two, and uses BLX crossover at the specified crossover usage rate. If a pair of parents is allowed to be recombined, they are combined using the BLX combination as detailed above and produce one child.
	 * 
	 * @since 1.0
	 * @param population The current population
	 * @param parent The current set of parents
	 * @return An array of individuals representing the set of children. This  
	 *     array is of size parent.length / 2.
	 */
	public Individual[] recombine(Population population, Individual[] parent) {
		Individual[] child = new Individual[parent.length / 2];
		int numDimensions = parent[0].getChromosome().getSize();
		double mom;
		double dad;
		double min;
		double max;
		double delta;
		double value;
		int count = 0;
		for(int i = 0; i < parent.length; i+=2) {
			child[count] = new Individual(new Chromosome(parent[0].getChromosome().getBounds()));
			if(Math.random() <= crossoverUsageRate) {
				int lowSlice = lowIndex;
				int highSlice = Math.min(numDimensions - 1, highIndex);
				for(int j = lowSlice; j <= highSlice; j++) {
					if(parent[i].getChromosome().getBounds(j).getType() == Interval.Type.DOUBLE && parent[i + 1].getChromosome().getBounds(j).getType() == Interval.Type.DOUBLE) {
						mom = ((Double)parent[i].getChromosome().getGene(j)).doubleValue();
						dad = ((Double)parent[i + 1].getChromosome().getGene(j)).doubleValue();
						min = Math.min(mom, dad);
						max = Math.max(mom, dad);
						delta = blxAlpha * (max - min);
						value = min - delta + Math.random() * (max - min + 2.0f * delta); 
						value = Math.max(((Double)child[count].getChromosome().getBounds(j).getMin()).doubleValue(), Math.min(((Double)child[count].getChromosome().getBounds(j).getMax()).doubleValue(), value));
						child[count].getChromosome().setGene(j, new Double(value));
					}
					else if(parent[i].getChromosome().getBounds(j).getType() == Interval.Type.FLOAT && parent[i + 1].getChromosome().getBounds(j).getType() == Interval.Type.FLOAT) {
						mom = (double)((Float)parent[i].getChromosome().getGene(j)).floatValue();
						dad = (double)((Float)parent[i + 1].getChromosome().getGene(j)).floatValue();
						min = Math.min(mom, dad);
						max = Math.max(mom, dad);
						delta = blxAlpha * (max - min);
						value = min - delta + Math.random() * (max - min + 2.0f * delta); 
						value = Math.max(((Float)child[count].getChromosome().getBounds(j).getMin()).floatValue(), Math.min(((Float)child[count].getChromosome().getBounds(j).getMax()).floatValue(), value));
						child[count].getChromosome().setGene(j, new Float(value));
					}
					else {
						int parentIndex = (Math.random() >= 0.5)? i : i + 1;
						child[count].getChromosome().setGene(j, parent[parentIndex].getChromosome().getGene(j));
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
