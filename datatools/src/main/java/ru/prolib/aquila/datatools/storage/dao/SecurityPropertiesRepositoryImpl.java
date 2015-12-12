package ru.prolib.aquila.datatools.storage.dao;

import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.datatools.storage.SecurityProperties;
import ru.prolib.aquila.datatools.storage.model.SecurityPropertiesEntity;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;

@Repository
public class SecurityPropertiesRepositoryImpl implements
		SecurityPropertiesRepository
{
	private SessionFactory sessionFactory;

	public SecurityPropertiesRepositoryImpl() {
		super();
	}
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public SecurityPropertiesEntity getById(Long id) {
		SecurityPropertiesEntity x;
		x = (SecurityPropertiesEntity)
				getSession().get(SecurityPropertiesEntity.class, id);
		if ( x == null ) {
			throw new ObjectNotFoundException(id,
					SecurityProperties.class.toString());
		}
		return x;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SecurityPropertiesEntity> getAll() {
		return getSession()
				.createCriteria(SecurityPropertiesEntity.class).list();
	}

	@Override
	public SecurityPropertiesEntity getBySymbol(Symbol symbol) {
		Session session = getSession();
		SecurityPropertiesEntity x = (SecurityPropertiesEntity)
			session.createCriteria(SecurityPropertiesEntity.class, "p")
			.createAlias("p.symbol", "s")
			.add(Restrictions.eq("s.symbol", symbol))
			.setMaxResults(1)
			.uniqueResult();
		if ( x == null ) {
			throw new RepositoryObjectNotFoundException(
					SecurityPropertiesEntity.class.toString()
					+ " of "
					+ symbol.toString());
		}
		return x;
	}

	@Override
	public SecurityPropertiesEntity getBySymbolEntity(SymbolEntity symbol) {
		Session session = getSession();
		SecurityPropertiesEntity x = (SecurityPropertiesEntity)
				session.createCriteria(SecurityPropertiesEntity.class)
				.add(Restrictions.eq("symbol", symbol))
				.setMaxResults(1)
				.uniqueResult();
		if ( x == null ) {
			throw new RepositoryObjectNotFoundException(
					SecurityPropertiesEntity.class.toString()
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
	public void save(SecurityPropertiesEntity entity) {
		getSession().saveOrUpdate(entity);
	}

	@Override
	public void delete(SecurityPropertiesEntity entity) {
		getSession().delete(entity);
	}

	@Override
	public SecurityPropertiesEntity createEntity() {
		return new SecurityPropertiesEntity();
	}

}
