package ru.prolib.aquila.utils.experimental.chart.axis;

import ru.prolib.aquila.utils.experimental.chart.Rectangle;

public interface PreparedRuler {
	
	void drawRuler(RulerSetup setup, Rectangle target, Object device);
	void drawGridLines(GridLinesSetup setup, Rectangle plot, Object device);

}
