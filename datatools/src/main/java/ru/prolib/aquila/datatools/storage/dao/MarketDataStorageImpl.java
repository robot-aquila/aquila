package ru.prolib.aquila.datatools.storage.dao;

import java.util.List;

import org.hibernate.*;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
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
	public SecurityProperties getProperties(SecurityDescriptor descr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Aqiterator<SecuritySessionProperties>
		getSessionData(SecurityDescriptor descr, LocalDateTime startingTime) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Aqiterator<Tick>
		getTickData(SecurityDescriptor descr, LocalDateTime startingTime)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
