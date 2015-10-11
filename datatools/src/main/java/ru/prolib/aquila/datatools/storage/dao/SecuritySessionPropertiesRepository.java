package ru.prolib.aquila.datatools.storage.dao;

import java.util.List;

import ru.prolib.aquila.datatools.storage.model.SecuritySessionPropertiesEntity;

public interface SecuritySessionPropertiesRepository {
	
	public SecuritySessionPropertiesEntity createEntity();
	
	public SecuritySessionPropertiesEntity getById(Long id);
	
	public List<SecuritySessionPropertiesEntity> getAll();
	
	public void save(SecuritySessionPropertiesEntity entity);
	
	public void delete(SecuritySessionPropertiesEntity entity);

}
