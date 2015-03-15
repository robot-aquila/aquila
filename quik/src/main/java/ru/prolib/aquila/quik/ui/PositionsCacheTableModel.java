package ru.prolib.aquila.quik.ui;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.quik.assembler.cache.Cache;
import ru.prolib.aquila.ui.ClassLabels;
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

	public PositionsCacheTableModel(ClassLabels labels, Columns columns,
			Cache cache)
	{
		super(labels, columns);
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
