package ru.prolib.aquila.probe.storage;

import org.joda.time.DateTime;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.*;

public interface TradingDataStorage {
	
	public ConstantSecurityProperties getProperties(SecurityDescriptor descr);
	
	public Aqiterator<TradingSessionProperties>
		getSessionData(SecurityDescriptor descr, DateTime startingTime);
	
	public Aqiterator<Tick>
		getTickData(SecurityDescriptor descr, DateTime startingTime);

}
