
package lib.genevot;


/** This interface allows the definition of an evaluation function for an EC. The implementing classes need to implement the evaluate() method.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public interface EvaluationFunction {
	/** This method takes a chromosome and returns its fitness. It must be implemented by all classes that implement this interface.
	 * 
	 * @since 1.0
	 * @param c The chromosome to be evaluated
	 * @return The fitness values of the chromosome
	 */
	public double[] evaluate(Chromosome c);
}
