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

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D;
import java.util.Vector;
import java.text.DecimalFormat;
import java.awt.geom.Rectangle2D;

import java.awt.*;
import java.awt.event.*;


public class GraphCanvas extends JPanel implements MouseListener, MouseMotionListener {

	private static final Color DEFAULT_COLOR = Color.black;
	private double ZOOM_STEP = 2.0;
	private double TRANSLATE_X_STEP = 1.0;
	private double TRANSLATE_Y_STEP = 1.0;
	private static final double POINT_SIZE = 5.0;
	private static final double	TICK_SIZE = 10.0;
	private double garrett;
	private double xMin;
	private double xMax;
	private double yMin;
	private double yMax;
	private boolean interactive;
	private TranslateHorizontalAction translateRightAction;
	private TranslateHorizontalAction translateLeftAction;
	private TranslateVerticalAction translateUpAction;
	private TranslateVerticalAction translateDownAction;
	private final JPopupMenu pop = new JPopupMenu();
	private JMenuItem item = new JMenuItem();
	private int maxGenerations;
	private double xRes;
	private double yRes;
	private Point2D.Double panelCenter;
	private Point2D.Double graphOrigin;
	private Vector<GraphPoint> points;
	private boolean drawZoomBox = false;
	private int zoomBoxStartX = 0;
	private int zoomBoxStartY = 0;
	private int zoomBoxEndX = 0;
	private int zoomBoxEndY = 0;
	
	class TranslateHorizontalAction extends AbstractAction {
		private double translate_step = 1.0;
		public void actionPerformed(ActionEvent e) {
			translate(translate_step, 0.0);
		}
		public void setTranslateStep(double s) {
			translate_step = s;
		}
	}

	class TranslateVerticalAction extends AbstractAction {
		private double translate_step = 1.0;
		public void actionPerformed(ActionEvent e) {
			translate(0.0, translate_step);
		}
		public void setTranslateStep(double s) {
			translate_step = s;
		}
	}
	
	public GraphCanvas() {
		this(-100.0, 1000.0, -100.0, 1000.0);
	}

