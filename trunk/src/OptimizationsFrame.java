import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;

public class OptimizationsFrame extends JFrame implements WindowListener{
	JScrollPane scrollPane;
	JPanel optPanel;
	JButton addOptButton;
	VerticalLayout layout;
	
	String version = "1.3  (Alpha 2)";
	String webAddr = "http://nxt.ncat.edu/";
	
	JMenuBar menuBar; 
    JMenu fileMenu, helpMenu;
    JMenuItem indexMenuItem, aboutMenuItem, exitMenu, newOptMenu;
    
	public OptimizationsFrame(){
		super();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(500,600);
		Dimension d = new Dimension(425,450);
		setMinimumSize(d);
		setTitle("X-TOOLSS "+version);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		
		//Set up menu options
		menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        
        newOptMenu = new JMenuItem("New Optimization");
        newOptMenu.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				scrollPane.revalidate();
				OptimizationPanel op = new OptimizationPanel();
				addOptimization(op);
			}
        });
        fileMenu.add(newOptMenu);
        
        exitMenu = new JMenuItem("Exit", KeyEvent.VK_T); 
        exitMenu.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				exitApplication();
			}
        });
        fileMenu.add(exitMenu);
        
        menuBar.add(fileMenu);
        helpMenu = new JMenu("Help");
		indexMenuItem = new JMenuItem("Index");
        indexMenuItem.addActionListener(
               new ActionListener(){
                  public void actionPerformed(ActionEvent event) {
                    XHelpFrame.showHelp();
                 }});
        aboutMenuItem = new JMenuItem("About X-TOOLSS");
        aboutMenuItem.addActionListener(
               new ActionListener(){
                  public void actionPerformed(ActionEvent event) {
                    JOptionPane.showMessageDialog(null, "X-TOOLSS "+version+"\nWebsite: "+webAddr+"\n\nCopyright (c) 2005 by the Applied Computational " + 
                       					"Intelligence Lab,\nDepartment of Computer Science & Software Engineering (Auburn " +
                       					"University)\ncreated by Mike Tinker, Gerry Dozier, Aaron Garrett, Lauren Goff, \nMike " + 
                       					"SanSoucie, and Patrick Hull.\n\nCopyright (c) 2011 Joshua Adams\n\n" +
                       					"X-TOOLSS is free software: you can redistribute it and/or modify \n" +
                       					"it under the terms of the GNU General Public License as published by \n" +
                       					"the Free Software Foundation, either version 3 of the License, or \n" +
                       					"(at your option) any later version.",
                       					"About X-TOOLSS", JOptionPane.INFORMATION_MESSAGE);
                 }});
        helpMenu.add(indexMenuItem);
        helpMenu.add(aboutMenuItem);
        menuBar.add(helpMenu);
		
		optPanel = new JPanel();
		optPanel.setSize(optPanel.getMinimumSize());
		
		layout = new VerticalLayout();
		optPanel.setLayout(layout);
		
		scrollPane = new JScrollPane(optPanel);
		
		addOptButton = new JButton("New Optimization");
		addOptButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				//Disable the button so multiple optimizations aren't clicked.
				setEnabled(false);
				scrollPane.revalidate();
				OptimizationPanel op = new OptimizationPanel();
				addOptimization(op);
				setEnabled(true);
			}
			
		});
		add(menuBar, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(addOptButton, BorderLayout.SOUTH);
		
		addWindowListener(this);
		
		setVisible(true);
	}
	
	public void addOptimization(OptimizationPanel op){
		optPanel.add(op);
		optPanel.setSize(optPanel.getMinimumSize());
		
		scrollPane.revalidate();
		//optPanelList.add(op);
	}
	
	public void removeOptimization(OptimizationPanel op){
		op.setVisible(false);
		op = null;
	}
	
	@Override
	public void windowActivated(WindowEvent e) {
		//Nothing to do here.
	}
	
	@Override
	public void windowClosed(WindowEvent e) {
		//Nothing to do here.
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		//Make sure the user wants to exit the application.
		exitApplication();
	}
	
	@Override
	public void windowDeactivated(WindowEvent e) {
		//Nothing to do here.
	}
	
	@Override
	public void windowDeiconified(WindowEvent e) {
		//Nothing to do here.
	}
	
	@Override
	public void windowIconified(WindowEvent e) {
		//Nothing to do here.
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
		//Nothing to do here.
	}
	
	private void exitApplication(){
		int n = JOptionPane.showConfirmDialog(optPanel,
                "Exit Current Application?\nThis will end any currently running optimization.",
                "Exit",
                JOptionPane.YES_NO_CANCEL_OPTION); 								
		if (n == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}
	
}
