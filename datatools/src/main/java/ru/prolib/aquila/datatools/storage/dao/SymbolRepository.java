package ru.prolib.aquila.datatools.storage.dao;

import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;

public interface SymbolRepository {
	
	public SymbolEntity getByDescriptor(SecurityDescriptor descr);
	
	public SymbolEntity getById(Long id);
	
	public List<SymbolEntity> getAll();

}
