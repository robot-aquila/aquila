package ru.prolib.aquila.core.data.tseries;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.threeten.extra.Interval;

import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.concurrency.Lockable;
import ru.prolib.aquila.core.data.TSeriesUpdate;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.ValueOutOfRangeException;

public class TSeriesNodeStorage implements Lockable {
	private final LID lid;
	private final Lock lock;
	private final List<TSeriesNode> nodeList;
	private final Map<Instant, TSeriesNode> nodeMap;
	private final TimeFrame timeFrame;
	private int lastSeriesID;
	private TSeriesNode lastNode;
	
	TSeriesNodeStorage(TimeFrame timeFrame, List<TSeriesNode> nodeList,
			Map<Instant, TSeriesNode> nodeMap)
	{
		this.lid = LID.createInstance();
		this.lock = new ReentrantLock();
		this.timeFrame = timeFrame;
		this.nodeList = nodeList;
		this.nodeMap = nodeMap;
		this.lastSeriesID = -1;
	}
	
	public TSeriesNodeStorage(TimeFrame timeFrame) {
		this(timeFrame, new ArrayList<>(), new HashMap<>());
	}

	@Override
	public LID getLID() {
		return lid;
	}

	@Override
	public void lock() {
		lock.lock();
	}

	@Override
	public void unlock() {
		lock.unlock();
	}
	
	/**
	 * Get timeframe.
	 * <p>
	 * @return timeframe of the series
	 */
	public TimeFrame getTimeFrame() {
		return timeFrame;
	}
	
	/**
	 * Register new series.
	 * <p>
	 * @return new series ID
	 */
	public int registerSeries() {
		lock();
		try {
			return ++lastSeriesID;
		} finally {
			unlock();
		}
	}
	
	/**
	 * Set value.
	 * <p>
	 * @param time - the time is used to determine interval
	 * @param seriesID - series identifier
	 * @param value - value to set
	 * @return information of an update
	 */
	public TSeriesUpdate set(Instant time, int seriesID, Object value) {
		lock();
		try {
			Interval interval = timeFrame.getInterval(time);
			TSeriesUpdateImpl update = new TSeriesUpdateImpl(interval);
			Instant intervalStart = interval.getStart();
			TSeriesNode node = nodeMap.get(intervalStart);
			if ( node == null ) {
				node = createNode(interval);
				update.setNewNode(true);
			}
			update.setNodeIndex(node.getNodeIndex())
				.setNewValue(value)
				.setOldValue(node.set(seriesID, value));
			return update;
		} finally {
			unlock();
		}
	}
	
	/**
	 * Get value by timestamp.
	 * <p>
	 * @param time - timestamp
	 * @param seriesID - series identifier
	 * @return value or null if has no value associated with the time
	 */
	public Object get(Instant time, int seriesID) {
		lock();
		try {
			TSeriesNode node = nodeMap.get(timeFrame.getInterval(time).getStart());
			return node == null ? null : node.get(seriesID);	
		} finally {
			unlock();
		}
	}
	
	/**
	 * Get value by index.
	 * <p>
	 * @param index - index in series
	 * @param seriesID - series identifier
	 * @return value
	 * @throws ValueOutOfRangeException - index is out of range
	 * @throws ValueException - an error occurred
	 */
	public Object get(int index, int seriesID) throws ValueException {
		lock();
		try {
			if ( index < 0 ) {
				index = nodeList.size() - 1 + index;
			}
			TSeriesNode node = nodeList.get(index);
			return node.get(seriesID);
		} catch ( IndexOutOfBoundsException e ) {
			throw new ValueOutOfRangeException(e);
		} finally {
			unlock();
		}
	}
	
	/**
	 * Get last value.
	 * <p>
	 * @param seriesID - series identifier
	 * @return last value or null if no values in series
	 */
	public Object get(int seriesID) {
		lock();
		try {
			return lastNode == null ? null : lastNode.get(seriesID);
		} finally {
			unlock();
		}
	}
	
	/**
	 * Remove all elements.
	 */
	public void clear() {
		lock();
		try {
			nodeList.clear();
			nodeMap.clear();
			lastNode = null;
		} finally {
			unlock();
		}
	}
	
	/**
	 * Get number of elements in series.
	 * <p>
	 * @return number of elements
	 */
	public int getLength() {
		lock.lock();
		try {
			return nodeList.size();
		} finally {
			unlock();
		}
	}
	
	/**
	 * Set last node.
	 * <p>
	 * Note: for testing purposes only.
	 * <p>
	 * @param node - node to set
	 */
	void setLastNode(TSeriesNode node) {
		lock();
		try {
			lastNode = node;
		} finally {
			unlock();
		}
	}
	
	/**
	 * Get last node.
	 * <p>
	 * Note: for testing purposes only.
	 * <p>
	 * @return last node
	 */
	TSeriesNode getLastNode() {
		lock();
		try {
			return lastNode;
		} finally {
			unlock();
		}
	}
	
	private TSeriesNode createNode(Interval interval) {
		Instant intervalStart = interval.getStart();
		TSeriesNode node = new TSeriesNode(interval);
		nodeMap.put(intervalStart, node);
		if ( lastNode == null ) {
			// case 1
			node.setNodeIndex(0);
			lastNode = node;
			nodeList.add(node);
		} else if ( intervalStart.isAfter(lastNode.getIntervalStart()) ) {
			// case 3
			node.setNodeIndex(lastNode.getNodeIndex() + 1);
			lastNode = node;
			nodeList.add(node);
		} else {
			// case 4
			// Перебирая узлы с конца, найти такой узел, время начала
			// которого меньше времени нового узла.
			for ( int i = nodeList.size() - 1; i >= 0; i -- ) {
				TSeriesNode x = nodeList.get(i);
				if ( intervalStart.isBefore(x.getIntervalStart()) ) {
					// Этот узел позже нового узла -> его индекс
					// увеличится после вставки нового узла
					x.nodeIndexIncrement();
				} else if ( intervalStart.equals(x.getIntervalStart()) ) {
					// case 5
					throw new IllegalStateException();
				} else {
					// Этот узел раньше нового узла -> это место вставки.
					// Индекс нового узла равен индексу текущего + 1.
					node.setNodeIndex(x.getNodeIndex() + 1);
					nodeList.add(node.getNodeIndex(), node);
				}
			}
			// case 6
			if ( node.getNodeIndex() < 0 ) {
				// Это будет первый узел в серии.
				node.setNodeIndex(0);
				nodeList.add(0, node);
			}
		}
		return node;
	}

}
