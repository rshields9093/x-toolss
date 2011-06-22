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
import java.util.Calendar;
import java.util.Date;

import javax.swing.*;

import lib.genevot.ECResult;
import lib.genevot.Population;

public class OptimizationPanel extends JPanel implements ActionListener{
	//JFrame resultsFrame;
	XTOOLSECMonitor monitor;
	XTOOLSRunnable ecThread;
	ThreadTerminator tt;
	Population population;
	ECResult result;
	int maxNumEvals;
	int numEvalsPerRun;
	
	//Update panel on time interval
	Timer timer;
	boolean isComplete = false;
	boolean forceUpdate = true;
	
	//Variables for calculating time
	Calendar cal;
	long startTime;
	long lastUpdateTime;
	long lastEvalMillis = Long.MAX_VALUE;
	int secondsPassed = 0;
	int secondsRemaining = 0;
	double percentComplete = 0.0;
	
	String algInfo;
	String runInfo;
	GridBagLayout layout;
	GridBagConstraints c;
	JLabel desc;
	JLabel timeRemaining;
	JLabel jLblAlgInfo;
	JLabel jLblRunInfo;
	JProgressBar progressBar;
	JButton btnShow;
	JButton btnStop;
	JButton btnPauseResume;
	
	OptimizationPanel(XTOOLSECMonitor mon, XTOOLSRunnable xtrunnable, ThreadTerminator threadTerm, Population p, String description, int evals, int runs){
		monitor = mon;
		tt = threadTerm;
		timer = new Timer(1000, this);
		ecThread = xtrunnable;
		population = p;
		numEvalsPerRun = evals;
		maxNumEvals = evals*runs;
		cal = Calendar.getInstance();
		startTime = cal.getTimeInMillis();
		lastUpdateTime = cal.getTimeInMillis();
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
		desc = new JLabel(description);
		timeRemaining = new JLabel("Calculating Time...");
		progressBar = new JProgressBar();
		jLblRunInfo = new JLabel("");
		jLblAlgInfo = new JLabel("");
		btnShow = new JButton("Show");
		btnStop = new JButton("Stop");
		btnPauseResume = new JButton("Pause");
		
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
				monitor.getFrame().setExtendedState(JFrame.NORMAL);
				monitor.getFrame().setVisible(true);
			}
			
		});
		
		btnStop.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(tt != null){
					if(tt.killThread){
						remove();
					}else{
						int n = JOptionPane.showConfirmDialog(null,
				                "Are you sure you wish to end this optimization?\n" +
				                "You will not be able to resume it at a later time.",
				                "End Optimization?",
				                JOptionPane.YES_OPTION); 								
						if (n == JOptionPane.YES_OPTION) {
							timer.stop();
							timeRemaining.setText("Canceled");
							tt.killThread = true;
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
				if(population != null){
					if(population.getPaused()){
						//Currently paused, needs to be started.
						timer.start();
						population.setPaused(false);
						progressBar.setEnabled(true);
						timeRemaining.setText("Resuming...");
						btnPauseResume.setText("Pause");
					}else{
						//Currently started, needs to be paused.
						timer.stop();
						progressBar.setEnabled(false);
						timeRemaining.setText("Paused");
						population.setPaused(true);
						btnPauseResume.setText("Resume");
					}
					updateComponents();
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
		
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 2;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.SOUTHWEST;
		layout.setConstraints(jLblAlgInfo, c);
		add(jLblAlgInfo);
		
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 3;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.SOUTHWEST;
		layout.setConstraints(jLblRunInfo, c);
		add(jLblRunInfo);
		
		c.gridwidth = 3;
		c.gridheight = 2;
		c.gridx = 1;
		c.gridy = 2;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.SOUTHEAST;
		layout.setConstraints(buttonPanel, c);
		add(buttonPanel);
		
		
		timer.start();
		setVisible(true);
	}
	private void remove(){
		setVisible(false);
		this.removeAll();
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
		secondsPassed++;
		if(forceUpdate && secondsPassed%20==19){
			//Will update even if the initial population has not been completed.
			updateData();
			forceUpdate = true;
		}
		//updateData();
		updateComponents();
	}
	
	private void updateComponents(){
		String sTimeRemaining = "";
		if(percentComplete >= 0.995){
			btnStop.setText("Remove");
			btnPauseResume.setEnabled(false);
			timeRemaining.setText("Run Completed");
			progressBar.setValue(100);
			timer.stop();
		}else{
			if(secondsPassed%20 == 0 && percentComplete > 0){
				long numEvalsCompleted = (long) ((ecThread.getNumRuns()*numEvalsPerRun)+(double)population.getNumFunctionEvaluations());
				long numEvalsRemaining = maxNumEvals - numEvalsCompleted;
				long avgMillisPerEval = (lastUpdateTime-startTime)/numEvalsCompleted;
				/*
				 * Calculating time remaining using the following formula:
				 *   seconds remaining = avg(lastEvalTime,avgEvalTime) * numEvalsRemaining
				 */
				secondsRemaining = (int) ((numEvalsRemaining*((lastEvalMillis+avgMillisPerEval)/2))/1000);
				sTimeRemaining = getTimeRemaining(secondsRemaining);
				timeRemaining.setText(sTimeRemaining);
			}
		}
		setAlgInfo(algInfo);
		int currentRun = ecThread.getNumRuns() + 1;
		if(currentRun > ecThread.getMaxNumRuns()) currentRun = ecThread.getMaxNumRuns();
		setRunInfo("Run "+ currentRun + " of " + ecThread.getMaxNumRuns());
	}
	
	public synchronized void updateData() {
		forceUpdate = false;
		percentComplete = (((ecThread.getNumRuns()*numEvalsPerRun)+(double)population.getNumFunctionEvaluations())/maxNumEvals);
		progressBar.setValue((int)(percentComplete*100));
		cal = Calendar.getInstance();
		lastEvalMillis = cal.getTimeInMillis()-lastUpdateTime;
		lastUpdateTime = cal.getTimeInMillis();
		//System.out.println("Time Passed: "+(lastUpdateTime-startTime));
		//System.out.println("Time For Last Eval: "+lastEvalMillis);
		//System.out.println();
	}
	public void setRunInfo(String string) {
		runInfo = string;
		jLblRunInfo.setText(" "+runInfo);
	}
	public void setAlgInfo(String string){
		algInfo = string;
		jLblAlgInfo.setText(" GA: "+algInfo);
	}
}
