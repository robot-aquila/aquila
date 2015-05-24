package ru.prolib.aquila.datatools.storage.dao;

import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;

@Repository
public class SymbolRepositoryImpl implements SymbolRepository {
	private SessionFactory sessionFactory;
	
	public SymbolRepositoryImpl() {
		super();
	}
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;		
	}

	@Override
	public SymbolEntity getByDescriptor(SecurityDescriptor descr) {
		Session session = getSession();
		SymbolEntity x = (SymbolEntity)
				session.createCriteria(SymbolEntity.class)
				.add(Restrictions.eq("descr", descr))
				.setMaxResults(1)
				.uniqueResult();
		if ( x == null ) {
			x = new SymbolEntity();
			x.setDescriptor(descr);
			session.save(x);			

		}
		return x;
	}

	@Override
	public SymbolEntity getById(Long id) {
		SymbolEntity x = (SymbolEntity) getSession().get(SymbolEntity.class, id);
		if ( x == null ) {
			throw new ObjectNotFoundException(id, SymbolEntity.class.toString());
		}
		return x;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SymbolEntity> getAll() {
		return getSession().createCriteria(SymbolEntity.class).list();
	}
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

}
