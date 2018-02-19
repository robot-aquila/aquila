package ru.prolib.aquila.utils.experimental.chart.axis;

public interface ValueAxisRulerRenderer extends RulerRenderer {

	PreparedRuler prepareRuler(ValueAxisDisplayMapper mapper, Object device);
	
}
