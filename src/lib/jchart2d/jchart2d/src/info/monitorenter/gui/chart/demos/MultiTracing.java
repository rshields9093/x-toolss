/*
 * MultiTracing, a demo testing the thread- safetiness of the Chart2D.
 * Copyright (c) 2007 - 2010  Achim Westermann, Achim.Westermann@gmx.de
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  If you modify or optimize the code in a useful way please let me know.
 *  Achim.Westermann@gmx.de
 */
package info.monitorenter.gui.chart.demos;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyMinimumViewport;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.views.ChartPanel;
import info.monitorenter.util.Range;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * <p>
 * An example that tests the ability of multithreaded use of a single
 * <code>Chart2D</code>. Six different Threads are painting subsequently a
 * single point to the chart and go to a sleep. After having painted the whole
 * trace, each Thread sleeps for a random time, removes it's trace, sleeps for
 * another random time and starts again. <br>
 * To be true: the data for the <code>TracePoint</code> instances is computed
 * a single time at startup.
 * </p>
 * <p>
 * This test may blow your CPU. I am currently working on an AMD Athlon 1200,
 * 512 MB RAM so I did not get these problems.
 * </p>
 * 
 * @version $Revision: 1.11 $
 * 
 * @author <a href='mailto:Achim.Westermann@gmx.de'>Achim Westermann </a>
 */

