package ru.prolib.aquila.utils.experimental.chart.data;

import ru.prolib.aquila.core.BusinessEntities.OrderExecution;

/**
 * Order execution validator used by order execution data provider to ensure
 * that execution meets all required conditions.
 */
public interface OEValidator {
	
	boolean isValid(OrderExecution execution);

}
