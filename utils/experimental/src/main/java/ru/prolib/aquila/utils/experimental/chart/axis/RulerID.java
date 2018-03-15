package ru.prolib.aquila.utils.experimental.chart.axis;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Complete key which identifies ruler inside the chart space.
 * <p>
 * Помимо идентификаторов оси и рендерера, дескриптор представляет информацию
 * о расположении линейки относительно графика. Линейка может находиться на
 * на координатной оси либо в области меньших относительно области отображения
 * графика (данных графика) значений, либо в области больших значений.
 * Линейка, находящаяся в области меньших значений идентифицируется как имеющая
 * низшую позицию (lower position). В области больших - как имеющая высшую
 * позицию (upper position). Например, при рассмотрении стандартного экрана,
 * где ось X направлена слева-направо, область отображения низшей позиции будет
 * находиться левее графика, а высшей - справа от него.
 */
public class RulerID {
	private final RulerRendererID rulerRendererID;
	private final boolean isUpperPos;
	
	public RulerID(RulerRendererID rulerRendererID, boolean isUpperPos) {
		this.rulerRendererID = rulerRendererID;
		this.isUpperPos = isUpperPos;
	}
	
	public RulerID(String axisID, String rulerRendererID, boolean isUpperPos) {
		this(new RulerRendererID(axisID, rulerRendererID), isUpperPos);
	}
	
	public RulerRendererID getRulerRendererID() {
		return rulerRendererID;
	}
	
	public String getAxisID() {
		return rulerRendererID.getAxisID();
	}
	
	public String getRendererID() {
		return rulerRendererID.getRendererID();
	}
	
	public boolean isUpperPosition() {
		return isUpperPos;
	}
	
	public boolean isLowerPosition() {
		return ! isUpperPos;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != RulerID.class ) {
			return false;
		}
		RulerID o = (RulerID) other;
		return new EqualsBuilder()
				.append(o.isUpperPos, isUpperPos)
				.append(o.rulerRendererID, rulerRendererID)
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("rulerRendererID", rulerRendererID)
				.append("isUpper", isUpperPos)
				.toString();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(83921, 1412117)
				.append(rulerRendererID)
				.append(isUpperPos)
				.toHashCode();
	}

}
