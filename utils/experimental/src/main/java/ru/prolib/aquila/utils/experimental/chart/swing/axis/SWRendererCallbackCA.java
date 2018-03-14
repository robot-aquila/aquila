package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.RLabel;

/**
 * Category axis renderer callback interface.
 */
public interface SWRendererCallbackCA {

	void drawRuler(RulerSetup setup,
					Rectangle target,
					Graphics2D graphics,
					CategoryAxisDisplayMapper mapper,
					List<RLabel> labels,
					Font labelFont);
	
	void drawGridLines(Rectangle plot,
					Graphics2D graphics,
					CategoryAxisDisplayMapper mapper,
					List<RLabel> labels);
	
}