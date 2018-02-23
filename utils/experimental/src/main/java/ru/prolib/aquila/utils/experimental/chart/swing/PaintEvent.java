package ru.prolib.aquila.utils.experimental.chart.swing;

import java.awt.Graphics2D;

import javax.swing.JComponent;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

public class PaintEvent extends EventImpl {
	private final Graphics2D graphics;
	private final JComponent component;

	public PaintEvent(EventType type, Graphics2D graphics, JComponent component) {
		super(type);
		this.graphics = graphics;
		this.component = component;
	}
	
	public Graphics2D getGraphics() {
		return graphics;
	}
	
	public JComponent getComponent() {
		return component;
	}

}
