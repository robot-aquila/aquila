package ru.prolib.aquila.quik.ui;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.quik.dde.*;
import ru.prolib.aquila.ui.*;
import ru.prolib.aquila.ui.table.Columns;
import ru.prolib.aquila.ui.table.TableModel;

/**
 * Модель таблицы для отображения кэша заявок. 
 */
public class OrdersCacheTableModel extends TableModel
	implements EventListener
{
	private static final long serialVersionUID = -5170798510001365062L;
	private final OrdersCache cache;
	
	public OrdersCacheTableModel(ClassLabels labels,
			Columns columns, OrdersCache cache)
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
