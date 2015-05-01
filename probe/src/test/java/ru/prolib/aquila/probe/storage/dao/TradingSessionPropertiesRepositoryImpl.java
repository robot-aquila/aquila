package ru.prolib.aquila.probe.storage.dao;

import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;

import ru.prolib.aquila.probe.storage.model.TradingSessionPropertiesEntity;

public class TradingSessionPropertiesRepositoryImpl
	implements TradingSessionPropertiesRepository
{
	private SessionFactory sessionFactory;
	
	public TradingSessionPropertiesRepositoryImpl() {
		super();
	}
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public TradingSessionPropertiesEntity createEntity() {
		return new TradingSessionPropertiesEntity();
	}

	@Override
	public TradingSessionPropertiesEntity getById(Long id) {
		TradingSessionPropertiesEntity x;
		x = (TradingSessionPropertiesEntity)
				getSession().get(TradingSessionPropertiesEntity.class, id);
		if ( x == null ) {
			throw new ObjectNotFoundException(id,
					TradingSessionPropertiesEntity.class.toString());
		}
		return x;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TradingSessionPropertiesEntity> getAll() {
		return getSession()
				.createCriteria(TradingSessionPropertiesEntity.class)
				.addOrder(Order.asc("snapshotTime"))
				.addOrder(Order.asc("id"))
				.list();
	}

	@Override
	public void update(TradingSessionPropertiesEntity entity) {
		getSession().saveOrUpdate(entity);
	}

	@Override
	public void delete(TradingSessionPropertiesEntity entity) {
		getSession().delete(entity);
	}

}
