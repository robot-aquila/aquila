package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Market depth builder.
 * <p>
 * This class is not thread-safe.
 */
public class MDBuilder implements MDUpdateConsumer {
	private final DoubleUtils doubleUtils;
	private final Vector<Tick> askQuotes, bidQuotes; // do not switch to other type
	private final Symbol symbol;
	private MarketDepth md;
	private int marketDepthLevels = 10;
	
	public MDBuilder(Symbol symbol) {
		doubleUtils = new DoubleUtils();
		askQuotes = new Vector<>();
		bidQuotes = new Vector<>();
		this.symbol = symbol;
		md = new MarketDepth(symbol, askQuotes, bidQuotes, Instant.EPOCH, 0);
	}
	
	public void setPriceScale(int scale) {
		doubleUtils.setScale(scale);
	}
	
	public MarketDepth getMarketDepth() {
		return md;
	}

	@Override
	public void consume(MDUpdate update) {
		boolean hasAskUpdate = false, hasBidUpdate = false;
		if ( ! symbol.equals(update.getSymbol()) ) {
			throw new IllegalArgumentException("Unexpected symbol: " + update.getSymbol());
		}
		MDUpdateType updateType = update.getType();
		if ( updateType == MDUpdateType.REFRESH || updateType == MDUpdateType.REFRESH_ASK ) {
			askQuotes.clear();
			hasAskUpdate = true;
		}
		if ( updateType == MDUpdateType.REFRESH || updateType == MDUpdateType.REFRESH_BID ) {
			bidQuotes.clear();
			hasBidUpdate = true;
		}
		
		for ( MDUpdateRecord record : update.getRecords() ) {
			Tick tick = record.getTick();
			switch ( tick.getType() ) {
			case ASK:
				hasAskUpdate = true;
				break;
			case BID:
				hasBidUpdate = true;
				break;
			default:
				throw new IllegalArgumentException("Invalid tick type: " + tick.getType());
			}
			switch ( record.getTransactionType() ) {
			case DELETE:
				removeQuote(tick);
				break;
			default:
				replaceQuote(tick);
			}
		}

		if ( hasAskUpdate || hasBidUpdate ) {
			if ( hasAskUpdate ) {
				Collections.sort(askQuotes, TickPriceComparator.ASK);
				askQuotes.setSize(Math.min(marketDepthLevels, askQuotes.size()));
			}
			if ( hasBidUpdate ) {
				Collections.sort(bidQuotes, TickPriceComparator.BID);
				bidQuotes.setSize(Math.min(marketDepthLevels, bidQuotes.size()));
			}
			md = new MarketDepth(update.getSymbol(), askQuotes, bidQuotes,
					update.getTime(), doubleUtils.getScale());
		}
	}
	
	/**
	 * Remove tick by price.
	 * <p>
	 * @param tick - the tick to remove
	 * @return true if removed, otherwise false
	 */
	private boolean removeQuote(Tick tick) {
		List<Tick> target = tick.getType() == TickType.ASK ? askQuotes : bidQuotes;
		List<Integer> to_remove = findQuoteWithSamePrice(target, tick);
		for ( int i : to_remove ) {
			target.remove(i);
		}
		return to_remove.size() > 0;
	}
	
	/**
	 * Replace tick by price or add if not exists.
	 * <p>
	 * @param tick - the tick to replace
	 */
	private void replaceQuote(Tick tick) {
		removeQuote(tick);
		List<Tick> target = tick.getType() == TickType.ASK ? askQuotes : bidQuotes;
		target.add(tick.withPrice(doubleUtils.round(tick.getPrice())));
	}

	private List<Integer> findQuoteWithSamePrice(List<Tick> target, Tick expected) {
		List<Integer> to_remove = new ArrayList<Integer>();
		double expectedPrice = expected.getPrice();
		for ( int i = 0; i < target.size(); i ++ ) {
			if ( doubleUtils.isEquals(target.get(i).getPrice(), expectedPrice) ) {
				to_remove.add(i);
			}
		}
		return to_remove;
	}

}
