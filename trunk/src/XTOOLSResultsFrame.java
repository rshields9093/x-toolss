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

import java.awt.Component;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

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
import java.util.Vector;

import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.demos.MultiTracing.AddPaintRemoveThread;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.demos.MultiTracing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import lib.genevot.ECMonitor;


//public class XTOOLSResultsFrame extends JFrame implements ActionListener, WindowListener, ComponentListener {
public class XTOOLSResultsFrame {
	private String bestIndividualInfo;
	private String currentPopulationInfo;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JRadioButton[] radioButton;
	private GraphCanvas graphCanvas;
	private JPanel mainPanel;
	private double bestFit = Double.POSITIVE_INFINITY;
        private String inFile;
        private MultiTracing wnd;
        private ITrace2D trace;
        private boolean jChart2DInstantiated = false;
        public AddPaintRemoveThread thr;
        public AddPaintRemoveThread thr1;
        protected XTOOLSECMonitor monitor;
 //       protected GraphDialog graphDialog;
        protected NewResultsFrame newResultsFrame;


	public XTOOLSResultsFrame(XTOOLSECMonitor ECmonitor, int numberOfGenerations) {
                
                this.monitor = ECmonitor;
		bestIndividualInfo = "";
		currentPopulationInfo = "";
                newResultsFrame = new NewResultsFrame(this);
              //  newResultsFrame = new NewResultsFrame(this);

	}

