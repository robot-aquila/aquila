package ru.prolib.aquila.utils.experimental.chart.axis;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;

public class CategoryAxisDriverImpl extends AxisDriverImpl implements CategoryAxisDriver {
	private static final int BASE_SCALE = 5;
	
	public CategoryAxisDriverImpl(String id,
								  AxisDirection dir,
								  RulerRendererRegistry rulerRegistry)
	{
		super(id, dir, rulerRegistry);
		if ( dir != AxisDirection.RIGHT ) {
			throw new IllegalArgumentException("Unsupported axis direction: " + dir);
		}
	}
	
	public CategoryAxisDriverImpl(String id,
								  AxisDirection dir)
	{
		this(id, dir, new RulerRendererRegistryImpl());
	}

	@Override
	public synchronized CategoryAxisDisplayMapper
		createMapper(Segment1D segment, CategoryAxisViewport viewport)
	{
		if ( dir.isVertical() ) {
			throw new IllegalStateException("Axis direction is not supported: " + dir);
		}
		int numberOfBars = viewport.getNumberOfCategories();
		if ( viewport.getPreferredNumberOfBars() != null ) {
			numberOfBars = viewport.getPreferredNumberOfBars();
		}
		CDecimal barWidth = CDecimalBD.of((long) segment.getLength())
				.divideExact((long) numberOfBars, BASE_SCALE);
		if ( barWidth.compareTo(CDecimalBD.of(1L)) < 0 ) {
			barWidth = CDecimalBD.of(1L).withScale(BASE_SCALE);
			numberOfBars = segment.getLength();
		}
		return new CategoryAxisDisplayMapperHR(segment.getStart(),
				viewport.getLastCategory() - numberOfBars + 1,
				numberOfBars,
				barWidth);
	}

}
