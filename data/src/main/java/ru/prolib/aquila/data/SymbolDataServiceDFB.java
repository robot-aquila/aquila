package ru.prolib.aquila.data;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounter;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounter.Field;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrRepository;

/**
 * Symbol data service based on data source and data-feed management.
 */
public class SymbolDataServiceDFB implements SymbolDataService, SymbolSubscrHandler.Owner {
	private static final Map<Integer, MDLevel> TOKEN_TO_FEED;
	private static final Map<MDLevel, DFSubscrState> GROUP_TEMPLATE;
	
	static {
		TOKEN_TO_FEED = new LinkedHashMap<>();
		TOKEN_TO_FEED.put(Field.NUM_L0,		MDLevel.L0);
		TOKEN_TO_FEED.put(Field.NUM_L1_BBO,	MDLevel.L1);
		TOKEN_TO_FEED.put(Field.NUM_L2,		MDLevel.L2);
		GROUP_TEMPLATE = new LinkedHashMap<>();
		GROUP_TEMPLATE.put(MDLevel.L0, new DFSubscrState());
		GROUP_TEMPLATE.put(MDLevel.L1, new DFSubscrState());
		GROUP_TEMPLATE.put(MDLevel.L2, new DFSubscrState());
	}
	
	static class Insider {
		
		/**
		 * Determine and mark required data feeds as need to subscribe or unsubscribe.
		 * <p> 
		 * @param symbol - symbol
		 * @param counters - consumer subscription counters
		 * @return true if at least one feed has pending subscription or pending
		 * unsubscription state.
		 */
		public boolean syncSubscrState(Symbol symbol,
				SymbolSubscrCounter counter,
				DFGroupRepo<Symbol, MDLevel> data_feed_subscr_states)
		{
			int x = 0;
			Iterator<Map.Entry<Integer, MDLevel>> it = TOKEN_TO_FEED.entrySet().iterator();
			while ( it.hasNext() ) {
				Map.Entry<Integer, MDLevel> entry = it.next();
				if ( counter.getInteger(entry.getKey()) > 0 ) {
					if ( data_feed_subscr_states.haveToSubscribe(symbol, entry.getValue()) ) {
						x ++;
					}
				} else {
					if ( data_feed_subscr_states.haveToUnsubscribe(symbol, entry.getValue()) ) {
						x ++;
					}
				}
			}
			return x > 0;
		}
		
		public void applyPendingChanges(DataSource data_source,
				EditableTerminal terminal,
				DFGroupRepo<Symbol, MDLevel> data_feed_subscr_states)
		{
			for ( Symbol symbol : data_feed_subscr_states.getPendingSubscr(MDLevel.L0) ) {
				data_source.subscribeSymbol(symbol, terminal.getEditableSecurity(symbol));
				data_feed_subscr_states.subscribed(symbol, MDLevel.L0);
			}
			for ( Symbol symbol : data_feed_subscr_states.getPendingSubscr(MDLevel.L1) ) {
				data_source.subscribeL1(symbol, terminal.getEditableSecurity(symbol));
				data_feed_subscr_states.subscribed(symbol, MDLevel.L1);
			}
			for ( Symbol symbol : data_feed_subscr_states.getPendingSubscr(MDLevel.L2) ) {
				data_source.subscribeMD(symbol, terminal.getEditableSecurity(symbol));
				data_feed_subscr_states.subscribed(symbol, MDLevel.L2);
			}

			for ( Symbol symbol : data_feed_subscr_states.getPendingUnsubscr(MDLevel.L0) ) {
				data_source.unsubscribeSymbol(symbol, terminal.getEditableSecurity(symbol));
				data_feed_subscr_states.unsubscribed(symbol, MDLevel.L0);
			}
			for ( Symbol symbol : data_feed_subscr_states.getPendingUnsubscr(MDLevel.L1) ) {
				data_source.unsubscribeL1(symbol, terminal.getEditableSecurity(symbol));
				data_feed_subscr_states.unsubscribed(symbol, MDLevel.L1);
			}
			for ( Symbol symbol : data_feed_subscr_states.getPendingUnsubscr(MDLevel.L2) ) {
				data_source.unsubscribeMD(symbol, terminal.getEditableSecurity(symbol));
				data_feed_subscr_states.unsubscribed(symbol, MDLevel.L2);
			}
		}
		
	}
	
