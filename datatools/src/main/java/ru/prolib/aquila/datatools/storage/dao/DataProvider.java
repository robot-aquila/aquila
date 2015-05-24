package ru.prolib.aquila.datatools.storage.dao;

import java.util.List;

import ru.prolib.aquila.datatools.storage.model.SymbolEntity;

public interface DataProvider {
	
	public List<SymbolEntity> getSecurityDescriptors();

}
