package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.axis.GridLinesSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.RLabel;

/**
 * Value axis renderer callback interface.
 */
public interface SWRendererCallbackVA {
	
	void drawRuler(RulerSetup setup,
				   	Rectangle target,
				   	Graphics2D graphics,
				   	ValueAxisDisplayMapper mapper,
				   	List<RLabel> labels,
				   	Font labelFont);
	
	void drawGridLines(GridLinesSetup setup,
					Rectangle plot,
					Graphics2D graphics,
					ValueAxisDisplayMapper mapper,
					List<RLabel> labels);

}