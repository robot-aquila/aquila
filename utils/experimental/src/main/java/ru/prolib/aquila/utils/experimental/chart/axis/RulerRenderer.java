package ru.prolib.aquila.utils.experimental.chart.axis;

public interface RulerRenderer {
	
	/**
	 * Get symbolic renderer ID.
	 * <p>
	 * @return renderer ID
	 */
	String getID();
	
	int getMaxLabelWidth(Object device);
	
	int getMaxLabelHeight(Object device);
	
	RulerSetup createRulerSetup(RulerID rulerID);

}
