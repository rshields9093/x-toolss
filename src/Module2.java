//package edu.auburn.eng.aci.xtoolss;

   import java.awt.*;
   import java.awt.event.*;
   import javax.swing.*;
   import java.io.*;
   import javax.swing.border.TitledBorder;
   import javax.swing.border.EtchedBorder;
   import java.util.List;
   import java.util.Vector;
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
	
	//**********************************************************************
   //
   // MODULE OBJECT
   //
   //**********************************************************************
    class Module2
   {
      static final int maxVarLength = 25;
      static final String comment = "#";
   
      private String filePath, fileName, infile, codefile, codefileType, outfile;
      private Vector inputVars, outputVars, inputVarTypes;
      public Vector upBounds, lowBounds, inputVarValues;
      
      private boolean isSet[];
     
      private int realMaxVarLength = maxVarLength+1;
   
       public Module2(){//new module
         init("");
      }
      
       public Module2(String file){//existing module
         init(file);  
      }
   
       public void reset(){
         filePath = ""; 
         fileName = ""; 
         infile = ""; 
         codefile = ""; 
         codefileType = ""; 
         outfile = "";
         inputVars.clear(); 
         outputVars.clear(); 
         inputVarValues.clear(); 
         inputVarTypes.clear();
         upBounds.clear(); 
         lowBounds.clear();
      }
   	
       private void init(String file){
         filePath = file;
         if(!file.equals(""))
            setFileName();
         else
            fileName = "";
         infile = "";
         outfile = "";
         inputVars = new Vector();
         outputVars = new Vector();
         inputVarValues = new Vector();
         inputVarTypes = new Vector();
         codefile = "";
         codefileType = "";
         upBounds = new Vector(); 
         lowBounds = new Vector();
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
    
       public boolean addInputVariable(String variableName, String lowerBound, String upperBound, String type){
         boolean isAdded = false;
         
         variableName = variableName.trim();
         String range = "["+lowerBound+".."+upperBound+"]";
         upBounds.add(upperBound);
         lowBounds.add(lowerBound);
         if(isVar(variableName)){// && isRange(range)
                  	
            Variable tmp = new Variable();
            tmp.setType(1); //1 indicates an input variable
            tmp.setLowerRange(lowerBound);
            tmp.setUpperRange(upperBound);
            tmp.setValueType(type);
            
            int spacesToAdd = Module2.maxVarLength+1 - variableName.length();
            //add remaining spaces to get to realMaxVarLength
            for(int f = 0; f < spacesToAdd; f++){
               variableName = variableName+" ";
            }
            tmp.setDisplayName(variableName);
            inputVars.add(tmp.getDisplayName());
            inputVarValues.add("["+tmp.getLowerRange()+".."+tmp.getUpperRange()+"]");
            inputVarTypes.add(tmp.getValueType());
            isAdded = true;
         }
         return isAdded;
      }
   
       public boolean addOutputVariable(String variableName){
         boolean isAdded = false;
         variableName = variableName.trim();
      
         Variable tmp = new Variable();
         tmp.setType(0); //0 indicates an output variable
            
         if(isVar(variableName)){
            int spacesToAdd = Module2.maxVarLength+1 - variableName.length();
            //add remaining spaces to get to realMaxVarLength
            for(int f = 0; f < spacesToAdd; f++){
               variableName = variableName+" ";
            }
            tmp.setDisplayName(variableName);
            outputVars.add(variableName);
            isAdded = true;
         }
         return isAdded;
      }
   
       //public boolean deleteVariable(String variableName){ 
       
       public void setInputFile(String set){
         infile = set;
      }
      	
       public String getInputFile(){
         return infile;
      }
   	
       public Vector getInputVariables(){
         return inputVars;
      }
      
       public Vector getInputVariablesRange(){
         return inputVarValues;
      }
      
       public Vector getOutputVariables(){
         return outputVars;
      }
     
       public Vector getAllVariables(){
         Vector allVars = new Vector();
      
         String adder;
      //add input variables
         for(int i = 0; i < inputVars.size(); i++){
            adder= (inputVars.get(i)).toString()+(inputVarValues.get(i)).toString()
               +" "+(inputVarTypes.get(i)).toString();
            allVars.add(adder);
         }
      //add output Variables
         for(int i = 0; i < outputVars.size(); i++){
            adder= (outputVars.get(i)).toString();
            allVars.add(adder);
         }
      
         return allVars;
      }
   	 	
       public void setCodeFile(String set){
         codefile = set;
      }
   	
       public String getCodeFile(){
         return codefile;
      }
      
       public void setCodeFileType(String set){
         codefileType = set;
      }
   	
       public String getCodeFileType(){
         return codefileType;
      }
      
       public void setOutputFile(String set){
         outfile = set;
      }
   	
       public String getOutputFile(){
         return outfile;
      }
	  
	  public Vector getInputVariableTypes() {
		return inputVarTypes;
	  }
      
       public int getMaxVariableLength(){
         return realMaxVarLength;
      }
      
       // public boolean hasAllFields(){
        	
       public String processFile(){
         
         String errorReport = "", totalError = "", token;
         String state = "", state2 = "", prevState="", nextState="";
         boolean set[] = {false, false, false, false, false};
         isSet = set;
      	// [inputVars, outputvars, codefile, infile, outfile]
         setFileName();
         
         int lineCount = 0; //current line number
         int tokenCount = 0;
         String line = "", prevLine="", word = ""; 
         StringTokenizer st;
         
         if(!filePath.equals("")){
            try { 
               FileReader fr = new FileReader(filePath); 
               BufferedReader br = new BufferedReader(fr); 
            
            //process each line and set variables
               while ((line = br.readLine()) != null) { 
                  lineCount++;
                  tokenCount = 0;
                  line = line.trim();
                  if(line.equals("") || line.startsWith(comment)){//ignores commented / empty lines
                     continue;
                  }
                  
                  st = new StringTokenizer(line, " :;,", true);
                  
                  while(st.hasMoreTokens()){
                     token = (st.nextToken()).trim();
                     
                     if(token.equals("")){ //ignore spaces
                        prevLine = line;
                        continue;
                     }
                     
                     if(token.charAt(0) == '#'){ //comments
                        prevLine = line;
                        break;
                     }
                     tokenCount++;
                     
                     if(!state.equals("") && !state.endsWith(":")){//check for semicolon at end of state declaration
                        if(!token.equals(":")){
                           if(tokenCount > 1){
                              errorReport = "     -ERROR (missing :) line " + lineCount + ": \n          " + line;
                              break;
                           }
                           else{
                              errorReport = "     -ERROR (missing :) line " + (lineCount-1) + ": \n          " + prevLine;
                              break;
                           }
                        }
                        else{
                           state = state+":";
                           prevLine = line;
                           continue;
                        }
                     }
                     //System.out.println(state + tokenCount);
                     if(state != "")
                        prevState = state;
                  
                     //check for state changes
                     nextState = stateCheck(token);
                     
                     if (state.equals("") && nextState.equals("")){//check for no state declared
                        errorReport = "     -ERROR (syntax) line " + lineCount + ": \n          " + line;
                        prevLine = line;
                        break;
                     }
                     
                     if(!prevState.equals(nextState+":") && !nextState.equals("")){//check for state changes
                        if (!state2.equals("")){//make sure last state is complete
                           if(tokenCount > 1){
                              errorReport = "     -ERROR (syntax) line " + lineCount + ": \n          " + line;
                              prevLine = line;
                              break;
                           }
                           else{
                              errorReport = "     -ERROR (syntax) line " + (lineCount-1) + ": \n          " + prevLine;
                              prevLine = line;
                              break;
                           }
                        }
                        state = nextState;
                        prevLine = line;
                        continue;
                     }
                     else{
                     	//do process for each state type
                        if(state.equals("")){
                           errorReport = "     -ERROR (missing statement) line " + lineCount + ": \n          " + line;
                        }
                        state2 = nextState(state, state2, token);
                       
                        if(state2.equals("error")){
                           errorReport = "     -ERROR (syntax) line " + lineCount + ": \n          " + line;
                           prevLine = line;
                           break;
                        }
                        if(state2.equals("varError")){
                           errorReport = "     -ERROR (variable) line " + lineCount + ": \n          " + line;
                           prevLine = line;
                           break;
                        }
                     }
                     prevLine = line;
                  }
                  
                  if(!errorReport.equals("")){
                     break;
                  }
               } 
            }
             
                catch (IOException e) {  
                  errorReport += "     -Error opening file.";
               }
         }
         else{
            System.out.println("Error: Can't check syntax before file fileName has been given.");
         }
      	
         if(!errorReport.equals("")){
            totalError = filePath+"\n"+ errorReport;
            errorReport = totalError;
         }
         //check that all vectors contain values  infile, codefile, codefileType, outfile;
         else {
         
            if(inputVars.isEmpty() && outputVars.isEmpty()){
               errorReport += "-     ERROR: no variables declared"+"\n";
            }
            else if(inputVarValues.isEmpty() || inputVarValues.size() != inputVars.size()){
               errorReport += "-     ERROR: missing input variable value(s)"+"\n";
            }
            else if(inputVarTypes.isEmpty() || inputVarTypes.size() != inputVars.size()){
               errorReport += "-     ERROR: missing input variable type(s)"+"\n";
            }
            else if(infile.equals("")){
               errorReport += "-     ERROR: no input file declared"+"\n";
            }
            else if(codefile.equals("")){
               errorReport += "-     ERROR: no code file declared"+"\n";
            }
            else if(codefileType.equals("")){
               errorReport += "-     ERROR: code file type not declared"+"\n";
            }
            else if(outfile.equals("")){
               errorReport += "-     ERROR: no output file declared"+"\n";
            }
         }
         
         if(errorReport.equals("") && !state2.equals("")){
            errorReport = "     -ERROR (missing ;) line " + lineCount + ": \n          " + prevLine;
         }
         
      	//TESTING AREA
      	//Vector inputVars, outputVars, inputVarValues, inputVarTypes;
      	
      // 	System.out.println("inputVars");
      // 	for(int i = 0; i<inputVars.size(); i++ ){
      // 	System.out.println("     "+inputVars.get(i)+"||");
      // 	}
      // 	System.out.println("outputVars");
      // 	for(int i = 0; i<outputVars.size(); i++ ){
      // 	System.out.println("     "+outputVars.get(i)+"||");
      // 	}
      // 	System.out.println("inputVarValues");
      // 	for(int i = 0; i<inputVarValues.size(); i++ ){
      // 	System.out.println("     "+inputVarValues.get(i)+"||");
      // 	}
      // 	System.out.println("inputVarTypes");
      // 	for(int i = 0; i<inputVarTypes.size(); i++ ){
      // 	System.out.println("     "+inputVarTypes.get(i)+"||");
      // 	}
      	
      	//END TESTING AREA
         if(errorReport.equals("")){
            createArrays();
         }
      	
         return errorReport;
      }
      
       public String processFile(String file){
         filePath = file;
         return processFile();
      }
      
       // public boolean writeToFile(String path){// setFileName();
   
       private String stateCheck(String token){
         String state = "";
         if((token.toLowerCase()).equals("input")){
            state = "input";
         }
         else if((token.toLowerCase()).equals("inputfile")){
            state = "inputfile";
         }
         else if((token.toLowerCase()).equals("codefile")){
            state = "codefile";
         }
         else if((token.toLowerCase()).equals("output")){
            state = "output";
         }
         else if((token.toLowerCase()).equals("outputfile")){
            state = "outputfile";
         }
         
         return state;
      }
      
       private String nextState(String mainState, String minState, String token){//check states and adds all variables / files to module
         String nextState = "error";// return error or varError
      	
         if(mainState.equals("input:")){//input statement
            
            if(minState.equals("") && token.equals("define")){//check for define statement
               nextState = "inState2";
            }
            else if(minState.equals("inState2")){//check for variable
               if(isVar(token)){
                  int spacesToAdd = realMaxVarLength - token.length();
                 //add remaining spaces to get to realMaxVarLength
                  for(int f = 0; f < spacesToAdd; f++){
                     token = token+" ";
                  }
                  inputVars.add(token);  
                  nextState = "inState3";
               }
               else
                  nextState = "varError";
            }
            else if(minState.equals("inState3") && token.equals(":")){//check for colon
               nextState = "inState4";
            }
            else if(minState.equals("inState4")){//check for [&] && isRange(token)
               nextState = "inState5";
               inputVarValues.add(token);
            }
            else if(minState.equals("inState5") && token.equals(":")){//check for colon
               nextState = "inState6";
            }
            else if(minState.equals("inState6")){//check for type
               nextState = "inState7";
               inputVarTypes.add(token);
            }
            else if(minState.equals("inState7") && token.equals(";")){//check for ;
               nextState = "";
            }
         }
         
         else if(mainState.equals("inputfile:")){
            if(minState.equals("")){// && token.endsWith(".input")){//check for .input
               infile = token;
               nextState = "ifState2";
            }
            else if(minState.equals("ifState2") && token.equals(";")){//check for ;
               nextState = "";
            }
         }
         else if(mainState.equals("codefile:")){
            if(minState.equals("")){//check for codefile
               codefile = token;
               nextState = "coState2";
            }
            else if(minState.equals("coState2") && token.equals(":")){//check for :
               nextState = "coState3";
            }
            else if(minState.equals("coState3")){//check for type
               codefileType = token;
               nextState = "coState4";
            }
            else if(minState.equals("coState4") && token.equals(";")){//check for ;
               nextState = "";
            }
         }
         else if(mainState.equals("output:")){
            if(minState.equals("") && token.equals("define")){//check for define
               nextState = "outState2";
            }
            else if(minState.equals("outState2")){//check for variables
               if(isVar(token)){
                  int spacesToAdd = realMaxVarLength - token.length();
                 //add remaining spaces to get to realMaxVarLength
                  for(int f = 0; f < spacesToAdd; f++){
                     token = token+" ";
                  }
                  outputVars.add(token);
                  nextState = "outState3";
               }
               else
                  nextState = "varError";
            }
            else if(minState.equals("outState3") && token.equals(",")){//check for ,
               nextState = "outState2";
            }
            else if(minState.equals("outState3") && token.equals(";")){//check for ;
               nextState = "";
            }
         }
         else if(mainState.equals("outputfile:")){
            if(minState.equals("")){// && token.endsWith(".output")){//check for .output
               outfile = token;
               nextState = "ofState2";
            }
            else if(minState.equals("ofState2") && token.equals(";")){//check for ;
               nextState = "";
            }
         }
         
         return nextState;
      }
      
       public static boolean isVar(String var){// determines whether this is a valid variable fileName 
         boolean goodVar = true;
         int length = var.length();
         char c = 'a';
         if(length > 25)
            goodVar = false;
         if(goodVar){
            var = var.toLowerCase();
            for(int i = 0; i < length; i++){
               c = var.charAt(i);
               if(c != '_'&& c != '0'&& c != '1'&& c != '2'&& c != '3'&& c != '4'&& c != '5'&& c != '6'&& c != '7'&& c != '8'&& 
               c != '9'&& c != 'a'&& c != 'b'&& c != 'c'&& c != 'd'&& c != 'e'&& c != 'f'&& c != 'g'&& c != 'h'&& c != 'i'&&
               c != 'j'&& c != 'k'&& c != 'l'&& c != 'm'&& c != 'n'&& c != 'o'&& c != 'p'&& c != 'q'&& c != 'r'&& c != 's'&& 
               c != 't'&& c != 'u'&& c != 'v'&& c != 'w'&& c != 'x'&& c != 'y'&& c != 'z' && c != '[' && c != ']'){ 
                  goodVar = false;}
            }
         }
         return goodVar;
      }
    
       // public static boolean isRange(String range){ 
       // public static boolean isInputFile(String str){//    
       // public static boolean isOutputFile(String str){
       private boolean createArrays(){
         Integer converter = new Integer(0);
         String temp, name;
         int size, spacesToAdd;
         String varName, orig, range, type;
      //inputVars
      //inputVarValues
      int limit = inputVarTypes.size();
         for(int i = 0; i < limit; i++){
            temp = (String)inputVarTypes.get(i);
            if(temp.indexOf('[') > 1 && temp.indexOf(']') > 1 && (temp.indexOf('[') < temp.indexOf(']'))){
               temp = temp.substring(temp.indexOf('[')+1, temp.indexOf(']'));
               try {
                  size = converter.parseInt(temp);
                 
                  orig = (String)inputVars.remove(i);
                  range = (String)inputVarValues.remove(i);
                  type = (String)inputVarTypes.remove(i);
                  
                  for(int f = 0; f < size; f++){
                     varName = orig.trim();
                     varName = varName+"["+f+"]";
                     spacesToAdd = realMaxVarLength - varName.length();
                  //add remaining spaces to get to realMaxVarLength
                     for(int h = 0; h < spacesToAdd; h++){
                        varName = varName+" ";
                     }
                     inputVars.add(i+f, varName);
                     inputVarValues.add(i+f, range);
                     inputVarTypes.add(i+f, type);
                  }
                  limit = inputVarTypes.size();
               	i=i+size-1;
               }
                   catch (NumberFormatException nfe) {
                     System.out.println("Error Reading Array Size: "+temp);
                     size = 0;
                  }
            }
         }
         
         return true;
      }
   }   
	//**********************************************************************
   //
   // VARIABLE OBJECT
   //
   //**********************************************************************
    class Variable{
      private String displayName;
      private String value;
      private boolean isArray, isAlias, isConstant;
      private String constantValue;
      private int aliasMod, aliasVar;
      private int arrayLoc;
      private int type; // 1 input, 0 output, -1 not set
   //input vars only
      private String lowerRange, upperRange, valueType;
   
   //**************************************************
   // initialization methods
   //**************************************************
       public Variable(){//new module
         init("");
      }
      
       public Variable(String name){//existing module
         init(name);  
      }
      
       public void init(String name){
         displayName = name;
         value = "";
         type = -1;
         
         lowerRange = "";
         upperRange = "";
         valueType = "";  
      	
         isArray = false;
         arrayLoc = 0;
      	
         isAlias = false;
         aliasMod = 0; 
         aliasVar = 0;
         
         isConstant = false;
         constantValue = "";
      }
   
   //**************************************************
   // set methods
   //**************************************************
       public void setDisplayName(String name){
         displayName = name;
      }  
      
       public void setValue(String val){
         value = val;
      }
      
       public boolean setType(int t){
         boolean isSet = false;
         if(t == 0 || t == 1){
            type = t;
            isSet = true;
         }
         return isSet;
      }
   
       public void setLowerRange(String lowRange){
         lowerRange = lowRange;
      }
      
       public void setUpperRange(String upRange){
         upperRange = upRange;
      }
         
       public void setValueType(String t){
         valueType = t;
      }
   
       public void setArrayLoc(int arrLoc, boolean isInArray){
         arrayLoc = arrLoc;
         isArray = isInArray;
      }
   
       public void setConstant(String con){
         constantValue = con;
         isConstant = true;
      }
      
       public void setConstant(boolean is){
         isConstant = is;
      }
      
       public void setAlias(int modNum, int varNum){
         aliasMod = modNum;
         aliasVar = varNum;
         isAlias = true;
      }
             
       public void setAlias(boolean is){
         isAlias = is;
      }
   
   //**************************************************
   // get methods
   //**************************************************
       public String getName(){
         return displayName.trim();
      }
      
       public String getDisplayName(){
         return displayName;
      }  
     
       public String getValue(){
         return value;
      }
      
       public int getType(){
         return type;
      }
   
       public String getLowerRange(){
         return lowerRange;
      }
      
       public String getUpperRange(){
         return upperRange;
      }
         
       public String getValueType(){
         return valueType;
      }
   
       public int getArrayLoc(){
         return arrayLoc;
      }
   
       public String getConstant(){
         return constantValue;
      }
      
       public boolean getIsConstant(){
         return isConstant;
      }
      
       public int getAliasMod(){
         return aliasMod;
      }
        
       public int getAliasVar(){
         return aliasVar;
      }
               
       public boolean getIsAlias(){
         return isAlias;
      }
    
   }
/*	
   //**********************************************************************
   //
   // FILE FILTER SUPPORT (menu 1 "browse")
   //
   //**********************************************************************
    class MyFilter extends javax.swing.filechooser.FileFilter { 
      String allowedFileType = ".*"; 
       
       public MyFilter(String fileType){
         allowedFileType = fileType;
      }
       public boolean accept(File f) 
      { 
         if (f.isDirectory()) { 
            return true; } 
            
         String fileName = ((f.getName()).trim()).toLowerCase(); 
         boolean isGoodFile = false;
            
         if(fileName.endsWith(allowedFileType)){
            isGoodFile = true;
         }
         return isGoodFile; 
      } 
       public String getDescription() 
      { 
         return ("*"+allowedFileType); 
      } 
   }
   */