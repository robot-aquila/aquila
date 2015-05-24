package ru.prolib.aquila.datatools.storage;

import org.joda.time.*;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.*;

public interface TradingDataStorage {
	
	public ConstantSecurityProperties getProperties(SecurityDescriptor descr);
	
	public Aqiterator<TradingSessionProperties>
		getSessionData(SecurityDescriptor descr, LocalDateTime startingTime);
	
	public Aqiterator<Tick>
		getTickData(SecurityDescriptor descr, LocalDateTime startingTime);

}
