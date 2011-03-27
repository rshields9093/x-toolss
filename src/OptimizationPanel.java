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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import lib.genevot.ECResult;
import lib.genevot.Population;

public class OptimizationPanel extends JPanel implements ActionListener{
	GUI10 wizard;
	ECResult result;
	
	//Update panel on time interval
	Timer t;
	boolean isComplete = false;
	boolean forceUpdate = true;
	
	int secondsPassed = 0;
	int secondsRemaining = 0;
	double percentComplete = 0.0;
	GridBagLayout layout;
	GridBagConstraints c;
	JLabel desc;
	JLabel timeRemaining;
	JProgressBar progressBar;
	JButton btnShow;
	JButton btnStop;
	JButton btnPauseResume;
	
	OptimizationPanel(){
		Color backgroundColor = new Color(173,194,255);
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.setBackground(backgroundColor);
		this.setSize(getMinimumSize());
		
		layout = new GridBagLayout();
		c = new GridBagConstraints();
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(backgroundColor);
		c.insets = new Insets(3,3,3,3);
		c.weightx = 1.0;
		c.weighty = 1.0;
		this.setLayout(layout);
		wizard = new GUI10(this);
		wizard.getExecuteButton().addActionListener(this);
		desc = new JLabel("Gathering Information...");
		timeRemaining = new JLabel("");
		progressBar = new JProgressBar();
		btnShow = new JButton("Show");
		btnStop = new JButton("Stop");
		btnStop.setEnabled(false);
		btnPauseResume = new JButton("Pause");
		btnPauseResume.setEnabled(false);
		
		buttonPanel.add(btnShow);
		buttonPanel.add(btnPauseResume);
		buttonPanel.add(btnStop);
		
		buttonPanel.setSize(buttonPanel.getMinimumSize());
		
		/*
		 * Add listeners to buttons...
		 */
		
		btnShow.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				wizard.getCurrentFrame().setExtendedState(JFrame.NORMAL);
				wizard.getCurrentFrame().setVisible(true);
			}
			
		});
		
		btnStop.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(wizard.getThreadTerminator() != null){
					if(wizard.getThreadTerminator().killThread){
						remove();
					}else{
						int n = JOptionPane.showConfirmDialog(null,
				                "Are you sure you wish to end this optimization?\n" +
				                "You will not be able to resume it at a later time.",
				                "End Optimization?",
				                JOptionPane.YES_OPTION); 								
						if (n == JOptionPane.YES_OPTION) {
							t.stop();
							timeRemaining.setText("Canceled");
							wizard.getThreadTerminator().killThread = true;
							btnStop.setText("Remove");
							btnPauseResume.setEnabled(false);
							progressBar.setEnabled(false);
						}
						
					}
				}
			}
			
		});
		
		btnPauseResume.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Population p = wizard.getPopulation();
				if(p != null){
					if(p.getPaused()){
						//Currently paused, needs to be started.
						t.start();
						p.setPaused(false);
						progressBar.setEnabled(true);
						btnPauseResume.setText("Pause");
					}else{
						//Currently started, needs to be paused.
						t.stop();
						progressBar.setEnabled(false);
						timeRemaining.setText("Paused");
						p.setPaused(true);
						btnPauseResume.setText("Resume");
					}
				}
			}
			
		});
		
		/*
		 * Add components to panel...
		 */
		
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(desc, c);
		add(desc);
		
		c.gridx = 2;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHEAST;
		layout.setConstraints(timeRemaining,c);
		add(timeRemaining);
		
		c.gridx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(progressBar, c);
		add(progressBar);
		
		c.gridwidth = 3;
		c.gridx = 1;
		c.gridy = 2;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.SOUTHEAST;
		layout.setConstraints(buttonPanel, c);
		add(buttonPanel);
		
		t = new Timer(1000, this);
		t.start();
	}
	private void remove(){
		setVisible(false);
		this.removeAll();
		wizard.destroy();
		result = null;
		System.gc();
	}
	
	private String getTimeRemaining(int s){
		String sTimeRemaining = "";
		int secondsRemaining = s;
		int daysRemaining = secondsRemaining/86400;
		secondsRemaining = secondsRemaining - (daysRemaining*86400);
		int hoursRemaining = secondsRemaining/3600;
		secondsRemaining = secondsRemaining - (hoursRemaining * 3600);
		int minutesRemaining = secondsRemaining/60;
		secondsRemaining = secondsRemaining - (minutesRemaining * 60);
		
		sTimeRemaining = "Time Remaining: "+daysRemaining+"d "+hoursRemaining+"h "+minutesRemaining+"m "+secondsRemaining+"s";
		return sTimeRemaining;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		//Handle timer event
		if(wizard.hasStarted()){
			secondsPassed++;
			if(forceUpdate){
				//Will update even if the initial population has not been completed.
				updateData();
				forceUpdate = true;
			}
			updateComponents();
		}
	}
	
	private void updateComponents(){
		String sTimeRemaining = "";
		if(percentComplete == 0.0){
			sTimeRemaining = "Calculating Time...";
			btnStop.setEnabled(true);
			btnPauseResume.setEnabled(true);
		}else if(percentComplete >= 0.99){
			btnStop.setText("Remove");
			btnPauseResume.setEnabled(false);
			sTimeRemaining = "Run Completed";
			t.stop();
		}else{
			secondsRemaining = (int)((secondsPassed/percentComplete)-secondsPassed);
			sTimeRemaining = getTimeRemaining(secondsRemaining);
			btnStop.setEnabled(true);
			btnPauseResume.setEnabled(true);
		}
		
		timeRemaining.setText(sTimeRemaining);
		desc.setText(wizard.application.getFile().getName());
	}
	
	public synchronized void updateData() {
		forceUpdate = false;
		double numFunctionEvaluations = wizard.getNumFunctionEvaluations();
		double maxFunctionEvaluations = wizard.getMaxFunctionEvaluations();
		percentComplete = (numFunctionEvaluations/maxFunctionEvaluations);
		progressBar.setValue((int)(percentComplete*100));
	}
}
