package ru.prolib.aquila.ta.ds.quik;

import java.util.Date;

import ru.prolib.aquila.ipc.IEvent;
import ru.prolib.aquila.ta.Deal;
import ru.prolib.aquila.ta.ds.DealWriter;
import ru.prolib.aquila.ta.ds.DealWriterException;

/**
 * Декоратор-уведомитель.
 * Сигнализирует о наличии новых данных посредством объекта IEvent.
 * Сигнал подается только в том случае, если время добавления бара больше
 * времени начала периода отслеживания. Это позволяет избегать ненужных
 * сигналов в процессе обработки устаревших данных. Однако при вызове
 * flush сигнал не подается никогда.
 */
public class ExportQuikNotifier implements DealWriter {
	private final IEvent event;
	private final DealWriter writer;
	private final Date startSignalAfter;
	
	public ExportQuikNotifier(DealWriter writer, IEvent event) {
		this.writer = writer;
		this.event = event;
		this.startSignalAfter = new Date();
	}

	public ExportQuikNotifier(DealWriter writer, IEvent event,
			Date startSignalAfter)
	{
		this.writer = writer;
		this.event = event;
		this.startSignalAfter = startSignalAfter;
	}

	public IEvent getEvent() {
		return event;
	}
	
	public DealWriter getQuoteWriter() {
		return writer;
	}
	
	public Date getStartSignalAfter() {
		return startSignalAfter;
	}

	@Override
	public boolean addDeal(Deal deal) throws DealWriterException {
		if ( writer.addDeal(deal) ) {
			if ( deal.getTime().after(startSignalAfter) ) {
				notifyReaders();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean flush() throws DealWriterException {
		return writer.flush();
	}
	
	private void notifyReaders() {
		try {
			event.pulse();
		} catch ( Exception e ) {
			e.printStackTrace(System.err);
		}
	}

}