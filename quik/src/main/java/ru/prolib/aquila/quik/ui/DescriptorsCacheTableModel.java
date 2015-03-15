package ru.prolib.aquila.quik.ui;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.quik.assembler.cache.Cache;
import ru.prolib.aquila.ui.ClassLabels;
import ru.prolib.aquila.ui.table.*;

/**
 * Модель таблицы для отображения кэша дескрипторов инструментов.
 */
public class DescriptorsCacheTableModel extends TableModel
	implements EventListener
{
	private static final long serialVersionUID = 1680298196695807848L;
	private final Cache cache;

	public DescriptorsCacheTableModel(ClassLabels labels, Columns columns,
			Cache cache)
	{
		super(labels, columns);
		this.cache = cache;
	}
	
	protected void invalidate() {
		rows = cache.getDescriptors();
		super.invalidate();
	}
	
	@Override
	public void start() {
		cache.OnDescriptorsUpdate().addListener(this);		
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		cache.OnDescriptorsUpdate().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		invalidate();
	}

}
