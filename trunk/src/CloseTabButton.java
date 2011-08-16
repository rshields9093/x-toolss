

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

class CloseTabButton extends JPanel implements ActionListener {
  private JTabbedPane pane;
  XTOOLSECMonitor.Graph graph;
  int itemIndex;

  public CloseTabButton(XTOOLSECMonitor.Graph graph, JTabbedPane pane,int itemIndex) {
    this.graph = graph;
    this.pane = pane;
    setOpaque(false);
    add(new JLabel(
        pane.getTitleAt(itemIndex+2),
        pane.getIconAt(itemIndex+2),
        JLabel.LEFT));
    Icon closeIcon = new CloseIcon();
    JButton btClose = new JButton(closeIcon);
    btClose.setPreferredSize(new Dimension(
        closeIcon.getIconWidth(), closeIcon.getIconHeight()));
    add(btClose);
    btClose.addActionListener(this);
    pane.setTabComponentAt(itemIndex+2, this);
  }
  public void actionPerformed(ActionEvent e) {
    int i = pane.indexOfTabComponent(this);
    if (i != -1) {
      pane.remove(i);
      graph.remove(i-2);
    }
  }
}