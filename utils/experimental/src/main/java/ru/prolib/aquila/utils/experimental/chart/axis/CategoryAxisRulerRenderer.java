package ru.prolib.aquila.utils.experimental.chart.axis;

public interface CategoryAxisRulerRenderer extends RulerRenderer {

	PreparedRuler prepareRuler(CategoryAxisDisplayMapper mapper, Object device);
	
}
