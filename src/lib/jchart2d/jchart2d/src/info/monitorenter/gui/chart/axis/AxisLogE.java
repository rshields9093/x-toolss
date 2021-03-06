/*
 *  AxisLogE.java of project jchart2d, Axis implementation with log display.
 *  Copyright (c) 2007 - 2010 Achim Westermann, created on 20:33:13.
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
 *
 */
package info.monitorenter.gui.chart.axis;

import info.monitorenter.gui.chart.IAxisLabelFormatter;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.labelformatters.LabelFormatterSimple;

import java.util.Iterator;

/**
 * An {@link info.monitorenter.gui.chart.axis.AAxis} with log scaled display of
 * values.
 * <p>
 * 
 * <h2>Caution</h2>
 * This will not work with negative values (Double.NaN is computed for log of
 * negative values).
 * <p>
 * This will even not work with values < 1.0 as the log transformation turns
 * negative for values < 1.0 and becomes {@link Double#NEGATIVE_INFINITY} with
 * lim -> 0.0 with more and more turns to a 100 % CPU load.
 * <p>
 * 
 * @author Pieter-Jan Busschaert (contributor)
 * 
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann</a>
 * 
 * 
 * @version $Revision: 1.12 $
 */
public class AxisLogE extends AAxisTransformation {

  /** Generated <code>serialVersionUID</code>. */
  private static final long serialVersionUID = 1839309514449455729L;

  /**
   * Creates an instance that uses a {@link LabelFormatterSimple} for formatting
   * numbers.
   * <p>
   * 
   */
  public AxisLogE() {
    super(new LabelFormatterSimple());
  }

  /**
   * Creates an instance that will the given label formatter for formatting
   * labels.
   * <p>
   * 
   * @param formatter
   *          needed for formatting labels of this axis.
   * 
   */
  public AxisLogE(final IAxisLabelFormatter formatter) {
    super(formatter);
  }

  /**
   * Performs {@link Math#log10(double)} with a check for reaching infinity.
   * <p>
   * 
   * The argument should not be negative, so only normalized values (no chart
   * values but their scaled values or pixel values) should be given here.
   * <p>
   * 
   * If the argument is close to zero, the result of log would be
   * {@link Double#POSITIVE_INFINITY} which is transformed to
   * {@link Double#MAX_VALUE}.
   * <p>
   * 
   * @param in
   *          the value to compute the log base 10 of.
   * 
   * @return log base 10 for the given value.
   */
  @Override
  protected double transform(final double in) {
    double toTransform = in;
    // Starting from 1 downwards the transformation of this value becomes
    // negative,
    // lim -> 0 becomes Double.NEGATIVE_INFINITY, which
    // causes the "while(true)" 100 % load effect.
    // So everything is disallowed below 1.0.
    if (toTransform < 1) {
      // allow to transform the input for empty traces or all traces with empty
      // points:
      Iterator<ITrace2D> itTraces = this.m_accessor.getChart().getTraces().iterator();
      if (!itTraces.hasNext()) {
        toTransform = 1.0;
      } else {
        ITrace2D trace;
        while (itTraces.hasNext()) {
          trace = itTraces.next();
          if (trace.iterator().hasNext()) {
            // Illegal value for transformation defined by a point added:
            throw new IllegalArgumentException(this.getClass().getName()
                + " must not be used with values < 1 :(" + toTransform + ")!");
          }
        }
        // No illegal point: everything was empty
        toTransform = 1.0;
      }
    }
    // TODO: change this to Math.log10 as soon as java 1.5 is used:
    double result = Math.log(toTransform);
    if (result == Double.POSITIVE_INFINITY) {
      result = Double.MAX_VALUE;
    }
    return result;
  }

  /**
   * @see AAxisTransformation#untransform(double)
   */
  @Override
  protected double untransform(final double in) {
    return Math.pow(Math.E, in);
  }

}