	protected final Insider insider;
	protected final SymbolSubscrRepository consumerSubscriptionCounters;
	protected final DFGroupRepo<Symbol, MDLevel> dataFeedSubscriptionStates;
	protected DataSource dataSource;
	protected EditableTerminal terminal;
	protected boolean connected = false;
	
	public SymbolDataServiceDFB(Insider insider,
			SymbolSubscrRepository consumer_subscription_counters,
			DFGroupRepo<Symbol, MDLevel> data_feed_subscription_states)
	{
		this.insider = insider;
		this.consumerSubscriptionCounters = consumer_subscription_counters;
		this.dataFeedSubscriptionStates = data_feed_subscription_states; 
	}
	
	public SymbolDataServiceDFB(EventQueue queue) {
		this(new Insider(),
			 new SymbolSubscrRepository(queue, "SDS-DFB"),
			 new DFGroupRepo<>(new DFGroupFactoryTmpl<>(GROUP_TEMPLATE)));
	}
	
	public synchronized void setDataSource(DataSource data_source) {
		this.dataSource = data_source;
	}
	
	public synchronized DataSource getDataSource() {
		if ( dataSource == null ) {
			throw new NullPointerException("Data source not defined");
		}
		return dataSource;
	}
	
	public synchronized void setTerminal(EditableTerminal terminal) {
		this.terminal = terminal;
	}

	public synchronized EditableTerminal getTerminal() {
		if ( terminal == null ) {
			throw new NullPointerException("Terminal not defined");
		}
		return terminal;
	}
	
	public synchronized boolean connected() {
		return connected;
	}
	
	@Override
	public synchronized SubscrHandler onSubscribe(Symbol symbol, MDLevel level) {
		SymbolSubscrHandler handler = new SymbolSubscrHandler(this, symbol, level, true);
		SymbolSubscrCounter counter = consumerSubscriptionCounters.subscribe(symbol, level);
		if ( connected
		  && dataFeedSubscriptionStates.isNotAvailable(symbol) == false
		  && insider.syncSubscrState(symbol, counter, dataFeedSubscriptionStates) )
		{
			insider.applyPendingChanges(getDataSource(), getTerminal(), dataFeedSubscriptionStates);
		}
		return handler;
	}

	@Override
	public synchronized void onUnsubscribe(Symbol symbol, MDLevel level) {
		SymbolSubscrCounter counter = consumerSubscriptionCounters.unsubscribe(symbol, level);
		if ( connected
		  && dataFeedSubscriptionStates.isNotAvailable(symbol) == false
		  && insider.syncSubscrState(symbol, counter, dataFeedSubscriptionStates) )
		{
			insider.applyPendingChanges(getDataSource(), getTerminal(), dataFeedSubscriptionStates);
		}
	}

	@Override
	public synchronized void onConnectionStatusChange(boolean connected) {
		int x = 0;
		if ( this.connected = connected ) {
			for ( SymbolSubscrCounter counter : consumerSubscriptionCounters.getEntities() ) {
				if ( insider.syncSubscrState(counter.getSymbol(), counter, dataFeedSubscriptionStates) ) {
					x ++;
				}
			}
		} else {
			x += dataFeedSubscriptionStates.haveToUnsubscribeAll(MDLevel.L0).size()
			   + dataFeedSubscriptionStates.haveToUnsubscribeAll(MDLevel.L1).size()
			   + dataFeedSubscriptionStates.haveToUnsubscribeAll(MDLevel.L2).size();
		}
		if ( x > 0 ) {
			insider.applyPendingChanges(getDataSource(), getTerminal(), dataFeedSubscriptionStates);
		}
	}

}
