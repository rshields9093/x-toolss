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

import lib.genevot.Interval;
import lib.genevot.Population;
import lib.genevot.MigrationOperator;
import lib.genevot.Chromosome;
import lib.genevot.Individual;
import lib.genevot.Particle;

import java.util.Random;


public class XTOOLSMigrationOperator implements MigrationOperator {
	private XTOOLSMemespaceInterface memespaceInterface;
	private float migrationRate;
	private Random random;

	public XTOOLSMigrationOperator(String host, int port, float migRate) {
		memespaceInterface = new XTOOLSMemespaceInterface(host, port);
		migrationRate = migRate;
		random = new Random();
	}

	public Individual[] migrate(Population population, Individual[] individual) {
		if(Math.random() <= migrationRate) {
			// Randomly migrate individual
			//int randIndex = random.nextInt(individual.length);

			// Migrate best individual
			
			//Find best individual
			int randIndex = 0;
			for(int i = 1; i < individual.length; i++) {
				if(individual[i].getFitness() < individual[randIndex].getFitness()) {
					randIndex = i;
				}
			}
			
			//Create new individual with same fitness as best individual, then return new individual
			Individual individualOut = null;
			if(individual[randIndex] instanceof Particle) {
				individualOut = new Individual(((Particle)individual[randIndex]).getP());
				individualOut.setFitness(((Particle)individual[randIndex]).getPFitness());
			}
			else {
				individualOut = individual[randIndex];
			}
			Individual individualIn = memespaceInterface.migrateToMemespace(individualOut);
			if(individualIn == null) {
				return individual;
			}
			else {
				if(individual[randIndex] instanceof Particle) {
					Particle newParticle = new Particle(individualIn.getChromosome());
					newParticle.setPFitness(individualIn.getFitness());
					individual[randIndex] = (Particle)newParticle.clone();
				}
				else {
					Individual newIndividual = new Individual(individualIn.getChromosome());
					newIndividual.setFitness(individualIn.getFitness());				
					individual[randIndex] = (Individual)newIndividual.clone();
				}
				return individual;			
			}
		}
		else {
			return individual;
		}
	}
}