package ru.prolib.aquila.utils.experimental.chart;

import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;

public class BCDisplayContextImpl implements BCDisplayContext {
	private final CategoryAxisDisplayMapper categoryAxisMapper;
	private final ValueAxisDisplayMapper valueAxisMapper;
	private final Rectangle plot;
	
	public BCDisplayContextImpl(CategoryAxisDisplayMapper categoryAxisMapper,
								ValueAxisDisplayMapper valueAxisMapper,
								Rectangle plot)
	{
		this.categoryAxisMapper = categoryAxisMapper;
		this.valueAxisMapper = valueAxisMapper;
		this.plot = plot;
	}

	@Override
	public CategoryAxisDisplayMapper getCategoryAxisMapper() {
		return categoryAxisMapper;
	}

	@Override
	public ValueAxisDisplayMapper getValueAxisMapper() {
		return valueAxisMapper;
	}
	
	@Override
	public Rectangle getPlotArea() {
		return plot;
	}

}
