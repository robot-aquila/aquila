package ru.prolib.aquila.quik.ui;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.quik.dde.SecuritiesCache;
import ru.prolib.aquila.ui.ClassLabels;

/**
 * Модель таблицы для отображения кэша инструментов.
 */
public class SecuritiesCacheTableModel extends TableModel
	implements EventListener
{
	private static final long serialVersionUID = 1680298196695807848L;
	private final SecuritiesCache cache;

	public SecuritiesCacheTableModel(ClassLabels labels, Columns columns,
			SecuritiesCache cache)
	{
		super(labels, columns);
		this.cache = cache;
	}
	
	protected void invalidate() {
		rows = cache.getAll();
		super.invalidate();
	}
	
	@Override
	public void start() {
		cache.OnCacheUpdate().addListener(this);		
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		cache.OnCacheUpdate().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		invalidate();
	}

}
