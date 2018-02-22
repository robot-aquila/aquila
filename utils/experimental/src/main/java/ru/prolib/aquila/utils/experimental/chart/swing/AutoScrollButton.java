package ru.prolib.aquila.utils.experimental.chart.swing;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class AutoScrollButton extends JButton {
	private static final long serialVersionUID = 2016769509574440446L;
	private static final Icon iconEnabled, iconDisabled;
	
	static {
		ClassLoader loader = AutoScrollButton.class.getClassLoader();
		iconEnabled = new ImageIcon(loader.getResource("locked.png"));
		iconDisabled = new ImageIcon(loader.getResource("unlocked.png"));
	}
	
	public AutoScrollButton() {
		setPreferredSize(new Dimension(20, 20));
	}
	
	public void setAutoScroll(boolean autoScrollEnabled) {
		setIcon(autoScrollEnabled ? iconEnabled : iconDisabled);
	}

}
