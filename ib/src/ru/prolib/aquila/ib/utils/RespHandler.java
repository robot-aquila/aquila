package ru.prolib.aquila.ib.utils;

import java.io.IOException;

import com.csvreader.CsvWriter;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;

/**
 * $Id$
 */
public class RespHandler implements EventListener {

	private CsvWriter writer;
	
	public RespHandler(CsvWriter writer) {
		this.writer = writer;
	}
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.EventListener#onEvent(ru.prolib.aquila.core.Event)
	 */
	@Override
	public void onEvent(Event evt) {
		if(evt instanceof IBEventHistoricalData) {
			IBEventHistoricalData e = (IBEventHistoricalData) evt;
			try {
				writer.writeRecord(e.getRow().toValuesArray());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}		
	}

	
}
