package ru.prolib.aquila.datatools.storage;

import java.time.LocalDateTime;

import ru.prolib.aquila.core.BusinessEntities.Security;

/**
 * Security attributes storage service.
 */
@Deprecated
public interface SecurityStorageService {
	
	/**
	 * Make a snapshot of trading session attributes.
	 * <p>
	 * This method will make snapshot of the attributes only if they are changed
	 * with previous one. The terminal's current time will be used as a snapshot
	 * timestamp.
	 * <p>
	 * @param security - security to store attributes
	 */
	public void snapshotSessionAttributes(Security security);
	
	/**
	 * Make a snapshot of trading session attributes.
	 * <p>
	 * This method will force create a snapshot independently of changes with
	 * previous one.
	 * <p>
	 * @param security - security to store attributes
	 * @param time - timestamp of the snapshot
	 */
	public void snapshotSessionAttributes(Security security, LocalDateTime time);

}
