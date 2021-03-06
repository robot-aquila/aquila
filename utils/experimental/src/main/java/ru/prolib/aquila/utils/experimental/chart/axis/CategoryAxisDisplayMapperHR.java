package ru.prolib.aquila.utils.experimental.chart.axis;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

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
	private final boolean allowBarWidthLtOne;
	
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
	 * @param allowBarWidthLtOne - if enabled then less than one pixel bar width
	 * allowed. This may lead to bar overlapping and weird results. But this
	 * guarantee that all desired data range will be visible independently of
	 * plot size.
	 */
	public CategoryAxisDisplayMapperHR(int firstBarX,
									  int firstBarCategory,
									  int numberOfBars,
									  CDecimal barWidth,
									  boolean allowBarWidthLtOne)
	{
		if ( ! allowBarWidthLtOne && barWidth.compareTo(CDecimalBD.of(1L)) < 0 ) {
			throw new IllegalArgumentException("Bar width expected to be >= 1 but: " + barWidth);
		}
		this.firstBarX = firstBarX;
		this.firstBarCategory = firstBarCategory;
		this.numberOfBars = numberOfBars;
		this.barWidth = barWidth;
		this.allowBarWidthLtOne = allowBarWidthLtOne;
	}
	
	public CategoryAxisDisplayMapperHR(int firstBarX,
									   int firstBarCategory,
									   int numberOfBars,
									   CDecimal barWidth)
	{
		this(firstBarX, firstBarCategory, numberOfBars, barWidth, false);
	}
	
	public boolean isBarWidthLtOneAllowed() {
		return allowBarWidthLtOne;
	}
	
	public CDecimal getBarWidth() {
		return barWidth;
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
		int x1 = displayCoord - firstBarX;
		int rel_index = of((long) x1).divideExact(barWidth, 0).toBigDecimal().intValue();
		int abs_index = rel_index + firstBarCategory;
		Segment1D seg = getSegmentOfBar(rel_index);
		if ( displayCoord >= seg.getStart() && displayCoord <= seg.getEnd() ) {
			return checkIndex(abs_index);
		} else if ( displayCoord < seg.getStart() ) {
			return checkIndex(abs_index - 1);
		}
		throw new IllegalStateException();
	}
	
	private int checkIndex(int index) {
		if ( index < firstBarCategory || index > getLastVisibleCategory() ) {
			throw new IllegalArgumentException("Illegal index: " + index);
		}
		return index;
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
		if ( numberOfBars <= 0 ) {
			return barWidth.toBigDecimal().intValue();
		}
		return getSegmentOfBar(numberOfBars - 1).getEnd() - getPlotStart() + 1;
	}
	
	@Override
	public Segment1D getPlot() {
		return new Segment1D(getPlotStart(), getPlotSize());
	}

	private Segment1D getSegmentOfBar(long index) {
		CDecimal barStart = barWidth.multiply(index);
		int x1 = barStart.withScale(0, RoundingMode.HALF_UP).toBigDecimal().intValue();
		int x2 = barStart.add(barWidth).withScale(0, RoundingMode.HALF_UP).toBigDecimal().intValue();
		return new Segment1D(firstBarX + x1, x2 - x1);
	}

}
