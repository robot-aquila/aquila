package ru.prolib.aquila.core.BusinessEntities;

import java.io.Closeable;
import java.io.IOException;

public interface OrderDefinitionProvider extends Closeable {
	
	/**
	 * Get next definition of an order.
	 * <p>
	 * @return order definition or null if no more definitions
	 * @throws IOException an error occurred
	 */
	OrderDefinition getNextDefinition() throws IOException;
}