        /* jChart2DInstantiated is a flag that tells the caller if the jChart2D
         * chart has been instantiated. This keeps from referncing a NULL pointer.
         * Author: Scott Akridge
         */
        public boolean jChart2DInstantiated() {
          return jChart2DInstantiated;
        }


//  private void initComponents() {
//
//    jLayeredPane1 = new javax.swing.JLayeredPane();
//    jTabbedPane1 = new javax.swing.JTabbedPane();
//    jPanel1 = new javax.swing.JPanel();
//    jScrollPane1 = new javax.swing.JScrollPane();
//    jTextPane1 = new javax.swing.JTextPane();
////    jTabbedPaneGraph1 = new javax.swing.JTabbedPane();
//    jScrollPane2 = new javax.swing.JScrollPane();
//    jTextPane2 = new javax.swing.JTextPane();
//    CurrPop = new javax.swing.JTabbedPane();
////    jTabbedPaneGraph2 = new javax.swing.JTabbedPane();
////    jTabbedPaneGraph3 = new javax.swing.JTabbedPane();
////    jTabbedPaneGraph4 = new javax.swing.JTabbedPane();
////    jTabbedPaneGraph5 = new javax.swing.JTabbedPane();
//    jMenuBar1 = new javax.swing.JMenuBar();
//    jMenu1 = new javax.swing.JMenu();
//    jMenuItem1 = new javax.swing.JMenuItem();
//    jMenuItem2 = new javax.swing.JMenuItem();
//    jMenuItem3 = new javax.swing.JMenuItem();
//    jMenu3 = new javax.swing.JMenu();
//
//    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
//    addWindowListener(new java.awt.event.WindowAdapter() {
//      public void windowClosed(java.awt.event.WindowEvent evt) {
//        formWindowClosed(evt);
//      }
//    });
//
//    jTabbedPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));
//    jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
//    jTabbedPane1.setName("test2"); // NOI18N
//    jTabbedPane1.setPreferredSize(new java.awt.Dimension(700, 500));
//
//    jScrollPane1.setViewportView(jTextPane1);
//
//    org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
//    jPanel1.setLayout(jPanel1Layout);
//    jPanel1Layout.setHorizontalGroup(
//      jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
//      .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 673, Short.MAX_VALUE)
//    );
//    jPanel1Layout.setVerticalGroup(
//      jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
//      .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
//    );
//
//    jTabbedPane1.addTab("Gen Best", jPanel1);
//
//    jScrollPane2.setViewportView(jTextPane2);
//    jTabbedPane1.addTab("Current Pop", jScrollPane2);
//
////    jTabbedPane1.addTab("Graph1",jTabbedPaneGraph1 );
////    jTabbedPane1.addTab("Graph2", jTabbedPaneGraph2);
////    jTabbedPane1.addTab("Graph3", jTabbedPaneGraph3);
////    jTabbedPane1.addTab("Graph4", jTabbedPaneGraph4);
////    jTabbedPane1.addTab("Graph5", jTabbedPaneGraph5);
//
//    // CSA- set graphs invisible
////    CurrPop.setVisible(false);
////    jTabbedPaneGraph2.setVisible(false);
////    jTabbedPaneGraph3.setVisible(false);
////    jTabbedPaneGraph4.setVisible(false);
////    jTabbedPaneGraph5.setVisible(false);
//
//    jMenu1.setText("File");
//
//    jMenuItem1.setText("New Plot");
//    jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
//      public void actionPerformed(java.awt.event.ActionEvent evt) {
//        jMenuItem1ActionPerformed_newPlot(evt);
//      }
//    });
//    jMenu1.add(jMenuItem1);
//
//    jMenuItem2.setText("Generation Best");
//    jMenuItem2.addMouseListener(new java.awt.event.MouseAdapter() {
//      public void mouseReleased(java.awt.event.MouseEvent evt) {
//        jMenuItem2MouseReleased(evt);
//      }
//    });
//    jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
//      public void actionPerformed(java.awt.event.ActionEvent evt) {
//        jMenuItem2ActionPerformed(evt);
//      }
//    });
//    jMenu1.add(jMenuItem2);
//
//    jMenuItem3.setText("Current Population");
//    jMenu1.add(jMenuItem3);
//
//    jMenuBar1.add(jMenu1);
//
//    jMenu3.setText("Help");
//    jMenuBar1.add(jMenu3);
//
//    setJMenuBar(jMenuBar1);
//
//    org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
//    getContentPane().setLayout(layout);
//    layout.setHorizontalGroup(
//      layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
//      .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
//        .addContainerGap(115, Short.MAX_VALUE)
//        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
//          .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 700, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
//          .add(jLayeredPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
//        .addContainerGap())
//    );
//    layout.setVerticalGroup(
//      layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
//      .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
//        .add(50, 50, 50)
//        .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 500, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
//        .add(52, 52, 52)
//        .add(jLayeredPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
//        .add(305, 305, 305))
//    );
//
//    jTabbedPane1.getAccessibleContext().setAccessibleName("");
//
//
//    pack();
//  }// </editor-fold>
//
//    /** jMenuItem1ActionPerformed_newPlot is called when the user selects a "new plot" from
//     * the pull-down menu.
//     * @param evt
//     */
//    private void jMenuItem1ActionPerformed_newPlot(java.awt.event.ActionEvent evt) {
////      graphDialog = new GraphDialog(this, rootPaneCheckingEnabled, this.monitor.genGraph.varName, this);
////
////      this.monitor.graphs.get(0).tabName = "Testing";
////      this.monitor.graphs.get(0).xVarName = graphDialog.getxValueAdded();
////      this.monitor.graphs.get(0).yVarNames = graphDialog.getyValuesAdded();
//    //  GraphDialog graphDialog = new GraphDialog(this, rootPaneCheckingEnabled, graphVarNames);
////      graphDialog.setVisible(rootPaneCheckingEnabled);
//    }
//
//    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {
// /*     textArea.setText(bestIndividualInfo);
//      textArea.setCaretPosition(textArea.getDocument().getLength());
//      mainPanel.removeAll();
//      scrollPane.setMinimumSize(new Dimension(485, 430));
//      mainPanel.add(scrollPane, BorderLayout.CENTER);
//*/
//    }
//
//    private void formWindowClosed(java.awt.event.WindowEvent evt) {
//      setVisible(false);
//    }
//
//    private void jMenuItem2MouseReleased(java.awt.event.MouseEvent evt) {
//      jTabbedPane1.setVisible(isAlwaysOnTop());
//      jTextPane1.setText(bestIndividualInfo);
//    }
//    public JTabbedPane newJTabbedPane(String tabName, JTabbedPane pane) {
//      JTabbedPane attachingPane = new JTabbedPane();
//      pane.addTab(tabName, attachingPane);
//      return(attachingPane);
//    }
//
//    /**
//    * @param args the command line arguments
//    */
//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//               // new NewResultsFrame().setVisible(true);
//            }
//        });
//    }
//
//  // Variables declaration - do not modify
//  private javax.swing.JLayeredPane jLayeredPane1;
//  private javax.swing.JMenu jMenu1;
//  private javax.swing.JMenu jMenu3;
//  private javax.swing.JMenuBar jMenuBar1;
//  private javax.swing.JMenuItem jMenuItem1;
//  private javax.swing.JMenuItem jMenuItem2;
//  private javax.swing.JMenuItem jMenuItem3;
//  private javax.swing.JPanel jPanel1;
//  private javax.swing.JScrollPane jScrollPane1;
//  private javax.swing.JScrollPane jScrollPane2;
//  protected javax.swing.JTabbedPane jTabbedPane1;
//  private javax.swing.JTabbedPane CurrPop;
////  private javax.swing.JTabbedPane jTabbedPaneGraph2;
// // private javax.swing.JTabbedPane jTabbedPaneGraph1;
////  private javax.swing.JTabbedPane jTabbedPaneGraph3;
////  private javax.swing.JTabbedPane jTabbedPaneGraph4;
////  private javax.swing.JTabbedPane jTabbedPaneGraph5;
//  private javax.swing.JTextPane jTextPane1;
//  private javax.swing.JTextPane jTextPane2;
//  // End of variables declaration
// // GraphDialog graphDialog;

	
  
	
	public String getBestIndividualInfo() {
		return bestIndividualInfo;
	}	
	
