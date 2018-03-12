package ru.prolib.aquila.utils.experimental.chart.data;

import ru.prolib.aquila.core.BusinessEntities.Order;

/**
 * Validator to test order for some conditions to include in set of price levels.
 */
public interface ALOValidator {
	
	boolean isValid(Order order);

}
