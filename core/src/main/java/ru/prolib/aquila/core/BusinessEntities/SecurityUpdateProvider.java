package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.data.DataProviderException;


/**
 * Security update provider interface.
 */
public interface SecurityUpdateProvider {
	
	/**
	 * Start update specified security.
	 * <p>
	 * @param security - security instance
	 * @throws DataProviderException - throws if an error occurred
	 */
	public void startUpdating(EditableSecurity security) throws DataProviderException;
	
	/**
	 * Stop update specified security.
	 * <p>
	 * @param security - security instance
	 */
	public void stopUpdating(EditableSecurity security);
	
	/**
	 * Close all acquired resources.
	 */
	public void close();

}
