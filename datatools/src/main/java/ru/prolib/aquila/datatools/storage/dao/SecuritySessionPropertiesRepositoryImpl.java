package ru.prolib.aquila.datatools.storage.dao;

import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;

import ru.prolib.aquila.datatools.storage.dao.SecuritySessionPropertiesRepository;
import ru.prolib.aquila.datatools.storage.model.SecuritySessionPropertiesEntity;

public class SecuritySessionPropertiesRepositoryImpl
	implements SecuritySessionPropertiesRepository
{
	private SessionFactory sessionFactory;
	
	public SecuritySessionPropertiesRepositoryImpl() {
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
	public SecuritySessionPropertiesEntity createEntity() {
		return new SecuritySessionPropertiesEntity();
	}

	@Override
	public SecuritySessionPropertiesEntity getById(Long id) {
		SecuritySessionPropertiesEntity x;
		x = (SecuritySessionPropertiesEntity)
				getSession().get(SecuritySessionPropertiesEntity.class, id);
		if ( x == null ) {
			throw new ObjectNotFoundException(id,
					SecuritySessionPropertiesEntity.class.toString());
		}
		return x;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SecuritySessionPropertiesEntity> getAll() {
		return getSession()
				.createCriteria(SecuritySessionPropertiesEntity.class)
				.addOrder(Order.asc("snapshotTime"))
				.addOrder(Order.asc("id"))
				.list();
	}

	@Override
	public void save(SecuritySessionPropertiesEntity entity) {
		getSession().saveOrUpdate(entity);
	}

	@Override
	public void delete(SecuritySessionPropertiesEntity entity) {
		getSession().delete(entity);
	}

}
