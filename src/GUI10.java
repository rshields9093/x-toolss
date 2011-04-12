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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import lib.genevot.*;
   
    
    public class GUI10 extends JFrame implements ActionListener, DropTargetListener
   {
      
    	ThreadTerminator tt;
    	Population population;
    	ParticleSwarmOptimization pso;
        XTOOLSECMonitor xtoolsECMon;
        OptimizationsFrame optFrame;
        OptimizationPanel tempOptPanel;
        XTOOLSRunnable ecThread;
        boolean hasStarted = false;
        
        //THESE NEED TO BE MODIFIED TO IMPLEMEMENT GA  
        String[] nameOfGA = {"Generational GA with BLX", "Steady-state GA", "Steady-state GA with BLX", 
      		  "Steady-generational GA with BLX", "PSO", "Generational DEA", "Steady-state DEA", 
      		  "Elitist EDA", "Standard EP", "Continuous Standard EP", "Meta-EP", "Continuous Meta-EP"};
        Integer popSize, numEval, numElites, tournSize, neighborhoodSize, constCoeff, numRuns, numThreads, logInterval;
        Float crossoverUsageRate, blxAlpha, mutUsageRate, mutRate, mutRange, phi;
        String logFileName;
  	    String memespaceIP;
  	    Integer memespacePort;
  	    Float migrationRate;
  	    String useOneFifthRule;
        //*******************************************
      
        public AppFile application;
      
        String ErrorMsg = "";
        String allowedFileType = ".xts";
        String title = "X-TOOLSS Application Builder";
     
        int fileCount, maxVarLength, moduleLocations[];
        int[] avAliasLoc;
     	
        Vector xlsFiles, xtsNames, modules, variables, variableNames, geneticVars, geneticLoc, avAlias, varType;
        Vector isConstant, isAlias, originalValues, actualLoc, modNum;
        Font font = new Font("f", Font.BOLD, 12);
        Font setwfont = new Font( "Monospaced", Font.PLAIN, 12 ); 
     	
        private JFrame pg1, error, pg3, currentFrame;
        
        ImageIcon nextIcon = null;//new ImageIcon(Toolkit.getDefaultToolkit().getImage(java.net.URLClassLoader.getSystemResource("next.gif")));
        ImageIcon backIcon = null;//new ImageIcon(Toolkit.getDefaultToolkit().getImage(java.net.URLClassLoader.getSystemResource("back.gif")));
     
        Image minIcon = null;//Toolkit.getDefaultToolkit().getImage(java.net.URLClassLoader.getSystemResource("CEV1.jpg"));
        Image errIcon = null;//Toolkit.getDefaultToolkit().getImage(java.net.URLClassLoader.getSystemResource("err1.jpg"));
        	
        EtchedBorder b1;
        boolean infoLoss;   
        
        private JFileChooser fc;
        MyFilter filter;
        
     	
        //----------------------------------------------------------------------
        // Menu # 1
        //----------------------------------------------------------------------
        private JPanel pg1BG;
        private JPanel pg1N, pg1S, pg1W, pg1E, pg1NE, pg1SE, pg1ModCase;
        private JButton pg1Browse, pg1Add, pg1Delete, pg1MoveUp, pg1MoveDown; 
        private JButton pg1Back, pg1Next;
        private JTextField pg1AddFile;
        private JList pg1Mod;
        DropTarget dt;
        private String xtsDir, workingDir;
        private String fileName;
        private boolean isAdd = true;
        //----------------------------------------------------------------------
        // Menu # 2
        //----------------------------------------------------------------------   	
        private JPanel pg2BG;
        private JPanel pg2top, pg2bottom, pg2S, pg2VarPan, pg2AliasPan,
        	pg2varleft, pg2varright, pg2FieldCase, pg2Sg, pg2ButtonCase, pg2DeCase, pg2ConVal;
        private JButton makeConstant,restoreValue, createAlias, pg2Back, pg2Next;
        private JList pg2VarTx, pg2AliasTx;
        private JTextField constantValue, genetics;
        private JLabel pg2InLabel, pg2DeLabel;
       
        //----------------------------------------------------------------------
        // Menu # 3
        //----------------------------------------------------------------------   	
        private JPanel pg3BG;
        private JPanel pg3S, pg3top, pg3bottom, comboHolder, gaVarHolder, envHolder, gaEx;
        private JButton pg3Back, pg3Next, varChangeB;
        private Vector gaSelections, gaVars;
        private JComboBox gaSelect;
        private JTextField envVarChange;
        private JList pg3Var;
        private JTextArea exeText; 
       
        JScrollPane scrollPane, scrollvar,  deVar, errScroll, pg3scrollPane, exeSCPane;
        
        //----------------------------------------------------------------------
        // Error Frame
        //---------------------------------------------------------------------- 
        private JTextArea errTx;
        private JPanel errorPan;
     
        //----------------------------------------------------------------------
        // Menu Bar
        //----------------------------------------------------------------------
        JMenuBar menuBar; 
        JMenu fileMenu;
        JMenu helpMenu;
        JMenuItem indexMenuItem, aboutMenuItem;
        JMenuItem newFile, open, exitMenu;
        
        
      public GUI10(OptimizationsFrame optimizationsFrame) {
    	 optFrame = optimizationsFrame;
         init();
      }
      
      public void setVisible(boolean visible){
    	  pg1.setVisible(visible);
      }
   
      //**********************************************************************
      //
      // START GUI
      //
      //**********************************************************************   
   	
       private void init(){ 
         application = new AppFile();
      	
      	// variables
         xlsFiles = new Vector(); // holds all module filepaths
         xtsNames = new Vector(); // holds all module filenames
         modules = new Vector(); //holds all module objects
         variableNames = new Vector();
         variables = new Vector(); // holds all variables for variable list
         originalValues = new Vector(); // holds all original range values for variables
      
         varType = new Vector();
         avAlias = new Vector();
         geneticVars = new Vector();
         geneticLoc = new Vector();
         avAliasLoc = new int[0];
         actualLoc = new Vector();
         modNum = new Vector();
      	
         isConstant = new Vector();
         isAlias = new Vector(); // - if not, varName if is
      		
         Module2 set = new Module2();
         maxVarLength = set.getMaxVariableLength();
      		
         fileCount = 0;  
         infoLoss = false;
         
      	//Parameters from GA
         popSize  = new Integer(20);
         numEval  = new Integer(500);
         numElites = new Integer(1);
         tournSize = new Integer(2);
         neighborhoodSize = new Integer(3);
         constCoeff = new Integer(1);
         numRuns = new Integer(1);
         numThreads = new Integer(1);
         numThreads = 1;
         
         crossoverUsageRate = new Float(1.0);
         blxAlpha = new Float(0.25);
         mutUsageRate = new Float(1.0);
         mutRate  = new Float(1.0);
         mutRange = new Float(0.2);
		 useOneFifthRule = "NO";
         phi = new Float(0.2);
         logFileName = "xtoolss";
         logInterval = new Integer(1);
		 memespaceIP = "NONE";
		 memespacePort = new Integer(-1);
		 migrationRate = new Float(0.0);
       
      	
      	// setup file chooser
         fileName = "";
         fc = new JFileChooser();
         fc.addChoosableFileFilter(new MyFilter(".app"));
         fc.setAcceptAllFileFilterUsed(false);
         
      	//setup menu bar
         
      	
      	//Frame initialization
/*         try {
            UIManager.setLookAndFeel(
               UIManager.getSystemLookAndFeelClassName());
         }
             catch(Exception ex) {
               System.out.println("Unable to load native look and feel");
            }
      
         JFrame.setDefaultLookAndFeelDecorated(true); //false
*/         
		pg1 = new JFrame(title);
		currentFrame = pg1;
         pg3 = new JFrame("Module Execution");
         
         pg1.setResizable(false);
//         pg1.setIconImage(minIcon);
         pg3.setResizable(false);
//         pg3.setIconImage(minIcon);
      	
         b1 = new EtchedBorder(pg1.getBackground(), pg1.getBackground());
         
         initMenu1();
         initMenu2();
         initMenu3();
      			
         //pg1.setJMenuBar(menuBar);						
         setupMenu1();
         setupMenu2();
         setupMenu3();
      	
         setupDialogs();
      	
         pg1.pack();
         pg3.pack();
        
         pg1.setLocationRelativeTo(null);
         error.setLocationRelativeTo(null);
        
         pg1.setVisible(false);
         pg3.setVisible(false);
      }
       
   	  //**********************************************************************
      //
      // COMPONENT INITIALIZATION
      //
      //**********************************************************************
       private void initMenu1(){
       
       //----------------------------------------------------------------------
       // Menu # 1
       //----------------------------------------------------------------------
         pg1.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
         pg1.addWindowListener(
                new WindowAdapter() { 
                   public void windowClosing(WindowEvent e) {
                     pg1.setVisible(false);
                  }
                   public void windowIconified(WindowEvent e) {
                     pg3.toFront();                 
                  }
                   public void windowDeiconified(WindowEvent e) {
                     pg3.toFront();                 
                  }
               });
         pg1BG = new JPanel();
         pg1BG.setLayout(new BoxLayout(pg1BG, BoxLayout.Y_AXIS));
         pg1N = new JPanel(new FlowLayout(0, 0, 0));
         pg1N.setOpaque(false);
         pg1S = new JPanel();
         pg1S.setLayout(new BoxLayout(pg1S, BoxLayout.X_AXIS));
         pg1S.setPreferredSize(new java.awt.Dimension(550, 60));
         pg1S.setOpaque(false);
         pg1W = new JPanel();
         pg1W.setLayout(new BoxLayout(pg1W, BoxLayout.Y_AXIS));
         pg1W.setPreferredSize(new java.awt.Dimension(400, 280));
         pg1W.setOpaque(false);
         pg1E = new JPanel(new FlowLayout(1, 30, 20));
         pg1E.setLayout(new BoxLayout(pg1E, BoxLayout.Y_AXIS));
         pg1E.setPreferredSize(new java.awt.Dimension(150, 280));
         pg1E.setOpaque(false);
         pg1NE = new JPanel(new FlowLayout(1, 5, 15));
         pg1NE.setOpaque(false);
      
         pg1SE = new JPanel(new FlowLayout(1, 1, 15));
         pg1SE.setOpaque(false);
      
         pg1ModCase = new JPanel();
         pg1ModCase.setLayout(new BoxLayout(pg1ModCase, BoxLayout.Y_AXIS));
         pg1ModCase.setOpaque(false);
        
         //LineBorder b1 = new LineBorder(Color.white);
      
         BorderFactory.createTitledBorder(b1, "Module",  TitledBorder.LEFT, TitledBorder.TOP, font, Color.black);
         pg1ModCase.setBorder(
              BorderFactory.createCompoundBorder(
                              BorderFactory.createTitledBorder(b1, "Module File",  TitledBorder.LEFT, TitledBorder.TOP, font, Color.black)
                              ,BorderFactory.createEmptyBorder(5,5,5,5)));
         pg1NE.setBorder(
               BorderFactory.createCompoundBorder(
                               BorderFactory.createTitledBorder(b1, "Add Module",  TitledBorder.LEFT, TitledBorder.TOP, font, Color.black)
                               ,BorderFactory.createEmptyBorder(5,5,5,5)));
         pg1SE.setBorder(
               BorderFactory.createCompoundBorder(
                               BorderFactory.createTitledBorder(b1, "Current Modules",  TitledBorder.LEFT, TitledBorder.TOP, font, Color.black)
                               ,BorderFactory.createEmptyBorder(5,5,5,5)));    
      		
      		
      	// MENU 1 BUTTONS	
         pg1Browse = new JButton("Browse");
         pg1Browse.setPreferredSize(new java.awt.Dimension(100, 25));
         pg1Browse.addActionListener(this);
         
         pg1Add = new JButton("Add");
         pg1Add.setPreferredSize(new java.awt.Dimension(100, 25));
         pg1Add.setEnabled(false);
         pg1Add.addActionListener(this);  
      	
         pg1Delete = new JButton("Delete");
         pg1Delete.setPreferredSize(new java.awt.Dimension(100, 25));
         pg1Delete.addActionListener(this);
         
         pg1MoveUp = new JButton("Move Up");
         pg1MoveUp.setPreferredSize(new java.awt.Dimension(100, 25));
         pg1MoveUp.addActionListener(this);
         
         pg1MoveDown = new JButton("Move Down");
         pg1MoveDown.setPreferredSize(new java.awt.Dimension(100, 25));
         pg1MoveDown.addActionListener(this);
         
         pg1Back = new JButton("    Back    ", backIcon); //, backIcon
         pg1Back.setEnabled(false);  
      	
         pg1Next = new JButton("    Next    ", nextIcon); //, nextIcon
         pg1Next.setVerticalTextPosition(AbstractButton.CENTER); 
         pg1Next.setHorizontalTextPosition(AbstractButton.LEADING);
         pg1Next.addActionListener(this);
       
         // MENU 1 TEXT FIELDS  
         pg1AddFile = new JTextField(12);
         pg1AddFile.setEditable(false);
         pg1AddFile.setToolTipText("Press \"Browse\" to select a file.");
         pg1Mod = new JList(xlsFiles);
         dt = new DropTarget(pg1Mod, this);
         pg1Mod.setDragEnabled(true);
         pg1Mod.setPreferredSize(new Dimension(20,20));
         scrollPane = new JScrollPane(pg1Mod,
                                       JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
         
         fc = new JFileChooser();
         filter = new MyFilter(".xts");
         fc.addChoosableFileFilter(filter);
         fc.setAcceptAllFileFilterUsed(false);									
      											
      
      }
       
       private void initMenu2(){
               	
      	//----------------------------------------------------------------------
      	// Menu # 2
      	//----------------------------------------------------------------------
         	//basic background layout
         pg2BG = new JPanel();
         pg2BG.setLayout(new BoxLayout(pg2BG, BoxLayout.Y_AXIS));
         
         pg2top = new JPanel();
         pg2top.setLayout(new BoxLayout(pg2top, BoxLayout.X_AXIS));
         pg2top.setPreferredSize(new java.awt.Dimension(600, 260));
         pg2top.setOpaque(false);						
         pg2bottom = new JPanel();
         pg2bottom.setLayout(new BoxLayout(pg2bottom, BoxLayout.X_AXIS));
         pg2bottom.setPreferredSize(new java.awt.Dimension(600, 190));
         pg2bottom.setOpaque(false);
         
         pg2VarPan = new JPanel();
         pg2VarPan.setLayout(new BoxLayout(pg2VarPan, BoxLayout.X_AXIS));
         pg2VarPan.setOpaque(false);
         pg2VarPan.setBorder(
              BorderFactory.createCompoundBorder(
                              BorderFactory.createTitledBorder(b1, "Variables",  TitledBorder.LEFT, TitledBorder.TOP, font, Color.black)
                              ,BorderFactory.createEmptyBorder(5,5,5,5)));				
         pg2AliasPan = new JPanel();
         pg2AliasPan.setLayout(new BoxLayout(pg2AliasPan, BoxLayout.X_AXIS));
         pg2AliasPan.setOpaque(false);
         pg2AliasPan.setBorder(
              BorderFactory.createCompoundBorder(
                              BorderFactory.createTitledBorder(b1, "Alias",  TitledBorder.LEFT, TitledBorder.TOP, font, Color.black)
                              ,BorderFactory.createEmptyBorder(5,5,5,5)));
            						
         pg2ConVal = new JPanel();
         //pg2ConVal.setLayout(new BoxLayout(pg2ConVal, BoxLayout.Y_AXIS));
         pg2ConVal.setOpaque(false);		
      								
      								
      	// back / next button area
         pg2S = new JPanel();
         pg2S.setLayout(new BoxLayout(pg2S, BoxLayout.X_AXIS));
         pg2S.setPreferredSize(new java.awt.Dimension(610, 60));
         pg2S.setOpaque(false);
         
         pg2Sg = new JPanel();
         pg2Sg.setPreferredSize(new java.awt.Dimension(610, 60));
         pg2Sg.setOpaque(false);
         pg2Sg.setBorder(
              BorderFactory.createCompoundBorder(
                              BorderFactory.createTitledBorder(b1, "Genetic Representation",  TitledBorder.LEFT, TitledBorder.TOP, font, Color.black)
                              ,BorderFactory.createEmptyBorder(5,5,5,5)));
      	
      	// variable section
         pg2varleft = new JPanel();
         pg2varleft.setMinimumSize(new java.awt.Dimension(296, 222));
         pg2varleft.setPreferredSize(new java.awt.Dimension(296, 222));
         pg2varleft.setMaximumSize(new java.awt.Dimension(296, 222));
         pg2varleft.setLayout(new BoxLayout(pg2varleft, BoxLayout.Y_AXIS));
         pg2varleft.setOpaque(false);
         pg2varleft.setMinimumSize(new java.awt.Dimension(300, 20));
         pg2varright = new JPanel();
         pg2varright.setLayout(new BoxLayout(pg2varright, BoxLayout.Y_AXIS));
         pg2varright.setOpaque(false);
         pg2FieldCase = new JPanel(new FlowLayout(3, 0,0));
         pg2FieldCase.setOpaque(false);
        
         pg2VarTx = new JList(variables);
         pg2VarTx.setFont(setwfont);
         pg2VarTx.addListSelectionListener(new SelectionChange());
      	
         makeConstant = new JButton("       Set Constant       ");
         makeConstant.addActionListener(this);
         restoreValue = new JButton("         Reset Value         ");
         restoreValue.addActionListener(this);
         constantValue = new JTextField(10);
         constantValue.setText("");
         
      	//alias section
      		//panels
         pg2ButtonCase = new JPanel();
         pg2ButtonCase.setLayout(new BoxLayout(pg2ButtonCase, BoxLayout.Y_AXIS));
         pg2ButtonCase.setOpaque(false);
         pg2DeCase = new JPanel();
         pg2DeCase.setLayout(new BoxLayout(pg2DeCase, BoxLayout.Y_AXIS));
      		//labels
         pg2DeLabel = new JLabel("Dependent Variable:");
         pg2InLabel = new JLabel("Independent Variable:");
      		//listboxes
         pg2AliasTx = new JList(avAlias);
         pg2AliasTx.setFont(setwfont);
      
      		//buttons
         createAlias = new JButton("     Set Alias    ");
         createAlias.addActionListener(this);
      	
      	//scroll panes
         scrollvar = new JScrollPane(pg2VarTx,
                                       JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
         deVar = new JScrollPane(pg2AliasTx,
                                       JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);  	
         genetics = new JTextField(50);
         genetics.setEditable(false);
        
         pg2Back = new JButton("    Back    ", backIcon);
         //pg2Back.addActionListener(new SwitchFrames( pg2, pg1 ));	
         pg2Back.addActionListener(this);
         pg2Next = new JButton("    Next    ", nextIcon);
         pg2Next.addActionListener(this);
      }
      
       private void initMenu3(){
         pg3.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
         pg3.addWindowListener(
                new WindowAdapter() { 
                   public void windowClosing(WindowEvent e) {
                     pg3Back.doClick();
                  }
                   public void windowIconified(WindowEvent e) {
                     pg3.setLocationRelativeTo(null);
                     pg1.setVisible(false);                 
                  }
                   public void windowDeiconified(WindowEvent e) {
                     pg3.setLocationRelativeTo(null);
                     pg1.setVisible(true);
                     pg3.toFront();               
                  }
               });
         pg3BG = new JPanel();
         pg3BG.setLayout(new BoxLayout(pg3BG, BoxLayout.Y_AXIS));
               
         pg3top = new JPanel();
         pg3top.setLayout(new BoxLayout(pg3top, BoxLayout.Y_AXIS));
         pg3top.setPreferredSize(new java.awt.Dimension(400, 50));
         pg3top.setOpaque(false);						
         pg3bottom = new JPanel();
         pg3bottom.setLayout(new BoxLayout(pg3bottom, BoxLayout.X_AXIS));
         pg3bottom.setPreferredSize(new java.awt.Dimension(400, 150));
         pg3bottom.setOpaque(false);
         
      	//MENU 3 COMBO BOX
         comboHolder = new JPanel();
         comboHolder.setOpaque(false);
         gaSelections = new Vector();
      		//add g.a.s here
         gaSelections.add("      --  Select EC  --    ");
         for(int i = 0; i < nameOfGA.length; i++) {
            gaSelections.add(nameOfGA[i]);
         }
         gaSelect = new JComboBox(gaSelections);
         gaSelect.addActionListener(this);
      	
      	//MENU 3 VAR BOX
         gaVarHolder = new JPanel();
         gaVarHolder.setLayout(new BoxLayout(gaVarHolder, BoxLayout.X_AXIS));
         comboHolder.setOpaque(false);
         gaVars = new Vector();
      
         pg3Var = new JList(new Vector());
         pg3Var.setFont(setwfont);
         pg3scrollPane = new JScrollPane(pg3Var,
                                       JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            
         //MENU 3 BUTTONS
         pg3Back = new JButton("    Edit    ", backIcon); 
         pg3Back.addActionListener(this);
         pg3Next = new JButton("    Execute", nextIcon);
         pg3Next.setVerticalTextPosition(AbstractButton.CENTER); 
         pg3Next.setHorizontalTextPosition(AbstractButton.LEADING);
         pg3Next.addActionListener(this);
         
         envHolder = new JPanel();
         envHolder.setLayout(new BoxLayout(envHolder, BoxLayout.Y_AXIS));
         envHolder.setOpaque(false);
      	
         envVarChange = new JTextField();
         varChangeB = new JButton("Set Value");
         varChangeB.addActionListener(this);
      	
         pg3S = new JPanel();
         pg3S.setLayout(new BoxLayout(pg3S, BoxLayout.X_AXIS));
         pg3S.setPreferredSize(new java.awt.Dimension(400, 60));
         pg3S.setOpaque(false);
         
         gaEx = new JPanel();
         gaEx.setLayout(new BoxLayout(gaEx, BoxLayout.X_AXIS));
         gaEx.setPreferredSize(new java.awt.Dimension(400, 200));
         gaEx.setOpaque(false);
         exeText = new JTextArea(); 
         exeText.setEditable(false);
         exeSCPane = new JScrollPane(exeText,
                                       JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      }
       
   	//**********************************************************************
      //
      // COMPONENT SETUP
      //
      //**********************************************************************    
       private void setupMenu1(){
             	
      	//----------------------------------------------------------------------
      	// Menu # 1
      	//----------------------------------------------------------------------
         pg1.getContentPane().add(pg1BG);
         pg1BG.add(Box.createRigidArea(new Dimension(10,0))); // BG Left Border
         pg1BG.add(pg1N);
         pg1BG.add(Box.createRigidArea(new Dimension(0,10)));
         pg1BG.add(pg1S);
         pg1N.add(Box.createRigidArea(new Dimension(20,0)));//far left spacer
         pg1N.add(pg1W);
         pg1N.add(Box.createRigidArea(new Dimension(20,0)));//middle spacer
         pg1N.add(pg1E);
         pg1W.add(Box.createRigidArea(new Dimension(0,20)));//top of text area
         pg1N.add(Box.createRigidArea(new Dimension(20,0)));//far right spacer
        //******** Text Area *********
         pg1W.add(pg1ModCase);
         pg1ModCase.add(scrollPane);
         //pg1W.add(Box.createRigidArea(new Dimension(0,50)));//bottom of text area
        //******** Right Side *********
         pg1E.add(Box.createRigidArea(new Dimension(0,20)));//top of button area
         pg1E.add(pg1NE);
         //pg1E.add(Box.createRigidArea(new Dimension(0,60)));//middle of button area
         //pg1E.add(pg1SE);
         //pg1E.add(Box.createRigidArea(new Dimension(0,60)));//bottom of button area
         //******** Back / Next Bar *********
         pg1S.add(pg1Back);
         pg1Back.setOpaque(false);
         pg1S.add(Box.createRigidArea(new Dimension(75,0))); // horiz button cushion
         pg1S.add(pg1Next);
         //******** Right Side Buttons *********
         pg1NE.add(Box.createRigidArea(new Dimension(20,20)));
         pg1NE.add(pg1Browse);
         //pg1NE.add(Box.createRigidArea(new Dimension(0,5)));
         pg1NE.add(pg1AddFile);
         pg1NE.add(Box.createRigidArea(new Dimension(0,5)));
         pg1NE.add(pg1Add);
         pg1NE.add(Box.createRigidArea(new Dimension(0,5)));
         // pg1SE.add(Box.createRigidArea(new Dimension(0,5)));
         // pg1NE.add(pg1Delete);
         // pg1NE.add(Box.createRigidArea(new Dimension(0,5)));
         // pg1SE.add(pg1MoveUp);
         // pg1SE.add(Box.createRigidArea(new Dimension(0,5)));
         // pg1SE.add(pg1MoveDown);
         // pg1SE.add(Box.createRigidArea(new Dimension(0,5)));
      }
      
       private void setupMenu2(){
         
      	//----------------------------------------------------------------------
      	// Menu # 2
      	//----------------------------------------------------------------------
         pg2BG.add(Box.createRigidArea(new Dimension(0,20))); //top spacer
         pg2BG.add(pg2top);
         //pg2BG.add(Box.createRigidArea(new Dimension(0,20))); //middle spacer
         //pg2BG.add(pg2bottom);
         pg2BG.add(Box.createRigidArea(new Dimension(0,10))); //bottom spacer
         pg2BG.add(pg2S);
         
         pg2top.add(Box.createRigidArea(new Dimension(20,0)));//far left spacer
         pg2top.add(pg2VarPan);
         pg2top.add(Box.createRigidArea(new Dimension(20,0)));//far right spacer
      	
         //pg2bottom.add(Box.createRigidArea(new Dimension(20,0)));//far left spacer
         //pg2bottom.add(pg2AliasPan);
         //pg2bottom.add(Box.createRigidArea(new Dimension(20,0)));//far right spacer
      	
         pg2VarPan.add(pg2varleft);
         pg2VarPan.add(Box.createRigidArea(new Dimension(30,40)));
         pg2VarPan.add(pg2varright);
         pg2VarPan.add(Box.createRigidArea(new Dimension(50,40)));//variable section - right side spacer
      	
         pg2varleft.add(scrollvar);
         pg2varright.add(Box.createRigidArea(new Dimension(10,30)));
         pg2varright.add(makeConstant);
         pg2varright.add(Box.createRigidArea(new Dimension(10,30)));
         pg2varright.add(pg2ConVal);
         //pg2ConVal.add(Box.createRigidArea(new Dimension(40,0)));
         pg2ConVal.add(constantValue);
         //pg2ConVal.add(Box.createRigidArea(new Dimension(12,0)));//
         pg2varright.add(Box.createRigidArea(new Dimension(10,50)));
         pg2varright.add(restoreValue);
         pg2varright.add(Box.createRigidArea(new Dimension(10,40)));
      	
         //pg2AliasPan.add(scrollalias);
         // alias section
         //pg2DeCase.add(pg2DeLabel);
         pg2DeCase.add(deVar);
         pg2ButtonCase.add(createAlias);
         
         // pg2AliasPan.add(pg2DeCase);
         // pg2AliasPan.add(Box.createRigidArea(new Dimension(10,0)));
      // 
         // pg2AliasPan.add(Box.createRigidArea(new Dimension(55,0)));
         // pg2AliasPan.add(pg2ButtonCase);
         // pg2AliasPan.add(Box.createRigidArea(new Dimension(70,0)));
      	//pg2FieldCase.add(constantValue);//
         pg2Sg.add(genetics);
      	//******** Back / Next Bar *********
         pg2S.add(pg2Back);
         pg2S.add(Box.createRigidArea(new Dimension(75,0))); // horiz button cushion
         pg2S.add(pg2Next);
      }
      
       private void setupMenu3(){
         pg3.getContentPane().add(pg3BG);
         pg3BG.add(pg3top);
         pg3BG.add(pg3bottom);
         pg3BG.add(pg3S);
       //  pg3BG.add(gaEx);
      	
         pg3top.add(Box.createRigidArea(new Dimension(0,10)));
         pg3top.add(comboHolder);
         comboHolder.add(gaSelect);
      	
         pg3bottom.add(Box.createRigidArea(new Dimension(15,0)));
         pg3bottom.add(gaVarHolder);
         pg3bottom.add(Box.createRigidArea(new Dimension(10,0)));
         pg3bottom.add(envHolder);
         pg3bottom.add(Box.createRigidArea(new Dimension(15,0)));
         
         envHolder.add(Box.createRigidArea(new Dimension(0,30)));
         envHolder.add(envVarChange);
         envHolder.add(Box.createRigidArea(new Dimension(0,20)));
         envHolder.add(varChangeB);
         envHolder.add(Box.createRigidArea(new Dimension(0,50)));
      
         gaVarHolder.add(pg3scrollPane);
      	
         pg3S.add(pg3Back);
         pg3S.add(Box.createRigidArea(new Dimension(60,0))); // horiz button cushion
         pg3S.add(pg3Next);
         
         gaEx.add(Box.createRigidArea(new Dimension(10,0)));
         gaEx.add(exeSCPane);
         gaEx.add(Box.createRigidArea(new Dimension(10,0)));
      }
   	
       private void setupDialogs(){
         //set up error msg************************
         error = new JFrame("Error");
         error.setIconImage(errIcon);
         error.setResizable(false);
         errorPan = new JPanel();
         errorPan.setLayout(new BoxLayout(errorPan, BoxLayout.Y_AXIS));
         errorPan.setOpaque(false);
         errTx = new JTextArea();
         errTx.setPreferredSize(new java.awt.Dimension(400, 450));
         errTx.setEditable(false);
         errScroll = new JScrollPane(errTx,
                                       JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
         
         error.getContentPane().add(errorPan);
         errorPan.add(errScroll);
         error.pack();
         error.setLocationRelativeTo(null);
      	//end err msg*****************************
      }  
   	
   	//**********************************************************************
      //
      // ACTION SUPPORT
      //
      //**********************************************************************
       public void actionPerformed(ActionEvent e) { 
         
      	//**********************************************************
      	// Page 1 buttons
      	//**********************************************************
      	
         // ** Browse Button - menu #1 **
         if (e.getSource() == pg1Browse) { 
            int returnVal = fc.showOpenDialog(GUI10.this);
         
            if (returnVal == JFileChooser.APPROVE_OPTION) {
               File file = fc.getSelectedFile();
               pg1AddFile.setText(file.getAbsolutePath());
               pg1Add.setEnabled(true);
               fileName = file.getAbsolutePath();
               //System.out.println(fileName);
               pg1AddFile.setToolTipText(fileName);
            }
         } 
         // ** Add Button - menu #1 **
         else if (e.getSource() == pg1Add){// Add module to list
            if(isAdd && !(pg1AddFile.getText()).equals("")){
               if(xlsFiles.indexOf(pg1AddFile.getText()) < 0){
                  xlsFiles.add(fileCount, pg1AddFile.getText());
                  xtsDir = (new File(pg1AddFile.getText())).getParent();
                  //System.out.println(xtsDir);
                  fileCount++;
                  pg1Mod.setListData(xlsFiles);
                  pg1AddFile.setText("");
                  //pg1Add.setEnabled(false);
                  pg1AddFile.setToolTipText("Press \"Browse\" to select a file.");
                  infoLoss = true;
                  isAdd = false;
                  pg1Add.setText("Delete");
               }
               else{// Module already in list
                  JOptionPane.showMessageDialog(pg1,
                     					"The File\n"+
                                    pg1AddFile.getText()+
                     					"\nIs Already in the Module list.",
                                    "",
                                    JOptionPane.ERROR_MESSAGE); 								
                  pg1AddFile.setText("");
                  pg1Add.setEnabled(false);
                  pg1AddFile.setToolTipText("Press \"Browse\" to select a file.");
               }  
            }
            else if(!isAdd){
               pg1Delete.doClick();
            }
            
         }
         
         // ** Delete Button - menu #1 **
         else if (e.getSource() == pg1Delete){
            int[] index = pg1Mod.getSelectedIndices();
            //for(int i = 0; i<index.length; i++){
               //if(index[i] >= 0){
            xlsFiles.remove(0);
            fileCount--;
            isAdd = true;
            pg1Add.setText("Add");
               //}
               //for(int f = 0; f<index.length; f++){
                  //if(index[f] > index[i]){
                     //index[f]--;
                  //}
               //}
            //}
            pg1Mod.setListData(xlsFiles);
            infoLoss = true;
         }
         // ** Move Up Button - menu #1 **
         else if (e.getSource() == pg1MoveUp){
            if(pg1Mod.getSelectedIndex() > 0){
               int index = pg1Mod.getSelectedIndex();
               Object temp = xlsFiles.get(index);
               xlsFiles.set(index, xlsFiles.get(index-1));
               xlsFiles.set(index-1, temp);
               pg1Mod.setListData(xlsFiles);
               pg1Mod.setSelectedIndex(index-1);
               infoLoss = true;
            }
         }
         // ** Move Down Button - menu #1 **
         else if (e.getSource() == pg1MoveDown){
            if(pg1Mod.getSelectedIndex() < fileCount-1){
               int index = pg1Mod.getSelectedIndex();
               Object temp = xlsFiles.get(index);
               xlsFiles.set(index, xlsFiles.get(index+1));
               xlsFiles.set(index+1, temp);
               pg1Mod.setListData(xlsFiles);
               pg1Mod.setSelectedIndex(index+1);
               infoLoss = true;
            }
         }
         // ** Next Button - menu #1 **
         else if (e.getSource() == pg1Next){               
            
         	//add each module to vector
            resetModules();
            Module2 temp;
            Vector newVars, values;
            String modError = "";
            boolean moveOn = true;
         	
            if(!xlsFiles.isEmpty()){
               
               for(int i = 0; i < xlsFiles.size(); i++){
                  temp = new Module2(xlsFiles.elementAt(i).toString());
                  modError += temp.processFile();
                  if(!modError.equals("")){
                     moveOn = false;
                  }
                  if(moveOn){
                     modules.add(temp);
                  }
               }
            }
            else{
               modError = "Error: no files in module list.";
               moveOn = false;
            }
         	
            if(moveOn){
               temp = null;
             //add all variables to variable list
               if(!modules.isEmpty()){
                  moduleLocations = new int[modules.size()];
                  int counter = 0;
                  int realLoc = 0;
               
                  for(int i = 0; i < modules.size(); i++){
                     moduleLocations[i] = counter;
                     temp = (Module2) modules.get(i);
                     newVars = temp.getInputVariables();
                     values = temp.getInputVariablesRange();
                     //add file name
                     variables.add(" ---"+temp.getFileName()+"--- ");
                     varType.add("title");
                     originalValues.add("");
                     isConstant.add("#");
                     isAlias.add("#");
                     variableNames.add(temp.getFileName());
                   
                     counter++;
                  	//add all input variables
                     for(int f = 0; f < newVars.size(); f++){
                        variables.add((newVars.get(f)).toString());
                        variableNames.add((newVars.get(f)).toString());
                        isConstant.add("f");
                        isAlias.add("-");
                        varType.add("input");
                        originalValues.add((values.get(f)).toString());
                        counter++;
                        realLoc++;
                        actualLoc.add(new Integer(realLoc));
                        modNum.add(new Integer(i));
                     }
                     //add all output variables
                     newVars = temp.getOutputVariables();
                     for(int f = 0; f < newVars.size(); f++){
                        variables.add((newVars.get(f)).toString());
                        variableNames.add((newVars.get(f)).toString());
                        isConstant.add("f");
                        isAlias.add("-");
                        varType.add("output");
                        originalValues.add("-");
                        counter++;
                        realLoc++;
                        actualLoc.add(new Integer(realLoc));
                        modNum.add(new Integer(i));
                     }
                  }
               }
            	
            //add all range values to variables
               String display;
               if(variables.size() == originalValues.size()){
                  for(int b = 0; b < variables.size(); b++){
                     if(!(originalValues.get(b)).equals("-")){
                        display = (variables.get(b)).toString() + (originalValues.get(b)).toString();
                        variables.set(b, display);
                     }
                  }
               }
               else{
                  
               }
            
               pg2VarTx.setListData(variables);
               pg2AliasTx.setListData(avAlias);
               
               error.setVisible(false);
               //sw = new SwitchFrames( pg1, pg2 );
               //sw.switcher();
               pg1.getContentPane().removeAll();
               pg1.getContentPane().add(pg2BG);
               pg1.pack();
               pg2BG.repaint();
               pg1AddFile.setText("");
            }
            else{
               errTx.setText(modError);
               error.setLocationRelativeTo(pg1);
               error.setVisible(true);
               error.toFront();
            }
         }
         //**********************************************************
         // Page 2 buttons
         //**********************************************************
         else if (e.getSource() == pg2Next){
            boolean isSaved = false;
//            if(infoLoss)
//               isSaved = saveFile();
            isSaved = true;
			
            if(isSaved || !infoLoss){
               pg2Next.setVisible(false);
               pg2Back.setVisible(false);
               makeConstant.setEnabled(false);
               restoreValue.setEnabled(false);
               constantValue.setEditable(false);
               createAlias.setEnabled(false);
               pg2BG.remove(pg2S);
               pg2BG.add(pg2Sg);
            
               //pg1.setLocationRelativeTo(null);
               int xLoc = pg1.getLocation().x + pg1.getWidth() / 4;
               int yLoc = pg1.getLocation().y;
               pg3.setLocation(xLoc, yLoc);
               pg3.setVisible(true);
               currentFrame = pg3;
               genetics.setText(setupGeneticRep());
            }
         }
         // ** Back - menu #2 **
         else if (e.getSource() == pg2Back){
            int n = JOptionPane.showConfirmDialog(pg1,
                                    "Return to Module list?\nCurrent constant and alias values will be lost.",
                                    "Possible Data Loss",
                                    JOptionPane.YES_NO_OPTION); 								
            if (n == JOptionPane.YES_OPTION) {
               resetModules();
               pg1.getContentPane().removeAll();
               pg1.getContentPane().add(pg1BG);
               pg1.pack();
               pg1BG.repaint();
               infoLoss = true;
               pg1Add.setText("Delete");
               pg1Add.setEnabled(true);
               currentFrame = pg1;
            }
         }
         // ** Set Constant - menu #2 **
         else if (e.getSource() == makeConstant){
            String constVal = constantValue.getText(), str;
            int index = pg2VarTx.getSelectedIndex();
            
            if(index >=0 && !(constVal.trim()).equals("") && !isTitleLoc(index)){
               
               restoreValue(index);
               //delete range value
               if(((variables.get(index)).toString()).endsWith("]")){
                  str = (variables.get(index)).toString();
                  int n = str.length()-1;
                  while(n>0 && str.charAt(n) != '[' && str.charAt(n) != ' ' && str.charAt(n) != '='){
                     n--;
                  }
                  if(str.charAt(n) == '['){
                     str = str.substring(0, n);
                  }
                  variables.set(index, str);
               }
               //set constant value
               variables.set(index, (variables.get(index)+"  = "+constVal));
               isConstant.set(index, "t"+constVal);
               infoLoss = true;
            }
            else{
            //nothing selected or no value entered
            }
            
            pg2VarTx.setListData(variables);
            constantValue.setText("");
         }
         // ** Remove Constant - menu #2 **
         else if (e.getSource() == restoreValue){
            if(!isTitleLoc(pg2VarTx.getSelectedIndex())){
               restoreValue(pg2VarTx.getSelectedIndex());
               infoLoss = true; 
            }
            pg2VarTx.setListData(variables);
            constantValue.setText("");
         }
         else if (e.getSource() == createAlias){
            int index = pg2VarTx.getSelectedIndex();
            int aliasIndex = pg2AliasTx.getSelectedIndex(), realAliasLoc;
            String str1 = "", str2 = "";
           
            if(index >=0 && aliasIndex >=0 && !isTitleLoc(index) && avAliasLoc[aliasIndex] >= 0){
               restoreValue(index);
            
               str1 = variableNames.get(index).toString(); // upper variable
               str2 = variableNames.get(avAliasLoc[aliasIndex]).toString(); //lower variable (gets @)
               realAliasLoc = avAliasLoc[aliasIndex];
            
               variables.set(realAliasLoc, variableNames.get(realAliasLoc).toString());
              
               variables.set(realAliasLoc, (variables.get(realAliasLoc)+"  @ "+str1));
               Integer varLoc = new Integer(index);
               isAlias.set(realAliasLoc, varLoc.toString());
               pg2VarTx.setListData(variables);
               infoLoss = true;
            }
           
         }
         else if (e.getSource() == pg3Back){
            pg2Next.setVisible(true);
            pg2Back.setVisible(true);
            makeConstant.setEnabled(true);
            restoreValue.setEnabled(true);
            constantValue.setEditable(true);
            createAlias.setEnabled(true);
            pg2BG.remove(pg2Sg);
            pg2BG.add(pg2S);
            currentFrame = pg1;
            pg3.setVisible(false);
         }
         else if (e.getSource() == varChangeB){
            int selIndex = pg3Var.getSelectedIndex();
            String textCh = envVarChange.getText();
			updateParameter((String)gaSelect.getSelectedItem(), selIndex, textCh); 
         
         }
         else if (e.getSource() == pg3Next){
            if(gaSelect.getSelectedIndex() == 0){
               JOptionPane.showMessageDialog(pg3,
                  						"Please select a GA before execution.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE); 
            }
            else{
               pg1.setVisible(false);
               pg3.setVisible(false);
               
               //Divide the number of runs evenly between threads
               if(numThreads>numRuns) numThreads = numRuns;
               int numRunsPerThread = numRuns/numThreads;
               int leftoverRuns = numRuns%numThreads;
               for(int i = 0; i < numThreads; i++){
            	   if(leftoverRuns > 0){
            		   executeApp(numRunsPerThread + 1);
            		   leftoverRuns--;
            	   }else{
            		   executeApp(numRunsPerThread);
            	   }
               }
            }
         }
		 else if(e.getSource() == gaSelect) {
			String gaName = (String)gaSelect.getSelectedItem();
			reloadParameterList(gaName);
		 }
      }

      
       private String setupGeneticRep(){
         String genRep = "< ";
         String isConst, isAls, isInput;
        
         geneticVars.clear();
         geneticLoc.clear();
         int location = 0;
         for(int i = 0; i < variables.size(); i++){
            // make sure variable isn't an alias and isn't a constant
            isConst = (isConstant.get(i)).toString();
            isAls = (isAlias.get(i)).toString();
            isInput = (varType.get(i)).toString();
         	
         	
            if(isAls.equals("-") && isConst.equals("f") && !isAls.equals("#") && !isConst.equals("#") && isInput.equals("input")){
               geneticVars.add((variableNames.get(i)).toString());
               geneticLoc.add(new Integer(location)); 
            }
            if(!isAls.equals("#")){
               location++;
            }
           
         }
         
         for(int i = 0; i < geneticVars.size(); i++){
            genRep = genRep + ((geneticVars.get(i)).toString()).trim();
            if(i < geneticVars.size()-1){
               genRep = genRep + ", ";
            }
         }
      	
         genRep = genRep + " >";
      	
         return genRep;
      }
   	
   	//**********************************************************************
      //
      // RESTORE VARIABLE VALUES
      //
      //**********************************************************************
       private boolean restoreValue(int index){
         boolean success = false;
         String var, str;
      
         if(index < variables.size() && index >= 0){
         //delete current value
         
            if(((variables.get(index)).toString()).endsWith("]")){// if it has a ranged value
               str = (variables.get(index)).toString();
               int n = str.length()-1;
               while(n>0 && str.charAt(n) != '['){
                  n--;
               }
               
               if(str.charAt(n) == '[' || str.charAt(n) == ' '){
                  str = str.substring(0, n);
               }
               variables.set(index, str);
            }
            
            if(!isAlias.get(index).equals("-")){// if it is an alias
               str = (variables.get(index)).toString();
               int n = str.length()-1;
               while(n>0 && str.charAt(n) != '@'){
                  n--;
               }
               //n--;
               if(str.charAt(n) == '@' || str.charAt(n) == ' '){
                  str = str.substring(0, maxVarLength);
               }
               variables.set(index, str);
            }
            
            if(((String)isConstant.get(index)).startsWith("t")){// if it is a constant
               var = (variables.get(index)).toString();
               int i = var.length()-1;
               while(i>0 && var.charAt(i) != '='){
                  i--;
               }
               //i--;
               var = (var.substring(0, maxVarLength));
               variables.set(index , var);
            }
            
         // restore value
         
            String display;
            if(variables.size() == originalValues.size()){
               if(!(originalValues.get(index)).equals("-")){
                  display = (variables.get(index)).toString() + (originalValues.get(index)).toString();
                  variables.set(index, display);
               }
               isConstant.set(index, "f");
               isAlias.set(index, "-");
            }
            success = true;
         }
         
         return success;
      }
   	
      //**********************************************************************
      //
      // RESET ALL MODULES
      //
      //**********************************************************************
       public void resetModules(){
         modules.clear();
         variables.clear();
         varType.clear();
         isAlias.clear();
         isConstant.clear();
         originalValues.clear();
         avAlias.clear();
         variableNames.clear();
         geneticVars.clear();
         ErrorMsg = "";
         pg2VarTx.setListData(variables);
      }
   	
   	//**********************************************************************
      //
      // determines if the integer is a location of a module title
      //
      //**********************************************************************
       private boolean isTitleLoc(int index){
         boolean ret = false;
         
         for(int i = 0; i < modules.size(); i++){
            if(moduleLocations[i] == index)
               ret = true;
         }
         
         return ret;
      }
   	
   	//**********************************************************************
      //
      // SHOW AVAIlABLE ALIASES (Used for variables section)
      //
      //**********************************************************************
       class SelectionChange implements ListSelectionListener {
          public void valueChanged(ListSelectionEvent e) {
            avAlias.clear();
           
            if (e.getValueIsAdjusting())
               return;
         
            JList list = (JList)e.getSource();
            int index = list.getSelectedIndex();
         	
            if (index < 0) { // if nothing is selected in variable list
               pg2AliasTx.setListData(avAlias);
            }
            else if(isTitleLoc(index)){ // if a module title is selected in variable list
               pg2AliasTx.setListData(avAlias);
            }
            else if(index > moduleLocations[moduleLocations.length-1]){ // if it's the last module to be processed
               pg2AliasTx.setListData(avAlias);
            }
            else {
               String selection = (variables.get(index)).toString();
               int modNum = 0;
              //find which module it's in
               for(int i = 0; i < modules.size()-1; i++){
                  if(moduleLocations[i] > index)
                     break;
                  else
                     modNum = i;
               }
               compileAliasList(modNum);
               pg2AliasTx.setListData(avAlias);
            }
         }
         
          private void compileAliasList(int modNum){
            int actualIndex[] = new int[variables.size()]; // holds the actual index of variables, -1 for titles
            int counter = 0; // counts the index within avAlias
            int index = 0; // counts the actual index
               
            if(!modules.isEmpty() && modNum < modules.size()-1){
               Module2 temp;
               Vector newVars, values;
               
               for(int i = 0; i < modules.size(); i++){
                  temp = (Module2) modules.get(i);
                  newVars = temp.getInputVariables();
                  values = temp.getInputVariablesRange();
                     //add file name
                  if(i >= modNum+1){
                     avAlias.add(" ---"+temp.getFileName()+"--- ");
                     actualIndex[index] = -1;
                     index++;
                  }
                  counter++;
                  	//add all input variables
                  for(int f = 0; f < newVars.size(); f++){
                     if(i >= modNum+1){
                        avAlias.add((newVars.get(f)).toString() + (values.get(f)).toString());
                        actualIndex[index] = counter;
                        index++;
                     }
                     counter++;
                  }
                     //count all output variables
                  newVars = temp.getOutputVariables();
                  for(int f = 0; f < newVars.size(); f++){
                     // avAlias.add((newVars.get(f)).toString());
                     counter++;
                  }
               }
            }
         	
            avAliasLoc = new int[index];
            
         	//copy actual index values
            for(int i = 0; i < index; i++){
               avAliasLoc[i] = actualIndex[i];
            }
         }
      }
   
      //**********************************************************************
      //
      // Drag and Drop Support
      //
      //**********************************************************************
       public void dragEnter(DropTargetDragEvent dtde) {}
   
       public void dragExit(DropTargetEvent dte) {}
   
       public void dragOver(DropTargetDragEvent dtde) {}
   
       public void dropActionChanged(DropTargetDragEvent dtde) {}
   
       public void drop(DropTargetDropEvent e) {
         try {
            
            String file, ext;
            Transferable transfer = e.getTransferable();
            DataFlavor[] flavors = transfer.getTransferDataFlavors();
            
            for (int i = 0; i < flavors.length; i++) {
            
               if (flavors[i].isFlavorJavaFileListType()) {
                  e.acceptDrop(DnDConstants.ACTION_COPY);
                  List list = (List) transfer.getTransferData(flavors[i]);
                  
                  for (int j = 0; j < list.size(); j++) {
                     //add each file to Modules list
                     fileName = ((list.get(j)).toString()).trim();
                     
                     if((fileName.toLowerCase()).endsWith(allowedFileType)){
                        if(!isAdd){
                           JOptionPane.showMessageDialog(pg1,
                              		"Error: Only one module can be added to Module List.",
                                    "",
                                    JOptionPane.ERROR_MESSAGE); 
                           break;
                        }
                        else{
                           if(xlsFiles.indexOf(fileName) < 0){
                              xlsFiles.add(fileCount, fileName);
                              fileCount++;
                              pg1Mod.setListData(xlsFiles);
                              infoLoss = true;
                              isAdd = false;
                              pg1Add.setEnabled(true);
                              pg1Add.setText("Delete");
                              break;
                           }
                           else{
                           //ERROR: TRYING TO ADD FILE ALREADY IN LIST
                           }
                        }
                     }
                  }
                  return;
               }
            }
            
            //System.out.println("Drop failed: " + e);
            e.rejectDrop();
         } 
             catch (Exception ex) {
               ex.printStackTrace();
               e.rejectDrop();
            }
      }
   
      //**********************************************************************
      //
      // BACKGROUND PANEL CLASS
      //
      //**********************************************************************
/*       class BackgroundPanel extends JPanel
      {
         Image BGpic;
          public void paintComponent(Graphics g) 
         {
           BGpic =  Toolkit.getDefaultToolkit()
               .getImage(java.net.URLClassLoader.getSystemResource("CEV1.jpg"));
            Dimension dim = getSize();
            g.drawImage(BGpic, 0, 0, dim.width, dim.height, null);
         }
      }
*/      
   	//**********************************************************************
      //
      // ICON SUPPORT
      //
      //**********************************************************************
/*       public ImageIcon newIcon(String loc)
      {
         ImageIcon img = null;
         java.net.URL location = GUI10.class.getResource(loc);
         if (location != null) {
            img = new ImageIcon(location);
         } 
         else {
            System.out.println("Error: couldn't find icon file (" + loc+").");
         }
         
         return img;
      }
*/   
   	//**********************************************************************
      //
      // FILE SAVE
      //
      //**********************************************************************
       public boolean saveFile(){
         JFileChooser fc2;
         MyFilter filter2;
         fc2 = new JFileChooser();
         fc2.addChoosableFileFilter(new MyFilter(".app"));
         fc2.setAcceptAllFileFilterUsed(false);
         
         boolean isSaved = false, okToSave = true, askAgain = true;
         String allowedFileType = ".app";             
      	   
         String path, fileN, var = "";
         File file;
         int n;
         
         while(askAgain){
            askAgain = false;
            isSaved = false; 
            okToSave = true;
            int returnVal = fc2.showSaveDialog(GUI10.this);
         
            if (returnVal == JFileChooser.APPROVE_OPTION) {
               file = fc2.getSelectedFile();
               path = (file.getAbsolutePath()).trim(); 
               fileN = file.getName();
            
            //make sure it's an app file, if not, make it one
               if(!path.endsWith(allowedFileType)){// give it an xts extension
                  path = path+allowedFileType;
                  fileN = fileN+allowedFileType;
                  file = new File(path);
               }
               else if(!path.equals("")){//save module
                  file = new File(path);
               }
               else
               {
                  errTx.setText("File not saved... no file name given.");
                  error.setLocationRelativeTo(pg1);
                  error.show();
                  error.toFront();
               }
            
               if(file.exists()){//ask if they want to save over
                  n = JOptionPane.showConfirmDialog(pg1,
                                    "The application\n"+file+"\nalready exists.\nReplace existing file?",
                                    "Save As",
                                    JOptionPane.YES_NO_OPTION); 								
                  if (!(n == JOptionPane.YES_OPTION)) {
                     okToSave = false;
                  }
                  if (n == JOptionPane.NO_OPTION) {
                     askAgain = true;
                  }
               }
            
               if(okToSave){
               //**** save file
                  try {
                  //set file name if necessary
                     FileWriter fw = new FileWriter(path);
                     boolean commaNeeded = false;
                  
                  //write app code to file   		
                  //modules
                     fw.write("m: \n");
                     for(int i = 0; i < modules.size(); i++){
                        fw.write(((Module2)(modules.get(i))).getFilePath()+"\n");
                     }
                     fw.write(";\n");
                  //variables
                     fw.write("v: ");
                     for(int f = 0; f < isConstant.size(); f++){
                        fw.write((isConstant.get(f)).toString()+" ");
                     }
                     fw.write(";\na: ");
                  //aliases
                     for(int f = 0; f < isAlias.size(); f++){
                        fw.write((isAlias.get(f)).toString()+" ");
                     }
                     fw.write(";");
                     isSaved = true;
                     infoLoss = false;
                     fw.close();
                     application.setFilePath(path);
                  }
                  
                      catch (IOException e) {  
                        errTx.setText("ERROR: file could not be saved to location\n\t"
                           +path);
                        error.setLocationRelativeTo(pg1);
                        error.show();
                        error.toFront();    
                     }
               }
            
            }
            else {
            //errTx.setText("File not saved... save cancelled by user.");
            //error.setLocationRelativeTo(pg3);
            //error.show();
            //error.toFront();
            } 
         }
         
         return isSaved;
      }
   	   	
   	//**********************************************************************
      //
      // MENU SUPPORT
      //
      //**********************************************************************
       
   	
       private String createWorkingDir(String curDir, String runsFolderName){
    	   //Finds unique working directory name, creates working directory, and copies all 
    	   //files/folders (except [runsFolerName]) into working dir.
    	   //Returns working directory path.
    	   File fWorkingDir, fXTSDir, fLogDir;
    	   Calendar cal = Calendar.getInstance();
    	   String dateStr = ""+cal.get(Calendar.YEAR)+String.format("%02d", (cal.get(Calendar.MONTH)+1))+String.format("%02d", cal.get(Calendar.DATE));
    	   //System.out.println(curDir+File.separator+"X-TOOLSS_RUNS");
    	   fXTSDir = new File(curDir);
    	   String workingDirPath = curDir+File.separator+runsFolderName+File.separator;
    	   boolean cont = true;
		   int i = 1;
		   while(cont){
			   fWorkingDir = new File(workingDirPath+dateStr+"_"+String.format("%03d", i));
			   if(!fWorkingDir.exists()){
				   //If it doesn't exist, create the file and end the loop
				   fLogDir = new File(workingDirPath+dateStr+"_"+String.format("%03d", i)+File.separator+"X-TOOLSS_LOGS");
	    		   if(!fLogDir.mkdirs()) System.err.println("ERROR: Unable to create X-TOOLSS_LOGS directory.");
	    		   
	    		   workingDirPath = workingDirPath+dateStr+"_"+String.format("%03d", i);
				   cont = false;
			   }
			   i++;
		   }
    	   
		   //New directory created, need to get a list of files to copy.
		   String[] files = fXTSDir.list();
		   for(int fileIndex = 0; fileIndex < files.length; fileIndex++){
			   //System.out.println(files[fileIndex]);
			   //Copy each item (other than runs directory) to working directory
			   if(!files[fileIndex].endsWith(runsFolderName)){
				   //System.out.println(files[fileIndex]);
				   File tempFile = new File(xtsDir, files[fileIndex]);
				   if(tempFile.isDirectory()){
					   copyDir(xtsDir, workingDirPath, files[fileIndex]);
				   }else{
					   copyFile(xtsDir, workingDirPath, files[fileIndex]);
				   }
			   }
		   }
		   
		   return workingDirPath;
       }
       
       private void copyDir(String fromDir, String toDir, String dirName){
    	   //This function creates a new directory in [toDir] and copies all files to it.
    	   String sOldDir = fromDir+File.separator+dirName;
    	   String sNewDir = toDir+File.separator+dirName;
    	   
    	   File fNewDir = new File(sNewDir);
    	   File fOldDir = new File(sOldDir);
    	   if(!fNewDir.mkdirs()){
    		   System.err.println("ERROR: Unable to create/copy directory from copyDir(...)");
    	   }
    	   
    	   String[] files = fOldDir.list();
		   for(int fileIndex = 0; fileIndex < files.length; fileIndex++){
			   //Copy each item (other than runs directory) to working directory
			   //System.out.println(files[fileIndex]);
			   File tempFile = new File(sOldDir, files[fileIndex]);
			   if(tempFile.isDirectory()){
				   copyDir(sOldDir, sNewDir, files[fileIndex]);
			   }else{
				   copyFile(sOldDir, sNewDir, files[fileIndex]);
			   }
		   }
       }
       
       private void copyFile(String fromDir, String toDir, String fileName){
    	   //This function copies the [fileName] from [fromDir] to [toDir].
    	   //This function will not work if [fileName] is a directory, displays error.
    	   //System.out.println("Copying "+fromDir+" "+fileName+"  ->  "+toDir+" "+fileName);
    	   if(System.getProperty("os.name").startsWith("Windows")){
	    	   File fromFile = new File(fromDir, fileName);
	    	   File toFile = new File(toDir, fileName);
	    	   
	    	   FileInputStream from = null;
	    	   FileOutputStream to = null;
	    	   
	    	   try {
	    		   from = new FileInputStream(fromFile);
	    		   to = new FileOutputStream(toFile);
	    		   byte[] buffer = new byte[4096];
	    		   int bytesRead = from.read(buffer);
	    		   while (bytesRead != -1){
	    			   to.write(buffer, 0, bytesRead);
	    			   bytesRead = from.read(buffer);
	    		   }
	    		   
	    		   from.close();
	        	   to.close();
	    	   }catch(Exception e){
	    		   System.err.println(e);
	    	   }
    	   }else{
	    	   try{
	    		   String[] cmd = new String[3];
	    		   cmd[0] = "cp";
	    		   cmd[1] = fromDir+File.separator+fileName;
	    		   cmd[2] = toDir+File.separator+fileName;
	    		   Runtime rt = Runtime.getRuntime();
	    		   Process proc = rt.exec(cmd);
	               InputStream stderr = proc.getErrorStream();
	               InputStreamReader isr = new InputStreamReader(stderr);
	               BufferedReader br = new BufferedReader(isr);
	               int exitVal = proc.waitFor();
	               if(exitVal != 0){
		               String line = null;
		               System.out.println("<ERROR>");
		               while ( (line = br.readLine()) != null)
		                   System.out.println(line);
		               System.out.println("</ERROR>");
		               System.out.println("Process exitValue: " + exitVal);
	               }
	    	   }catch(Exception e){
	    		   System.err.println("Error: "+e);
	    	   }
    	   }
       }
       
       private void executeApp(int numberOfRuns){
    	   /*
    	    * This function executes a single independent optimization.
    	    */
    	 workingDir = createWorkingDir(xtsDir, "X-TOOLSS_RUNS");
    	 Module2 oldMod = (Module2) modules.get(0);
    	 
    	 //Copy the original module file
    	 Module2 newMod = (Module2)oldMod.clone();
    	 
    	 //Change the path of the module file to the working directory
    	 newMod.setFilePath(workingDir+File.separator+oldMod.getFileName());
    	 
    	 //Create a new vector to be compatible.
    	 /*
    	  * I don't think vectors are needed because an a problem is only
    	  * related to one xts file, but further research is needed to verify
    	  * this.  To stay compatible, the new module is added to a vector.
    	  */
    	 Vector<Module2> newModules = new Vector();
    	 newModules.add(newMod);
    	 
    	 
    	 application = new AppFile();
         application.setModuleArray(newModules);
         application.setGeneticRep(geneticLoc);
         application.setConstantVector(isConstant);
         application.setAliasVector(isAlias);
       
         String temp, r1, r2;
         Vector varValues = ((Module2)newModules.get(0)).inputVarValues;
         Vector varTypes = ((Module2)newModules.get(0)).getInputVariableTypes();
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
         
         
		 tt = new ThreadTerminator();
         String gaName = (String)gaSelect.getSelectedItem();
         XTOOLSEvaluationFunction xtoolsEvalFun = new XTOOLSEvaluationFunction(application);
         MaxFunctionEvalTermination mfeTermination = new MaxFunctionEvalTermination(numEval.intValue(), tt);
         boolean shouldLog = !logFileName.equalsIgnoreCase("");
         //File xtsFile = new File((String) xlsFiles.get(0));
         
		 String logFilename = (shouldLog)? logFileName + ".log" : "xtoolss.log";
		 logFilename = workingDir+File.separator+"X-TOOLSS_LOGS"+File.separator+logFilename;
         String outFilename = (shouldLog)? logFileName + ".out" : "xtoolss.out";
         outFilename = workingDir+File.separator+"X-TOOLSS_LOGS"+File.separator+outFilename;
         String statFilename = (shouldLog)? logFileName + ".stat" : "xtoolss.stat";
         statFilename = workingDir+File.separator+"X-TOOLSS_LOGS"+File.separator+statFilename;
         
         
		 XTOOLSMigrationOperator migOp = null;
		 if(!memespaceIP.equalsIgnoreCase("NONE") && (memespacePort.intValue() > 0)) {
			migOp = new XTOOLSMigrationOperator(memespaceIP, memespacePort.intValue(), migrationRate.floatValue());
		 }
		 else {
			migOp = new XTOOLSMigrationOperator("", 0, 0.0f);		 
		 }
		
		 population = null;
		 ParentSelection parentSelection = null;
		 RecombinationOperator recombinationOp = null;
		 MutationOperator mutationOp = null;
		 SurvivorSelection survivorSelection = null;
		 OneFifthRule oneFifthRule = (useOneFifthRule.equalsIgnoreCase("YES"))? new OneFifthRule() : null;
		 
         if(!gaName.equals("PSO")) {
			 if(gaName.equals("Standard EP")) {
				EPOperators epOps = new EPOperators(false, false, mutRate.floatValue());
				parentSelection = epOps;
				recombinationOp = epOps;
				mutationOp = new GaussianMutationOperator(1.0, mutRate.floatValue(), mutRange.floatValue());
				survivorSelection = new MuPlusLambdaSelection();
	         }
	         else if(gaName.equals("Continuous Standard EP")) {
				EPOperators epOps = new EPOperators(true, false, mutRate.floatValue());
				parentSelection = epOps;
				recombinationOp = epOps;
				mutationOp = new GaussianMutationOperator(1.0, mutRate.floatValue(), mutRange.floatValue());
				survivorSelection = new MuPlusLambdaSelection();
	         }
	         else if(gaName.equals("Meta-EP")) {
				EPOperators epOps = new EPOperators(false, true, mutRate.floatValue());
				parentSelection = epOps;
				recombinationOp = epOps;
				mutationOp = epOps;
				survivorSelection = new MuPlusLambdaSelection();
	         }
	         else if(gaName.equals("Continuous Meta-EP")) {
				EPOperators epOps = new EPOperators(true, true, mutRate.floatValue());
				parentSelection = epOps;
				recombinationOp = epOps;
				mutationOp = epOps;
				survivorSelection = new MuPlusLambdaSelection();
	         }
	         else if(gaName.equals("Steady-state GA with BLX")) {
				parentSelection = new TournamentSelection(2, 2);
				survivorSelection = new SteadyStateSelection();
				recombinationOp = new BLXCrossoverOperator(crossoverUsageRate.floatValue(), blxAlpha.floatValue());
				mutationOp = new GaussianMutationOperator(mutUsageRate.floatValue(), mutRate.floatValue(), mutRange.floatValue());
	         }
	         else if(gaName.equals("Generational GA with BLX")) {
				parentSelection = new TournamentSelection(2, (popSize.intValue() - numElites.intValue()) * 2);
				survivorSelection = new GenerationalSelection(numElites.intValue());
				recombinationOp = new BLXCrossoverOperator(crossoverUsageRate.floatValue(), blxAlpha.floatValue());
				mutationOp = new GaussianMutationOperator(mutUsageRate.floatValue(), mutRate.floatValue(), mutRange.floatValue());
	         }
	         else if(gaName.equals("Steady-generational GA with BLX")) {
				parentSelection = new TournamentSelection(2, 2);
				survivorSelection = new SteadyGenerationalSelection(1);
				recombinationOp = new BLXCrossoverOperator(crossoverUsageRate.floatValue(), blxAlpha.floatValue());
				mutationOp = new GaussianMutationOperator(mutUsageRate.floatValue(), mutRate.floatValue(), mutRange.floatValue());
	         }
	         else if(gaName.equals("Steady-state GA")) {
				parentSelection = new TournamentSelection(2, 2);
				survivorSelection = new SteadyStateSelection();
				recombinationOp = new UniformCrossoverOperator(crossoverUsageRate.floatValue());
				mutationOp = new GaussianMutationOperator(mutUsageRate.floatValue(), mutRate.floatValue(), mutRange.floatValue());
	         }
	         else if(gaName.equals("Generational DEA")) {
				parentSelection = new TournamentSelection(2, (popSize.intValue() - numElites.intValue()) * 2);
				survivorSelection = new GenerationalSelection(numElites.intValue());
				mutationOp = new GaussianMutationOperator(mutUsageRate.floatValue(), mutRate.floatValue(), mutRange.floatValue());
				recombinationOp = new DEOperators(true, phi.floatValue());
	         }
	         else if(gaName.equals("Steady-state DEA")) {
				parentSelection = new TournamentSelection(2, (popSize.intValue() - numElites.intValue()) * 2);
				survivorSelection = new SteadyStateSelection();
				mutationOp = new GaussianMutationOperator(mutUsageRate.floatValue(), mutRate.floatValue(), mutRange.floatValue());
				recombinationOp = new DEOperators(false, phi.floatValue());
	         }
	         else if(gaName.equals("Elitist EDA")) {
				EDAOperators edaOps = new EDAOperators(numElites.intValue());
				parentSelection = edaOps;
				survivorSelection = edaOps;
				mutationOp = edaOps;
				recombinationOp = edaOps;
	         }
			 xtoolsECMon = new XTOOLSECMonitor(tempOptPanel, true, logInterval.intValue(), numEval.intValue(), tt, logFilename, outFilename);
	         currentFrame = xtoolsECMon.getFrame();
			 population = new Population(popSize.intValue(), interval, xtoolsEvalFun, parentSelection, recombinationOp, mutationOp, survivorSelection, migOp);
			 ecThread = new XTOOLSRunnable(population, mfeTermination, xtoolsECMon, numberOfRuns, tt, statFilename, true, oneFifthRule);
			 ecThread.start();
			 hasStarted = true;
			 tempOptPanel = new OptimizationPanel(xtoolsECMon, ecThread, tt, population, newMod.getFileName(), numEval.intValue(), numberOfRuns);
			 xtoolsECMon.setOptPanel(tempOptPanel);
		 }
		 else {
			if(canUsePSO) {
	            boolean useCC = (constCoeff.intValue() > 0)? true : false;
	            xtoolsECMon = new XTOOLSECMonitor(tempOptPanel, true, logInterval.intValue(), numEval.intValue(), tt, logFilename, outFilename);
	            currentFrame = xtoolsECMon.getFrame();
	            pso = new ParticleSwarmOptimization(popSize.intValue(), neighborhoodSize.intValue(), minForPSO, maxForPSO, 2.05, 2.05, useCC, ParticleSwarmOptimization.ASYNCHRONOUS_UPDATE, xtoolsEvalFun, mfeTermination, xtoolsECMon, migOp);
				ecThread = new XTOOLSRunnable(pso, numberOfRuns, tt, statFilename, true);
				ecThread.start();
				hasStarted = true;
				tempOptPanel = new OptimizationPanel(xtoolsECMon, ecThread, tt, pso.getPopulation(), newMod.getFileName(), numEval.intValue(), numberOfRuns);
				xtoolsECMon.setOptPanel(tempOptPanel);
			}
			else {
				JOptionPane.showMessageDialog(null, "You must have only float or double inputs to use PSO.");
			}
         }
         tempOptPanel.setAlgInfo(gaName);
         optFrame.addOptimization(tempOptPanel);
      }
   	   
       private void updateParameter(String gaName, int selIndex, String text) {
         float value = 0.0f;
         try {
            value = Float.parseFloat(text);
         }
         catch(NumberFormatException nfe) {}
      
         gaVars.clear();
         gaVars.add("Parameters:");
         if(gaName.equals("Standard EP")) {
            if(selIndex == 1){
               popSize = new Integer((int)value);
            }	
            if(selIndex == 2){
               mutRate = new Float(value);
            }	
            if(selIndex == 3){
               mutRange = new Float(value);
            }	
            else if(selIndex == 4){
               numEval = new Integer((int)value);
            }
            else if(selIndex == 5) {
               numRuns = new Integer((int)value);
            }
            else if(selIndex == 6) {
                numThreads = new Integer((int)value);
            }
            else if(selIndex == 7) {
               logFileName = text;
            }
            else if(selIndex == 8) {
               logInterval = new Integer((int)value);
            }
            else if(selIndex == 9) {
               memespaceIP = text;
            }
            else if(selIndex == 10) {
               memespacePort = new Integer((int)value);
            }
            else if(selIndex == 11) {
               migrationRate = value;
            }
            gaVars.add(" Population Size:     "+popSize);
            gaVars.add(" Mutation Rate:       "+mutRate);
            gaVars.add(" Mutation Range:      "+mutRange);
            gaVars.add(" Total Evaluations:   "+numEval);
            gaVars.add(" Number of Runs:      "+numRuns);
            gaVars.add(" Number of Threads:   "+numThreads);
            gaVars.add(" Log File Name:       "+logFileName);
            gaVars.add(" Log Interval:        "+logInterval);
            gaVars.add(" Memespace IP:        "+memespaceIP);
            gaVars.add(" Memespace Port:      "+memespacePort);
            gaVars.add(" Migration Rate:      "+migrationRate);
         }
         else if(gaName.equals("Continuous Standard EP")) {
            if(selIndex == 1){
               popSize = new Integer((int)value);
            }	
            if(selIndex == 2){
               mutRate = new Float(value);
            }	
            if(selIndex == 3){
               mutRange = new Float(value);
            }	
            else if(selIndex == 4){
               numEval = new Integer((int)value);
            }
            else if(selIndex == 5) {
               numRuns = new Integer((int)value);
            }
            else if(selIndex == 6) {
                numThreads = new Integer((int)value);
             }
            else if(selIndex == 7) {
               logFileName = text;
            }
            else if(selIndex == 8) {
               logInterval = new Integer((int)value);
            }
            else if(selIndex == 9) {
               memespaceIP = text;
            }
            else if(selIndex == 10) {
               memespacePort = new Integer((int)value);
            }
            else if(selIndex == 11) {
               migrationRate = value;
            }
            gaVars.add(" Population Size:    "+popSize);
            gaVars.add(" Mutation Rate:      "+mutRate);
            gaVars.add(" Mutation Range:     "+mutRange);
            gaVars.add(" Total Evaluations:  "+numEval);
            gaVars.add(" Number of Runs:     "+numRuns);
            gaVars.add(" Number of Threads:  "+numThreads);
            gaVars.add(" Log File Name:      "+logFileName);
            gaVars.add(" Log Interval:       "+logInterval);
            gaVars.add(" Memespace IP:       "+memespaceIP);
            gaVars.add(" Memespace Port:     "+memespacePort);
            gaVars.add(" Migration Rate:     "+migrationRate);
         }
         else if(gaName.equals("Meta-EP")) {
            if(selIndex == 1){
               popSize = new Integer((int)value);
            }	
            else if(selIndex == 2) {
               mutRate = new Float(value);
            }
            else if(selIndex == 3){
               numEval = new Integer((int)value);
            }
            else if(selIndex == 4) {
               numRuns = new Integer((int)value);
            }
            else if(selIndex == 5) {
            	numThreads = new Integer((int)value);
            }
            else if(selIndex == 6) {
               logFileName = text;
            }
            else if(selIndex == 7) {
               logInterval = new Integer((int)value);
            }
            else if(selIndex == 8) {
               memespaceIP = text;
            }
            else if(selIndex == 9) {
               memespacePort = new Integer((int)value);
            }
            else if(selIndex == 10) {
               migrationRate = value;
            }
            gaVars.add(" Population Size:      "+popSize);
            gaVars.add(" Eta Mutation Rate:    "+mutRate);
            gaVars.add(" Total Evaluations:    "+numEval);
            gaVars.add(" Number of Runs:       "+numRuns);
            gaVars.add(" Number of Threads:    "+numThreads);
            gaVars.add(" Log File Name:        "+logFileName);
            gaVars.add(" Log Interval:         "+logInterval);
            gaVars.add(" Memespace IP:         "+memespaceIP);
            gaVars.add(" Memespace Port:       "+memespacePort);
            gaVars.add(" Migration Rate:       "+migrationRate);
         }
         else if(gaName.equals("Continuous Meta-EP")) {
            if(selIndex == 1){
               popSize = new Integer((int)value);
            }	
            else if(selIndex == 2) {
               mutRate = new Float(value);
            }
            else if(selIndex == 3){
               numEval = new Integer((int)value);
            }
            else if(selIndex == 4) {
               numRuns = new Integer((int)value);
            }
            else if(selIndex == 5) {
            	numThreads = new Integer((int)value);
            }
            else if(selIndex == 6) {
               logFileName = text;
            }
            else if(selIndex == 7) {
               logInterval = new Integer((int)value);
            }
            else if(selIndex == 8) {
               memespaceIP = text;
            }
            else if(selIndex == 9) {
               memespacePort = new Integer((int)value);
            }
            else if(selIndex == 10) {
               migrationRate = value;
            }
            gaVars.add(" Population Size:      "+popSize);
            gaVars.add(" Eta Mutation Rate:    "+mutRate);
            gaVars.add(" Total Evaluations:    "+numEval);
            gaVars.add(" Number of Runs:       "+numRuns);
            gaVars.add(" Number of Threads:    "+numThreads);
            gaVars.add(" Log File Name:        "+logFileName);
            gaVars.add(" Log Interval:         "+logInterval);
            gaVars.add(" Memespace IP:         "+memespaceIP);
            gaVars.add(" Memespace Port:       "+memespacePort);
            gaVars.add(" Migration Rate:       "+migrationRate);
         }
         else if(gaName.equals("Steady-state GA with BLX")) {
            if(selIndex == 1){
               popSize = new Integer((int)value);
            }	
            else if(selIndex == 2) {
               crossoverUsageRate = new Float(value);
            }
            else if(selIndex == 3) {
               blxAlpha = new Float(value);
            }
            else if(selIndex == 4) {
               mutUsageRate = new Float(value);
            }
            else if(selIndex == 5) {
               mutRate = new Float(value);
            }
            else if(selIndex == 6) {
               mutRange = new Float(value);
            }
            else if(selIndex == 7){
               numEval = new Integer((int)value);
            }
            else if(selIndex == 8) {
               numRuns = new Integer((int)value);
            }
            else if(selIndex == 9) {
            	numThreads = new Integer((int)value);
            }
            else if(selIndex == 10) {
               logFileName = text;
            }
            else if(selIndex == 11) {
               logInterval = new Integer((int)value);
            }
            else if(selIndex == 12) {
               memespaceIP = text;
            }
            else if(selIndex == 13) {
               memespacePort = new Integer((int)value);
            }
            else if(selIndex == 14) {
               migrationRate = value;
            }
            else if(selIndex == 15) {
               useOneFifthRule = text.toUpperCase();
            }
            gaVars.add(" Population Size:      "+popSize);
            gaVars.add(" Crossover Usage Rate: "+crossoverUsageRate);
            gaVars.add(" BLX-alpha:            "+blxAlpha);
            gaVars.add(" Mutation Usage Rate:  "+mutUsageRate);
            gaVars.add(" Mutation Rate:        "+mutRate);
            gaVars.add(" Mutation Range:       "+mutRange);
            gaVars.add(" Total Evaluations:    "+numEval);
            gaVars.add(" Number of Runs:       "+numRuns);
            gaVars.add(" Number of Threads:    "+numThreads);
            gaVars.add(" Log File Name:        "+logFileName);
            gaVars.add(" Log Interval:         "+logInterval);
            gaVars.add(" Memespace IP:         "+memespaceIP);
            gaVars.add(" Memespace Port:       "+memespacePort);
            gaVars.add(" Migration Rate:       "+migrationRate);
			gaVars.add(" Use One-Fifth Rule?:  "+useOneFifthRule);
         }
         else if(gaName.equals("Generational GA with BLX")) {
            if(selIndex == 1){
               popSize = new Integer((int)value);
            }	
            else if(selIndex == 2) {
               numElites = new Integer((int)value);
            }
            else if(selIndex == 3) {
               crossoverUsageRate = new Float(value);
            }
            else if(selIndex == 4) {
               blxAlpha = new Float(value);
            }
            else if(selIndex == 5) {
               mutUsageRate = new Float(value);
            }
            else if(selIndex == 6) {
               mutRate = new Float(value);
            }
            else if(selIndex == 7) {
               mutRange = new Float(value);
            }
            else if(selIndex == 8){
               numEval = new Integer((int)value);
            }
            else if(selIndex == 9) {
               numRuns = new Integer((int)value);
            }
            else if(selIndex == 10) {
            	numThreads = new Integer((int)value);
            }
            else if(selIndex == 11) {
               logFileName = text;
            }
            else if(selIndex == 12) {
               logInterval = new Integer((int)value);
            }
            else if(selIndex == 13) {
               memespaceIP = text;
            }
            else if(selIndex == 14) {
               memespacePort = new Integer((int)value);
            }
            else if(selIndex == 15) {
               migrationRate = value;
            }
            else if(selIndex == 16) {
               useOneFifthRule = text.toUpperCase();
            }
            gaVars.add(" Population Size:       "+popSize);
            gaVars.add(" Number of Elites:      "+numElites);
            gaVars.add(" Crossover Usage Rate:  "+crossoverUsageRate);
            gaVars.add(" BLX-alpha:             "+blxAlpha);
            gaVars.add(" Mutation Usage Rate:   "+mutUsageRate);
            gaVars.add(" Mutation Rate:         "+mutRate);
            gaVars.add(" Mutation Range:        "+mutRange);
            gaVars.add(" Total Evaluations:     "+numEval);
            gaVars.add(" Number of Runs:        "+numRuns);
            gaVars.add(" Number of Threads:     "+numThreads);
            gaVars.add(" Log File Name:         "+logFileName);
            gaVars.add(" Log Interval:          "+logInterval);
            gaVars.add(" Memespace IP:          "+memespaceIP);
            gaVars.add(" Memespace Port:        "+memespacePort);
            gaVars.add(" Migration Rate:        "+migrationRate);
			gaVars.add(" Use One-Fifth Rule?:   "+useOneFifthRule);
         }
         else if(gaName.equals("Steady-generational GA with BLX")) {
            if(selIndex == 1){
               popSize = new Integer((int)value);
            }	
            else if(selIndex == 2) {
               crossoverUsageRate = new Float(value);
            }
            else if(selIndex == 3) {
               blxAlpha = new Float(value);
            }
            else if(selIndex == 4) {
               mutUsageRate = new Float(value);
            }
            else if(selIndex == 5) {
               mutRate = new Float(value);
            }
            else if(selIndex == 6) {
               mutRange = new Float(value);
            }
            else if(selIndex == 7){
               numEval = new Integer((int)value);
            }
            else if(selIndex == 8) {
               numRuns = new Integer((int)value);
            }
            else if(selIndex == 9) {
               logFileName = text;
            }
            else if(selIndex == 10) {
               logInterval = new Integer((int)value);
            }
            else if(selIndex == 11) {
               memespaceIP = text;
            }
            else if(selIndex == 12) {
               memespacePort = new Integer((int)value);
            }
            else if(selIndex == 13) {
               migrationRate = value;
            }
            else if(selIndex == 14) {
               useOneFifthRule = text.toUpperCase();
            }
            gaVars.add(" Population Size:       "+popSize);
            gaVars.add(" Crossover Usage Rate:  "+crossoverUsageRate);
            gaVars.add(" BLX-alpha:             "+blxAlpha);
            gaVars.add(" Mutation Usage Rate:   "+mutUsageRate);
            gaVars.add(" Mutation Rate:         "+mutRate);
            gaVars.add(" Mutation Range:        "+mutRange);
            gaVars.add(" Total Evaluations:     "+numEval);
            gaVars.add(" Number of Runs:        "+numRuns);
            gaVars.add(" Number of Threads:     "+numThreads);
            gaVars.add(" Log File Name:         "+logFileName);
            gaVars.add(" Log Interval:          "+logInterval);
            gaVars.add(" Memespace IP:          "+memespaceIP);
            gaVars.add(" Memespace Port:        "+memespacePort);
            gaVars.add(" Migration Rate:        "+migrationRate);
			gaVars.add(" Use One-Fifth Rule?:   "+useOneFifthRule);
         }
         else if(gaName.equals("Steady-state GA")) {
            if(selIndex == 1){
               popSize = new Integer((int)value);
            }	
            else if(selIndex == 2) {
               crossoverUsageRate = new Float(value);
            }
            else if(selIndex == 3) {
               mutUsageRate = new Float(value);
            }
            else if(selIndex == 4) {
               mutRate = new Float(value);
            }
            else if(selIndex == 5) {
               mutRange = new Float(value);
            }
            else if(selIndex == 6){
               numEval = new Integer((int)value);
            }
            else if(selIndex == 7) {
               numRuns = new Integer((int)value);
            }
            else if(selIndex == 8) {
            	numThreads = new Integer((int)value);
            }
            else if(selIndex == 9) {
               logFileName = text;
            }
            else if(selIndex == 10) {
               logInterval = new Integer((int)value);
            }
            else if(selIndex == 11) {
               memespaceIP = text;
            }
            else if(selIndex == 12) {
               memespacePort = new Integer((int)value);
            }
            else if(selIndex == 13) {
               migrationRate = value;
            }
            else if(selIndex == 14) {
               useOneFifthRule = text.toUpperCase();
            }
            gaVars.add(" Population Size:        "+popSize);
            gaVars.add(" Crossover Usage Rate:   "+crossoverUsageRate);
            gaVars.add(" Mutation Usage Rate:    "+mutUsageRate);
            gaVars.add(" Mutation Rate:          "+mutRate);
            gaVars.add(" Mutation Range:         "+mutRange);
            gaVars.add(" Total Evaluations:      "+numEval);
            gaVars.add(" Number of Runs:         "+numRuns);
            gaVars.add(" Number of Threads:      "+numThreads);
            gaVars.add(" Log File Name:          "+logFileName);
            gaVars.add(" Log Interval:           "+logInterval);
            gaVars.add(" Memespace IP:           "+memespaceIP);
            gaVars.add(" Memespace Port:         "+memespacePort);
            gaVars.add(" Migration Rate:         "+migrationRate);
			gaVars.add(" Use One-Fifth Rule?:    "+useOneFifthRule);
         }
         else if(gaName.equals("PSO")) {
            if(selIndex == 1){
               popSize = new Integer((int)value);
            }	
            else if(selIndex == 2) {
               neighborhoodSize = new Integer((int)value);
            }
            else if(selIndex == 3) {
               constCoeff = new Integer((int)value);
            }
            else if(selIndex == 4){
               numEval = new Integer((int)value);
            }
            else if(selIndex == 5) {
               numRuns = new Integer((int)value);
            }
            else if(selIndex == 6) {
            	numThreads = new Integer((int)value);
            }
            else if(selIndex == 7) {
               logFileName = text;
            }
            else if(selIndex == 8) {
               logInterval = new Integer((int)value);
            }
            else if(selIndex == 9) {
               memespaceIP = text;
            }
            else if(selIndex == 10) {
               memespacePort = new Integer((int)value);
            }
            else if(selIndex == 11) {
               migrationRate = value;
            }
            gaVars.add(" Number of Particles:          "+popSize);
            gaVars.add(" Neighborhood Size:            "+neighborhoodSize);
            gaVars.add(" Constriction Coefficient?:    "+constCoeff);
            gaVars.add(" Total Evaluations:            "+numEval);
            gaVars.add(" Number of Runs:               "+numRuns);
            gaVars.add(" Number of Threads:            "+numThreads);
            gaVars.add(" Log File Name:                "+logFileName);
            gaVars.add(" Log Interval:                 "+logInterval);
            gaVars.add(" Memespace IP:                 "+memespaceIP);
            gaVars.add(" Memespace Port:               "+memespacePort);
            gaVars.add(" Migration Rate:               "+migrationRate);
         }
         else if(gaName.equals("Generational DEA")) {
            if(selIndex == 1){
               popSize = new Integer((int)value);
            }	
            else if(selIndex == 2) {
               numElites = new Integer((int)value);
            }
            else if(selIndex == 3) {
               phi = new Float(value);
            }
            else if(selIndex == 4) {
               mutUsageRate = new Float(value);
            }
            else if(selIndex == 5) {
               mutRate = new Float(value);
            }
            else if(selIndex == 6) {
               mutRange = new Float(value);
            }
            else if(selIndex == 7){
               numEval = new Integer((int)value);
            }
            else if(selIndex == 8) {
               numRuns = new Integer((int)value);
            }
            else if(selIndex == 9) {
            	numThreads = new Integer((int)value);
            }
            else if(selIndex == 10) {
               logFileName = text;
            }
            else if(selIndex == 11) {
               logInterval = new Integer((int)value);
            }
            else if(selIndex == 12) {
               memespaceIP = text;
            }
            else if(selIndex == 13) {
               memespacePort = new Integer((int)value);
            }
            else if(selIndex == 14) {
               migrationRate = value;
            }
            gaVars.add(" Population Size:       "+popSize);
            gaVars.add(" Number of Elites:      "+numElites);
            gaVars.add(" Phi:                   "+phi);
            gaVars.add(" Mutation Usage Rate:   "+mutUsageRate);
            gaVars.add(" Mutation Rate:         "+mutRate);
            gaVars.add(" Mutation Range:        "+mutRange);
            gaVars.add(" Total Evaluations:     "+numEval);
            gaVars.add(" Number of Runs:        "+numRuns);
            gaVars.add(" Number of Threads:     "+numThreads);
            gaVars.add(" Log File Name:         "+logFileName);
            gaVars.add(" Log Interval:          "+logInterval);
            gaVars.add(" Memespace IP:          "+memespaceIP);
            gaVars.add(" Memespace Port:        "+memespacePort);
            gaVars.add(" Migration Rate:        "+migrationRate);
         }
         else if(gaName.equals("Steady-state DEA")) {
            if(selIndex == 1){
               popSize = new Integer((int)value);
            }	
            else if(selIndex == 2) {
               phi = new Float(value);
            }
            else if(selIndex == 3) {
               mutUsageRate = new Float(value);
            }
            else if(selIndex == 4) {
               mutRate = new Float(value);
            }
            else if(selIndex == 5) {
               mutRange = new Float(value);
            }
            else if(selIndex == 6){
               numEval = new Integer((int)value);
            }
            else if(selIndex == 7) {
               numRuns = new Integer((int)value);
            }
            else if(selIndex == 8) {
            	numThreads = new Integer((int)value);
            }
            else if(selIndex == 9) {
               logFileName = text;
            }
            else if(selIndex == 10) {
               logInterval = new Integer((int)value);
            }
            else if(selIndex == 11) {
               memespaceIP = text;
            }
            else if(selIndex == 12) {
               memespacePort = new Integer((int)value);
            }
            else if(selIndex == 13) {
               migrationRate = value;
            }
            gaVars.add(" Population Size:       "+popSize);
            gaVars.add(" Phi:                   "+phi);
            gaVars.add(" Mutation Usage Rate:   "+mutUsageRate);
            gaVars.add(" Mutation Rate:         "+mutRate);
            gaVars.add(" Mutation Range:        "+mutRange);
            gaVars.add(" Total Evaluations:     "+numEval);
            gaVars.add(" Number of Runs:        "+numRuns);
            gaVars.add(" Number of Threads:     "+numThreads);
            gaVars.add(" Log File Name:         "+logFileName);
            gaVars.add(" Log Interval:          "+logInterval);
            gaVars.add(" Memespace IP:          "+memespaceIP);
            gaVars.add(" Memespace Port:        "+memespacePort);
            gaVars.add(" Migration Rate:        "+migrationRate);
         }
         else if(gaName.equals("Elitist EDA")) {
            if(selIndex == 1){
               popSize = new Integer((int)value);
            }	
            else if(selIndex == 2) {
               numElites = new Integer((int)value);
            }
            else if(selIndex == 3){
               numEval = new Integer((int)value);
            }
            else if(selIndex == 4) {
               numRuns = new Integer((int)value);
            }
            else if(selIndex == 5) {
            	numThreads = new Integer((int)value);
            }
            else if(selIndex == 6) {
               logFileName = text.toUpperCase();
            }
            else if(selIndex == 7) {
               logInterval = new Integer((int)value);
            }
            else if(selIndex == 8) {
               memespaceIP = text;
            }
            else if(selIndex == 9) {
               memespacePort = new Integer((int)value);
            }
            else if(selIndex == 10) {
               migrationRate = value;
            }
            gaVars.add(" Population Size:     "+popSize);
            gaVars.add(" Number of Elites:    "+numElites);
            gaVars.add(" Total Evaluations:   "+numEval);
            gaVars.add(" Number of Runs:      "+numRuns);
            gaVars.add(" Number of Threads:   "+numThreads);
            gaVars.add(" Log File Name:       "+logFileName);
            gaVars.add(" Log Interval:        "+logInterval);
            gaVars.add(" Memespace IP:        "+memespaceIP);
            gaVars.add(" Memespace Port:      "+memespacePort);
            gaVars.add(" Migration Rate:      "+migrationRate);
         }
      
         envVarChange.setText("");
         pg3Var.setListData(gaVars);
      }
   
       private void reloadParameterList(String gaName) {
         gaVars.clear();
         gaVars.add("Parameters:");
         if(gaName.equals("Standard EP")) {
            gaVars.add(" Population Size:    "+popSize);
            gaVars.add(" Mutation Rate:      "+mutRate);
            gaVars.add(" Mutation Range:     "+mutRange);
            gaVars.add(" Total Evaluations:  "+numEval);
            gaVars.add(" Number of Runs:     "+numRuns);
            gaVars.add(" Number of Threads:  "+numThreads);
            gaVars.add(" Log File Name:      "+logFileName);
            gaVars.add(" Log Interval:       "+logInterval);
            gaVars.add(" Memespace IP:       "+memespaceIP);
            gaVars.add(" Memespace Port:     "+memespacePort);
            gaVars.add(" Migration Rate:     "+migrationRate);
         }
         else if(gaName.equals("Continuous Standard EP")) {
            gaVars.add(" Population Size:    "+popSize);
            gaVars.add(" Mutation Rate:      "+mutRate);
            gaVars.add(" Mutation Range:     "+mutRange);
            gaVars.add(" Total Evaluations:  "+numEval);
            gaVars.add(" Number of Runs:     "+numRuns);
            gaVars.add(" Number of Threads:  "+numThreads);
            gaVars.add(" Log File Name:      "+logFileName);
            gaVars.add(" Log Interval:       "+logInterval);
            gaVars.add(" Memespace IP:       "+memespaceIP);
            gaVars.add(" Memespace Port:     "+memespacePort);
            gaVars.add(" Migration Rate:     "+migrationRate);
         }
         else if(gaName.equals("Meta-EP")) {
            gaVars.add(" Population Size:    "+popSize);
            gaVars.add(" Eta Mutation Rate:  "+mutRate);
            gaVars.add(" Total Evaluations:  "+numEval);
            gaVars.add(" Number of Runs:     "+numRuns);
            gaVars.add(" Number of Threads:  "+numThreads);
            gaVars.add(" Log File Name:      "+logFileName);
            gaVars.add(" Log Interval:       "+logInterval);
            gaVars.add(" Memespace IP:       "+memespaceIP);
            gaVars.add(" Memespace Port:     "+memespacePort);
            gaVars.add(" Migration Rate:     "+migrationRate);
         }
         else if(gaName.equals("Continuous Meta-EP")) {
            gaVars.add(" Population Size:    "+popSize);
            gaVars.add(" Eta Mutation Rate:  "+mutRate);
            gaVars.add(" Total Evaluations:  "+numEval);
            gaVars.add(" Number of Runs:     "+numRuns);
            gaVars.add(" Number of Threads:  "+numThreads);
            gaVars.add(" Log File Name:      "+logFileName);
            gaVars.add(" Log Interval:       "+logInterval);
            gaVars.add(" Memespace IP:       "+memespaceIP);
            gaVars.add(" Memespace Port:     "+memespacePort);
            gaVars.add(" Migration Rate:     "+migrationRate);
         }
         else if(gaName.equals("Steady-state GA with BLX")) {
            gaVars.add(" Population Size:      "+popSize);
            gaVars.add(" Crossover Usage Rate: "+crossoverUsageRate);
            gaVars.add(" BLX-alpha:            "+blxAlpha);
            gaVars.add(" Mutation Usage Rate:  "+mutUsageRate);
            gaVars.add(" Mutation Rate:        "+mutRate);
            gaVars.add(" Mutation Range:       "+mutRange);
            gaVars.add(" Total Evaluations:    "+numEval);
            gaVars.add(" Number of Runs:       "+numRuns);
            gaVars.add(" Number of Threads:    "+numThreads);
            gaVars.add(" Log File Name:        "+logFileName);
            gaVars.add(" Log Interval:         "+logInterval);
            gaVars.add(" Memespace IP:         "+memespaceIP);
            gaVars.add(" Memespace Port:       "+memespacePort);
            gaVars.add(" Migration Rate:       "+migrationRate);
			gaVars.add(" Use One-Fifth Rule?:  "+useOneFifthRule);
         }
         else if(gaName.equals("Generational GA with BLX")) {
            gaVars.add(" Population Size:      "+popSize);
            gaVars.add(" Number of Elites:     "+numElites);
            gaVars.add(" Crossover Usage Rate: "+crossoverUsageRate);
            gaVars.add(" BLX-alpha:            "+blxAlpha);
            gaVars.add(" Mutation Usage Rate:  "+mutUsageRate);
            gaVars.add(" Mutation Rate:        "+mutRate);
            gaVars.add(" Mutation Range:       "+mutRange);
            gaVars.add(" Total Evaluations:    "+numEval);
            gaVars.add(" Number of Runs:       "+numRuns);
            gaVars.add(" Number of Threads:    "+numThreads);
            gaVars.add(" Log File Name:        "+logFileName);
            gaVars.add(" Log Interval:         "+logInterval);
            gaVars.add(" Memespace IP:         "+memespaceIP);
            gaVars.add(" Memespace Port:       "+memespacePort);
            gaVars.add(" Migration Rate:       "+migrationRate);
			gaVars.add(" Use One-Fifth Rule?:  "+useOneFifthRule);
         }
         else if(gaName.equals("Steady-generational GA with BLX")) {
            gaVars.add(" Population Size:      "+popSize);
            gaVars.add(" Crossover Usage Rate: "+crossoverUsageRate);
            gaVars.add(" BLX-alpha:            "+blxAlpha);
            gaVars.add(" Mutation Usage Rate:  "+mutUsageRate);
            gaVars.add(" Mutation Rate:        "+mutRate);
            gaVars.add(" Mutation Range:       "+mutRange);
            gaVars.add(" Total Evaluations:    "+numEval);
            gaVars.add(" Number of Runs:       "+numRuns);
            gaVars.add(" Number of Threads:    "+numThreads);
            gaVars.add(" Log File Name:        "+logFileName);
            gaVars.add(" Log Interval:         "+logInterval);
            gaVars.add(" Memespace IP:         "+memespaceIP);
            gaVars.add(" Memespace Port:       "+memespacePort);
            gaVars.add(" Migration Rate:       "+migrationRate);
			gaVars.add(" Use One-Fifth Rule?:  "+useOneFifthRule);
         }
         else if(gaName.equals("Steady-state GA")) {
            gaVars.add(" Population Size:      "+popSize);
            gaVars.add(" Crossover Usage Rate: "+crossoverUsageRate);
            gaVars.add(" Mutation Usage Rate:  "+mutUsageRate);
            gaVars.add(" Mutation Rate:        "+mutRate);
            gaVars.add(" Mutation Range:       "+mutRange);
            gaVars.add(" Total Evaluations:    "+numEval);
            gaVars.add(" Number of Runs:       "+numRuns);
            gaVars.add(" Number of Threads:    "+numThreads);
            gaVars.add(" Log File Name:        "+logFileName);
            gaVars.add(" Log Interval:         "+logInterval);
            gaVars.add(" Memespace IP:         "+memespaceIP);
            gaVars.add(" Memespace Port:       "+memespacePort);
            gaVars.add(" Migration Rate:       "+migrationRate);
			gaVars.add(" Use One-Fifth Rule?:  "+useOneFifthRule);
         }
         else if(gaName.equals("PSO")) {
            gaVars.add(" Number of Particles:       "+popSize);
            gaVars.add(" Neighborhood Size:         "+neighborhoodSize);
            gaVars.add(" Constriction Coefficient?: "+constCoeff);
            gaVars.add(" Total Evaluations:         "+numEval);
            gaVars.add(" Number of Runs:            "+numRuns);
            gaVars.add(" Number of Threads:         "+numThreads);
            gaVars.add(" Log File Name:             "+logFileName);
            gaVars.add(" Log Interval:              "+logInterval);
            gaVars.add(" Memespace IP:              "+memespaceIP);
            gaVars.add(" Memespace Port:            "+memespacePort);
            gaVars.add(" Migration Rate:            "+migrationRate);
         }
         else if(gaName.equals("Generational DEA")) {
            gaVars.add(" Population Size:      "+popSize);
            gaVars.add(" Number of Elites:     "+numElites);
            gaVars.add(" Phi:                  "+phi);
            gaVars.add(" Mutation Usage Rate:  "+mutUsageRate);
            gaVars.add(" Mutation Rate:        "+mutRate);
            gaVars.add(" Mutation Range:       "+mutRange);
            gaVars.add(" Total Evaluations:    "+numEval);
            gaVars.add(" Number of Runs:       "+numRuns);
            gaVars.add(" Number of Threads:    "+numThreads);
            gaVars.add(" Log File Name:        "+logFileName);
            gaVars.add(" Log Interval:         "+logInterval);
            gaVars.add(" Memespace IP:         "+memespaceIP);
            gaVars.add(" Memespace Port:       "+memespacePort);
            gaVars.add(" Migration Rate:       "+migrationRate);
         }
         else if(gaName.equals("Steady-state DEA")) {
            gaVars.add(" Population Size:      "+popSize);
            gaVars.add(" Phi:                  "+phi);
            gaVars.add(" Mutation Usage Rate:  "+mutUsageRate);
            gaVars.add(" Mutation Rate:        "+mutRate);
            gaVars.add(" Mutation Range:       "+mutRange);
            gaVars.add(" Total Evaluations:    "+numEval);
            gaVars.add(" Number of Runs:       "+numRuns);
            gaVars.add(" Number of Threads:    "+numThreads);
            gaVars.add(" Log File Name:        "+logFileName);
            gaVars.add(" Log Interval:         "+logInterval);
            gaVars.add(" Memespace IP:         "+memespaceIP);
            gaVars.add(" Memespace Port:       "+memespacePort);
            gaVars.add(" Migration Rate:       "+migrationRate);
         }
         else if(gaName.equals("Elitist EDA")) {
            gaVars.add(" Population Size:      "+popSize);
            gaVars.add(" Number of Elites:     "+numElites);
            gaVars.add(" Total Evaluations:    "+numEval);
            gaVars.add(" Number of Runs:       "+numRuns);
            gaVars.add(" Number of Threads:    "+numThreads);
            gaVars.add(" Log File Name:        "+logFileName);
            gaVars.add(" Log Interval:         "+logInterval);
            gaVars.add(" Memespace IP:         "+memespaceIP);
            gaVars.add(" Memespace Port:       "+memespacePort);
            gaVars.add(" Migration Rate:       "+migrationRate);
         }
      
         pg3Var.setListData(gaVars);
      }

       public JFrame getCurrentFrame() {
    	   return currentFrame;
       }
       
       public XTOOLSRunnable getECThread(){
    	   return ecThread;
       }
       
       public boolean hasStarted(){
    	   return hasStarted;
       }
       
       public ThreadTerminator getThreadTerminator(){
    	   return tt;
       }
	
       public XTOOLSECMonitor getMonitor(){
    	   return xtoolsECMon;
       }
       
       public Population getPopulation(){
    	   if(population != null) return population;
    	   else return pso.getPopulation();
       }

       public int getNumFunctionEvaluations() {
    	    int numEvals = 0;
    	    int numRunsCompleted = numEval.intValue() * ecThread.getNumRuns();
    	    if(population != null){
    	    	numEvals = population.getNumFunctionEvaluations() + numRunsCompleted;
    	    }else{
    	    	if(pso != null){
    	    		numEvals = pso.getPopulation().getNumFunctionEvaluations() + numRunsCompleted;
    	    	}
    	    }
    	    return numEvals;
       }

	public void destroy() {
		if(pg1 != null){
			pg1.setVisible(false);
			pg1.removeAll();
			pg1 = null;
		}
		if(pg3 != null){
			pg3.setVisible(false);
			pg3.removeAll();
			pg3 = null;
		}
		if(error != null){
			error.setVisible(false);
			error.removeAll();
			error = null;
		}
		if(currentFrame != null){
			currentFrame.setVisible(false);
			currentFrame.removeAll();
			currentFrame = null;
		}
		System.gc();
	}

	public JButton getExecuteButton() {
		return pg3Next;
	}
}
	
