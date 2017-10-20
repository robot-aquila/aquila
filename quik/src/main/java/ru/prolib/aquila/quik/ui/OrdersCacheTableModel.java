package ru.prolib.aquila.quik.ui;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.quik.assembler.cache.*;
import ru.prolib.aquila.ui.table.Columns;
import ru.prolib.aquila.ui.table.TableModel;

/**
 * Модель таблицы для отображения кэша заявок. 
 */
public class OrdersCacheTableModel extends TableModel
	implements EventListener
{
	private static final long serialVersionUID = -5170798510001365062L;
	private final Cache cache;
	
	public OrdersCacheTableModel(IMessages messages,
			Columns columns, Cache cache)
	{
		super(messages, columns);
		this.cache = cache;
	}
	
	protected void invalidate() {
		rows = cache.getOrders();
		super.invalidate();
	}
	
	@Override
	public void start() {
		cache.OnOrdersUpdate().addListener(this);		
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		cache.OnOrdersUpdate().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		invalidate();
	}

}
