/*
 *  LabelFormatterNumber.java of jchart2d, a label formatter that 
 *  formats the labels with a number format. 
 *  Copyright (C) 2005 - 2010 Achim Westermann, created on 20.04.2005, 22:34:16
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
package info.monitorenter.gui.chart.labelformatters;

import info.monitorenter.gui.chart.IAxisLabelFormatter;
import info.monitorenter.util.Range;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * An ILabelFormatter that is based on a {@link java.text.NumberFormat}
 * <p>
 * To avoid loss of precision please choose a sufficient resolution for your
 * constructor given NumberFormat. Example: If you add new
 * {@link info.monitorenter.gui.chart.TracePoint2D} instances to the
 * {@link info.monitorenter.gui.chart.Chart2D} every second, prefer using a
 * NumberFormat that at least formats the seconds like (e.g.):
 * 
 * <pre>
 * NumberFormat format = new java.text.SimpleDateFormat(&quot;HH:mm:ss&quot;);
 * </pre>
 * 
 * <p>
 * 
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann </a>
 * 
 * @version $Revision: 1.15 $
 */
public class LabelFormatterNumber extends ALabelFormatter implements IAxisLabelFormatter {

  /** Generated <code>serialVersionUID</code>. */
  private static final long serialVersionUID = 7659252726783423615L;

  /**
   * The internal cached minimum shift of value required to get to distinct
   * Strings from method <code>{@link #format(double)}</code>. This value is
   * computed once and cached because it's computation is expensive.
   */
  private double m_cachedMinValueShift = Double.MAX_VALUE;

  /** The number format to use. */
  protected NumberFormat m_numberFormat;

  /**
   * Default constructor that uses the defalut constructor of
   * <code>{@link DecimalFormat}</code>.
   * <p>
   * 
   */
  public LabelFormatterNumber() {
    this.m_numberFormat = new DecimalFormat();
  }

  /**
   * Creates a label formatter that uses the given number format.
   * <p>
   * 
   * @param numberFormat
   *          the number format to use.
   */
  public LabelFormatterNumber(final NumberFormat numberFormat) {
    super();
    if (numberFormat == null) {
      throw new IllegalArgumentException("Argument numberFormat must not be null.");
    }
    this.setNumberFormat(numberFormat);
  }

  /**
   * @see info.monitorenter.gui.chart.IAxisLabelFormatter#format(double)
   */
  public String format(final double value) {
    return this.m_numberFormat.format(value);
  }

  /**
   * @see info.monitorenter.gui.chart.IAxisLabelFormatter#getMaxAmountChars()
   */
  @Override
  public int getMaxAmountChars() {
    // find the fractions by using range information:
    int fractionDigits = 0;
    Range range = this.getAxis().getRange();
    double dRange = range.getExtent();
    if (dRange < 1) {
      if (dRange == 0) {
        fractionDigits = 1;
      } else {
        if (dRange == 0) {
          fractionDigits = 1;
        } else {
          // find the power
          while (dRange < 1) {
            dRange *= 10;
            fractionDigits++;
          }
        }
      }
    } else {
      if (dRange < 10) {
        fractionDigits = 2;
      } else if (dRange < 100) {
        fractionDigits = 1;
      } else {
        fractionDigits = 0;
      }
    }

    // find integer digits by using longest value:
    int integerDigits = 0;
    double max = range.getMax();
    double min = Math.abs(range.getMin());
    if (max == 0 && min == 0) {
      integerDigits = 1;
    } else if (max < min) {
      while (min > 1) {
        min /= 10;
        integerDigits++;
      }
    } else {
      while (max > 1) {
        max /= 10;
        integerDigits++;
      }
    }

    // check if the internal numberformat would cut values and cause rendering
    // errors:
    if (integerDigits > this.m_numberFormat.getMaximumIntegerDigits()) {
      this.m_numberFormat.setMaximumIntegerDigits(integerDigits);
    }
    if (fractionDigits > this.m_numberFormat.getMaximumFractionDigits()) {
      this.m_numberFormat.setMaximumFractionDigits(fractionDigits);
    }

    // check if the internal numberformat will format bigger numbers than the
    // computed ones thus causing labels overwriting the y axis or each other
    // for the x axis:

    int minFractionDigits = this.m_numberFormat.getMinimumFractionDigits();
    int minIntegerDigits = this.m_numberFormat.getMinimumIntegerDigits();
    if (minFractionDigits > fractionDigits) {
      fractionDigits = minFractionDigits;
    }
    if (minIntegerDigits > integerDigits) {
      integerDigits = minFractionDigits;
    }

    // <sign> integerDigits <dot> fractionDigits:
    int result = 1 + integerDigits + 1 + fractionDigits;
    return result;
   }

  /**
   * @see info.monitorenter.gui.chart.IAxisLabelFormatter#getMinimumValueShiftForChange()
   */
  public double getMinimumValueShiftForChange() {
    if (this.m_cachedMinValueShift == Double.MAX_VALUE) {
      int fractionDigits = this.m_numberFormat.getMaximumFractionDigits();
      this.m_cachedMinValueShift = 1 / Math.pow(10, fractionDigits);
    }
    return this.m_cachedMinValueShift;
  }

  /**
   * @see info.monitorenter.gui.chart.IAxisLabelFormatter#getNextEvenValue(double,
   *      boolean)
   */
  public double getNextEvenValue(final double value, final boolean ceiling) {
    double result;
    double divisor = Math.pow(10, this.m_numberFormat.getMaximumFractionDigits());
    if (ceiling) {
      result = Math.ceil(value * divisor) / divisor;
    } else {
      result = Math.floor(value * divisor) / divisor;
    }
    return result;
  }

  /**
   * Returns the internal <code>NumberFormat</code>.
   * <p>
   * 
   * @return the internal <code>NumberFormat</code>.
   * 
   */
  NumberFormat getNumberFormat() {
    return this.m_numberFormat;
  }

  /**
   * @see info.monitorenter.gui.chart.IAxisLabelFormatter#parse(java.lang.String)
   */
  public Number parse(final String formatted) throws NumberFormatException {
    try {
      return this.m_numberFormat.parse(formatted);
    } catch (ParseException pe) {
      pe.printStackTrace(); //CSA
      throw new NumberFormatException(pe.getMessage());
    }
  }

  /**
   * Sets the number formatter to use.
   * <p>
   * 
   * Fires a <code>{@link java.beans.PropertyChangeEvent}</code> to the
   * listeners added via
   * <code>{@link #addPropertyChangeListener(String, java.beans.PropertyChangeListener)}</code>
   * with the property key <code>{@link #PROPERTY_FORMATCHANGE}</code>.
   * <p>
   * 
   * @param numberFormat
   *          the number formatter to use.
   */
  public final void setNumberFormat(final NumberFormat numberFormat) {
    NumberFormat old = this.m_numberFormat;
    this.m_numberFormat = numberFormat;
    this.m_propertyChangeSupport
        .firePropertyChange(PROPERTY_FORMATCHANGE, old, this.m_numberFormat);
  }
}
