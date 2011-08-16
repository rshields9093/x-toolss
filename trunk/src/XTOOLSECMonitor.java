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

import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.demos.MultiTracing;
import info.monitorenter.gui.chart.demos.MultiTracing.AddPaintRemoveThread;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import java.awt.Component;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.Color;
import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JFrame;
import java.util.LinkedList;
import javax.swing.JTabbedPane;

import lib.genevot.*;

/*
 * The Population object of the genetic algorithm runs this every time it completes
 * a function evaluation.  This is where you update the GUI and log files.
 */
public class XTOOLSECMonitor implements ECMonitor {

  private ECResult ecResult;
  private XTOOLSResultsFrame frame;
  private OptimizationPanel optPanel;
  private PrintWriter logfile;
  private PrintWriter outfile;
  private int logInterval;
  private int numIntervals;
  private int numFunEvals;
  private int maxFunEvals;
  private int lastPopFunEvals;
  private ThreadTerminator threadTerminator;
  protected LinkedList<Graph> graphs;
  protected generalGraph genGraph;
  int pos;
  private Module2 mod;
  private AppFile appFile;
  private double avgFit;
  private double bestFit;
  private Double numGenerations;
  private long elapsedTime, startTime;
  private String convergenceFilename = "convergence.csv";
  private PrintWriter convergenceFile;
  private final String workingDir;
  private static LinkedList<Integer>graphNumber =  new LinkedList<Integer>(); // Used by newGraph for tab names
  private boolean bFirstPass = true;
  



//  public XTOOLSECMonitor(OptimizationPanel op, boolean showFrame, int logInterval, int maxFunEvals, ThreadTerminator tt, String logFilename, Vector inputVarNames, Vector inputVarTypes, Module2 module, AppFile appFile, String workingDir, String outFilename) {
public XTOOLSECMonitor(OptimizationPanel op, boolean showFrame, int logInterval, int maxFunEvals, ThreadTerminator tt, String logFilename, Module2 module, AppFile appFile, String workingDir) {
  ecResult = new ECResult();
    this.workingDir = workingDir;
    optPanel = op;
    startTime = System.currentTimeMillis();
    if (showFrame) {
      graphs = new LinkedList<Graph>();
  //    graphs = null;
      genGraph = new generalGraph();
      mod = (Module2)module;
      this.appFile = appFile;
      genGraph.setVarName(sDataTrkHdr());
      // instantiate
      frame = new XTOOLSResultsFrame(this, maxFunEvals);
    } else {
      frame = null;
    }
    logfile = null;
    outfile = null;
    
    if (logFilename != null) {
      try {
        logfile = new PrintWriter(new FileOutputStream(logFilename));
      } catch (IOException e) {
        System.err.println(e);
      }
    }
    if (mod.getOutputFile() != null) {
      try {
        outfile = new PrintWriter(new FileOutputStream(mod.getOutputFile()));
      } catch (IOException e) {
        System.err.println(e);
      }
    }
    if(convergenceFile == null) {
      try {
         String dirAndFile = workingDir+File.separator+convergenceFilename;
         convergenceFile = new PrintWriter(new FileOutputStream(dirAndFile));
      } catch(IOException e) {
        System.err.println(e);
      }
    }

    this.logInterval = logInterval;
    numIntervals = 0;
    numFunEvals = 0;
    lastPopFunEvals = 0;
    this.maxFunEvals = maxFunEvals;
    threadTerminator = tt;
    if (frame != null) {
      frame.newResultsFrame.setLocationRelativeTo(null);
    }
  }

  /** generalGraph is a nested class that contains the general graph data that
   * is used for updating all the graphs.
   * 
   */
   protected class generalGraph {

