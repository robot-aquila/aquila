package ru.prolib.aquila.ib.ui;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.ib.assembler.cache.*;
import ru.prolib.aquila.ui.*;
import ru.prolib.aquila.ui.table.*;

/**
 * Модель таблицы кэша заявок.
 */
public class IBOrderCacheTableModel extends TableModel
	implements EventListener
{
	private static final long serialVersionUID = -2444978833986792970L;
	private final Cache cache;

	public IBOrderCacheTableModel(ClassLabels labels,
			Columns columns, Cache cache)
	{
		super(labels, columns);
		this.cache = cache;
	}

	@Override
	protected void invalidate() {
		rows = cache.getOrderEntries();
		super.invalidate();
	}
	
	@Override
	public void start() {
		cache.OnOrderUpdated().addListener(this);
		super.start();
	}
	
	@Override
	public void stop() {
		super.stop();
		cache.OnOrderUpdated().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		invalidate();
	}

}
