//package edu.auburn.eng.aci.xtoolss;

   import java.awt.*;
   import java.awt.event.*;
   import javax.swing.*;
   import java.io.*;
   import javax.swing.border.TitledBorder;
   import javax.swing.border.EtchedBorder;
   import java.util.List;
   import java.util.Vector;
   import java.util.Arrays;
   import java.util.Map;
   import java.util.StringTokenizer;
   import java.lang.reflect.Array;
   import java.awt.datatransfer.DataFlavor;
   import java.awt.datatransfer.Transferable;
   import java.awt.dnd.DnDConstants;
   import java.awt.dnd.DropTarget;
   import java.awt.dnd.DropTargetDragEvent;
   import java.awt.dnd.DropTargetDropEvent;
   import java.awt.dnd.DropTargetEvent;
   import java.awt.dnd.DropTargetListener;
   
   import javax.swing.event.*;
   import javax.swing.text.*; 
   
   import edu.auburn.eng.aci.genevot.Chromosome;
	
    class AppFile
   {
      private String filePath, fileName, directory;
      private Vector modules;//for object
      private Vector genVec, aliases, constants;//from file
      float[] upperBounds, lowerBounds;
      Object[] genArray;
      
       public AppFile(){//new app
         init("");
      }
      
       public AppFile(String file){//existing app
         init(file);  
      }
   
       private void init(String file){
         filePath = file;
         if(!file.equals(""))
            setFileName();
         else
            fileName = "";
            
         modules = new Vector();
         aliases = new Vector();
         constants = new Vector();
         genVec = new Vector();
         genArray = genVec.toArray();
         float[] temp = {0};  
         upperBounds = temp;
         lowerBounds = temp;
      }
      
       public void setFilePath(String path){
         filePath = path;
         setFileName();
      }
   
       public String getFilePath(){
         return filePath;
      }
   
       private void setFileName(){
         if(!filePath.equals("")){
            try {
               File xtsFile = new File(filePath);
               fileName = xtsFile.getName();
               directory = xtsFile.getParent();
            }  
                catch (Exception e) {  
                  System.out.println("Error reading file " + filePath); 
                  e.printStackTrace();
                  fileName = "";
               }
         	
            if(fileName.length()-4 >= 0){
               //fileName = fileName.substring(0, fileName.length()-4);
            }
         }
      }
    
       public String getFileName(){      
         return fileName;
      }
   
       public void setModuleArray(Vector mod){
         modules = mod;
      }
   
       public void setGeneticRep(Vector gen){
         genArray = gen.toArray();
      }
      
       public void setConstantVector(Vector con){
         constants = con;
      }
      
       public void setAliasVector(Vector al){
         aliases = al;
      }
   
       public void setUpperBounds(Vector ups){
         float[] upperVals = new float[genArray.length];
         float temp;
         Float converter = new Float(1);
         String num;
         int index;
         for(int i = 0; i < genArray.length; i++){
            index = ((Integer)genArray[i]).intValue();
            num = (String)ups.get(index);
            try {
               temp = converter.parseFloat(num);
            }
                catch (NumberFormatException nfe) {
                  System.out.println("Error Reading Upper Ranges");
                  System.out.println(nfe);
                  temp = 0;
               }
            upperVals[i] = temp;	
         }
         upperBounds = upperVals;
      }
      
       public void setLowerBounds(Vector downs){
         float[] lowerVals = new float[genArray.length];
         float temp;
         Float converter = new Float(1);
         String num;
         int index;
         for(int i = 0; i < genArray.length; i++){
            index = ((Integer)genArray[i]).intValue();
            num = (String)downs.get(index);
            try {
               temp = converter.parseFloat(num);
            }
                catch (NumberFormatException nfe) {
                  System.out.println("Error Reading Lower Ranges");
                  System.out.println(nfe);
                  temp = 0;
               }
            lowerVals[i] = temp;	
         }
         lowerBounds = lowerVals;
       
      }
      
       public int getNumberGenes(){
         return genArray.length;
      }
   
       public float[] getUpperBounds(){
         return upperBounds;
      }
      
       public float[] getLowerBounds(){
         return lowerBounds;
      }
      
       public double[] runWith(Chromosome chromosome){
		 Vector<Double> fitVec = new Vector<Double>();
      	
         if(modules.size() > 0){
            Module2 theMod = (Module2)modules.get(0);
         
            File xtsF = new File(theMod.getFilePath());
            directory = xtsF.getParent();
                        
            try {
               FileWriter fw = new FileWriter(directory+ File.separator +theMod.getInputFile());
               for(int i = 0; i < chromosome.getSize(); i++){
                  fw.write(chromosome.getGene(i)+"\r\n");
               }
               fw.close();
            }
                  
                catch (IOException e) {  
               
               }
         
         
	         //EXECUTE CODE FILE
			try {
				if(theMod.getCodeFileType().equalsIgnoreCase("java")) {
					Runtime.getRuntime().exec("java -classpath .;\"" + System.getenv("CLASSPATH") + "\" " + theMod.getCodeFile(), null, new File(directory)).waitFor();
				}
				else if(theMod.getCodeFileType().equalsIgnoreCase("matlab")) {
					ProcessBuilder pb = new ProcessBuilder(directory + File.separator + "matlab -nodisplay < " + theMod.getCodeFile());
					pb.directory(new File(directory));
					pb.start().waitFor();
				}
				else {
					ProcessBuilder pb = new ProcessBuilder(directory + File.separator + theMod.getCodeFile());
					pb.directory(new File(directory));
					pb.start().waitFor();
				}
			}
			catch(Exception e) {
				System.out.println(e);
			}
			
			 
			 
	         //READ FROM OUTPUT FILE
			String line;
			double fitness;
			StringTokenizer tok;
			String s;
			try {
				FileReader fr = new FileReader(directory+ File.separator +theMod.getOutputFile()); 
				BufferedReader br = new BufferedReader(fr);
				line = br.readLine();
				while(line != null) {
					tok = new StringTokenizer(line);
					while(tok.hasMoreTokens()) {
						s = tok.nextToken();
						try {
							fitness = Double.parseDouble(s);
							fitVec.add(new Double(fitness));
						}	
						catch (NumberFormatException nfe) {
							System.out.println("Error Reading Fitness Value: " + s);
						}
					}	
					line = br.readLine();
				}
			}
			catch (IOException e) {  
				System.out.println("Error Reading from Output File");
			}
        }
		double[] fitArray = new double[fitVec.size()];
		for(int i = 0; i < fitArray.length; i++) {
			fitArray[i] = fitVec.elementAt(i);
		}
        return fitArray;
      }
	  
	
	class ProcessStreamReader implements Runnable {
     private InputStream is;

     public ProcessStreamReader(InputStream is) {
          this.is = is;
     }

     
     public void run() {
          try {
               BufferedReader in = new BufferedReader(new InputStreamReader(is));
               String temp = null;
               while ((temp = in.readLine()) != null) {
                    System.out.println(temp);
               }
               is.close();
          }
          catch (Exception e) {
               e.printStackTrace();
          }
     }
}
	
   }
   
   

   