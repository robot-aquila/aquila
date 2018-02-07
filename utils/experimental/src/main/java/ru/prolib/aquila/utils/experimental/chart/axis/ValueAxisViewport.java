package ru.prolib.aquila.utils.experimental.chart.axis;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Range;

public interface ValueAxisViewport {
	
	Range<CDecimal> getValueRange();
	
	void setValueRange(Range<CDecimal> range);

}
