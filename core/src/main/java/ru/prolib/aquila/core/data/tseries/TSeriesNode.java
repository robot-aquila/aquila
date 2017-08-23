package ru.prolib.aquila.core.data.tseries;

import java.time.Instant;
import java.util.ArrayList;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.threeten.extra.Interval;

public class TSeriesNode {
	private final ArrayList<Object> values;
	private final Interval interval;
	private int nodeIndex;
	
	/**
	 * Constructor for testing purposes.
	 * <p>
	 * @param interval - time interval associated with the node
	 * @param values - values storage
	 */
	TSeriesNode(Interval interval, ArrayList<Object> values) {
		this.interval = interval;
		this.nodeIndex = -1;
		this.values = values;
	}
	
	TSeriesNode(Interval interval) {
		this(interval, new ArrayList<>());
	}
	
	public Interval getInterval() {
		return interval;
	}
	
	public int getNodeIndex() {
		return nodeIndex;
	}
	
	public void setNodeIndex(int nodeNewIndex) {
		this.nodeIndex = nodeNewIndex;
	}
	
	public void nodeIndexIncrement() {
		this.nodeIndex ++;
	}
	
	public void nodeIndexDecrement() {
		this.nodeIndex --;
	}
	
	public Object set(int valueID, Object value) {
		if ( valueID >= values.size() ) {
			values.ensureCapacity(valueID + 1);
			for ( int i = values.size(); i <= valueID; i ++ ) {
				values.add(null);
			}
		}
		return values.set(valueID, value);
	}
	
	public Object get(int valueID) {
		return valueID < values.size() ? values.get(valueID) : null;
	}
	
	public Instant getIntervalStart() {
		return interval.getStart();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TSeriesNode.class ) {
			return false;
		}
		TSeriesNode o = (TSeriesNode) other;
		return new EqualsBuilder()
				.append(o.interval, interval)
				.append(o.nodeIndex, nodeIndex)
				.append(o.values, values)
				.isEquals();
	}
	
}
