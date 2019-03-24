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
import ru.prolib.aquila.core.data.TSeriesUpdate;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.ValueOutOfRangeException;

public class TSeriesNodeStorageImpl implements TSeriesNodeStorage {
	private final LID lid;
	private final Lock lock;
	private final List<TSeriesNode> nodeList;
	private final Map<Instant, TSeriesNode> nodeMap;
	private final ZTFrame timeFrame;
	private int lastSeriesID;
	private TSeriesNode lastNode;
	
	TSeriesNodeStorageImpl(ZTFrame timeFrame,
						   List<TSeriesNode> nodeList,
						   Map<Instant, TSeriesNode> nodeMap)
	{
		this.lid = LID.createInstance();
		this.lock = new ReentrantLock();
		this.timeFrame = timeFrame;
		this.nodeList = nodeList;
		this.nodeMap = nodeMap;
		this.lastSeriesID = -1;
	}
	
	public TSeriesNodeStorageImpl(ZTFrame timeFrame) {
		this(timeFrame, new ArrayList<>(), new HashMap<>());
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.tseries.TSeriesNodeStorage#getLID()
	 */
	@Override
	public LID getLID() {
		return lid;
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.tseries.TSeriesNodeStorage#lock()
	 */
	@Override
	public void lock() {
		lock.lock();
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.tseries.TSeriesNodeStorage#unlock()
	 */
	@Override
	public void unlock() {
		lock.unlock();
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.tseries.TSeriesNodeStorage#getTimeFrame()
	 */
	@Override
	public ZTFrame getTimeFrame() {
		return timeFrame;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.tseries.TSeriesNodeStorage#registerSeries()
	 */
	@Override
	public int registerSeries() {
		lock();
		try {
			return ++lastSeriesID;
		} finally {
			unlock();
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.tseries.TSeriesNodeStorage#set(java.time.Instant, int, java.lang.Object)
	 */
	@Override
	public TSeriesUpdate setValue(Instant time, int seriesID, Object value) {
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
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.tseries.TSeriesNodeStorage#get(java.time.Instant, int)
	 */
	@Override
	public Object getValue(Instant time, int seriesID) {
		lock();
		try {
			TSeriesNode node = nodeMap.get(timeFrame.getInterval(time).getStart());
			return node == null ? null : node.get(seriesID);	
		} finally {
			unlock();
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.tseries.TSeriesNodeStorage#get(int, int)
	 */
	@Override
	public Object getValue(int index, int seriesID) throws ValueException {
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
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.tseries.TSeriesNodeStorage#get(int)
	 */
	@Override
	public Object getValue(int seriesID) {
		lock();
		try {
			return lastNode == null ? null : lastNode.get(seriesID);
		} finally {
			unlock();
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.tseries.TSeriesNodeStorage#clear()
	 */
	@Override
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
	
	@Override
	public void truncate(int length) {
		if ( length < 0 ) {
			throw new IllegalArgumentException("Expected length >= 0 but " + length);
		}
		lock();
		try {
			int curr_length = nodeList.size();
			for ( TSeriesNode n : nodeList.subList(length, curr_length) ) {
				nodeMap.remove(n.getIntervalStart());
			}
			nodeList.subList(length, curr_length).clear();
			lastNode = nodeList.get(length - 1);
		} finally {
			unlock();
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.tseries.TSeriesNodeStorage#getLength()
	 */
	@Override
	public int getLength() {
		lock.lock();
		try {
			return nodeList.size();
		} finally {
			unlock();
		}
	}
	
	@Override
	public Instant getIntervalStart(int index) throws ValueException {
		lock();
		try {
			if ( index < 0 ) {
				index = nodeList.size() - 1 + index;
			}
			return nodeList.get(index).getIntervalStart();
		} catch ( IndexOutOfBoundsException e ) {
			throw new ValueOutOfRangeException(e);
		} finally {
			unlock();
		}
	}

	@Override
	public int getIntervalIndex(Instant time) {
		lock();
		try {
			TSeriesNode node = nodeMap.get(timeFrame.getInterval(time).getStart());
			return node == null ? -1 : node.getNodeIndex();
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
	
	/**
	 * Get node by index.
	 * <p>
	 * Note: for testing purposes only.
	 * <p>
	 * @param index - node index
	 * @return node
	 */
	TSeriesNode getNode(int index) {
		lock();
		try {
			return nodeList.get(index);
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
					break;
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
