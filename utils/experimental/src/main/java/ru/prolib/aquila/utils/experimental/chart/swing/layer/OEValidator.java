package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import ru.prolib.aquila.core.BusinessEntities.OrderExecution;

public interface OEValidator {
	
	boolean isValid(OrderExecution execution);

}
