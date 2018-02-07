package ru.prolib.aquila.utils.experimental.chart.axis;

import java.math.RoundingMode;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;

/**
 * The mapper to display categories axis which points to the right.
 */
public class CategoryAxisDisplayMapperHR implements CategoryAxisDisplayMapper {
	private final int firstBarX, firstBarCategory, numberOfBars;
	private final CDecimal barWidth;
	
	/**
	 * Constructor.
	 * <p>
	 * @param firstBarX - The first visible bar is always leftmost bar. This is
	 * X coordinate of the first bar.
	 * @param firstBarCategory - The index of the category corresponding to
	 * the first visible bar. This index may points to negative or non-existing
	 * category. It's OK, it's just used to specify the offset which will
	 * affect position when the first visible category will be displayed.
	 * @param numberOfBars - Total number of visible bars. This value is used
	 * to calculate the last visible category. All categories greater than the
	 * last considered as out of range. 
	 * @param barWidth - Using an integer as bar width will cause weird
	 * results for most cases. To avoid glitches when displaying bars the
	 * arbitrary precision number is used to represents width of bar.
	 */
	public CategoryAxisDisplayMapperHR(int firstBarX,
									  int firstBarCategory,
									  int numberOfBars,
									  CDecimal barWidth)
	{
		if ( barWidth.compareTo(CDecimalBD.of(1L)) < 0 ) {
			throw new IllegalArgumentException("Bar width expected to be >= 1 but: " + barWidth);
		}
		this.firstBarX = firstBarX;
		this.firstBarCategory = firstBarCategory;
		this.numberOfBars = numberOfBars;
		this.barWidth = barWidth;
	}

	@Override
	public AxisDirection getAxisDirection() {
		return AxisDirection.RIGHT;
	}

	@Override
	public int getNumberOfVisibleBars() {
		return numberOfBars;
	}

	@Override
	public int getFirstVisibleCategory() {
		return firstBarCategory < 0 ? 0 : firstBarCategory;
	}

	@Override
	public int getLastVisibleCategory() {
		return numberOfBars + firstBarCategory - 1;
	}

	@Override
	public int getNumberOfVisibleCategories() {
		return getLastVisibleCategory() - getFirstVisibleCategory() + 1;
	}

	@Override
	public int toCategory(int displayCoord) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public Segment1D toDisplay(int categoryIndex) {
		long index = categoryIndex - firstBarCategory;
		if ( index < 0 || index >= numberOfBars ) {
			throw new IllegalArgumentException("Category out of range: " + categoryIndex);
		}
		return getSegmentOfBar(index);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CategoryAxisDisplayMapperHR.class ) {
			return false;
		}
		CategoryAxisDisplayMapperHR o = (CategoryAxisDisplayMapperHR) other;
		return new EqualsBuilder()
				.append(o.firstBarCategory, firstBarCategory)
				.append(o.firstBarX, firstBarX)
				.append(o.numberOfBars, numberOfBars)
				.append(o.barWidth, barWidth)
				.isEquals();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("firstBarX", firstBarX)
				.append("firstBarCategory", firstBarCategory)
				.append("numberOfBars", numberOfBars)
				.append("barWidth", barWidth)
				.toString();
	}

	@Override
	public int getPlotStart() {
		return firstBarX;
	}

	@Override
	public int getPlotSize() {
		return getSegmentOfBar(numberOfBars - 1).getEnd() - getPlotStart() + 1;
	}

	private Segment1D getSegmentOfBar(long index) {
		CDecimal barStart = barWidth.multiply(index);
		int x1 = barStart.withScale(0, RoundingMode.HALF_UP).toBigDecimal().intValue();
		int x2 = barStart.add(barWidth).withScale(0, RoundingMode.HALF_UP).toBigDecimal().intValue();
		return new Segment1D(firstBarX + x1, x2 - x1);
	}

}