	public void setBestIndividualInfo(String info, double fitness) {
		if(fitness < bestFit){
			bestFit = fitness;
			bestIndividualInfo = info;
			updateTextArea();
		}
		
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
		//graphCanvas.setInteractive(true);
                //testing
	}
	
	public void clearPoints() {
		graphCanvas.clearPoints();
	}

	private void updateTextArea() {
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				/*if(radioButton[1].isSelected()) {
					textArea.setText(bestIndividualInfo);
					textArea.setCaretPosition(textArea.getDocument().getLength());
				}
				else if(radioButton[2].isSelected()) {
					textArea.setText(currentPopulationInfo);
					textArea.setCaretPosition(0);
				} */
//                          if(jTabbedPane1.isShowing()) {
//                            jTextPane1.setText(bestIndividualInfo);
//                            //jTextPane1.setCaretPosition(jTextPane1.getSelectionEnd());
//                            System.out.print("inside run for jTabbedPane1\n");
//                          }
//                           else if(jTabbedPaneGraph2.isShowing()) {
//                             jTextPane2.setText(currentPopulationInfo);
//                             System.out.print("inside run for CurrPop\n");
                          if(newResultsFrame.jTabbedPane1.isEnabled()) {
                            newResultsFrame.jTextPane1.setText(bestIndividualInfo);
                          }
                             newResultsFrame.jTextPane2.setText(currentPopulationInfo);


//                          if(jTabbedPaneGraph1.isEnabled()) {
//                            if(!jChart2DInstantiated) {
//                            jChart2DInstantiate(jTabbedPane1);
//                            }
//			}
                  }
                });

  }
	
	public void actionPerformed(ActionEvent e) {
		boolean isGraph = false;
		if(e.getActionCommand().equals("Convergence Graph")) {
			mainPanel.removeAll();
			mainPanel.add(graphCanvas, BorderLayout.CENTER);
			isGraph = true;
		}
		else if(e.getActionCommand().equals("Generation Best Only")) {
			textArea.setText(bestIndividualInfo);
			textArea.setCaretPosition(textArea.getDocument().getLength());
			mainPanel.removeAll();
			scrollPane.setMinimumSize(new Dimension(485, 430));
			mainPanel.add(scrollPane, BorderLayout.CENTER);
		}
		else if(e.getActionCommand().equals("Current Population")) {
			textArea.setText(currentPopulationInfo);
			textArea.setCaretPosition(0);
			mainPanel.removeAll();
			scrollPane.setMinimumSize(new Dimension(485, 430));
			mainPanel.add(scrollPane, BorderLayout.CENTER);
		}
//		validate();
		mainPanel.repaint();
		if(isGraph) graphCanvas.requestFocusInWindow();
	}
	
	
//	public void windowClosed(WindowEvent e) {
//		System.out.println("test");
//	}
//
//	public void windowClosing(WindowEvent e) {
//		setVisible(false);
//	}
//	public void windowActivated(WindowEvent e) {}
//
//	public void windowDeactivated(WindowEvent e) {}
//
//	public void windowDeiconified(WindowEvent e) {}
//
//	public void windowIconified(WindowEvent e) {}
//
//	public void windowOpened(WindowEvent e) {}
//
////	@Override
//	public void componentHidden(ComponentEvent arg0) {
//		// TODO Auto-generated method stub
//
//	}

//	@Override
//	public void componentMoved(ComponentEvent arg0) {
//		// TODO Auto-generated method stub
//
//	}
//
////	@Override
//	public void componentResized(ComponentEvent arg0) {
//		scrollPane.setPreferredSize(mainPanel.getSize());
//		graphCanvas.setPreferredSize(mainPanel.getSize());
//	}
//
////	@Override
//	public void componentShown(ComponentEvent arg0) {
//		// TODO Auto-generated method stub
//
//	}
}
