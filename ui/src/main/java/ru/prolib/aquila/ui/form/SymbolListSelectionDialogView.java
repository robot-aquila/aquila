package ru.prolib.aquila.ui.form;

import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public interface SymbolListSelectionDialogView {
	
	public List<Symbol> showDialog(List<Symbol> initialList);

}
