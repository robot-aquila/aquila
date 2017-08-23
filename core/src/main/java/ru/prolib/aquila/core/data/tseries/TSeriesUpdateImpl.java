package ru.prolib.aquila.core.data.tseries;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.data.TSeriesUpdate;

public class TSeriesUpdateImpl implements TSeriesUpdate {
	private final Interval interval;
	private int nodeIndex;
	private Object oldValue, newValue;
	private boolean newNode;
	
	public TSeriesUpdateImpl(Interval interval) {
		this.interval = interval;
		this.nodeIndex = -1;
	}
	
	public TSeriesUpdateImpl setNodeIndex(int nodeIndex) {
		this.nodeIndex = nodeIndex;
		return this;
	}
	
	public TSeriesUpdateImpl setOldValue(Object value) {
		this.oldValue = value;
		return this;
	}
	
	public TSeriesUpdateImpl setNewValue(Object value) {
		this.newValue = value;
		return this;
	}
	
	public TSeriesUpdateImpl setNewNode(boolean newNode) {
		this.newNode = newNode;
		return this;
	}
	
	public TSeriesUpdateImpl setNewNode() {
		return setNewNode(true);
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.tseries.TSeriesUpdate#isNewNode()
	 */
	@Override
	public boolean isNewNode() {
		return newNode;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.tseries.TSeriesUpdate#hasChanged()
	 */
	@Override
	public boolean hasChanged() {
		return ! new EqualsBuilder()
			.append(oldValue, newValue)
			.isEquals();
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.tseries.TSeriesUpdate#getInterval()
	 */
	@Override
	public Interval getInterval() {
		return interval;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.tseries.TSeriesUpdate#getNodeIndex()
	 */
	@Override
	public int getNodeIndex() {
		return nodeIndex;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.tseries.TSeriesUpdate#getOldValue()
	 */
	@Override
	public Object getOldValue() {
		return oldValue;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.tseries.TSeriesUpdate#getNewValue()
	 */
	@Override
	public Object getNewValue() {
		return newValue;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TSeriesUpdateImpl.class ) {
			return false;
		}
		TSeriesUpdateImpl o = (TSeriesUpdateImpl) other;
		return new EqualsBuilder()
				.append(interval, o.interval)
				.append(newNode, o.newNode)
				.append(nodeIndex, o.nodeIndex)
				.append(oldValue, o.oldValue)
				.append(newValue, o.newValue)
				.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[Int=" + interval + " isNew=" + newNode
				+ " Ind=" + nodeIndex + " OV=" + oldValue + " NV=" + newValue + "]";
	}

}
