package ru.prolib.aquila.quik.ui;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.quik.assembler.cache.Cache;
import ru.prolib.aquila.ui.ClassLabels;
import ru.prolib.aquila.ui.table.*;

/**
 * Модель таблицы собственных сделок.
 */
public class OwnTradesCacheTableModel extends TableModel
	implements EventListener
{
	private static final long serialVersionUID = 3245137409064407464L;
	private final Cache cache;
	
	public OwnTradesCacheTableModel(ClassLabels labels,
			Columns columns, Cache cache)
	{
		super(labels, columns);
		this.cache = cache;
	}
	
	protected void invalidate() {
		rows = cache.getOwnTrades();
		super.invalidate();
	}
	
	@Override
	public void start() {
		cache.OnOwnTradesUpdate().addListener(this);		
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		cache.OnOwnTradesUpdate().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		invalidate();
	}

}
