package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerPosition;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.RLabel;

/**
 * Value axis renderer callback interface.
 */
public interface SWRendererCallbackVA {
	
	void drawRuler(RulerPosition position,
				   	Rectangle target,
				   	Graphics2D graphics,
				   	ValueAxisDisplayMapper mapper,
				   	List<RLabel> labels,
				   	Font labelFont);
	
	void drawGridLines(Rectangle plot,
					Graphics2D graphics,
					ValueAxisDisplayMapper mapper,
					List<RLabel> labels);

}