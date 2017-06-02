package ru.prolib.aquila.core.BusinessEntities.osc;

import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainer;

/**
 * Controller interface of container.
 */
public interface OSCController {
	
	/**
	 * Check that container contains minimum required data.
	 * <p>
	 * This method called on every update until container in unavailable status.
	 * When this method returns true, then availability status switches on.
	 * Derived classes should use this method to check specific conditions based
	 * on container contents. 
	 * <p>
	 * @param container - the container
	 * @return true if minimum requirements are met, false otherwise
	 */
	public boolean hasMinimalData(ObservableStateContainer container);
	
	/**
	 * Perform additional update processing.
	 * <p>
	 * This method called when an update event was enqueued. Derived classes
	 * should override this method to implement additional events.
	 * <p>
	 * @param container - the container
	 */
	public void processUpdate(ObservableStateContainer container);
	
	/**
	 * Perform availability status switching.
	 * <p>
	 * This method called when container become available. Derived classes
	 * should override this method to implement additional events.
	 * <p>
	 * @param container - the container
	 */
	public void processAvailable(ObservableStateContainer container);
	
}