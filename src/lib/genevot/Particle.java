
package lib.genevot;


/** This class is a subclass of Individual representing a particle to be used in particle swarm optimization. The particle is made up of three vectors (x, p, and v), each represented by a chromosome. The x vector is represented by the original chromosome inherited from Individual. Additionally, the particle has a p fitness representing the fitness value of the p vector.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class Particle extends Individual {
	/** This field represents the p vector for the particle.
	 */
	private Chromosome p;
	/** This field represents the v vector for the particle.
	 */
	private Chromosome v;
	/** This field holds the fitness of the p vector.
	 */
	private double pFitness;
		
	/** The constructor creates a new particle with the x vector initialized to the chromosome that is passed in. The p vector is also initialized to this parameter, and the v vector is initialized to all 0.0.
	 * 
	 * @since 1.0
	 * @param c The chromosome representation of the x vector
	 */
	public Particle(Chromosome c) {
		super(c);
		Interval[] bounds = c.getBounds();
		p = new Chromosome(bounds);
		v = new Chromosome(bounds);
		
		for(int i = 0; i < bounds.length; i++) {
			p.setGene(i, chromosome.getGene(i));
			v.setGene(i, new Double(0.0));	
		}
		pFitness = getFitness();
	}

	/** This method returns the x vector.
	 * 
	 * @since 1.0
	 * @return The chromosome representing the x vector
	 */
	public Chromosome getX() {
		return chromosome;	
	}
	
	/** This method returns the p vector.
	 * 
	 * @since 1.0
	 * @return The chromosome representing the p vector
	 */
	public Chromosome getP() {
		return p;	
	}
	
	/** This method sets the p vector to the specified chromosome.
	 * 
	 * @since 1.0
	 * @param c The new chromosome value for the p vector
	 */
	public void setP(Chromosome c) {
		p = c;	
	}

	/** This method returns the v vector.
	 * 
	 * @since 1.0
	 * @return The chromosome representation of the v vector
	 */
	public Chromosome getV() {
		return v;	
	}
	
	/** This method returns the fitness value for the x vector. This call is equivalent to getFitness().
	 * 
	 * @since 1.0
	 * @return The fitness of the x vector
	 */
	public double getXFitness() {
		return getFitness();	
	}
	
	/** This method sets the fitness of the x vector to the specified parameter. It is equivalent to sfetFitness().
	 * 
	 * @since 1.0
	 * @param xFit The new x fitness
	 */
	public void setXFitness(double xFit) {
		setFitness(xFit);	
	}

	/** This method returns the fitness of the p vector.
	 * 
	 * @since 1.0
	 * @return The fitness of the p vector
	 */
	public double getPFitness() {
		return pFitness;	
	}	
	
	/** This method sets the fitness of the p vector to the specified parameter.
	 * 
	 * @since 1.0
	 * @param pFit The new fitness of the p vector
	 */
	public void setPFitness(double pFit) {
		pFitness = pFit;	
	}
}



