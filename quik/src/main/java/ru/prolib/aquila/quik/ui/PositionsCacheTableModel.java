package ru.prolib.aquila.quik.ui;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.quik.assembler.cache.Cache;
import ru.prolib.aquila.ui.table.Columns;
import ru.prolib.aquila.ui.table.TableModel;

/**
 * Модель таблицы для отображения кэша позиций по деривативам.
 */
public class PositionsCacheTableModel extends TableModel
	implements EventListener
{
	private static final long serialVersionUID = 3346874631502525141L;
	private final Cache cache;

	public PositionsCacheTableModel(IMessages messages, Columns columns,
			Cache cache)
	{
		super(messages, columns);
		this.cache = cache;
	}

	protected void invalidate() {
		rows = cache.getPositions();
		super.invalidate();
	}
	
	@Override
	public void start() {
		cache.OnPositionsUpdate().addListener(this);		
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		cache.OnPositionsUpdate().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		invalidate();
	}

}
