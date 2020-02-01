package ru.prolib.aquila.qforts.impl;

import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.data.DataSource;
import ru.prolib.aquila.data.SymbolDataService;

public interface QFSymbolDataService extends SymbolDataService {
	void setTerminal(EditableTerminal terminal);
	void setDataSource(DataSource data_source);
}
