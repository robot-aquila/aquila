package ru.prolib.aquila.datatools.storage.dao;

import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.datatools.storage.model.SecurityPropertiesEntity;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;

public interface SecurityPropertiesRepository {
	
	public SecurityPropertiesEntity
		getByDescriptor(SecurityDescriptor descr);
	
	public SecurityPropertiesEntity getBySymbol(SymbolEntity symbol);
	
	public SecurityPropertiesEntity getById(Long id);
	
	public List<SecurityPropertiesEntity> getAll();
	
	public void save(SecurityPropertiesEntity entity);
	
	public void delete(SecurityPropertiesEntity entity);
	
	public SecurityPropertiesEntity createEntity();

}
