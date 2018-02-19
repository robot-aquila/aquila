package ru.prolib.aquila.utils.experimental.chart.axis;

import ru.prolib.aquila.utils.experimental.chart.Rectangle;

public interface PreparedRuler {
	
	void drawRuler(RulerPosition position, Rectangle target, Object device);
	void drawGridLines(Rectangle plot, Object device);

}
