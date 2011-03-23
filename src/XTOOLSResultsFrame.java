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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.Color;
import java.awt.Dimension;


public class XTOOLSResultsFrame extends JFrame implements ActionListener, WindowListener, ComponentListener {
	private String bestIndividualInfo;
	private String currentPopulationInfo;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JRadioButton[] radioButton;
	private GraphCanvas graphCanvas;
	private JPanel mainPanel;
	
	public XTOOLSResultsFrame() {
		bestIndividualInfo = "";
		currentPopulationInfo = "";
		textArea = new JTextArea();
		textArea.setEditable(false);
		mainPanel = new JPanel();
		mainPanel.addComponentListener(this);
		mainPanel.setLayout(new BorderLayout());
		scrollPane = new JScrollPane(textArea,
									JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
									JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVisible(true);
		scrollPane.setMinimumSize(new Dimension(485, 430));
		
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		
		graphCanvas = new GraphCanvas();
		graphCanvas.setMinimumSize(new Dimension(485, 430));
		
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BorderLayout());
		radioButton = new JRadioButton[3];
		radioButton[0] = new JRadioButton("Convergence Graph");
		radioButton[0].setActionCommand("Convergence Graph");
		radioButton[1] = new JRadioButton("Generation Best Only");
		radioButton[1].setActionCommand("Generation Best Only");
		radioButton[1].setSelected(true);
		radioButton[2] = new JRadioButton("Current Population");
		radioButton[2].setActionCommand("Current Population");
		ButtonGroup group = new ButtonGroup();
		for(int i = 0; i < radioButton.length; i++) {
			radioButton[i].addActionListener(this);
			group.add(radioButton[i]);
		}
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, radioButton.length));
		for(int i = 0; i < radioButton.length; i++) {
			buttonPanel.add(radioButton[i]);
		}
		controlPanel.add(buttonPanel, BorderLayout.CENTER);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		getContentPane().add(controlPanel, BorderLayout.NORTH);
	 	setResizable(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this); 
		setTitle("X-TOOLSS Results");
		setSize(500, 500);
	 	//setVisible(true);	
	}
	
	
	
	public String getBestIndividualInfo() {
		return bestIndividualInfo;
	}	
	
	public void setBestIndividualInfo(String info) {
		bestIndividualInfo = info;
		updateTextArea();
	}

	public String getCurrentPopulationInfo() {
		return currentPopulationInfo;
	}	
	
	public void setCurrentPopulationInfo(String info) {
		currentPopulationInfo = info;
		updateTextArea();
	}
	
	public void addPoint(Point2D.Double p, Color c) {
		graphCanvas.addPoint(p, c);
		graphCanvas.setInteractive(true);
	}
	
	public void clearPoints() {
		graphCanvas.clearPoints();
	}

	private void updateTextArea() {
		if(radioButton[1].isSelected()) {
			textArea.setText(bestIndividualInfo);
			textArea.setCaretPosition(textArea.getDocument().getLength());
		}
		else if(radioButton[2].isSelected()) {
			textArea.setText(currentPopulationInfo);
			textArea.setCaretPosition(0);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Convergence Graph")) {
			mainPanel.removeAll();
			mainPanel.add(graphCanvas, BorderLayout.CENTER);
			
			pack();
		}
		else if(e.getActionCommand().equals("Generation Best Only")) {
			textArea.setText(bestIndividualInfo);
			textArea.setCaretPosition(textArea.getDocument().getLength());
			mainPanel.removeAll();
			scrollPane.setMinimumSize(new Dimension(485, 430));
			mainPanel.add(scrollPane, BorderLayout.CENTER);
			pack();
		}
		else if(e.getActionCommand().equals("Current Population")) {
			textArea.setText(currentPopulationInfo);
			textArea.setCaretPosition(0);
			mainPanel.removeAll();
			scrollPane.setMinimumSize(new Dimension(485, 430));
			mainPanel.add(scrollPane, BorderLayout.CENTER);
			pack();
		}
	}
	
	
	public void windowClosed(WindowEvent e) {}
	
	public void windowClosing(WindowEvent e) {
		/*radioButton[1].setSelected(true);
		bestIndividualInfo = "";
		currentPopulationInfo = "";
		firePropertyChange("WindowClosed", 0, 1);*/
		setVisible(false);
	}
	public void windowActivated(WindowEvent e) {}
	
	public void windowDeactivated(WindowEvent e) {}
	
	public void windowDeiconified(WindowEvent e) {}
	
	public void windowIconified(WindowEvent e) {}
	
	public void windowOpened(WindowEvent e) {}



	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void componentResized(ComponentEvent arg0) {
		scrollPane.setPreferredSize(mainPanel.getSize());
		graphCanvas.setPreferredSize(mainPanel.getSize());
	}



	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	/*public static void main(String[] args) {
		XTOOLSResultsFrame window = new XTOOLSResultsFrame();
		window.addPoint(new Point2D.Double(3.0, 140.0), Color.blue);
		window.addPoint(new Point2D.Double(100.0, 40.0), Color.red);
	}*/
}