	public GraphCanvas(double xMin, double xMax, double yMin, double yMax) {
		this.setPreferredSize(new Dimension(485,430));
		this.interactive = false;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		garrett = Double.POSITIVE_INFINITY;
		panelCenter = new Point2D.Double();
		graphOrigin = new Point2D.Double();
		points = new Vector<GraphPoint>();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		Action zoomInAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				//interactive = true;
				zoom(1.0 / ZOOM_STEP);
			}
		};		

		Action zoomOutAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				//interactive = true;
				zoom(ZOOM_STEP);
			}
		};		

		translateRightAction = new TranslateHorizontalAction();
		translateLeftAction = new TranslateHorizontalAction();
		translateUpAction = new TranslateVerticalAction();
		translateDownAction = new TranslateVerticalAction();

		Action resetToOriginAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				resetToOrigin();
			}
		};
		
		getInputMap(JPanel.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("UP"), "up");
		getActionMap().put("up", translateUpAction);

		getInputMap(JPanel.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("DOWN"), "down");
		getActionMap().put("down", translateDownAction);

		getInputMap(JPanel.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("LEFT"), "left");
		getActionMap().put("left", translateLeftAction);

		getInputMap(JPanel.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("RIGHT"), "right");
		getActionMap().put("right", translateRightAction);

		getInputMap(JPanel.WHEN_FOCUSED).put(KeyStroke.getKeyStroke('z'), "zoomIn");
		getActionMap().put("zoomIn", zoomInAction);

		getInputMap(JPanel.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("shift Z"), "zoomOut");
		getActionMap().put("zoomOut", zoomOutAction);
		
		getInputMap(JPanel.WHEN_FOCUSED).put(KeyStroke.getKeyStroke('r'), "reset");
		getActionMap().put("reset", resetToOriginAction);
		
		pop.add(item);
		
		TRANSLATE_X_STEP = (xMax - xMin) / 10.0;
		TRANSLATE_Y_STEP = (yMax - yMin) / 10.0;
	}
	
	private double[] calculateBounds() {
		if(points.size() == 0){
			return null;
		}else{
			double[] bounds = new double[4];
			bounds[0] = bounds[1] = points.get(0).coordinate.x;
			bounds[2] = bounds[3] = points.get(0).coordinate.y;
			for(int i = 1; i < points.size(); i++) {
				if(points.get(i).coordinate.x < bounds[0]) {
					bounds[0] = points.get(i).coordinate.x;
				}
				if(points.get(i).coordinate.x > bounds[1]) {
					bounds[1] = points.get(i).coordinate.x;
				}
				if(points.get(i).coordinate.y < bounds[2]) {
					bounds[2] = points.get(i).coordinate.y;
				}
				if(points.get(i).coordinate.y > bounds[3]) {
					bounds[3] = points.get(i).coordinate.y;
				}
			}
			if(bounds[1] < 500) bounds[1] = 500;
			return bounds;
		}
	}
	
	private void calculateProperties() {
		double w = (double)this.getWidth();
		double h = (double)this.getHeight();
		double[] bounds = calculateBounds();
		double percent = 0.1;
		//if(bounds != null) garrett = bounds[1];
		
		if(!interactive && bounds != null) {
			
			xMin = bounds[0] - (bounds[1] - bounds[0]) * 0.2;
			xMax = bounds[1] + (bounds[1] - bounds[0]) * 0.1;
			double range = Math.abs(bounds[3] - bounds[2]);
			if(bounds[2] > 0 && bounds[3] > 0) {
				yMin = bounds[2] - Math.signum(bounds[2]) * percent * range;
				yMax = bounds[3] + Math.signum(bounds[3]) * percent * range;
			}
			else if(bounds[2] <= 0 && bounds[3] <= 0) {
				yMin = bounds[2] + Math.signum(bounds[2]) * percent * range;
				yMax = bounds[3] - Math.signum(bounds[3]) * percent * range;			
			}
			else {
				yMin = bounds[2] - percent * range;
				yMax = bounds[3] + percent * range;				
			}
			TRANSLATE_Y_STEP = bounds[3] - bounds[2] / 10.0;
			TRANSLATE_X_STEP = bounds[1] - bounds[0] / 10.0;
		}	
		
		translateRightAction.setTranslateStep(TRANSLATE_X_STEP);
		translateLeftAction.setTranslateStep(-TRANSLATE_X_STEP);
		translateUpAction.setTranslateStep(TRANSLATE_Y_STEP);
		translateDownAction.setTranslateStep(-TRANSLATE_Y_STEP);
		xRes = w / (xMax - xMin);
		yRes = h / (yMax - yMin);
		panelCenter.x = (double)this.getX() + w / 2.0;
		panelCenter.y = (double)this.getY() + h / 2.0;
		graphOrigin.x = panelCenter.x - (xMax + xMin) / 2.0 * xRes;
		graphOrigin.y = panelCenter.y + (yMax + yMin) / 2.0 * yRes;
		//zoom(1.0);
	}
	
	private Point2D.Double convertToGraph(double x, double y) {
		return convertToGraph(new Point2D.Double(x, y));
	}
	
	private Point2D.Double convertToGraph(Point2D.Double panelPoint) {		
		return new Point2D.Double((panelPoint.x - graphOrigin.x) / xRes, (graphOrigin.y - panelPoint.y) / yRes);
	}

	private Point2D.Double convertToPanel(double x, double y) {		
		return convertToPanel(new Point2D.Double(x, y));
	}
	
	private Point2D.Double convertToPanel(Point2D.Double graphPoint) {		
		return new Point2D.Double(graphOrigin.x + graphPoint.x * xRes, graphOrigin.y - graphPoint.y * yRes);
	}
	
	public void zoom(double zoomFactor) {
		interactive = true;
		Point2D.Double center = new Point2D.Double((xMax + xMin)/2.0, (yMax + yMin)/2.0);
		Point2D.Double upperLeft = new Point2D.Double(xMin - center.x, yMax - center.y);
		Point2D.Double lowerRight = new Point2D.Double(xMax - center.x, yMin - center.y);
		upperLeft.x *= zoomFactor;
		upperLeft.y *= zoomFactor;
		lowerRight.x *= zoomFactor;
		lowerRight.y *= zoomFactor;
		setGraphBounds(upperLeft.x + center.x, lowerRight.x + center.x, lowerRight.y + center.y, upperLeft.y + center.y);
		TRANSLATE_X_STEP = (xMax - xMin) / 10.0;
		TRANSLATE_Y_STEP = (yMax - yMin) / 10.0;
	}
	
	public void translate(double xAmount, double yAmount) {
		interactive = true;
		setGraphBounds(xMin + xAmount, xMax + xAmount, yMin + yAmount, yMax + yAmount);
	}
	
	public void resetToOrigin() {
		setGraphBounds(-100.0, 1000.0, -100.0, 1000.0);
		interactive = false;
	}
	
	public void setGraphBounds(double xMin, double xMax, double yMin, double yMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		calculateProperties();
		repaint();
	}
	
	public double[] getGraphBounds() {
		double[] b = new double[4];
		b[0] = this.xMin;
		b[1] = this.xMax;
		b[2] = this.yMin;
		b[3] = this.yMax;
		return b;
	}
	
	public void clearPoints() {
		points.clear();
		interactive = false;
		repaint();
	}
	
	public void addPoint(Point2D.Double p) {
		addPoint(p, Color.black);
	}
	
	public void addPoint(Point2D.Double p, Color c) {
		points.add(new GraphPoint(p, c));
		//interactive = false;
		repaint();
	}
	
	public Vector<GraphPoint> getPoints() {
		Vector<GraphPoint> p = new Vector<GraphPoint>();
		for(int i = 0; i < points.size(); i++) {
			p.add(points.elementAt(i));
		}
		return p;
	}
	
	private double calcTickWidth(double min, double max) {
		final double MIN_TICK = 0.00000000001;
		final double MAX_TICK = 10000000000.0;
		double tickSize = (max - min) / 10.0;
		boolean found = false;
		double tickMark = MIN_TICK;
		while(!found && tickMark < MAX_TICK) {
			if(tickSize <= 1.5 * tickMark) {
				found = true;
			}
			else {
				tickMark *= 10.0;
			}
		}
		return tickMark;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//System.out.println("Starting paint...");
		calculateProperties();
		double w = (double)this.getWidth();
		double h = (double)this.getHeight();
		Graphics2D g2D = (Graphics2D)g;
		g2D.clearRect(this.getX(), this.getY(), (int)w, (int)h);
		
		g2D.draw(new Line2D.Double(graphOrigin.x, graphOrigin.y, graphOrigin.x, (double)this.getY()));
		g2D.draw(new Line2D.Double(graphOrigin.x, graphOrigin.y, graphOrigin.x, (double)this.getY() + h));
		g2D.draw(new Line2D.Double(graphOrigin.x, graphOrigin.y, (double)this.getX(), graphOrigin.y));
		g2D.draw(new Line2D.Double(graphOrigin.x, graphOrigin.y, (double)this.getX() + w, graphOrigin.y));
		
		double currX = graphOrigin.x;
		double currY = graphOrigin.y;
		boolean badHigh = false;
		boolean badLow = false;
		double xTickWidth = calcTickWidth(xMin, xMax);
		double yTickWidth = calcTickWidth(yMin, yMax);
		double tickMark = xTickWidth;
		Point2D.Double labelPoint;
		DecimalFormat yDecFormat = new DecimalFormat("#0.0#");
		DecimalFormat xDecFormat = new DecimalFormat("#0.#");
		
		//System.out.println("Starting drawing ticks...");
		int tickCounter = 0;
		while((!badHigh || !badLow) && tickCounter < 100) {
			tickCounter++;
			currX = graphOrigin.x - (double)tickMark * xRes;
			if(currX > 0 && !badLow) {
				g2D.draw(new Line2D.Double(currX, currY - TICK_SIZE / 2.0, currX, currY + TICK_SIZE / 2.0));
				labelPoint = convertToGraph(currX, 0.0);
				Rectangle2D rec = g2D.getFontMetrics().getStringBounds(xDecFormat.format(labelPoint.x), g2D);
				double stringWidth = rec.getWidth();
				double stringHeight = rec.getHeight();
				g2D.drawString(xDecFormat.format(labelPoint.x), (float)(currX - stringWidth / 2.0), (float)(currY + TICK_SIZE / 2.0 + stringHeight));
				badLow = false;
			}	
			else {
				badLow = true;
			}
			currX = graphOrigin.x + (double)tickMark * xRes;
			if(currX < w && !badHigh) {
				g2D.draw(new Line2D.Double(currX, currY - TICK_SIZE / 2.0, currX, currY + TICK_SIZE / 2.0));
				labelPoint = convertToGraph(currX, 0.0);
				Rectangle2D rec = g2D.getFontMetrics().getStringBounds(xDecFormat.format(labelPoint.x), g2D);
				double stringWidth = rec.getWidth();
				double stringHeight = rec.getHeight();
				g2D.drawString(xDecFormat.format(labelPoint.x), (float)(currX - stringWidth / 2.0), (float)(currY + TICK_SIZE / 2.0 + stringHeight));
				badHigh = false;
			}	
			else {			
				badHigh = true;
			}
			tickMark += xTickWidth;
		}
		
		//System.out.println("Finished with x ticks...");
		currX = graphOrigin.x;
		badHigh = false;
		badLow = false;
		tickMark = yTickWidth;
		tickCounter = 0;
		while((!badHigh || !badLow) && tickCounter < 100) {
			tickCounter++;
			currY = graphOrigin.y - ((double)tickMark * yRes);
			//System.out.println("tickMark: "+tickMark+"    yRes: "+yRes+"    currY: "+currY);
			if(currY > 0 && !badLow) {
				g2D.draw(new Line2D.Double(currX - TICK_SIZE / 2.0, currY, currX + TICK_SIZE / 2.0, currY));
				labelPoint = convertToGraph(0.0, currY);
				Rectangle2D rec = g2D.getFontMetrics().getStringBounds(yDecFormat.format(labelPoint.y), g2D);
				double stringWidth = rec.getWidth();
				double stringHeight = rec.getHeight();
				g2D.drawString(yDecFormat.format(labelPoint.y), (float)(currX - stringWidth - TICK_SIZE / 2.0), (float)(currY + stringHeight / 2.0));
				badLow = false;
			}	
			else {
				badLow = true;
			}
			currY = graphOrigin.y + ((double)tickMark * yRes);
			if(currY < h && !badHigh) {
				g2D.draw(new Line2D.Double(currX - TICK_SIZE / 2.0, currY, currX + TICK_SIZE / 2.0, currY));
				labelPoint = convertToGraph(0.0, currY);
				Rectangle2D rec = g2D.getFontMetrics().getStringBounds(yDecFormat.format(labelPoint.y), g2D);
				double stringWidth = rec.getWidth();
				double stringHeight = rec.getHeight();
				g2D.drawString(yDecFormat.format(labelPoint.y), (float)(currX - stringWidth - TICK_SIZE / 2.0), (float)(currY + stringHeight / 2.0));
				badHigh = false;
			}
			else {
				badHigh = true;
			}
			tickMark += yTickWidth;
		}
		//System.out.println("Finished drawing ticks...");
		//System.out.println("Starting drawing points...");
		for(int i = 0; i < points.size(); i++) {
			Point2D.Double p = convertToPanel(points.elementAt(i).coordinate);
			g2D.setColor(points.elementAt(i).color);
			g2D.fill(new Ellipse2D.Double(p.x-((double)POINT_SIZE/2), p.y-((double)POINT_SIZE/2), POINT_SIZE, POINT_SIZE));
		}
		//System.out.println("Finished drawing points...");
		g2D.setColor(Color.black);
		if(garrett < Double.POSITIVE_INFINITY) {
			String s = "Generation " + String.valueOf((int)garrett);
			Rectangle2D rec = g2D.getFontMetrics().getStringBounds(s, g2D);
			double stringWidth = rec.getWidth();
			double stringHeight = rec.getHeight();
			g2D.drawString(s, (float)(this.getX() + w - stringWidth), (float)(this.getY() + stringHeight));
		}
		
		if(drawZoomBox){
			int x1,y1,boxW,boxH;
			x1 = Math.min(zoomBoxStartX, zoomBoxEndX);
			y1 = Math.min(zoomBoxStartY,zoomBoxEndY);
			boxW = Math.abs(zoomBoxEndX-zoomBoxStartX);
			boxH = Math.abs(zoomBoxEndY-zoomBoxStartY);
			g.drawRect(x1, y1, boxW, boxH);
			
			Graphics2D g2 = (Graphics2D)g;
			g2.setColor(Color.blue);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .2f));
			Rectangle2D rect = new Rectangle2D.Double(x1,y1,boxW,boxH);
			g2.fill(rect);
		}
		//System.out.println("Ending paint...");
	}
	
	private Vector<Color> currentColors() {
		Vector<Color> v = new Vector<Color>();
		for(int i = 0; i < points.size(); i++) {
			v.add(points.elementAt(i).color);
		}
		return v;
	}	
	
	public void setInteractive(boolean interact) {
		interactive = interact;
	}	

	public void mouseClicked(MouseEvent e) {
		double radius = 5.0;		
		for(int i = 0; i < points.size(); i++) {
			Point2D.Double p = convertToPanel(points.elementAt(i).coordinate);
			if(e.getX() >= (p.x - radius) && e.getX() <= (p.x + radius) && e.getY() >= (p.y - radius) && e.getY() <= (p.y + radius)) {
				setPop(e, "(" + points.elementAt(i).coordinate.x + ", " + points.elementAt(i).coordinate.y + ")");
				return;
			}else{
				pop.setVisible(false);
			}
		}
	}
	
	public void mouseMoved(MouseEvent e) {
		
	}
	
	public void mouseDragged(MouseEvent e) {
		
		if(!drawZoomBox){
			drawZoomBox = true;
			zoomBoxStartX = e.getX();
			zoomBoxStartY = e.getY();
		}else{
			zoomBoxEndX = e.getX();
			zoomBoxEndY = e.getY();
			repaint();
		}
	}	
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){
		
	}
	public void mouseReleased(MouseEvent e){
		if(drawZoomBox){
			drawZoomBox = false;
			interactive = true;
			//Math.min(zoomBoxStartX, zoomBoxEndX), Math.min(zoomBoxStartY,zoomBoxEndY), Math.abs(zoomBoxEndX-zoomBoxStartX), Math.abs(zoomBoxEndY-zoomBoxStartY)
			Point2D sp = this.convertToGraph(Math.min(zoomBoxStartX, zoomBoxEndX), Math.min(zoomBoxStartY,zoomBoxEndY));
			//System.out.println("Starting Point: "+sp.getX()+" "+sp.getY());
			Point2D ep = this.convertToGraph(Math.max(zoomBoxStartX,zoomBoxEndX), Math.max(zoomBoxStartY,zoomBoxEndY));
			//System.out.println("Ending Point: "+ep.getX()+" "+ep.getY());
			setGraphBounds(sp.getX(), ep.getX(), ep.getY(), sp.getY());
			TRANSLATE_X_STEP = (xMax - xMin) / 10.0;
			TRANSLATE_Y_STEP = (yMax - yMin) / 10.0;
			calculateProperties();
			repaint();
		}
	}
	
	
	public void setPop(MouseEvent e, String text) {
		item.setText(text);
		pop.show(e.getComponent(), e.getX() - 30, e.getY() - 30);
	}
}




