/*
 * Copyright 2005 Mike Tinker, Gerry Dozier, Aaron Gerrett, Lauren Goff, 
 * Mike SanSoucie, and Patrick Hull
 * Copyright 2011 Joshua Adams
 * 
 * This file is part of X-TOOLSS.
 *
 * X-TOOLSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * X-TOOLSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with X-TOOLSS.  If not, see <http://www.gnu.org/licenses/>.
 */

import lib.genevot.TerminationCriteria;
import lib.genevot.Population;


public class MaxFunctionEvalTermination implements TerminationCriteria {
	private int numFunctionEvals;
	private ThreadTerminator threadTerminator;
	
	public MaxFunctionEvalTermination(int numFE, ThreadTerminator tt) {
		numFunctionEvals = numFE;
		threadTerminator = tt;
	}
	
	public boolean terminate(Population population) {
		return (threadTerminator.killThread || (population.getNumFunctionEvaluations() >= numFunctionEvals));
	}
}
