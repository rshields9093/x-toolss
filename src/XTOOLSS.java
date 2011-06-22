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

/*
 * Class Overview:
 * This in the entry point for the X-TOOLSS application.
 * It includes Java Main() and handles command line parameters.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import lib.genevot.BLXCrossoverOperator;
import lib.genevot.GaussianMutationOperator;
import lib.genevot.GenerationalSelection;
import lib.genevot.Interval;
import lib.genevot.MuPlusLambdaSelection;
import lib.genevot.MutationOperator;
import lib.genevot.OneFifthRule;
import lib.genevot.ParentSelection;
import lib.genevot.ParticleSwarmOptimization;
import lib.genevot.Population;
import lib.genevot.RecombinationOperator;
import lib.genevot.SteadyGenerationalSelection;
import lib.genevot.SteadyStateSelection;
import lib.genevot.SurvivorSelection;
import lib.genevot.TournamentSelection;
import lib.genevot.UniformCrossoverOperator;


public class XTOOLSS {
	public static void main(String args[]) {
		if(args.length == 0) {
			java.awt.EventQueue.invokeLater(
					new Runnable() {
						public void run() {

							OptimizationsFrame tempFrame = new OptimizationsFrame();
							tempFrame.setVisible(true);
						}
					});
		}
		else if(args.length == 2) {
			// Read the xts file and the ec file
			Module2 problem = new Module2(args[0]);
			String error = problem.processFile();
			if(error.length() > 0) {
				System.out.println(error);
				return;
			}else{
				AppFile application = new AppFile();
				Vector modules = new Vector();
				modules.add(problem);
				application.setModuleArray(modules);

				String temp, r1, r2;
				Vector varValues = ((Module2)modules.get(0)).inputVarValues;
				Vector varTypes = ((Module2)modules.get(0)).getInputVariableTypes();
				Vector lowBounds = new Vector();
				Vector upBounds = new Vector();
				for(int i = 0; i < varValues.size(); i++){	         
					temp = (String)varValues.get(i);
					temp = temp.replace('[', ' ');
					temp = temp.replace(']', ' ');
					temp = temp.trim();
					r1 = temp.substring(0, temp.indexOf(".."));
					r2 = temp.substring(temp.indexOf("..") + 2, temp.length());
					r1.trim();
					r2.trim();
					lowBounds.add(r1);
					upBounds.add(r2);
				}

				application.setLowerBounds(lowBounds);  
				application.setUpperBounds(upBounds);
				Interval[] interval = new Interval[lowBounds.size()];
				boolean canUsePSO = true;
				double[] minForPSO = new double[interval.length];
				double[] maxForPSO = new double[interval.length];
				for(int i = 0; i < interval.length; i++) {
					String dataType = (String)varTypes.elementAt(i);
					if(dataType.equalsIgnoreCase("boolean") || dataType.equalsIgnoreCase("bool")) {
						interval[i] = new Interval(Interval.Type.BOOLEAN, new Boolean(false), new Boolean(true));
						canUsePSO = false;
					}
					else if(dataType.equalsIgnoreCase("integer") || dataType.equalsIgnoreCase("int")) {
						int min = Integer.parseInt((String)lowBounds.get(i));
						int max = Integer.parseInt((String)upBounds.get(i));
						interval[i] = new Interval(Interval.Type.INTEGER, new Integer(min), new Integer(max));
						canUsePSO = false;
					}
					else if(dataType.equalsIgnoreCase("ordinal") || dataType.equalsIgnoreCase("ord")) {
						double min = Double.parseDouble((String)lowBounds.get(i));
						double max = Double.parseDouble((String)upBounds.get(i));
						interval[i] = new Interval(Interval.Type.DOUBLE, new Double(min), new Double(max));
						canUsePSO = false;
					}
					else if(dataType.equalsIgnoreCase("float")) {
						float min = Float.parseFloat((String)lowBounds.get(i));
						float max = Float.parseFloat((String)upBounds.get(i));
						interval[i] = new Interval(Interval.Type.FLOAT, new Float(min), new Float(max));
						minForPSO[i] = min;
						maxForPSO[i] = max;
					}
					else {
						double min = Double.parseDouble((String)lowBounds.get(i));
						double max = Double.parseDouble((String)upBounds.get(i));
						interval[i] = new Interval(Interval.Type.DOUBLE, new Double(min), new Double(max));
						minForPSO[i] = min;
						maxForPSO[i] = max;
					}
				}


				XTOOLSEvaluationFunction xtoolsEvalFun = new XTOOLSEvaluationFunction(application);
				XTOOLSRunnable ecThread = readECFile(args[1], interval, canUsePSO, minForPSO, maxForPSO, xtoolsEvalFun);
				ecThread.start();

				while(ecThread.isAlive());
				System.out.println("X-TOOLSS has finished.");
			}

		}
		else {
			System.out.println("USAGE: java -jar xtoolss.jar");
			System.out.println("       java -jar xtoolss.jar [xts_file] [ec_file]");
		}
	}

	public static XTOOLSRunnable readECFile(String filename, Interval[] interval, boolean canUsePSO, double[] minForPSO, double[] maxForPSO, XTOOLSEvaluationFunction xtoolsEvalFun) {
		String ecName = "Steady-state GA with BLX";
		int popSize = 20;
		int numFunEvals = 500;
		int numElites = 1;
		int hoodSize = 3;
		int constCoeff = 1;
		double crossoverUsageRate = 1.0;
		double blxAlpha = 0.25;
		double mutationUsageRate = 1.0;
		double mutationRate = 0.1;
		double mutationRange = 0.1;
		double etaMutationRate = 0.1;
		double phi = 4.1;
		int logFileName = 0;
		int logInterval = -1;
		String memespaceIP = "";
		int memespacePort = 0;
		double migrationRate = 0.0;
		int numRuns = 1;
		String logFilename = "xtoolss.log";
		String outFilename = "xtoolss.out";
		String statFilename = "xtoolss.stat";
		String useOneFifthRule = "NO";
		boolean displayGUI = false;

		try {
			BufferedReader in = null;
			in = new BufferedReader(new FileReader(filename));
			while(in.ready()) {
				String line = in.readLine();
				StringTokenizer tok = new StringTokenizer(line, ":");
				int numToks = tok.countTokens();
				if(numToks == 2) {
					String label = tok.nextToken().trim();
					if(label.equalsIgnoreCase("Type")) {
						ecName = tok.nextToken().trim();
					}
					else if(label.equalsIgnoreCase("Population Size")) {
						popSize = Integer.parseInt(tok.nextToken().trim());
					}
					else if(label.equalsIgnoreCase("Number of Function Evaluations")) {
						numFunEvals = Integer.parseInt(tok.nextToken().trim());
					}
					else if(label.equalsIgnoreCase("Number of Elites")) {
						numElites = Integer.parseInt(tok.nextToken().trim());
					}
					else if(label.equalsIgnoreCase("Neighborhood Size")) {
						hoodSize = Integer.parseInt(tok.nextToken().trim());
					}
					else if(label.equalsIgnoreCase("Constriction Coefficient")) {
						constCoeff = Integer.parseInt(tok.nextToken().trim());
					}
					else if(label.equalsIgnoreCase("Crossover Usage Rate")) {
						crossoverUsageRate = Double.parseDouble(tok.nextToken().trim());
					}
					else if(label.equalsIgnoreCase("BLX Alpha")) {
						blxAlpha = Double.parseDouble(tok.nextToken().trim());
					}
					else if(label.equalsIgnoreCase("Mutation Usage Rate")) {
						mutationUsageRate = Double.parseDouble(tok.nextToken().trim());
					}
					else if(label.equalsIgnoreCase("Mutation Rate")) {
						mutationRate = Double.parseDouble(tok.nextToken().trim());
					}
					else if(label.equalsIgnoreCase("Mutation Range")) {
						mutationRange = Double.parseDouble(tok.nextToken().trim());
					}
					else if(label.equalsIgnoreCase("Eta Mutation Rate")) {
						etaMutationRate = Double.parseDouble(tok.nextToken().trim());
					}
					else if(label.equalsIgnoreCase("Phi")) {
						phi = Double.parseDouble(tok.nextToken().trim());
					}
					else if(label.equalsIgnoreCase("Log Results")) {
						logFileName = Integer.parseInt(tok.nextToken().trim());
					}
					else if(label.equalsIgnoreCase("Log Interval")) {
						logInterval = Integer.parseInt(tok.nextToken().trim());
					}
					else if(label.equalsIgnoreCase("Memespace IP")) {
						memespaceIP = tok.nextToken().trim();
					}
					else if(label.equalsIgnoreCase("Memespace Port")) {
						memespacePort = Integer.parseInt(tok.nextToken().trim());					
					}
					else if(label.equalsIgnoreCase("Migration Rate")) {
						migrationRate = Double.parseDouble(tok.nextToken().trim());
					}
					else if(label.equalsIgnoreCase("Number of Runs")) {
						numRuns = Integer.parseInt(tok.nextToken().trim());					
					}
					else if(label.equalsIgnoreCase("Log Filename")) {
						logFilename = tok.nextToken().trim();					
					}
					else if(label.equalsIgnoreCase("Out Filename")) {
						outFilename = tok.nextToken().trim();					
					}
					else if(label.equalsIgnoreCase("Stat Filename")) {
						statFilename = tok.nextToken().trim();					
					}
					else if(label.equalsIgnoreCase("Use One-Fifth Rule")) {
						useOneFifthRule = tok.nextToken().trim();					
					}
					else if(label.equalsIgnoreCase("Display GUI")) {
						if(tok.nextToken().trim().equalsIgnoreCase("YES")){
							displayGUI = true;
						}
					}
				}	
				if(logInterval <= 0) {
					logInterval = numFunEvals;
				}
			}
			in.close();
		}
		catch(IOException e) {
			System.out.println("IO Exception in " + filename);
			return null;
		}
		if(logFileName <= 0) {
			logFilename = null;
		}

		ThreadTerminator tt = new ThreadTerminator();
		MaxFunctionEvalTermination mfeTermination = new MaxFunctionEvalTermination(numFunEvals, tt);
		XTOOLSECMonitor xtoolsECMon = new XTOOLSECMonitor(null, displayGUI, logInterval, numFunEvals, tt, logFilename, outFilename);
		XTOOLSMigrationOperator migOp = null;
		if(memespaceIP.length() > 0 && memespacePort > 0) {
			migOp = new XTOOLSMigrationOperator(memespaceIP, memespacePort, (float)migrationRate);
		}
		else {
			migOp = new XTOOLSMigrationOperator("", 0, 0.0f);		 
		}


		Population population = null;
		ParentSelection parentSelection = null;
		RecombinationOperator recombinationOp = null;
		MutationOperator mutationOp = null;
		SurvivorSelection survivorSelection = null;

		if(ecName.equalsIgnoreCase("PSO")) {
			if(canUsePSO) {
				boolean useCC = (constCoeff > 0)? true : false;
				ParticleSwarmOptimization pso = new ParticleSwarmOptimization(popSize, hoodSize, minForPSO, maxForPSO, 2.05, 2.05, useCC, ParticleSwarmOptimization.ASYNCHRONOUS_UPDATE, xtoolsEvalFun, mfeTermination, xtoolsECMon, migOp);
				XTOOLSRunnable ecThread = new XTOOLSRunnable(pso, numRuns, tt, statFilename, false);
				return ecThread;
			}
			else {
				System.out.println("You must have only float or double inputs to use PSO.");
				return null;
			}
		}
		else 
		{
			/*
			 * User is running a GEC (non particle swarm)
			 */
			if(ecName.equalsIgnoreCase("Standard EP")) {
				EPOperators epOps = new EPOperators(false, false, etaMutationRate);
				parentSelection = epOps;
				recombinationOp = epOps;
				mutationOp = new GaussianMutationOperator(1.0, mutationRate, mutationRange);
				survivorSelection = new MuPlusLambdaSelection();
			}
			else if(ecName.equalsIgnoreCase("Continuous Standard EP")) {
				EPOperators epOps = new EPOperators(true, false, etaMutationRate);
				parentSelection = epOps;
				recombinationOp = epOps;
				mutationOp = new GaussianMutationOperator(1.0, mutationRate, mutationRange);
				survivorSelection = new MuPlusLambdaSelection();
			}
			else if(ecName.equalsIgnoreCase("Meta-EP")) {
				EPOperators epOps = new EPOperators(false, true, etaMutationRate);
				parentSelection = epOps;
				recombinationOp = epOps;
				mutationOp = epOps;
				survivorSelection = new MuPlusLambdaSelection();
			}
			else if(ecName.equalsIgnoreCase("Continuous Meta-EP")) {
				EPOperators epOps = new EPOperators(true, true, etaMutationRate);
				parentSelection = epOps;
				recombinationOp = epOps;
				mutationOp = epOps;
				survivorSelection = new MuPlusLambdaSelection();
			}
			else if(ecName.equalsIgnoreCase("Steady-state GA with BLX")) {
				parentSelection = new TournamentSelection(2, 2);
				survivorSelection = new SteadyStateSelection();
				recombinationOp = new BLXCrossoverOperator(crossoverUsageRate, blxAlpha);
				mutationOp = new GaussianMutationOperator(mutationUsageRate, mutationRate, mutationRange);
			}
			else if(ecName.equalsIgnoreCase("Generational GA with BLX")) {
				parentSelection = new TournamentSelection(2, 2);
				survivorSelection = new GenerationalSelection(numElites);
				recombinationOp = new BLXCrossoverOperator(crossoverUsageRate, blxAlpha);
				mutationOp = new GaussianMutationOperator(mutationUsageRate, mutationRate, mutationRange);
			}
			else if(ecName.equalsIgnoreCase("Steady-generational GA with BLX")) {
				parentSelection = new TournamentSelection(2, 2);
				survivorSelection = new SteadyGenerationalSelection(1);
				recombinationOp = new BLXCrossoverOperator(crossoverUsageRate, blxAlpha);
				mutationOp = new GaussianMutationOperator(mutationUsageRate, mutationRate, mutationRange);
			}
			else if(ecName.equalsIgnoreCase("Steady-state GA")) {
				parentSelection = new TournamentSelection(2, 2);
				survivorSelection = new SteadyStateSelection();
				recombinationOp = new UniformCrossoverOperator(crossoverUsageRate);
				mutationOp = new GaussianMutationOperator(mutationUsageRate, mutationRate, mutationRange);
			}
			else if(ecName.equalsIgnoreCase("Generational DEA")) {
				parentSelection = new TournamentSelection(2, (popSize - numElites) * 2);
				survivorSelection = new GenerationalSelection(numElites);
				mutationOp = new GaussianMutationOperator(mutationUsageRate, mutationRate, mutationRange);
				recombinationOp = new DEOperators(true, phi);
			}
			else if(ecName.equalsIgnoreCase("Steady-state DEA")) {
				parentSelection = new TournamentSelection(2, (popSize - numElites) * 2);
				survivorSelection = new SteadyStateSelection();
				mutationOp = new GaussianMutationOperator(mutationUsageRate, mutationRate, mutationRange);
				recombinationOp = new DEOperators(false, phi);
			}
			else if(ecName.equalsIgnoreCase("Elitist EDA")) {
				EDAOperators edaOps = new EDAOperators(numElites);
				parentSelection = edaOps;
				survivorSelection = edaOps;
				mutationOp = edaOps;
				recombinationOp = edaOps;
			}

			OneFifthRule oneFifthRule = (useOneFifthRule.equalsIgnoreCase("YES"))? new OneFifthRule() : null;
			population = new Population(popSize, interval, xtoolsEvalFun, parentSelection, recombinationOp, mutationOp, survivorSelection, migOp);
			XTOOLSRunnable ecThread = new XTOOLSRunnable(population, mfeTermination, xtoolsECMon, numRuns, tt, statFilename, false, oneFifthRule);
			return ecThread;
		}

	}

}