//public final class MultiTracing extends JPanel {
//  static JFrame multiFrame;
public final class MultiTracing {
  static JFrame multiFrame;
  static JPanel multiPanel;
/**
   * Thread that adds a trace to a chart, paints the points with configurable
   * sleep breaks and then removes it. It then goes to sleep and starts this
   * cycle anew.
   * <p>
   * 
   * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann </a>
   * 
   * 
   * @version $Revision: 1.11 $
   */
  public static final class AddPaintRemoveThread
      extends Thread {

    /** The y values to paint. */
 //   private double[] m_data;

    /** the chart to use. */
    private Chart2D m_innnerChart;

    /** The break the Thread takes between painting two points. */
    private long m_sleep;

    /** The trace to paint to. */
    private ITrace2D m_trace;
    /* CSA added */
    /** This is variable (iUpdateArrayCnt) keeps track of the end of the array of floats (fData)*/
 //   private static int iArrayCnt;
    private int iArrayCnt;
    /** Variable to keep track of the count of the variables for displaying */
 //   private static int iGetArrayCnt;
    private int iGetArrayCnt;
    /* dDataX[] is set within updateData for teh x-value to be plotted */
    private double dDataX[] = new double[CHART_ARR_SZ];
    /**  dDataY[] is the set within updateData for the y-value to be plotted */
    private double dDataY[] = new double[CHART_ARR_SZ];
    /** fDisplayX is the x-value to display in the run method */
    private double dDisplayX;
    /** fDisplayY is the y-value to display in the run method */
    private double dDisplayY;
    /** bDisplayData is a boolean that is returned from getData within the run method */
    private boolean bDisplayData = false;
    /** CHART_ARR_SZ is the array size of the buffers affecting the charting */
    private static final int CHART_ARR_SZ = 400;

    

    /**
     * Creates an instance that paints data to the trace that is added to the
     * chart.
     * <p>
     * 
     * @param chart
     *          the chart to use.
     * 
     * @param trace
     *          the trace to add points to.
     * 
     * @param data
     *          the y values of the points to add.
     * 
     * @param sleep
     *          the length of the sleep break between painting points in ms.
     */
//    public AddPaintRemoveThread(final Chart2D chart, final ITrace2D trace, final double[] data,
//        final long sleep) {
    public AddPaintRemoveThread(final Chart2D chart, final ITrace2D trace,
        final long sleep, String sThreadName) {
      this.m_innnerChart = chart;
      this.m_trace = trace;


      // this.m_trace.setName(this.getName());
      this.m_trace.setName(sThreadName);
     // this.m_data = data;
      this.m_sleep = sleep;
      this.iArrayCnt = 0;
      this.iGetArrayCnt = 0;
    }


        
 

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

      while (true) {
        if (Chart2D.DEBUG_THREADING) {
          System.out.println(this.getName() + "(" + Thread.currentThread().getName() + ") adding trace.");
        }
        this.m_innnerChart.addTrace(this.m_trace);
        for (int i = 0; i < this.dDataX.length; i++) {
          if (Chart2D.DEBUG_THREADING) {
            System.out.println(this.getName() + "(" + Thread.currentThread().getName()
                + ") adding point to " + this.m_trace.getName());
          }
          /* CSA added */
          this.bDisplayData = getData();  // See if new data exists
          if(this.bDisplayData == true) {     // Data to display?
              this.m_trace.addPoint(this.dDisplayX, this.dDisplayY);
            }
          /* END:CSA Added */
          try {
            Thread.sleep(this.m_sleep);
          } catch (InterruptedException e) {
            e.printStackTrace(System.err);
          }
        }
        try {
          Thread.sleep((long) (Math.random() * this.m_sleep));
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        if (Chart2D.DEBUG_THREADING) {
          System.out
              .println(this.getName() + "(" + Thread.currentThread().getName() + ") removing trace.");
        }
        this.m_innnerChart.removeTrace(this.m_trace);
        this.m_trace.removeAllPoints();
        
        try {
          Thread.sleep((long) (Math.random() * this.m_sleep));
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
      }
    }
    /** This method is for updating the data that will be plotted, it uses
     * iArrayCnt to keep track of the end of the array. This is the interface for
     * updating data from outside this class.
    */
    public synchronized void updateData(double x, double y) {
    if(this.iArrayCnt >= (CHART_ARR_SZ-1)) {  // Don't let the buffer overrun.
      this.iArrayCnt = 0;
    }
    this.iArrayCnt++;
    this.dDataX[this.iArrayCnt] = x;
    this.dDataY[this.iArrayCnt] = y;
  }
  /** getData is the method used by run to get the data that was updated with
   * updateData.
   */
  public synchronized boolean getData() {
    if(this.iGetArrayCnt == this.iArrayCnt) {        // If no data to get then return
        return(false);
    } else { // New data so get and return
        this.iGetArrayCnt = this.iArrayCnt;
        this.dDisplayX = this.dDataX[this.iArrayCnt];
        this.dDisplayY = this.dDataY[this.iArrayCnt];
//        System.out.println("x:" + this.dDataX[this.iArrayCnt] + "-- y:" + this.dDataY[this.iArrayCnt]);
//        this.dDisplayX = 7;
//        this.dDisplayY = 5;
        return(true);
    }
  }
}   // End Class AddPaintRemoveThread
  /**
   * Generated <code>serialVersionUID</code>.
   */
  private static final long serialVersionUID = 3256722879394820657L;

  /** Sleep break time between adding two points. */
  private static final int SLEEP = 100;
  

  /**
   * Main entry.
   * <p>
   * 
   * @param args
   *          ignored.
   */
  public static void main(final String[] args) {
//    final java.util.Random rand = new java.util.Random();
//    multiFrame = new JFrame();
//    multiPanel = new JPanel();
//    final MultiTracing wnd = new MultiTracing(multiFrame);
//    multiFrame.setLocation(100, 300);
//    multiFrame.setSize(800,300);
//    multiFrame.setResizable(true);
//    multiFrame.setVisible(true);
//    multiFrame.add(MultiTracing.multiPanel);
//    // first Thread:
//    ITrace2D trace;
//    trace = new Trace2DSimple();
//    trace.setColor(Color.red);
//    //trace.setName("Trace-1");
//    AddPaintRemoveThread thr1 = new AddPaintRemoveThread(wnd.m_chart, trace, MultiTracing.SLEEP,"CSA++");
//    thr1.start();
//
//    java.util.Random dJ = new java.util.Random();
//    int y;
//    for(int x1=0; x1<50;x1++) {
//      y = dJ.nextInt()*x1;
//      if(x1>=49) {
//        x1=0;
//      }
//      try
//      {
//        thr1.sleep((long)1000);
//      } catch(InterruptedException x) {
//        x.printStackTrace(); //CSA
//      }
//        thr1.updateData(x1,y);
//    }
//

    }


  /** The chart to fill. */
  public Chart2D m_chart = null;  //CSA- changed from protected to public
  protected AddPaintRemoveThread thr = null; //CSA
  //protected ITrace2D trace; //CSA

  /** Defcon. */
  public MultiTracing(JTabbedPane multiFrame, String xTitle) {
    final java.util.Random rand = new java.util.Random();
    this.m_chart = new Chart2D(xTitle);
    IAxis axisX = this.m_chart.getAxisX();
    this.m_chart.getAxisX().setPaintGrid(true);
    this.m_chart.getAxisY().setPaintGrid(true);
    this.m_chart.setBackground(Color.lightGray);
    this.m_chart.setGridColor(new Color(0xDD, 0xDD, 0xDD));
    // add WindowListener
 //   multiFrame.addWindowListener(new WindowAdapter() {
      /**
       * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
       */
//      @Override
//      public void windowClosing(final WindowEvent e) {
//      //  System.exit(0);
//      }
//    });
    //Container contentPane = multiFrame.getComponents();
    //contentPane.setLayout(new BorderLayout());
    //contentPane.add(new ChartPanel(this.m_chart), BorderLayout.CENTER);


  }

  /**
   * Enforces to display a certain visible x range that will be expanded if
   * traces in the chart have higher or lower values.
   * <p>
   * 
   * @param forceXRange
   *          the range that at least has to be kept visible.
   */
  public void setForceXRange(final Range forceXRange) {
    this.m_chart.getAxisX().setRangePolicy(new RangePolicyMinimumViewport(forceXRange));
  }

  /**
   * Enforces to display a certain visible x range that will be expanded if
   * traces in the chart have higher or lower values.
   * <p>
   * 
   * @param forceYRange
   *          the range that at least has to be kept visible.
   */
  public void setForceYRange(final Range forceYRange) {
    this.m_chart.getAxisY().setRangePolicy(new RangePolicyMinimumViewport(forceYRange));
  }
}


