package ru.prolib.aquila.quik.ui;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.quik.dde.TradesCache;
import ru.prolib.aquila.ui.ClassLabels;
import ru.prolib.aquila.ui.table.Columns;
import ru.prolib.aquila.ui.table.TableModel;

/**
 * Модель таблицы для отображения кэша сделок.
 */
public class TradesCacheTableModel extends TableModel
	implements EventListener
{
	private static final long serialVersionUID = -5026260965294799524L;
	private final TradesCache cache;

	public TradesCacheTableModel(ClassLabels labels, Columns columns,
			TradesCache cache)
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
