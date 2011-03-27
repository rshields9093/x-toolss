import lib.genevot.Individual;
import lib.genevot.Interval;


public class Memespace {
	private Individual[] array;
	private MemespaceInformation info;
	private int numIndividuals;
	private int nextIndex;
	
	public Memespace(int size, MemespaceInformation info) {
		array = new Individual[size];
		this.info = info;
		numIndividuals = 0;
		nextIndex = 0;
	}
	
	public Individual add(Individual individual) {
		Interval[] inBounds = individual.getChromosome().getBounds();
		Interval[] protoBounds = info.getBounds();
		for(int i = 0; i < inBounds.length; i++) {
			if(inBounds[i].getType() == Interval.Type.BOOLEAN && protoBounds[i].getType() == Interval.Type.BOOLEAN) {
				boolean inMin = ((Boolean)inBounds[i].getMin()).booleanValue();
				boolean protoMin = ((Boolean)protoBounds[i].getMin()).booleanValue();
				boolean inMax = ((Boolean)inBounds[i].getMax()).booleanValue();
				boolean protoMax = ((Boolean)protoBounds[i].getMax()).booleanValue();
				if(inMin != protoMin || inMax != protoMax) {
					return null;
				}
			}
			else if(inBounds[i].getType() == Interval.Type.INTEGER && protoBounds[i].getType() == Interval.Type.INTEGER) {
				int inMin = ((Integer)inBounds[i].getMin()).intValue();
				int protoMin = ((Integer)protoBounds[i].getMin()).intValue();
				int inMax = ((Integer)inBounds[i].getMax()).intValue();
				int protoMax = ((Integer)protoBounds[i].getMax()).intValue();
				if(inMin != protoMin || inMax != protoMax) {
					return null;
				}
			}
			else if(inBounds[i].getType() == Interval.Type.FLOAT && protoBounds[i].getType() == Interval.Type.FLOAT) {
				float inMin = ((Float)inBounds[i].getMin()).floatValue();
				float protoMin = ((Float)protoBounds[i].getMin()).floatValue();
				float inMax = ((Float)inBounds[i].getMax()).floatValue();
				float protoMax = ((Float)protoBounds[i].getMax()).floatValue();
				if(inMin != protoMin || inMax != protoMax) {
					return null;
				}
			}
			else if(inBounds[i].getType() == Interval.Type.DOUBLE && protoBounds[i].getType() == Interval.Type.DOUBLE) {
				double inMin = ((Double)inBounds[i].getMin()).doubleValue();
				double protoMin = ((Double)protoBounds[i].getMin()).doubleValue();
				double inMax = ((Double)inBounds[i].getMax()).doubleValue();
				double protoMax = ((Double)protoBounds[i].getMax()).doubleValue();
				if(inMin != protoMin || inMax != protoMax) {
					return null;
				}
			}
			else {
				return null;
			}			
		}

		Individual outbound = null;
		array[nextIndex] = individual;
		nextIndex = (nextIndex + 1) % array.length;
		if(numIndividuals < array.length) {
			numIndividuals++;
		}
		int outboundIndex = (int)(Math.random() * numIndividuals);
		outbound = array[outboundIndex];
		return outbound;
	}	
	
	public int getNumIndividuals() {
		return numIndividuals;
	}
	
	public Individual getIndividual(int index) {
		if(index >= 0 && index < numIndividuals) {
			return array[index];
		}
		else {
			return null;
		}
	}
	
	public MemespaceInformation getInfo() {
		return info;
	}
}


