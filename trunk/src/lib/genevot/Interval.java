
package lib.genevot;


/** This class represents an interval constraining the values of a variable.
 * 
 * @since 1.0
 * @author Aaron Garrett
 * @version 1.0
 */
public class Interval implements Cloneable {	
	public enum Type {BOOLEAN, INTEGER, FLOAT, DOUBLE};
	
	/** This field holds the datatype represented by this interval.
	 */
	private Type type;
	/** This field holds a reference type of the minimum value of the interval.
	 */
	private Object min;
	/** This field holds a reference type of the maximum value of the interval.
	 */
	private Object max;
	
	/** This constructor takes the type of the interval and initializes the min and max to null.
	 * 
	 * @since 1.0
	 * @param type The type of interval
	 */
	public Interval(Type type) {
		this.type = type;
		min = null;
		max = null;
	}
	
	/** This constructor takes the type, min, and max of the interval and initializes it accordingly.
	 * 
	 * @since 1.0
	 * @param type The type of the interval
	 * @param min The minimum value of the interval
	 * @param max The maximum value of the interval
	 */
	public Interval(Type type, Object min, Object max) {
		this.type = type;
		this.min = min;
		this.max = max;	
	}
	
	/** This method returns a deep copy of the interval.
	 * 
	 * @since 1.0
	 * @return A deep copy of the Interval object
	 */
	public Object clone() {
		try {
			Interval i = (Interval)super.clone();
			return i;
		}
		catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}
	
	/** This method returns the type of the interval.
	 * 
	 * @since 1.0
	 * @return The type of the interval
	 */
	public Type getType() {
		return type;
	}
	
	/** This method allows the setting of the type of the interval.
	 * 
	 * @since 1.0
	 * @param t The type of the interval
	 */
	public void setType(Type t) {
		type = t;
	}
		
	public static boolean canCast(Interval.Type from, Interval.Type to) {
		if(from == to || from == Interval.Type.BOOLEAN) {
			return true;
		}
		else if(from == Interval.Type.INTEGER) {
			return (to == Interval.Type.FLOAT || to == Interval.Type.DOUBLE);
		}
		else if(from == Interval.Type.FLOAT) {
			return (to == Interval.Type.DOUBLE);
		}
		else if(from == Interval.Type.DOUBLE) {
			return (to == Interval.Type.FLOAT);
		}
		else {
			return false;
		}
	}
	
	public static Object cast(Interval.Type fromType, Interval.Type toType, Object fromVal) {
		if(fromType == toType) {
			return fromVal;
		}
		else if(fromType == Interval.Type.BOOLEAN) {
			boolean v = ((Boolean)fromVal).booleanValue();
			if(toType == Interval.Type.INTEGER) {
				int w = (v)? 1 : 0;
				return new Integer(w);
			}
			else if(toType == Interval.Type.FLOAT) {
				float w = (v)? 1.0f : 0.0f;
				return new Float(w);			
			}
			else {
				double w = (v)? 1.0 : 0.0;
				return new Double(w);						
			}
		}
		else if(fromType == Interval.Type.INTEGER) {
			int v = ((Integer)fromVal).intValue();
			if(toType == Interval.Type.BOOLEAN) {
				boolean w = (v == 0)? false : true;
				return new Boolean(w);
			}
			else if(toType == Interval.Type.FLOAT) {
				float w = (float)v;
				return new Float(w);			
			}
			else {
				double w = (double)v;
				return new Double(w);						
			}
		}
		else if(fromType == Interval.Type.FLOAT) {
			float v = ((Float)fromVal).floatValue();
			if(toType == Interval.Type.BOOLEAN) {
				boolean w = (v == 0.0f)? false : true;
				return new Boolean(w);
			}
			else if(toType == Interval.Type.INTEGER) {
				int w = (int)v;
				return new Integer(w);			
			}
			else {
				double w = (double)v;
				return new Double(w);						
			}
		}
		else {
			double v = ((Double)fromVal).doubleValue();
			if(toType == Interval.Type.BOOLEAN) {
				boolean w = (v == 0.0)? false : true;
				return new Boolean(w);
			}
			else if(toType == Interval.Type.INTEGER) {
				int w = (int)v;
				return new Integer(w);			
			}
			else {
				float w = (float)v;
				return new Float(w);						
			}
		}
	}	
	
