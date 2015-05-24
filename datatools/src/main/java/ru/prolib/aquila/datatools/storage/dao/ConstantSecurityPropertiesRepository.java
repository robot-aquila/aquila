package ru.prolib.aquila.datatools.storage.dao;

import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.datatools.storage.model.ConstantSecurityPropertiesEntity;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;

public interface ConstantSecurityPropertiesRepository {
	
	public ConstantSecurityPropertiesEntity
		getByDescriptor(SecurityDescriptor descr);
	
	public ConstantSecurityPropertiesEntity getBySymbol(SymbolEntity symbol);
	
	public ConstantSecurityPropertiesEntity getById(Long id);
	
	public List<ConstantSecurityPropertiesEntity> getAll();
	
	public void update(ConstantSecurityPropertiesEntity entity);
	
	public void delete(ConstantSecurityPropertiesEntity entity);
	
	public ConstantSecurityPropertiesEntity createEntity();

}
