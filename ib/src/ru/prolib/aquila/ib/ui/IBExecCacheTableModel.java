package ru.prolib.aquila.ib.ui;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.ib.assembler.cache.*;
import ru.prolib.aquila.ui.*;
import ru.prolib.aquila.ui.table.*;

/**
 * Модель таблицы кэша сделок.
 */
public class IBExecCacheTableModel  extends TableModel
	implements EventListener
{
	private static final long serialVersionUID = 7080583577217792495L;
	private final Cache cache;

	public IBExecCacheTableModel(ClassLabels labels,
			Columns columns, Cache cache)
	{
		super(labels, columns);
		this.cache = cache;
	}

	@Override
	protected void invalidate() {
		rows = cache.getExecEntries();
		super.invalidate();
	}
	
	@Override
	public void start() {
		cache.OnExecUpdated().addListener(this);
		super.start();
	}
	
	@Override
	public void stop() {
		super.stop();
		cache.OnExecUpdated().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		invalidate();
	}

}
