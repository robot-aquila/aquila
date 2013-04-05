package ru.prolib.aquila.ta.ds;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.ta.Deal;

public class DealWriterToBarWriter implements DealWriter {
	private final BarWriter writer;
	
	public DealWriterToBarWriter(BarWriter writer) {
		super();
		this.writer = writer;
	}

	@Override
	public boolean addDeal(Deal deal) throws DealWriterException {
		try {
			return writer.addBar(new Candle(deal.getTime(), deal));
		} catch ( BarWriterException e ) {
			throw new DealWriterException(e.getMessage(), e);
		}
	}

	@Override
	public boolean flush() throws DealWriterException {
		try {
			return writer.flush();
		} catch ( BarWriterException e ) {
			throw new DealWriterException(e.getMessage(), e);
		}
	}

	public BarWriter getBarWriter() {
		return writer;
	}

}
