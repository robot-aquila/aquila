package ru.prolib.aquila.quik.ui;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.quik.dde.*;
import ru.prolib.aquila.ui.ClassLabels;

/**
 * Модель таблицы для отображения кэша стоп-заявок.
 */
public class StopOrdersCacheTableModel extends TableModel
	implements EventListener
{
	private static final long serialVersionUID = -5745418049032020737L;
	private final StopOrdersCache cache;

	public StopOrdersCacheTableModel(ClassLabels labels, Columns columns,
			StopOrdersCache cache)
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
