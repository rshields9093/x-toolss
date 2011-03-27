
package lib.genevot;

import java.util.Vector;


/** This class represents typical EC information. It includes the best fitness found, the number of function evaluations to find the best solution, and the actual best individual.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class ECResult {
	/** This field holds the best fitness found so far.
	 */
	public Vector<Double> bestFitness;
	/** This field holds the number of function evaluations to find the best solution.
	 */
	public int numFEBest;
	/** This field holds the best individual found so far.
	 */
	public Individual bestIndividual;
	
	/** This constructor creates a new EC result with best fitness set to 0.0, the number of function evaluations set to 0, and the best individual set to null.
	 * 
	 * @since 1.0
	 */
	public ECResult() {
		bestFitness = new Vector<Double>();	
		numFEBest = 0;	
		bestIndividual = null;
	}
}
