package ru.prolib.aquila.quik.ui;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.quik.assembler.cache.*;
import ru.prolib.aquila.ui.table.Columns;
import ru.prolib.aquila.ui.table.TableModel;

/**
 * Модель таблицы для отображения кэша всех сделок.
 */
public class TradesCacheTableModel extends TableModel
	implements EventListener
{
	private static final long serialVersionUID = -5026260965294799524L;
	private final Cache cache;

	public TradesCacheTableModel(IMessages messages, Columns columns,
			Cache cache)
	{
		super(messages, columns);
		this.cache = cache;
	}

	protected void invalidate() {
		rows = cache.getTrades();
		super.invalidate();
	}
	
	@Override
	public void start() {
		cache.OnTradesUpdate().addListener(this);		
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		cache.OnTradesUpdate().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		invalidate();
	}

}