    String[] varName = new String[MAX_VARIABLES];
    Double[] value = new Double[MAX_VARIABLES];
    /* This is the size of the data (ie. number of rows or lines in the input and output files)
     * where the first number is for the input and the second number is for the output file.
     * The example table below defines the format the data will be stored for plotting the graphs.
     */
    int[] rowSize = new int[3]; // contains 3 values. inputfile, outputfile, and calculated value row size
    final static int MAX_VARIABLES = 1000; // maximum number of variables
    //private static boolean bFirstGraph;      // used to identify when the first graph is created
    private boolean bFirstGraph=false;      // used to identify when the first graph is created
    private boolean bGraphDeleted = false;   // used to identify when a graph has been deleted.
    private int iGraphDisplayCnt = 0;       //
    

    /** setVarName is where the variable names are copied in a single array
     * to be used in graphing.
     * 
     */
    private void setVarName(String sVars) {
    //  String[] otherPlotNames = {"avgFit", "bestFit"};
      
      //Place String items into array
      String[] sArr = sVars.split(",");

      varName = new String[sArr.length];
      // add Data Tracker data to array
      System.arraycopy(sArr, 0, varName, 0, sArr.length);
      // add calculated names to varName
    //  System.arraycopy(otherPlotNames, 0, varName, sArr.length, otherPlotNames.length);
    }
    

    /** setVariables will be called every cycle to set the variables for each item described in 
     * the generalGraph class above.
     */
    private void setVariables() {
      
      System.arraycopy(appFile.getInputVars(), 0, value, 0, rowSize[0]);
      System.arraycopy(appFile.getOutputVars(), 0, value, rowSize[0], rowSize[1]);
      value[(rowSize[0]+rowSize[1])] = avgFit;
      value[(rowSize[0]+rowSize[1]+1)] = bestFit;
      value[(rowSize[0]+rowSize[1]+2)] = numGenerations;
    //  writeVariables();
    }

   }

   /** addGraph is used to add a new graph to the linked list
    * 
    */
   protected void addGraph(LinkedList<XTOOLSECMonitor.Graph> graph) {
     //LinkedList<XTOOLSECMonitor.Graph> tmp = new LinkedList<Graph>();
     XTOOLSECMonitor.Graph tmpGraph = new Graph();
     graph.add(tmpGraph);

   }

  /** Graph is a nested class that contains the structure used for each graph
   * 
   */
  protected class Graph {
    MultiTracing chartPane;
    LinkedList<String> yVarNames;
    LinkedList<Integer> yIndex;      // position variable is located in value variable
    LinkedList<AddPaintRemoveThread> yThreads;
    LinkedList<ITrace2D> traces;
    String xVarName;
    int xIndex;        // position variable is located in value variable
    AddPaintRemoveThread xThread;
    String tabName;
    CloseTabButton closeTabButton; // used to close graph window

    public Graph() {
      init();
    }

    private void init() {
      yVarNames = new LinkedList<String>();
      yIndex = new LinkedList<Integer>();
      yThreads = new LinkedList<AddPaintRemoveThread>();
      traces = new LinkedList<ITrace2D>();
      xVarName = null;
      xIndex = 0;
    }

