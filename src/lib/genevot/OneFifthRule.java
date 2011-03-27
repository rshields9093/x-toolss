
package lib.genevot;

public class OneFifthRule implements OnlineAdaptation {

	public void adaptMutation(Population pop, Individual[] parent, Individual[] child) {
		int numGoodChildren = 0;
		int parentChildRatio = parent.length / child.length;
		if(parentChildRatio == 0) {
			return;
		}
		int i = 0; 
		int j = 0;
		while(i < parent.length && j < child.length) {
			boolean betterThanAllParents = true;
			for(int k = 0; k < parentChildRatio; k++) {
				if(child[j].compareTo(parent[i + k]) > 0) {
					betterThanAllParents = false;
				}
			}
			if(betterThanAllParents) {
				numGoodChildren++;
			}
			i += parentChildRatio;
			j++;
		}
		
        double pTarget = 1.0 / 5.0;
        double val = 1.0 / 3.0 * (numGoodChildren - pTarget) / (1.0 - pTarget);
        pop.getMutationOperator().setMutationRate(pop.getMutationOperator().getMutationRate() * Math.exp(val));
/*		double frac = (double)numGoodChildren / (double)child.length;
		if(frac < 0.2) {
			pop.getMutationOperator().setMutationRate(pop.getMutationOperator().getMutationRate() * 0.8);
		}
		else if(frac > 0.2) {
			pop.getMutationOperator().setMutationRate(pop.getMutationOperator().getMutationRate() * 1.2);		
		}
*/	}
}
