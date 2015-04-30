package ru.prolib.aquila.probe.storage.dao;

import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.probe.storage.ConstantSecurityProperties;
import ru.prolib.aquila.probe.storage.model.ConstantSecurityPropertiesEntity;
import ru.prolib.aquila.probe.storage.model.SymbolEntity;

@Repository
public class ConstantSecurityPropertiesRepositoryImpl implements
		ConstantSecurityPropertiesRepository
{
	private SessionFactory sessionFactory;

	public ConstantSecurityPropertiesRepositoryImpl() {
		super();
	}
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public ConstantSecurityPropertiesEntity getById(Long id) {
		ConstantSecurityPropertiesEntity x;
		x = (ConstantSecurityPropertiesEntity)
				getSession().get(ConstantSecurityPropertiesEntity.class, id);
		if ( x == null ) {
			throw new ObjectNotFoundException(id,
					ConstantSecurityProperties.class.toString());
		}
		return x;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ConstantSecurityPropertiesEntity> getAll() {
		return getSession()
				.createCriteria(ConstantSecurityPropertiesEntity.class).list();
	}

	@Override
	public ConstantSecurityPropertiesEntity
		getByDescriptor(SecurityDescriptor descr)
	{
		Session session = getSession();
		ConstantSecurityPropertiesEntity x = (ConstantSecurityPropertiesEntity)
			session.createCriteria(ConstantSecurityPropertiesEntity.class, "p")
			.createAlias("p.symbol", "s")
			.add(Restrictions.eq("s.descr", descr))
			.setMaxResults(1)
			.uniqueResult();
		if ( x == null ) {
			throw new RepositoryObjectNotFoundException(
					ConstantSecurityPropertiesEntity.class.toString()
					+ " of "
					+ descr.toString());
		}
		return x;
	}

	@Override
	public ConstantSecurityPropertiesEntity getBySymbol(SymbolEntity symbol) {
		Session session = getSession();
		ConstantSecurityPropertiesEntity x = (ConstantSecurityPropertiesEntity)
				session.createCriteria(ConstantSecurityPropertiesEntity.class)
				.add(Restrictions.eq("symbol", symbol))
				.setMaxResults(1)
				.uniqueResult();
		if ( x == null ) {
			throw new RepositoryObjectNotFoundException(
					ConstantSecurityPropertiesEntity.class.toString()
					+ " of "
					+ SymbolEntity.class.toString()
					+ "#"
					+ symbol.getId());
		}
		return x;
	}
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void update(ConstantSecurityPropertiesEntity entity) {
		getSession().saveOrUpdate(entity);
	}

	@Override
	public void delete(ConstantSecurityPropertiesEntity entity) {
		getSession().delete(entity);
	}

	@Override
	public ConstantSecurityPropertiesEntity createEntity() {
		return new ConstantSecurityPropertiesEntity();
	}

}
