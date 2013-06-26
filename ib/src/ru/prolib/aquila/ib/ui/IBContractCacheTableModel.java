package ru.prolib.aquila.ib.ui;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.ib.assembler.cache.Cache;
import ru.prolib.aquila.ui.ClassLabels;
import ru.prolib.aquila.ui.table.Columns;
import ru.prolib.aquila.ui.table.TableModel;

/**
 * Модель таблицы кэша контрактов.
 */
public class IBContractCacheTableModel extends TableModel
	implements EventListener
{
	private static final long serialVersionUID = 7315776093893929358L;
	private final Cache cache;

	public IBContractCacheTableModel(ClassLabels labels,
			Columns columns, Cache cache)
	{
		super(labels, columns);
		this.cache = cache;
	}
	
	@Override
	protected void invalidate() {
		rows = cache.getContractEntries();
		super.invalidate();
	}
	
	@Override
	public void start() {
		cache.OnContractUpdated().addListener(this);
		super.start();
	}
	
	@Override
	public void stop() {
		super.stop();
		cache.OnContractUpdated().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		invalidate();
	}

}
