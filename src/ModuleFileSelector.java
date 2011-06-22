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
import javax.swing.JDialog;
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


public class ModuleFileSelector extends JDialog implements ActionListener, DropTargetListener
{
	Font font = new Font("f", Font.BOLD, 12);
    Font setwfont = new Font( "Monospaced", Font.PLAIN, 12 ); 
    Vector xlsFiles, xtsNames, modules, variables, variableNames, geneticVars, geneticLoc, avAlias, varType;
    Vector isConstant, isAlias, originalValues, actualLoc, modNum;
    int fileCount, maxVarLength, moduleLocations[];
    int[] avAliasLoc;
    String allowedFileType = ".xts";
	
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
	private String xtsDir;
	private String fileName;
	private boolean isAdd = true;
	EtchedBorder b1;
	
    JScrollPane scrollPane, scrollvar,  deVar, errScroll, pg3scrollPane, exeSCPane;
    JFileChooser fc;

	public ModuleFileSelector() {
		init();
	}

	//**********************************************************************
	//
	// START GUI
	//
	//**********************************************************************   

	private void init(){ 
		// setup file chooser
		fileName = "";
		fc = new JFileChooser();
		fc.addChoosableFileFilter(new MyFilter(".app"));
		fc.setAcceptAllFileFilterUsed(false);

		setResizable(false);

		b1 = new EtchedBorder(getBackground(), getBackground());

		initMenu1();
		setupMenu1();

		//pack();

		setLocationRelativeTo(null);
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
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(
				new WindowAdapter() { 
					public void windowClosing(WindowEvent e) {
						setVisible(false);
						dispose();
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

		pg1Back = new JButton("    Back    ", null); //, backIcon
		pg1Back.setEnabled(false);  

		pg1Next = new JButton("    Next    ", null); //, nextIcon
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
		fc.addChoosableFileFilter(new MyFilter(".xts"));
		fc.setAcceptAllFileFilterUsed(false);									


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
		getContentPane().add(pg1BG);
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

	

	public void addModuleFile(String fileName){
		if(isAdd && !(fileName).equals("")){
			if(xlsFiles.indexOf(fileName) < 0){
				xlsFiles.add(fileName);
				xtsDir = (new File(fileName)).getParent();
				pg1Mod.setListData(xlsFiles);
				pg1AddFile.setText("");
				//pg1Add.setEnabled(false);
				pg1AddFile.setToolTipText("Press \"Browse\" to select a file.");
				isAdd = false;
				pg1Add.setText("Delete");
			}
			else{// Module already in list
				JOptionPane.showMessageDialog(this,
						"The File\n"+
						fileName+
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
			int returnVal = fc.showOpenDialog(ModuleFileSelector.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				pg1AddFile.setText(file.getAbsolutePath());
				pg1Add.setEnabled(true);
				fileName = file.getAbsolutePath();
				//System.out.println(fileName);
				pg1AddFile.setToolTipText(fileName);

			}
		}else if (e.getSource() == pg1Add){// Add module to list
			addModuleFile(pg1AddFile.getText());
		}else if (e.getSource() == pg1Delete){
			int[] index = pg1Mod.getSelectedIndices();
			xlsFiles.remove(0);
			isAdd = true;
			pg1Add.setText("Add");
			pg1Mod.setListData(xlsFiles);
		}else if (e.getSource() == pg1MoveUp){
			if(pg1Mod.getSelectedIndex() > 0){
				int index = pg1Mod.getSelectedIndex();
				Object temp = xlsFiles.get(index);
				xlsFiles.set(index, xlsFiles.get(index-1));
				xlsFiles.set(index-1, temp);
				pg1Mod.setListData(xlsFiles);
				pg1Mod.setSelectedIndex(index-1);
			}
		}else if (e.getSource() == pg1MoveDown){
			//if(pg1Mod.getSelectedIndex() < fileCount-1){
				int index = pg1Mod.getSelectedIndex();
				Object temp = xlsFiles.get(index);
				xlsFiles.set(index, xlsFiles.get(index+1));
				xlsFiles.set(index+1, temp);
				pg1Mod.setListData(xlsFiles);
				pg1Mod.setSelectedIndex(index+1);
			//}
		}
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
							/*if(!isAdd){
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
                        }*/
							//System.out.println(fileName);
							addModuleFile(fileName);
						}else{
							JOptionPane.showMessageDialog(this,
									"Error: Module file must have an .xts extension.",
									"",
									JOptionPane.ERROR_MESSAGE); 
							break;
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
			int returnVal = fc2.showSaveDialog(ModuleFileSelector.this);

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
					System.err.println("File not saved... no file name given.");
				}

				if(file.exists()){//ask if they want to save over
					n = JOptionPane.showConfirmDialog(this,
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
						//infoLoss = false;
						fw.close();
						//application.setFilePath(path);
					}

					catch (IOException e) {  
						System.err.println("ERROR: file could not be saved to location\n\t"
								+path);   
					}
				}

			}
		}

		return isSaved;
	}

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
}
