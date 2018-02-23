package ru.prolib.aquila.utils.experimental.chart.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;

public class JCompBarChart extends JPanel {
	private static final long serialVersionUID = -6875011383926322841L;
	
	private final EventType onPaint;
	
	public JCompBarChart() {
		onPaint = new EventTypeImpl("ON_PAINT");
	}
	
	/**
	 * Get type of event which is fired to paint component.
	 * <p>
	 * Note that this event is synchronous. It'll fired directly without passing to event queue.
	 * <p>
	 * @return event type
	 */
	public EventType onPaint() {
		return onPaint;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		PaintEvent event = new PaintEvent(onPaint, (Graphics2D) g, this);
		for ( EventListener listener : onPaint.getListeners() ) {
			listener.onEvent(event);
		}
	}

}
