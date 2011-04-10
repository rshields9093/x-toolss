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

   import javax.swing.*;
   import javax.swing.tree.*;
   import javax.swing.event.*;
   import java.io.*;


    public class XHelpFrame extends JFrame {
    
       public XHelpFrame() {
         initComponents();
      }
   
       private void initComponents() {
         jPanel1 = new javax.swing.JPanel();
         jSplitPane1 = new javax.swing.JSplitPane();
         jPanel2 = new javax.swing.JPanel();
         jTree1 = null;
         DefaultMutableTreeNode root = new DefaultMutableTreeNode("Help Topics");
      
      
         DefaultMutableTreeNode child1 = new DefaultMutableTreeNode("Setting up a Module");
         root.add(child1);
         DefaultMutableTreeNode grandChild1_1 = new DefaultMutableTreeNode("Creating a Module (.xts) File");
         child1.add(grandChild1_1);
         DefaultMutableTreeNode grandChild1_2 = new DefaultMutableTreeNode("Creating an Input File and Output File");
         child1.add(grandChild1_2);
         DefaultMutableTreeNode grandChild1_3 = new DefaultMutableTreeNode("Creating a Codefile");
         child1.add(grandChild1_3);
      
      
         DefaultMutableTreeNode child2 = new DefaultMutableTreeNode("Using a Module in X-TOOLSS");
         root.add(child2);
         DefaultMutableTreeNode grandChild2_1 = new DefaultMutableTreeNode("Specifying a Module in X-TOOLSS");
         child2.add(grandChild2_1);
         DefaultMutableTreeNode grandChild2_2 = new DefaultMutableTreeNode("Viewing Variables in X-TOOLSS");
         child2.add(grandChild2_2);
      
      
         DefaultMutableTreeNode child3 = new DefaultMutableTreeNode("X-TOOLSS Output Options");
         root.add(child3);
         DefaultMutableTreeNode grandChild3_1 = new DefaultMutableTreeNode("Output Window");
         child3.add(grandChild3_1);
         DefaultMutableTreeNode grandChild3_2 = new DefaultMutableTreeNode("LOG File");
         child3.add(grandChild3_2);
         DefaultMutableTreeNode grandChild3_3 = new DefaultMutableTreeNode("OUT File");
         child3.add(grandChild3_3);
         DefaultMutableTreeNode grandChild3_4 = new DefaultMutableTreeNode("STAT File");
         child3.add(grandChild3_4);
      
         DefaultMutableTreeNode child4 = new DefaultMutableTreeNode("X-TOOLSS Evolutionary Computation Algorithms");
         root.add(child4);
         DefaultMutableTreeNode grandChild4_1 = new DefaultMutableTreeNode("Generational GA with BLX");
         child4.add(grandChild4_1);
         DefaultMutableTreeNode grandChild4_2 = new DefaultMutableTreeNode("Steady-state GA");
         child4.add(grandChild4_2);
         DefaultMutableTreeNode grandChild4_3 = new DefaultMutableTreeNode("Steady-state GA with BLX");
         child4.add(grandChild4_3);
         DefaultMutableTreeNode grandChild4_4 = new DefaultMutableTreeNode("Steady-generational GA with BLX");
         child4.add(grandChild4_4);
         DefaultMutableTreeNode grandChild4_5 = new DefaultMutableTreeNode("PSO");
         child4.add(grandChild4_5);
         DefaultMutableTreeNode grandChild4_6 = new DefaultMutableTreeNode("Steady-state DEA");
         child4.add(grandChild4_6);
         DefaultMutableTreeNode grandChild4_7 = new DefaultMutableTreeNode("Generational DEA");
         child4.add(grandChild4_7);
         DefaultMutableTreeNode grandChild4_8 = new DefaultMutableTreeNode("Elitist EDA");
         child4.add(grandChild4_8);
         DefaultMutableTreeNode grandChild4_9 = new DefaultMutableTreeNode("Standard EP");
         child4.add(grandChild4_9);
         DefaultMutableTreeNode grandChild4_10 = new DefaultMutableTreeNode("Continuous Standard EP");
         child4.add(grandChild4_10);
         DefaultMutableTreeNode grandChild4_11 = new DefaultMutableTreeNode("Meta-EP");
         child4.add(grandChild4_11);
         DefaultMutableTreeNode grandChild4_12 = new DefaultMutableTreeNode("Continuous Meta-EP");
         child4.add(grandChild4_12);
         
      
         JTree jTree1 = new JTree(root);
         jPanel3 = new javax.swing.JPanel();
         jScrollPane1 = new javax.swing.JScrollPane();
         jTextArea1 = new javax.swing.JTextArea();
      
         setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
         setTitle("X-TOOLSS Help");
         jPanel1.setLayout(new java.awt.BorderLayout());
      
         jPanel1.setPreferredSize(new java.awt.Dimension(800, 600));//500
         jPanel2.setLayout(new java.awt.BorderLayout());
      
         //jPanel2.setPreferredSize(new java.awt.Dimension(500, 64));//150
         jTree1.addTreeSelectionListener(
                new javax.swing.event.TreeSelectionListener() {
                   public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                     jTree1ValueChanged(evt);
                  }
               });
      
         jPanel2.add(jTree1, java.awt.BorderLayout.CENTER);
      
         jSplitPane1.setLeftComponent(jPanel2);
      
         jPanel3.setLayout(new java.awt.BorderLayout());
      
         jPanel3.setBackground(new java.awt.Color(255, 255, 255));
         jPanel3.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 5, 1, 5)));
         jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
         jScrollPane1.setBorder(null);
         jTextArea1.setEditable(false);
         jTextArea1.setLineWrap(true);
         jTextArea1.setWrapStyleWord(true);
         jTextArea1.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 0, 0, 0)));
         jScrollPane1.setViewportView(jTextArea1);
      
         jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);
      
         jSplitPane1.setRightComponent(jPanel3);
      
         jPanel1.add(jSplitPane1, java.awt.BorderLayout.CENTER);
      
         getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
      
         pack();
      }
   
       private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {
         jTextArea1.setText(evt.getNewLeadSelectionPath().toString());
         htr = new HelpTextReader();
         htr.setSection(evt.getNewLeadSelectionPath().toString());
         jTextArea1.append("\n\n" + htr.getSectionText());
         jTextArea1.moveCaretPosition(1);
      }
    
   
      private javax.swing.JPanel jPanel1;
      private javax.swing.JPanel jPanel2;
      private javax.swing.JPanel jPanel3;
      private javax.swing.JScrollPane jScrollPane1;
      private javax.swing.JSplitPane jSplitPane1;
      private javax.swing.JTextArea jTextArea1;
      private javax.swing.JTree jTree1;
      private HelpTextReader htr;
   
   
   
       class HelpTextReader {
         private final String sectionEnd = "</xsection>";
         private String section = null;
      
          public HelpTextReader() {
         }
      
          public String getSection() {
            return section;
         }
      
          public void setSection(String section) {
            this.section = "<xsection = " + section + ">";
         }
      
          public String getSectionText() {
            try {
               InputStream is = java.net.URLClassLoader.getSystemResource("HelpText.dat").openStream();
               if(is== null) is = new FileInputStream("HelpText.dat");
               BufferedReader in = new BufferedReader(new InputStreamReader(is));
               String str;
               String text = "";
               while ((str = in.readLine()) != null && !str.equals(section)) {
               }
               if (str == null)
                  return "Internal Error: Section not found.";
               while ((str = in.readLine()) != null && !str.equals(sectionEnd)) {
                  text = text + str + "\n";
               }
               in.close();
               return text;
            } 
                catch (IOException e) {
                  return "Internal Error: " + e.getMessage();
               }
         }
      
      }
      
       public static void showHelp() {
         new XHelpFrame().setVisible(true);
      }
   
   
       /*public static void main(String[] args) {
      
         java.awt.EventQueue.invokeLater(
                new Runnable() {
                   public void run() {
                     new XHelpFrame().setVisible(false);
                  }
               });
      }*/
   
   }