    /** newGraph is called to create a new JTabbedPane and place the jChart2D
   * graph on the pane.
   * @param yVarNames
   * @param xVarNames
   */
    public void newGraph(JTabbedPane tabbedPane, LinkedList<String> yVarNames, String xVarName) {
      int[] color = {0x000000, 0xFFFFFF, 0x800000, 0xFF0000, 0x800080, 0xFF00FF,
        0x008000, 0x00FF00, 0x808000, 0xFFFF00, 0x000080, 0x0000FF, 0x008080, 0x00FFFF};
      ITrace2D tmpTrace;
      AddPaintRemoveThread tmpThread;
      Color c;
      // locate index for the variables
      for (int index = 0, ySize = yVarNames.size(); index < ySize; index++) {
        String s = yVarNames.get(index);
        int iTmp = arrayLocation(s);
        this.yIndex.add(iTmp);
        this.yVarNames.add(yVarNames.get(index));  // add to instance
      }
      this.xIndex = arrayLocation(xVarName);
      this.xVarName = xVarName;

      this.chartPane = new MultiTracing(tabbedPane, this.xVarName);

      for (int index = 0, n = this.yVarNames.size(); index < n; index++) {
        //define color for each trace
        if (index > 14) {
          int rem = index % 13;
          c = new Color(color[rem]);
        } else {
          c = new Color(color[index]);
        }
        tmpTrace = new Trace2DSimple();
        this.traces.add(tmpTrace);
        this.traces.get(index).setColor(c);
        tmpThread = new AddPaintRemoveThread(this.chartPane.m_chart, this.traces.get(index), 10, this.yVarNames.get(index));
        this.yThreads.add(index, tmpThread);
        this.yThreads.get(index).start();
      }
      graphNumber.add(genGraph.iGraphDisplayCnt++);
      tabbedPane.add("Graph" + graphNumber.getLast().toString(), chartPane.m_chart);
      // closeTabButton = new CloseTabButton(tabbedPane, graphNumber + 1);
      if (genGraph.bGraphDeleted == true) {
        genGraph.bGraphDeleted = false;
        closeTabButton = new CloseTabButton(this, tabbedPane, graphNumber.indexOf(graphNumber.getLast()));
      } else {
        closeTabButton = new CloseTabButton(this, tabbedPane, graphNumber.indexOf(graphNumber.getLast()));

      }

      if (genGraph.bFirstGraph == false) {
        genGraph.bFirstGraph = true;
      }
    }
   

   /** remove is used to decrement the number of graphs.
    * 
    */
   protected void remove(int itemToRemove) {
     genGraph.bGraphDeleted = true;
     try {
          graphNumber.remove(itemToRemove);
          if(graphNumber.size() == 0) {  // reset bFirstGraph
            genGraph.bFirstGraph = false;
          }
     } catch(IndexOutOfBoundsException e) {
          System.err.println("Graph.remove:" + e);
     }
   }

    /** arrayLocation is the location (ie. index) of a variables location in the varName array.
   * This location for the name coincides with the location of the variable 
   * (GeneralGraph value). It will return the location (ie. index).
   * 
   */
   private int arrayLocation(String varToFind) {
      for (int i=0; i< genGraph.varName.length; i++) {
        int ret = genGraph.varName[i].compareTo(varToFind);
        if(ret == 0){ // are strings the same?
          // yes
          return i;
        }
      }
      System.out.println("arrayLocation--variable name not found\n");
      return -1;
    }

  }
  /**chromoToString takes an array of type Chromosome and changes to a String
   * then adds commas between the values.
   */
  private String chromoToString(Chromosome c) {
    String s = c.toString();
    String s1 = s.replace(" ", ",");
    return (s1);
  }

  private String sDataTrkHdr() {
    //Set first 2 header parameters which are constant
    String sReturn = "Evaluation,Elapsed Time(ms),";
    String[] outVarNamesBest;
    String[] outVarNamesCurr;
    String[] inVarNamesBest;
    String[] inVarNamesCurr;

    //Get fitness names
    String[] outVars = mod.getOutputVariablesToStrings();
    outVarNamesBest = new String[outVars.length];
    outVarNamesCurr = new String[outVars.length];
    for(int i=0; i < outVars.length && outVars[i]!=null; i++) {
      String s = outVars[i].replace(" ", "");
      outVarNamesBest[i] = s + "-Best";
      outVarNamesCurr[i] = s + "-Curr";
    }
    // Convert fitness names to String (ie. not an array of Strings)
    String sOutVarBest = Arrays.toString(outVarNamesBest);
    String sOutVarCurr = Arrays.toString(outVarNamesCurr);

    //Get input variable names
    String[] inVars = mod.getInputVariablesToStrings();
    inVarNamesBest = new String[inVars.length];
    inVarNamesCurr = new String[inVars.length];
    for(int i=0; i < inVars.length && inVars[i]!=null; i++) {
      String s = inVars[i].replace(" ", "");
      inVarNamesBest[i] = s + "-Best";
      inVarNamesCurr[i] = s + "-Curr";
    }
    //Convert input variable names to String (ie. not an array of Strings
    String sInVarBest = Arrays.toString(inVarNamesBest);
    String sInVarCurr = Arrays.toString(inVarNamesCurr);

    //Concatenate all the fields into a single String
    sReturn = sReturn + sOutVarBest + "," + sInVarBest + "," + sOutVarCurr + "," + sInVarCurr;
    //Add commas as separators
  //  sReturn = sReturn.replace(" ", ",");
    //Remove any square brackets (ie. [ or ])
    sReturn = sReturn.replace("[", "");
    sReturn = sReturn.replace("]", "");

    return(sReturn);
  }

