package ru.prolib.aquila.datatools.storage;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;

public interface MarketDataStorage {
	
	public List<SymbolEntity> getSecurityEntries();
	
	public SecurityProperties getProperties(Symbol symbol);
	
	public Aqiterator<SecuritySessionProperties>
		getSessionData(Symbol symbol, LocalDateTime startingTime);
	
	public Aqiterator<Tick>
		getTickData(Symbol symbol, LocalDateTime startingTime);

}
