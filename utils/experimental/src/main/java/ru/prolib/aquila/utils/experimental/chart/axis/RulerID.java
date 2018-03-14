package ru.prolib.aquila.utils.experimental.chart.axis;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
	private final String axisID, rendererID;
	private final boolean isUpperPos;
	
	public RulerID(String axisID, String rendererID, boolean isUpperPos) {
		this.axisID = axisID;
		this.rendererID = rendererID;
		this.isUpperPos = isUpperPos;
	}
	
	public String getAxisID() {
		return axisID;
	}
	
	public String getRendererID() {
		return rendererID;
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
				.append(o.axisID, axisID)
				.append(o.rendererID, rendererID)
				.isEquals();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[axisID=" + axisID
				+ ", rendererID=" + rendererID
				+ ", isUpper=" + isUpperPos
				+ "]";
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(83921, 1412117)
				.append(axisID)
				.append(rendererID)
				.append(isUpperPos)
				.toHashCode();
	}

}