  /** stringToDoubleArr will take a string of numbers that are delimited and return
   * a a Double array.
   * sInBuf is the input buffer to be converted. Must be delimited.
   * sDelimiter is the delimiter to split the buffer on.
   */
  private Double[] strigToDoubleArr(String sInBuf, String sDelimiter) {
    String[] sArr;
    Double[] dOutBuf;
    //make string into an array
    sArr = sInBuf.split(sDelimiter);

    // allocate buffer for holding Double array
    dOutBuf = new Double[sArr.length];

    // loop thru and convert to Double array
    for(int i=0; i<sArr.length; i++) {
      try {
        dOutBuf[i] = Double.parseDouble(sArr[i]);
      } catch(Exception e) {
        System.err.println("Error converting string:" + e);
      }
    }
    return (dOutBuf);
  }
  public ECResult getResults(Population population, Individual[] parents, Individual[] children) {
    Individual best = population.getIndividual(0);
    avgFit = 0.0;
    numGenerations = Double.parseDouble(String.valueOf(population.getNumGenerations()));
    elapsedTime = System.currentTimeMillis() - startTime;
    String sTmpDataTrkBest=null;


    /*
     * This section of code calculates the following:
     *   smallestFit
     *   avgFit
     *   ...
     */


    if (best instanceof Particle) {
      double smallestFit = Double.POSITIVE_INFINITY;
      if (children != null) {
        smallestFit = ((Particle) best).getPFitness();
        for (int i = 0; i < population.getSize(); i++) {
          avgFit += ((Particle) population.getIndividual(i)).getPFitness();
          if (((Particle) population.getIndividual(i)).getPFitness() < smallestFit) {
            best = (Particle) population.getIndividual(i);
            smallestFit = ((Particle) best).getPFitness();
          }
        }
      } else {
        smallestFit = ((Particle) best).getPFitness();
        for (int i = 0; i < population.getSize(); i++) {
          avgFit += ((Particle) population.getIndividual(i)).getPFitness();
          if (((Particle) population.getIndividual(i)).getPFitness() < smallestFit) {
            best = (Particle) population.getIndividual(i);
            smallestFit = ((Particle) best).getPFitness();
          }
        }
      }
      if (ecResult.bestIndividual == null || (ecResult.bestFitness.size() > 0 && smallestFit < ecResult.bestFitness.elementAt(0))) {
        ecResult.bestIndividual = (Particle) best.clone();
        if (ecResult.bestFitness.size() <= 0) {
          ecResult.bestFitness.add(smallestFit);
        } else {
          ecResult.bestFitness.setElementAt(smallestFit, 0);
        }
        ecResult.numFEBest = population.getNumFunctionEvaluations();
      } else if (ecResult.bestFitness.size() <= 0) {
        ecResult.bestIndividual = (Particle) best.clone();
        ecResult.bestFitness.add(smallestFit);
        ecResult.numFEBest = population.getNumFunctionEvaluations();
      }
    } else {
      for (int i = 0; i < population.getSize(); i++) {
        avgFit += population.getIndividual(i).getFitness();
        if (population.getIndividual(i).compareTo(best) < 0) {
          best = population.getIndividual(i);
        }
      }
      ecResult.bestFitness.clear();
      ecResult.bestIndividual = (Individual) best.clone();
      for (int i = 0; i < best.getNumFitnessValues(); i++) {
        ecResult.bestFitness.add(best.getFitness(i));
      }
      ecResult.numFEBest = population.getNumFunctionEvaluations();
    }

    /*
     * Calc avgFit, numFunEvals and lastPopFunEvals
     */
    avgFit /= (double) population.getSize();
    numFunEvals += (population.getNumFunctionEvaluations() - lastPopFunEvals);
    lastPopFunEvals = population.getNumFunctionEvaluations();

    // Print header for data tracker file
    if(bFirstPass == true) {
      bFirstPass = false;
      String s = sDataTrkHdr();
      convergenceFile.println(s);
    }
    /*
     * Write to files (should only take place if log interval is same as numFunEvals)
     */
    if (logfile != null) {
      //variables for graphing
      if (best instanceof Particle) {
        String s = ((Particle)best).getPrintableFitness();
        String s1 = s.replace(" ", ",");
        // writes out number of evaluations, current time, best fitness (ie. output), and best fitness input values
        if (children != null) {
          for (int i = 0; i < children.length; i++) {
            logfile.println(((Particle) children[i]).getP() + " " + ((Particle) children[i]).getPFitness());
            int numEvals = population.getNumFunctionEvaluations()-children.length+i+1;
            sTmpDataTrkBest = numEvals + "," + elapsedTime + "," + best.getPrintableFitness() + "," + s1;
            // format data to be comma delimited
            s = chromoToString(((Particle)children[i]).getP());
            convergenceFile.println(sTmpDataTrkBest + ((Particle) children[i]).getPFitness() + "," + s1);
          }
        } else {
          for (int i = 0; i < population.getSize(); i++) {
            logfile.println(((Particle) population.getIndividual(i)).getChromosome() + " " + ((Particle) population.getIndividual(i)).getFitness());
            int numEvals = population.getNumFunctionEvaluations()-population.getSize()+i+1;
            sTmpDataTrkBest = numEvals + "," + elapsedTime + "," + best.getPrintableFitness() + "," + s;
            // format data to be comma delimited
            s = chromoToString(((Particle) population.getIndividual(i)).getChromosome());
            convergenceFile.println(sTmpDataTrkBest + ((Particle) population.getIndividual(i)).getFitness() + "," + s);
          }
        }
      } else {
        // format data to be comma delimited
        String s = chromoToString(best.getChromosome());
        if (children != null) {
          for (int i = 0; i < children.length; i++) {
            logfile.println(children[i].getChromosome() + " || " + children[i].getPrintableFitness());
            int numEvals = population.getNumFunctionEvaluations()-children.length+i+1;

            sTmpDataTrkBest = numEvals + "," + elapsedTime + "," + best.getPrintableFitness() + "," + s;
            // format data to be comma delimited
            s = chromoToString(children[i].getChromosome());
            // temp!!!
            sTmpDataTrkBest = sTmpDataTrkBest + children[i].getPrintableFitness() + "," + s;
            genGraph.value = strigToDoubleArr(sTmpDataTrkBest, ",");
           convergenceFile.println(sTmpDataTrkBest + children[i].getPrintableFitness() + "," + s);
          }
        } else {
          for (int i = 0; i < population.getSize(); i++) {
            logfile.println(population.getIndividual(i).getChromosome() + " || " + population.getIndividual(i).getPrintableFitness());
            int numEvals = population.getNumFunctionEvaluations()-population.getSize()+i+1;
            sTmpDataTrkBest = numEvals + "," + elapsedTime + "," + best.getPrintableFitness() + "," + s;
            // format data to be comma delimited
            s = chromoToString(population.getIndividual(i).getChromosome());
            // temp!!!
            sTmpDataTrkBest = sTmpDataTrkBest + population.getIndividual(i).getPrintableFitness() + "," + s;
            genGraph.value = strigToDoubleArr(sTmpDataTrkBest, ",");
            convergenceFile.println(sTmpDataTrkBest + population.getIndividual(i).getPrintableFitness() + "," + s);
          }
        }
      }
      logfile.flush();
      convergenceFile.flush();
    }
    if (outfile != null) {
      if (numFunEvals >= logInterval) {
        if (best instanceof Particle) {
          if (children != null) {
            outfile.println("Evaluations: " + population.getNumFunctionEvaluations() + "          Total: " + maxFunEvals);
            

            outfile.println("Best: " + ((Particle) best).getP() + " fit: " + ((Particle) best).getPFitness());
            outfile.println("Average Fitness: " + avgFit);
            for (int i = 0; i < population.getSize(); i++) {
              outfile.println("Ind " + (i + 1) + ": " + ((Particle) population.getIndividual(i)).getP() + " fit: " + ((Particle) population.getIndividual(i)).getPFitness());
            }
          } else {
            outfile.println("Evaluations: " + population.getNumFunctionEvaluations() + "          Total: " + maxFunEvals);
            outfile.println("Best: " + ((Particle) best).getChromosome() + " fit: " + ((Particle) best).getFitness());
            outfile.println("Average Fitness: " + avgFit);
            for (int i = 0; i < population.getSize(); i++) {
              outfile.println("Ind " + (i + 1) + ": " + ((Particle) population.getIndividual(i)).getChromosome() + " fit: " + ((Particle) population.getIndividual(i)).getFitness());
            }
          }
        } else {
          outfile.println("Evaluations: " + population.getNumFunctionEvaluations() + "          Total: " + maxFunEvals);
          outfile.println("Best: " + best.getChromosome() + " fit: " + best.getPrintableFitness());
          outfile.println("Average Fitness: " + avgFit);
          for (int i = 0; i < population.getSize(); i++) {
            outfile.println("Ind " + (i + 1) + ": " + population.getIndividual(i).getChromosome() + " fit: " + population.getIndividual(i).getPrintableFitness());
          }
        }
        outfile.println();
        outfile.flush();
      } else if (population.getNumFunctionEvaluations() >= maxFunEvals) {
        if (best instanceof Particle) {
          outfile.println("Evaluations: " + population.getNumFunctionEvaluations() + "          Total: " + maxFunEvals);
          outfile.println("Best: " + ((Particle) best).getP() + " fit: " + ((Particle) best).getPFitness());
          outfile.println("Average Fitness: " + avgFit);
          for (int i = 0; i < population.getSize(); i++) {
            outfile.println("Ind " + (i + 1) + ": " + ((Particle) population.getIndividual(i)).getP() + " fit: " + ((Particle) population.getIndividual(i)).getPFitness());
          }
        } else {
          outfile.println("Evaluations: " + population.getNumFunctionEvaluations() + "          Total: " + maxFunEvals);
          outfile.println("Best: " + best.getChromosome() + " fit: " + best.getPrintableFitness());
          outfile.println("Average Fitness: " + avgFit);
          for (int i = 0; i < population.getSize(); i++) {
            outfile.println("Ind " + (i + 1) + ": " + population.getIndividual(i).getChromosome() + " fit: " + population.getIndividual(i).getPrintableFitness());
          }
        }
        outfile.println();
        outfile.flush();
      }
    }

    String tempString = "";
    bestFit = 0.0;
    String bestFitStr = "";
    if ((frame != null && population.getNumFunctionEvaluations() >= population.getSize()) || (population.getNumFunctionEvaluations() >= maxFunEvals)) {
      if (best instanceof Particle) {
        if (children != null) {
          bestFitStr = ((Particle) best).getPFitness() + " : " + ((Particle) best).getP();
          tempString += "Evaluations: " + population.getNumFunctionEvaluations() + "      Total: " + maxFunEvals + "\n";
          tempString += "Best: " + ((Particle) best).getP() + " fit: " + ((Particle) best).getPFitness() + "\n";
          tempString += "Average Fitness: " + avgFit + "\n";
          for (int i = 0; i < population.getSize(); i++) {
            tempString += "Ind " + (i + 1) + ": " + ((Particle) population.getIndividual(i)).getP() + " fit: " + ((Particle) population.getIndividual(i)).getPFitness() + "\n";
          }
          bestFit = ((Particle) best).getPFitness();
        } else {
          bestFitStr = ((Particle) best).getFitness() + " : " + ((Particle) best).getChromosome();
          tempString += "Evaluations: " + population.getNumFunctionEvaluations() + "      Total: " + maxFunEvals + "\n";
          tempString += "Best: " + ((Particle) best).getChromosome() + " fit: " + ((Particle) best).getFitness() + "\n";
          tempString += "Average Fitness: " + avgFit + "\n";
          for (int i = 0; i < population.getSize(); i++) {
            tempString += "Ind " + (i + 1) + ": " + ((Particle) population.getIndividual(i)).getChromosome() + " fit: " + ((Particle) population.getIndividual(i)).getFitness() + "\n";
          }
          bestFit = ((Particle) best).getFitness();
        }
      } else {
        bestFitStr = best.getPrintableFitness() + " : " + best.getChromosome();
        tempString += "Evaluations: " + population.getNumFunctionEvaluations() + "      Total: " + maxFunEvals + "\n";
        tempString += "Best: " + best.getChromosome() + " fit: " + best.getPrintableFitness() + "\n";
        tempString += "Average Fitness: " + avgFit + "\n";
        for (int i = 0; i < population.getSize(); i++) {
          tempString += "Ind " + (i + 1) + ": " + population.getIndividual(i).getChromosome() + " fit: " + population.getIndividual(i).getPrintableFitness() + "\n";
        }
        bestFit = best.getFitness();
      }
      frame.setBestIndividualInfo(frame.getBestIndividualInfo() + "Generation " + population.getNumGenerations() + " Best: " + bestFitStr + "\n", bestFit);
      frame.setCurrentPopulationInfo(tempString);
      // set variables for the items that are plottale
     //-should be able to delete genGraph.setVariables();
     
      // plot the items
      if(genGraph.bFirstGraph == true) {
        //int tSize;
        //tSize = graphs.get(0).yThreads.size();
        for(int graphIdx=0, graphSize=graphs.size(); graphIdx<graphSize; graphIdx++) {
          for(int thrIdx=0, thrSize=graphs.get(graphIdx).yThreads.size(); thrIdx<thrSize; thrIdx++) {
                      graphs.get(graphIdx).yThreads.get(thrIdx).updateData(genGraph.value[this.graphs.get(graphIdx).xIndex],
                              genGraph.value[this.graphs.get(graphIdx).yIndex.get(thrIdx)]);
                      int tmp = graphs.get(graphIdx).yThreads.size();
                      tmp = graphs.get(graphIdx).yThreads.size();

          }
        }
      }
    }
    if (numFunEvals >= logInterval) {
      numFunEvals = 0;
      numIntervals++;
    }
    if (optPanel != null) {
      //System.out.println("Calling optPanel.updateData() from XTOOLSECMon...");
      optPanel.updateData();
    }
//		System.out.println("Mutation Rate (" + population.getNumGenerations() + "): " + population.getMutationOperator().getMutationRate());
    return ecResult;
  }
  public boolean isDisplayed() {
    return (frame != null);
  }

  public int getNumFunctEval() {
    return maxFunEvals;
  }

  public int getCompFunctEval() {
    return (logInterval * numIntervals) + numFunEvals;
  }

  public void initialize() {
    ecResult = new ECResult();
    numFunEvals = 0;
    lastPopFunEvals = 0;
    if (frame != null) {
      frame.clearPoints();
    }
  }

  public void endOptimization() {
    threadTerminator.killThread = true;
    if (logfile != null) {
      logfile.close();
    }
    if (outfile != null) {
      outfile.close();
    }
    if(convergenceFile != null) {
      convergenceFile.close();
    }
  }
  /*
  //This function is no longer needed, window can't be closed, event never fires.
  public void propertyChange(PropertyChangeEvent event) {
  if(event.getPropertyName().equals("WindowClosed")) {
  endOptimization();
  }
  }
   */

  public JFrame getFrame() {
    return frame.newResultsFrame;
  }

  public ECResult getLastResult() {
    return ecResult;
  }

  public void setOptPanel(OptimizationPanel tempOptPanel) {
    optPanel = tempOptPanel;
  }
}
