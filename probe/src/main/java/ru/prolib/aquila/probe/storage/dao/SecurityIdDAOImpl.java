package ru.prolib.aquila.probe.storage.dao;

import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.probe.storage.model.SecurityId;

@Repository
public class SecurityIdDAOImpl implements SecurityIdDAO {
	private SessionFactory sessionFactory;
	
	public SecurityIdDAOImpl() {
		super();
	}
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SecurityId getByDescriptor(SecurityDescriptor descr) {
		Session session = getSession();
		List list = session.createCriteria(SecurityId.class)
				.add(Restrictions.eq("descr", descr))
				.setMaxResults(1)
				.list();
		if ( list.size() > 0 ) {
			return (SecurityId) list.get(0);	
		}
		SecurityId x = new SecurityId();
		x.setDescriptor(descr);
		session.save(x);
		return x;
	}

	@Override
	public SecurityId getById(long id) {
		SecurityId x = (SecurityId) getSession().get(SecurityId.class, id);
		if ( x == null ) {
			throw new ObjectNotFoundException(id, SecurityId.class.toString());
		}
		return x;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SecurityId> getAll() {
		return getSession().createCriteria(SecurityId.class).list();
	}
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

}
