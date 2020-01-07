package ru.prolib.aquila.core.data;

import java.io.IOException;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.concurrency.Multilock;

public class SeriesUtils {
	
	public void copy(CloseableIterator<Candle> it, EditableTSeries<Candle> target) throws IOException {
		while ( it.next() ) {
			Candle candle = it.item();
			target.set(candle.getStartTime(), candle);
		}
	}
	
	public void copy(TSeries<Candle> source, EditableTSeries<Candle> target) {
		Multilock lock = new Multilock(source, target);
		lock.lock();
		try {
			int length = source.getLength();
			for ( int i = 0; i < length; i ++ ) {
				Candle candle = source.get(i);
				if ( candle != null ) {
					target.set(candle.getStartTime(), candle);
				}
			}
		} catch ( ValueException e ) {
			throw new IllegalStateException("Unexpected exception: ", e);
		} finally {
			lock.unlock();
		}
	}

}