	/** This method returns the minimum value for this interval.
	 * 
	 * @since 1.0
	 * @return An object holding the minimum bound
	 */
	public Object getMin() {
		return min;
	}
	
	/** This method allows the setting of the minimum value for the interval.
	 * 
	 * @since 1.0
	 * @param m The new minimum value
	 */
	public void setMin(Object m) {
		min = m;
	}
	
	/** This method returns the maximum value for this interval.
	 * 
	 * @return An object holding the minimum bound
	 */
	public Object getMax() {
		return max;
	}
	
	/** This method allows the setting of the maximum value for the interval.
	 * 
	 * @since 1.0
	 * @param m The new maximum value
	 */
	public void setMax(Object m) {
		max = m;
	}
	
	/** This method returns TRUE if the argument interval is the same as the current interval.
	 * 
	 * @since 1.0
	 * @return The interval to compare
	 */
	public boolean equals(Interval i) {
		if(i.type != type) {
			return false;
		}
		else if(i.type == Type.BOOLEAN && type == Type.BOOLEAN) {
			if(min != null && max != null && i.min != null && i.max != null) {
				boolean a = ((Boolean)min).booleanValue();
				boolean b = ((Boolean)max).booleanValue();
				boolean c = ((Boolean)i.min).booleanValue();
				boolean d = ((Boolean)i.max).booleanValue();
				return (a == c) && (b == d);
			}
			else if(min == null && max == null && i.min == null && i.max == null) {
				return true;
			}
			else {
				return false;
			}
		}
		else if(i.type == Type.INTEGER && type == Type.INTEGER) {
			if(min != null && max != null && i.min != null && i.max != null) {
				int a = ((Integer)min).intValue();
				int b = ((Integer)max).intValue();
				int c = ((Integer)i.min).intValue();
				int d = ((Integer)i.max).intValue();
				return (a == c) && (b == d);
			}
			else if(min == null && max == null && i.min == null && i.max == null) {
				return true;
			}
			else {
				return false;
			}
		}
		else if(i.type == Type.FLOAT && type == Type.FLOAT) {
			if(min != null && max != null && i.min != null && i.max != null) {
				float a = ((Float)min).floatValue();
				float b = ((Float)max).floatValue();
				float c = ((Float)i.min).floatValue();
				float d = ((Float)i.max).floatValue();
				return (a == c) && (b == d);
			}
			else if(min == null && max == null && i.min == null && i.max == null) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			if(min != null && max != null && i.min != null && i.max != null) {
				double a = ((Double)min).doubleValue();
				double b = ((Double)max).doubleValue();
				double c = ((Double)i.min).doubleValue();
				double d = ((Double)i.max).doubleValue();
				return (a == c) && (b == d);
			}
			else if(min == null && max == null && i.min == null && i.max == null) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	/** This method returns the string representation for the interval.
	 * 
	 * @since 1.0
	 * @return The string represenation
	 */
	public String toString() {
		String s = "";
		if(type == Type.BOOLEAN) {
			s = "boolean [";
			if(min != null && max != null) {
				boolean a = ((Boolean)min).booleanValue();
				boolean b = ((Boolean)max).booleanValue();
				s += a + ".." + b;
			}
			s += "]";			
		}
		else if(type == Type.INTEGER) {
			s = "integer [";
			if(min != null && max != null) {
				int a = ((Integer)min).intValue();
				int b = ((Integer)max).intValue();
				s += a + ".." + b;
			}
			s += "]";			
		}
		else if(type == Type.FLOAT) {
			s = "float [";
			if(min != null && max != null) {
				float a = ((Float)min).floatValue();
				float b = ((Float)max).floatValue();
				s += a + ".." + b;
			}
			s += "]";			
		}
		else if(type == Type.DOUBLE) {
			s = "double [";
			if(min != null && max != null) {
				double a = ((Double)min).doubleValue();
				double b = ((Double)max).doubleValue();
				s += a + ".." + b;
			}
			s += "]";
		}
		else {
			s = null;
		}	
		return s;
	}	
	
	public static int convertTypeToInt(Type t) {
		if(t == Type.BOOLEAN) return 0;
		else if(t == Type.INTEGER) return 1;
		else if(t == Type.FLOAT) return 2;
		else return 3;
	}
}


