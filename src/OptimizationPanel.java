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
import javax.swing.border.Border;
import edu.auburn.eng.aci.genevot.ECResult;

public class OptimizationPanel extends JPanel implements ActionListener{
	GUI10 wizard;
	ECResult result;
	
	//Update panel on time interval
	Timer t;
	
	int secondsPassed = 0;
	GridBagLayout layout;
	GridBagConstraints c;
	JLabel desc;
	JLabel timeRemaining;
	JProgressBar progressBar;
	JButton btnVisible;
	JButton btnStop;
	JButton btnPauseResume;
	OptimizationPanel(GUI10 w/*, OptimizationsFrame oFrame*/){
		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		this.setBackground(new Color(220,220,220));
		this.setSize(getMinimumSize());
		
		layout = new GridBagLayout();
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 1.0;
		this.setLayout(layout);
		
		this.wizard = w;
		//this.optFrame = oFrame;
		desc = new JLabel("Gathering Information...");
		timeRemaining = new JLabel("");
		progressBar = new JProgressBar();
		btnVisible = new JButton("Results");
		btnStop = new JButton("Stop");
		btnPauseResume = new JButton("Pause");
		
		btnVisible.addActionListener(new ActionListener(){

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
					wizard.getThreadTerminator().killThread = true;
					remove();
				}
			}
			
		});
		
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(desc, c);
		add(desc);
		
		c.gridx = 2;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(timeRemaining,c);
		add(timeRemaining);
		
		c.gridx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(progressBar, c);
		add(progressBar);
		
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 2;
		layout.setConstraints(btnVisible, c);
		add(btnVisible);
		
		c.gridx = 2;
		layout.setConstraints(btnPauseResume, c);
		add(btnPauseResume);
		
		c.gridx = 3;
		layout.setConstraints(btnStop, c);
		add(btnStop);
		
		t = new Timer(1000, this);
		t.start();
	}
	private void remove(){
		//optFrame.removeOptimization(this);
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		//Handle timer event
		if(wizard.hasStarted()){
			secondsPassed++;
			if(secondsPassed%10 == 0){
				double numFunctionEvaluations = wizard.getNumFunctionEvaluations();
				double maxFunctionEvaluations = wizard.getMaxFunctionEvaluations();
				double percentComplete = (numFunctionEvaluations/maxFunctionEvaluations);
				progressBar.setValue((int)(percentComplete*100));
				String sTimeRemaining;
				if(percentComplete != 0){
					int secondsRemaining = (int)((secondsPassed/percentComplete)-secondsPassed);
					if(secondsRemaining == 0){
						sTimeRemaining = "Run Completed";
					}else{
						int daysRemaining = secondsRemaining/86400;
						secondsRemaining = secondsRemaining - (daysRemaining*86400);
						int hoursRemaining = secondsRemaining/3600;
						secondsRemaining = secondsRemaining - (hoursRemaining * 3600);
						int minutesRemaining = secondsRemaining/60;
						secondsRemaining = secondsRemaining - (minutesRemaining * 60);
						
						sTimeRemaining = "Time Remaining: "+daysRemaining+"d "+hoursRemaining+"h "+minutesRemaining+"m "+secondsRemaining+"s";
					}
				}else{
					sTimeRemaining = "Calculating Time...";
				}
				
				timeRemaining.setText(sTimeRemaining);
				desc.setText(wizard.application.getFile().getName());
			}
		}
	}
}
