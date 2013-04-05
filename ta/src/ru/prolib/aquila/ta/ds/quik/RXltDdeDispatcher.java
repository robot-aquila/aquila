package ru.prolib.aquila.ta.ds.quik;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.rxltdde.Dde;
import ru.prolib.aquila.rxltdde.Xlt;

public class RXltDdeDispatcher implements Dde.IHandler {
	static final Logger logger = LoggerFactory.getLogger(RXltDdeDispatcher.class);
	
	protected final Map<String, LinkedHashSet<RXltDdeTableHandler>> map;
	
	public RXltDdeDispatcher() {
		super();
		map = new Hashtable<String, LinkedHashSet<RXltDdeTableHandler>>();
	}
	
	public synchronized void add(String topic, RXltDdeTableHandler handler) {
		LinkedHashSet<RXltDdeTableHandler> set = map.get(topic);
		if ( set == null ) {
			set = new LinkedHashSet<RXltDdeTableHandler>();
			map.put(topic, set);
		}
		set.add(handler);
		logger.debug("Table handler registered: {}", topic);
	}
	
	public synchronized void remove(String topic, RXltDdeTableHandler handler) {
		LinkedHashSet<RXltDdeTableHandler> set = map.get(topic);
		if ( set != null ) {
			set.remove(handler);
			logger.debug("Table handler removed: {}", topic);
			if ( set.size() == 0 ) {
				map.remove(topic);
			}
		}
	}
	
	public synchronized void clear() {
		map.clear();
	}

	@Override
	public synchronized
		void onRawData(String topic, String item, byte[] data)
	{
		LinkedHashSet<RXltDdeTableHandler> set = map.get(topic);
		if ( set != null ) {
			Xlt.ITable table = readTable(topic, item, data);
			Iterator<RXltDdeTableHandler> i = set.iterator();
			while ( i.hasNext() ) {
				i.next().onTable(table);
			}
		} else {
			logger.warn("Handler for table {} not found", topic);
		}
	}
	
	protected Xlt.ITable readTable(String topic, String item, byte[] data) {
		return Xlt.readTable(topic, item, data);
	}
	
}