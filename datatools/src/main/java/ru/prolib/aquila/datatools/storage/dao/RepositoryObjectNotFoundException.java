package ru.prolib.aquila.datatools.storage.dao;

import org.hibernate.HibernateException;

public class RepositoryObjectNotFoundException extends HibernateException {
	private static final long serialVersionUID = 1L;

	public RepositoryObjectNotFoundException(String message) {
		super(message);
	}

}
