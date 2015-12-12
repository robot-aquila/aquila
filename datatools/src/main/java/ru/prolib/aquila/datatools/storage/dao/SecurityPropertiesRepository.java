package ru.prolib.aquila.datatools.storage.dao;

import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.datatools.storage.model.SecurityPropertiesEntity;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;

public interface SecurityPropertiesRepository {
	
	public SecurityPropertiesEntity getBySymbol(Symbol symbol);
	
	public SecurityPropertiesEntity getBySymbolEntity(SymbolEntity symbol);
	
	public SecurityPropertiesEntity getById(Long id);
	
	public List<SecurityPropertiesEntity> getAll();
	
	public void save(SecurityPropertiesEntity entity);
	
	public void delete(SecurityPropertiesEntity entity);
	
	public SecurityPropertiesEntity createEntity();

}
