package ru.prolib.aquila.ib.ui;


import ru.prolib.aquila.core.*;
import ru.prolib.aquila.ib.assembler.cache.*;
import ru.prolib.aquila.ui.*;
import ru.prolib.aquila.ui.table.*;

/**
 * Модель таблицы кэша позиций.
 */
public class IBPositionCacheTableModel extends TableModel
	implements EventListener
{
	private static final long serialVersionUID = 7339961905727431005L;
	private final Cache cache;

	public IBPositionCacheTableModel(ClassLabels labels,
			Columns columns, Cache cache)
	{
		super(labels, columns);
		this.cache = cache;
	}

	@Override
	protected void invalidate() {
		rows = cache.getPositionEntries();
		super.invalidate();
	}
	
	@Override
	public void start() {
		cache.OnPositionUpdated().addListener(this);
		super.start();
	}
	
	@Override
	public void stop() {
		super.stop();
		cache.OnPositionUpdated().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		invalidate();
	}

}
