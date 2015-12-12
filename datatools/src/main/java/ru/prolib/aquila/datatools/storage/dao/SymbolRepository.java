package ru.prolib.aquila.datatools.storage.dao;

import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;

public interface SymbolRepository {
	
	public SymbolEntity getBySymbol(Symbol symbol);
	
	public SymbolEntity getById(Long id);
	
	public List<SymbolEntity> getAll();

}
