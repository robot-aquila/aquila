package ru.prolib.aquila.probe.storage.dao;

import java.util.List;

import ru.prolib.aquila.probe.storage.model.SymbolEntity;

public interface DataProvider {
	
	public List<SymbolEntity> getSecurityDescriptors();

}
