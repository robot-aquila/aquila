package ru.prolib.aquila.datatools.storage;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.DataProviderException;

/**
 * Security updates recorder interface.
 */
public interface SecurityUpdateRecorder {
	
	/**
	 * Start recording updates of the specified security.
	 * <p>
	 * @param security - security instance
	 * @throws DataProviderException - throws if an error occurred
	 */
	public void startRecording(Security security) throws DataProviderException;
	
	/**
	 * Stop recording updates of the specified security.
	 * <p>
	 * @param security - security instance
	 */
	public void stopRecording(Security security);
	
	/**
	 * Close all acquired resources.
	 */
	public void close();

}
