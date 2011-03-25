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

/*
 * Class Overview:
 * This class was created to handle the OptimizationsPanel object.
 */

import java.awt.*;

public class VerticalLayout implements LayoutManager {
    private int hgap;
	private int vgap;
    private int minWidth = 0;
    private int minHeight = 0;
    private int preferredWidth = 0;
    private int preferredHeight = 0;

    public VerticalLayout() {
        this(2, 2);
    }

    public VerticalLayout(int hg, int vg) {
        hgap = hg;
    	vgap = vg;
    }
    
    private void setSizes(Container parent) {
        Dimension d = null;
        preferredWidth = 0;
        preferredHeight = 0;
        minWidth = 0;
        minHeight = 0;
        
        int numComps = parent.getComponentCount();
        for (int i = 0; i < numComps; i++) {
            Component c = parent.getComponent(i);
            if (c.isVisible()) {
                d = c.getPreferredSize();
                preferredHeight += d.height;
                if (i > 0) {
                    preferredHeight += vgap;
                } else {
                    preferredWidth = d.width;
                }
                
                minWidth = Math.max(c.getMinimumSize().width, minWidth);
                minHeight = preferredHeight;
            }
        }
    }

    /*
     * 
     * The following functions are required by LayoutManager...
     * 
     */
    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }
    
    public Dimension minimumLayoutSize(Container parent) {
        Dimension d = new Dimension(0, 0);
        Insets insets = parent.getInsets();
        d.width = minWidth + insets.left + insets.right;
        d.height = minHeight + insets.top + insets.bottom;
        return d;
    }

    public Dimension preferredLayoutSize(Container parent) {
        setSizes(parent);
        Insets insets = parent.getInsets();
        Dimension d = new Dimension(0, 0);
        d.width = preferredWidth + insets.left + insets.right;
        d.height = preferredHeight + insets.top + insets.bottom;
        return d;
    }

    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        int maxWidth = parent.getWidth() - (insets.left + insets.right + (2*hgap));
        int numComps = parent.getComponentCount();
        int previousHeight = 0;
        int x = hgap;
        int y = insets.top + vgap;

        for (int i = 0 ; i < numComps ; i++) {
            Component c = parent.getComponent(i);
            if (c.isVisible()) {
                Dimension d = c.getPreferredSize();
                
                if (i > 0) {
                    y += previousHeight + vgap;
                }

                c.setBounds(x, y, maxWidth, d.height);

                previousHeight = d.height;
            }
        }
    }
}
