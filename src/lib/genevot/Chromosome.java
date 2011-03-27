
package lib.genevot;


import java.util.Random;


/** This class represents a generic chromosome. It contains an array of Intervals that represent the bounds for each gene, as well as an array of Objects that hold the value of each gene.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class Chromosome implements Cloneable {
	/** This field holds the random number generator for this class. It is initialized statically.
	 */
	private static final Random random = new Random();
	/** This field holds the array of intervals corresponding to the bounds of each gene of the chromosome.
	 */
	private Interval[] bounds;
	/** This field holds the array of gene values for the chromosome. Each value is of type Object so that the type remains generic.
	 */
	private Object[] gene;
	
	/** The constructor creates a new chromosome based on the interval array corresponding to the bounds of each gene. The value of each gene is randomly generated within the specified bounds.
	 * 
	 * @since 1.0
	 * @param geneBounds The array of intervals corresponding to the bounds of  
	 *     each gene
	 */
	public Chromosome(Interval[] geneBounds) {
		bounds = new Interval[geneBounds.length];
		gene = new Object[bounds.length];
		for(int i = 0; i < bounds.length; i++) {
			bounds[i] = (Interval)geneBounds[i].clone();
			switch(bounds[i].getType()) {
				case BOOLEAN:
					boolean boolLow = ((Boolean)bounds[i].getMin()).booleanValue();
					boolean boolHigh = ((Boolean)bounds[i].getMax()).booleanValue();
					boolean boolVal = (random.nextFloat() >= 0.5)? boolLow : boolHigh;
					gene[i] = new Boolean(boolVal);
					break;
				case INTEGER:
					int intLow = ((Integer)bounds[i].getMin()).intValue();
					int intHigh = ((Integer)bounds[i].getMax()).intValue();
					int intVal = 0;
					int range = intHigh - intLow + 1;
					if(range <= 0) {
						intVal = random.nextInt();
					}
					else {
						intVal = intLow + random.nextInt(range);
					}
					gene[i] = new Integer(intVal);
					break;
				case FLOAT:
					float floatLow = ((Float)bounds[i].getMin()).floatValue();
					float floatHigh = ((Float)bounds[i].getMax()).floatValue();
					float floatVal = floatLow + random.nextFloat() * (floatHigh - floatLow);
					gene[i] = new Float(floatVal);
					break;
				case DOUBLE:
					double doubleLow = ((Double)bounds[i].getMin()).doubleValue();
					double doubleHigh = ((Double)bounds[i].getMax()).doubleValue();
					double doubleVal = doubleLow + random.nextDouble() * (doubleHigh - doubleLow);
					gene[i] = new Double(doubleVal);
					break;	
			}
		}
	}

	/** This method returns a deep copy of the chromosome.
	 * 
	 * @since 1.0
	 * @return An Object reference to the deep copy of this instance
	 */
	public Object clone() {
		try {
			Chromosome c = (Chromosome)super.clone();
			c.gene = (Object[])gene.clone();
			c.bounds = new Interval[bounds.length];
			for(int i = 0; i < bounds.length; i++) {
				c.bounds[i] = (Interval)bounds[i].clone();
			}
			return c;
		}
		catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}
		
	/** This method returns the size of the chromosome (i.e., the number of genes).
	 * 
	 * @since 1.0
	 * @return The size of the chromosome
	 */
	public int getSize() {
		return gene.length;	
	}
	
	/** This method returns the type of this particular gene. This type could be any of the types as specified in the Interval class.
	 * 
	 * @since 1.0
	 * @param i The gene index
	 * @return The type of the specified gene
	 */
	public Interval.Type getType(int i) {
		return bounds[i].getType();	
	}
	
	/** This method returns the value of the specified gene.
	 * 
	 * @since 1.0
	 * @param i The gene index
	 * @return An Object containing the value of the specified gene.
	 */
	public Object getGene(int i) {
		return gene[i];	
	}
	
	/** This method sets the specified gene to the specified value.
	 * 
	 * @since 1.0
	 * @param i The gene index
	 * @param o The new value
	 */
	public void setGene(int i, Object o) {
		if(bounds[i].getType() == Interval.Type.BOOLEAN && o instanceof Boolean) {
			gene[i] = o;
		}
		else if(bounds[i].getType() == Interval.Type.INTEGER && o instanceof Integer) {	
			gene[i] = o;
		}
		else if(bounds[i].getType() == Interval.Type.FLOAT && o instanceof Float) {	
			gene[i] = o;
		}
		else if(bounds[i].getType() == Interval.Type.DOUBLE && o instanceof Double) {	
			gene[i] = o;
		}
	}
	
	/** This method returns the interval representing the bounds of the specified gene.
	 * 
	 * @since 1.0
	 * @param i The gene index
	 * @return An interval holding the bounds of the specified gene
	 */
	public Interval getBounds(int i) {
		return bounds[i];	
	}
	
	/** This method returns the array of all the bounds for this chromosome.
	 * 
	 * @since 1.0
	 * @return An array of intervals holding the bounds for all the genes of this
	 *      chromosome
	 */
	public Interval[] getBounds() {
		return (Interval[])bounds.clone();	
	}
	
	/** This method returns the string representation of the chromosome.
	 * 
	 * @since 1.0
	 * @return The string representation
	 */
	public String toString() {
		String s = "";
		for(int i = 0; i < gene.length; i++) {
			s += gene[i] + " ";
		}		
		return s;	
	}
}

