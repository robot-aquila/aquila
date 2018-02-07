package ru.prolib.aquila.utils.experimental.chart.axis;

import ru.prolib.aquila.utils.experimental.chart.Rectangle;

public interface Ruler {
	
	void drawRuler(AxisPosition position, Rectangle target, Object device);
	void drawGrid(Rectangle plot, Object device);

}
