package ru.prolib.aquila.datatools.storage.dao;

import java.util.List;

import org.hibernate.*;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.storage.SecurityProperties;
import ru.prolib.aquila.datatools.storage.MarketDataStorage;
import ru.prolib.aquila.datatools.storage.SecuritySessionProperties;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;

@Repository
public class MarketDataStorageImpl implements MarketDataStorage {
	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<SymbolEntity> getSecurityEntries() {
		Session session = sessionFactory.getCurrentSession();
		return session.createQuery("from security_descriptors d").list();
	}

	@Override
	public SecurityProperties getProperties(Symbol symbol) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Aqiterator<SecuritySessionProperties>
		getSessionData(Symbol symbol, LocalDateTime startingTime) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Aqiterator<Tick>
		getTickData(Symbol symbol, LocalDateTime startingTime)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
