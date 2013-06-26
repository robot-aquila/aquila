package ru.prolib.aquila.ib.ui;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.ib.assembler.cache.*;
import ru.prolib.aquila.ui.*;
import ru.prolib.aquila.ui.table.*;

/**
 * Модель таблицы кэша статусов заявок.
 */
public class IBOrderStatusCacheTableModel extends TableModel
	implements EventListener
{
	private static final long serialVersionUID = -1137096779790495578L;
	private final Cache cache;

	public IBOrderStatusCacheTableModel(ClassLabels labels,
			Columns columns, Cache cache)
	{
		super(labels, columns);
		this.cache = cache;
	}

	@Override
	protected void invalidate() {
		rows = cache.getOrderStatusEntries();
		super.invalidate();
	}
	
	@Override
	public void start() {
		cache.OnOrderStatusUpdated().addListener(this);
		super.start();
	}
	
	@Override
	public void stop() {
		super.stop();
		cache.OnOrderStatusUpdated().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		invalidate();
	}

}
