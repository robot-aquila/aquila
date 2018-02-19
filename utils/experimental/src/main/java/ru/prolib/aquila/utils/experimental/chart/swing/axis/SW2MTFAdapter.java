package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabelMapper;

public interface SW2MTFAdapter {
	
	/**
	 * Get labels for specific parameters.
	 * <p>
	 * @param mapper - category display mapper
	 * @param tframe - time frame of category series
	 * @param labelDimensions - a service to determine label size
	 * @return the time-to-label mapper which was created, refreshed or cached
	 */
	public MTFLabelMapper getLabelMapper(CategoryAxisDisplayMapper mapper,
								  ZTFrame tframe,
								  SWLabelDimensions labelDimensions);

}
