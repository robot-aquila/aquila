package ru.prolib.aquila.quik.ui;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.quik.assembler.cache.Cache;
import ru.prolib.aquila.ui.table.*;

/**
 * Модель таблицы для отображения кэша дескрипторов инструментов.
 */
public class SymbolsCacheTableModel extends TableModel
	implements EventListener
{
	private static final long serialVersionUID = 1680298196695807848L;
	private final Cache cache;

	public SymbolsCacheTableModel(IMessages messages, Columns columns, Cache cache) {
		super(messages, columns);
		this.cache = cache;
	}
	
	protected void invalidate() {
		rows = cache.getSymbols();
		super.invalidate();
	}
	
	@Override
	public void start() {
		cache.OnSymbolsUpdate().addListener(this);		
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		cache.OnSymbolsUpdate().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		invalidate();
	}

}
