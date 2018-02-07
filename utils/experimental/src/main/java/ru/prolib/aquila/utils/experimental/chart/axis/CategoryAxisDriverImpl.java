package ru.prolib.aquila.utils.experimental.chart.axis;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;

public class CategoryAxisDriverImpl implements CategoryAxisDriver {
	private static final int BASE_SCALE = 5;
	
	private AxisDirection dir;
	
	public CategoryAxisDriverImpl(AxisDirection dir) {
		setAxisDirection(dir);
	}
	
	@Override
	public synchronized void setAxisDirection(AxisDirection dir) {
		if ( dir != AxisDirection.RIGHT ) {
			throw new IllegalArgumentException("Unsupported axis direction: " + dir);
		}
		this.dir = dir;
	}

	@Override
	public synchronized AxisDirection getAxisDirection() {
		return dir;
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
