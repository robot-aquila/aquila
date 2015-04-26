package ru.prolib.aquila.probe.storage.dao;

import java.util.List;

import org.hibernate.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

import ru.prolib.aquila.probe.storage.model.SymbolEntity;

@Repository
public class DataProviderImpl implements DataProvider {
	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<SymbolEntity> getSecurityDescriptors() {
		Session session = sessionFactory.getCurrentSession();
		return session.createQuery("from security_descriptors d").list();
	}

}
