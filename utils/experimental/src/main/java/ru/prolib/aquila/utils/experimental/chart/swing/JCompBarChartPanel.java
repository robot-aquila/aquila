package ru.prolib.aquila.utils.experimental.chart.swing;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;

public class JCompBarChartPanel extends JPanel {
	private static final long serialVersionUID = -1571529262095604481L;
	
	private final EventType onBeforePaintChildren;
	/**
	 * The main panel is used to add charts.
	 */
	private final JPanel mainPanel;
	private final JPanel scrollBarPanel;
	private final JCompAutoScrollButton autoScrollButton;
	private final JScrollBar scrollBar;
	
	public JCompBarChartPanel() {
		super(new BorderLayout());
		onBeforePaintChildren = new EventTypeImpl("ON_BEFORE_PAINT_CHILDREN");
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		scrollBarPanel = new JPanel(new BorderLayout());
        scrollBarPanel.add(scrollBar = new JScrollBar(JScrollBar.HORIZONTAL), BorderLayout.CENTER);
        scrollBarPanel.add(autoScrollButton = new JCompAutoScrollButton(), BorderLayout.EAST);
		
		add(mainPanel, BorderLayout.CENTER);
		add(scrollBarPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * Get panel containing charts.
	 * <p>
	 * @return panel instance
	 */
	public JPanel getMainPanel() {
		return mainPanel;
	}
	
	/**
	 * Get panel containing scrolling controls.
	 * <p>
	 * @return panel instance
	 */
	public JPanel getScrollBarPanel() {
		return scrollBarPanel;
	}
	
	/**
	 * Get scroll bar control.
	 * <p>
	 * @return scroll bar instance
	 */
	public JScrollBar getScrollBar() {
		return scrollBar;
	}
	
	/**
	 * Get button to control auto scroll feature.
	 * <p>
	 * @return button instance
	 */
	public JCompAutoScrollButton getAutoScrollButton() {
		return autoScrollButton;
	}
	
	/**
	 * Get type of event which called before children components paint.
	 * <p>
	 * Note that this event is synchronous. It'll fired directly without passing to event queue.
	 * <p>
	 * @return event type
	 */
	public EventType onBeforePaintChildren() {
		return onBeforePaintChildren;
	}
	
	@Override
	protected void paintChildren(Graphics g) {
		PaintEvent event = new PaintEvent(onBeforePaintChildren, (Graphics2D) g, this);
		for ( EventListener listener : onBeforePaintChildren.getListeners() ) {
			listener.onEvent(event);
		}
		super.paintChildren(g);
	}

}
