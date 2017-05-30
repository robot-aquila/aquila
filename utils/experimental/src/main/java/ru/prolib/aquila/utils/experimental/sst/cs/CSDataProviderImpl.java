package ru.prolib.aquila.utils.experimental.sst.cs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.SecurityEvent;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.CSFiller;
import ru.prolib.aquila.core.data.CSUtils;
import ru.prolib.aquila.core.data.TimeFrame;

public class CSDataProviderImpl implements CSDataProvider, EventListener {
	
	static class Node {
		private final CSDataSliceImpl data;
		private CSFiller filler;
		
		Node(CSDataSliceImpl data) {
			this.data = data;
		}
		
	}

	private final CSUtils csUtils;
	private final Terminal terminal;
	private final Map<Symbol, Map<TimeFrame, Node>> nodes;

	public CSDataProviderImpl(Terminal terminal) {
		this.csUtils = new CSUtils();
		this.terminal = terminal;
		this.nodes = new HashMap<>();
	}

	@Override
	public CSDataSlice getSlice(Symbol symbol, TimeFrame tf) {
		Node node = getNode(symbol, tf);
		synchronized ( node ) {
			if ( node.filler == null ) {
				terminal.lock();
				try {
					if ( terminal.isSecurityExists(symbol) ) {
						startFiller(node);
					}
				} finally {
					terminal.unlock();
				}
			}
		}
		return node.data;
	}

	@Override
	public Collection<CSDataSlice> getSlices(Symbol symbol) {
		List<CSDataSlice> list = new ArrayList<>();
		for ( Node node : getNodes(symbol).values() ) {
			list.add(node.data);
		}
		return list;
	}

	@Override
	public void start() {
		terminal.onSecurityAvailable().addListener(this);
	}

	@Override
	public void stop() {
		terminal.onSecurityAvailable().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		SecurityEvent e = (SecurityEvent) event;
		Symbol symbol = e.getSecurity().getSymbol();
		for ( Node node : getNodes(symbol).values() ) {
			synchronized ( node ) {
				if ( node.filler == null ) {
					startFiller(node);
				}
			}
		}
	}
	
	private synchronized Map<TimeFrame, Node> getNodes(Symbol symbol) {
		Map<TimeFrame, Node> x = nodes.get(symbol);
		if ( x == null ) {
			x = new HashMap<>();
			nodes.put(symbol, x);
		}
		return x;
	}
	
	private synchronized Node getNode(Symbol symbol, TimeFrame tf) {
		Map<TimeFrame, Node> nodes = getNodes(symbol);
		Node x = nodes.get(tf);
		if ( x == null ) {
			x = new Node(new CSDataSliceImpl(symbol, tf, terminal));
			nodes.put(tf, x);
		}
		return x;
	}
	
	private void startFiller(Node node) {
		CSDataSliceImpl slice = node.data;
		node.filler = csUtils.createFiller(terminal, slice.getSymbol(),
				slice.getTF(), slice.getCandleSeries_());
		node.filler.start();
	}

	@Override
	public synchronized Collection<Symbol> getSymbols() {
		return new HashSet<>(nodes.keySet());
	}

	@Override
	public Collection<TimeFrame> getTimeFrames(Symbol symbol) {
		return getNodes(symbol).keySet();
	}

}
