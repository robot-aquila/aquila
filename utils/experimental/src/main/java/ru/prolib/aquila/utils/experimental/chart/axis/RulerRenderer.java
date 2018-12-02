package ru.prolib.aquila.utils.experimental.chart.axis;

public interface RulerRenderer {
	
	/**
	 * Get symbolic renderer ID.
	 * <p>
	 * @return renderer ID
	 */
	String getID();
	
	/**
	 * Get maximum possible label width.
	 * <p>
	 * This method is used by space manager to determine how much space should
	 * be preserved for rulers of vertical axes (at the left and the right).
	 * Ruler renderer must return size it needs to use to draw ruler. The size
	 * must include all pixels needed to draw - texts, lines, spacers, etc...
	 * <p>
	 * @param device - rendering device. This object type is not known to space
	 * manager and just passed from the chart renderer. The ruler renderer
	 * must know type of rendering device and how to utilize it. 
	 * @return maximum possible label width in pixels
	 */
	int getMaxLabelWidth(Object device);

	/**
	 * Get maximum possible label height.
	 * <p>
	 * This method is used by space manager to determine how much space should
	 * be preserved for rulers of horizontal axes (at the top and the bottom).
	 * Ruler renderer must return size it needs to use to draw ruler. The size
	 * must include all pixels needed to draw - texts, lines, spacers, etc...
	 * <p>
	 * @param device - rendering device. This object type is not known to space
	 * manager and just passed from the chart renderer. The ruler renderer
	 * must know type of rendering device and how to utilize it.
	 * @return maximum possible label heigh in pixels
	 */
	int getMaxLabelHeight(Object device);
	
	RulerSetup createRulerSetup(RulerID rulerID);
	GridLinesSetup createGridLinesSetup(RulerRendererID rendererID);

}
