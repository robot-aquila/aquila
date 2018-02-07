package ru.prolib.aquila.utils.experimental.chart.axis;

public interface ValueAxisRulerBuilder extends RulerBuilder {

	Ruler prepareRuler(ValueAxisDisplayMapper mapper, Object device);
	
}
