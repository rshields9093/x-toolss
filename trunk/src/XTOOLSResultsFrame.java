//package edu.auburn.eng.aci.xtoolss;


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
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.Color;
import java.awt.Dimension;


public class XTOOLSResultsFrame extends JFrame implements ActionListener, WindowListener {
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
		mainPanel.setLayout(new BorderLayout());
		scrollPane = new JScrollPane(textArea,
									JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
									JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVisible(true);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		
		graphCanvas = new GraphCanvas();
		
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BorderLayout());
//		controlPanel.add(new JLabel("See Results"), BorderLayout.NORTH);
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
	 	setVisible(true);	
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
			graphCanvas.setSize(new Dimension(485, 430));
			repaint();
		}
		else if(e.getActionCommand().equals("Generation Best Only")) {
			textArea.setText(bestIndividualInfo);
			textArea.setCaretPosition(textArea.getDocument().getLength());
			mainPanel.removeAll();
			mainPanel.add(scrollPane, BorderLayout.CENTER);
			repaint();
		}
		else if(e.getActionCommand().equals("Current Population")) {
			textArea.setText(currentPopulationInfo);
			textArea.setCaretPosition(0);
			mainPanel.removeAll();
			mainPanel.add(scrollPane, BorderLayout.CENTER);
			repaint();
		}
	}
	
	
	public void windowClosed(WindowEvent e) {}
	
	public void windowClosing(WindowEvent e) {
		radioButton[1].setSelected(true);
		bestIndividualInfo = "";
		currentPopulationInfo = "";
		firePropertyChange("WindowClosed", 0, 1);
		setVisible(false);
	}
	public void windowActivated(WindowEvent e) {}
	
	public void windowDeactivated(WindowEvent e) {}
	
	public void windowDeiconified(WindowEvent e) {}
	
	public void windowIconified(WindowEvent e) {}
	
	public void windowOpened(WindowEvent e) {}
	
	
	public static void main(String[] args) {
		XTOOLSResultsFrame window = new XTOOLSResultsFrame();
		window.addPoint(new Point2D.Double(3.0, 140.0), Color.blue);
		window.addPoint(new Point2D.Double(100.0, 40.0), Color.red);
	}
}

