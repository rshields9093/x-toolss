
//package edu.auburn.eng.aci.xtoolss;

import edu.auburn.eng.aci.genevot.Interval;
import edu.auburn.eng.aci.genevot.Population;
import edu.auburn.eng.aci.genevot.MigrationOperator;
import edu.auburn.eng.aci.genevot.Chromosome;
import edu.auburn.eng.aci.genevot.Individual;
import edu.auburn.eng.aci.genevot.Particle;

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
//			int randIndex = random.nextInt(individual.length);

			// Migrate best individual
			int randIndex = 0;
			for(int i = 1; i < individual.length; i++) {
				if(individual[i].getFitness() < individual[randIndex].getFitness()) {
					randIndex = i;
				}
			}
			
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