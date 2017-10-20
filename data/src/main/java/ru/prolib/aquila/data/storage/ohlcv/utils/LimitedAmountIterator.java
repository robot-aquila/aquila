package ru.prolib.aquila.data.storage.ohlcv.utils;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.data.Candle;

public class LimitedAmountIterator implements CloseableIterator<Candle> {
	private final CloseableIterator<Candle> underlying;
	private int amount;
	private boolean finished = false, closed = false;
	
	public LimitedAmountIterator(CloseableIterator<Candle> underlying, int amount) {
		this.underlying = underlying;
		this.amount = amount;
	}
	
	public CloseableIterator<Candle> getUnderlyingIterator() {
		return underlying;
	}
	
	public int getCurrentAmount() {
		return amount;
	}

	@Override
	public void close() throws IOException {
		if ( ! closed ) {
			closed = finished = true;
			underlying.close();
		}
	}

	@Override
	public boolean next() throws IOException {
		if  ( closed ) {
			throw new IOException("Iterator closed");
		}
		if ( closed || finished ) {
			return false;
		}
		if ( amount <= 0 ) {
			finished = true;
			return false;
		}
		amount --;
		return underlying.next();
	}

	@Override
	public Candle item() throws IOException, NoSuchElementException {
		if ( closed ) {
			throw new IOException("Iterator closed");
		}
		if ( finished ) {
			throw new NoSuchElementException();
		}
		return underlying.item();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != LimitedAmountIterator.class ) {
			return false;
		}
		LimitedAmountIterator o = (LimitedAmountIterator) other;
		return new EqualsBuilder()
				.append(o.amount, amount)
				.append(o.closed, closed)
				.append(o.finished, finished)
				.append(o.underlying, underlying)
				.isEquals();
	}

}
