package ru.prolib.aquila.utils.experimental.chart.axis;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;

public class CategoryAxisDriverImpl extends AxisDriverImpl implements CategoryAxisDriver {
	private static final int BASE_SCALE = 5;
	private final boolean allowBarWidthLtOne;
	
	public CategoryAxisDriverImpl(String id,
								  AxisDirection dir,
								  RulerRendererRegistry rulerRegistry,
								  boolean allowBarWidthLtOne)
	{
		super(id, dir, rulerRegistry);
		if ( dir != AxisDirection.RIGHT ) {
			throw new IllegalArgumentException("Unsupported axis direction: " + dir);
		}
		this.allowBarWidthLtOne = allowBarWidthLtOne;
	}
	
	public CategoryAxisDriverImpl(String id,
								  AxisDirection dir,
								  RulerRendererRegistry rulerRegistry)
	{
		this(id, dir, rulerRegistry, false);
	}
	
	public CategoryAxisDriverImpl(String id,
								  AxisDirection dir,
								  boolean allowBarWidthLtOne)
	{
		this(id, dir, new RulerRendererRegistryImpl(), allowBarWidthLtOne);
	}
	
	public CategoryAxisDriverImpl(String id,
								  AxisDirection dir)
	{
		this(id, dir, new RulerRendererRegistryImpl());
	}
	
	public boolean isBarWidthLtOneAllowed() {
		return allowBarWidthLtOne;
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
		
		// The special case: viewport length is zero
		if ( numberOfBars == 0 ) {
			return new CategoryAxisDisplayMapperHR(
					segment.getStart(),
					0,
					0,
					CDecimalBD.of((long) segment.getLength()),
					allowBarWidthLtOne
				);
		}
		
		CDecimal barWidth = CDecimalBD.of((long) segment.getLength())
				.divideExact((long) numberOfBars, BASE_SCALE);
		if ( ! allowBarWidthLtOne && barWidth.compareTo(CDecimalBD.of(1L)) < 0 ) {
			barWidth = CDecimalBD.of(1L).withScale(BASE_SCALE);
			numberOfBars = segment.getLength();
		}
		return new CategoryAxisDisplayMapperHR(segment.getStart(),
				viewport.getLastCategory() - numberOfBars + 1,
				numberOfBars,
				barWidth,
				allowBarWidthLtOne);
	}

}
