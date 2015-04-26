package ru.prolib.aquila.probe.storage.dao;

import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.probe.storage.model.SecurityId;

public interface SecurityIdDAO {
	
	public SecurityId getByDescriptor(SecurityDescriptor descr);
	
	public SecurityId getById(long id);
	
	public List<SecurityId> getAll();

}
