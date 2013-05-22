package ru.prolib.aquila.ib.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvWriter;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventType;

/**
 * $Id$
 */
public class RespHandler implements EventListener {

	private static final Logger logger;	
	private Vector<IBHistoricalRow> rows = new Vector<IBHistoricalRow>();
	
	private String destFile;
	private EventDispatcher dispatcher;
	private EventType onHandled;
	
	static {
		logger = LoggerFactory.getLogger(RespHandler.class);
	}
	
	public RespHandler(String destFile,EventDispatcher dispatcher, 
			EventType onHandled) {
		this.destFile = destFile;
		this.dispatcher = dispatcher;
		this.onHandled = onHandled;
	}
	
	public EventType OnHandled() {
		return onHandled;
	}
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.EventListener#onEvent(ru.prolib.aquila.core.Event)
	 */
	@Override
	public void onEvent(Event evt) {
		if(evt instanceof IBEventHistoricalData) {
			IBEventHistoricalData e = (IBEventHistoricalData) evt;
			IBHistoricalRow row = e.getRow();
			if(row.isLast()) {
				try {
					writeToFile();
				} catch (IOException e1) {					
					logger.error(e1.getMessage());
				}
			}else {
				rows.add(row);
			}
		}		
	}
	
	private void writeToFile() throws IOException {
		logger.debug("Records recieved: {}", rows.size());
		CsvWriter writer = new CsvWriter(new FileWriter(destFile, true), ',');
		String lastDate = rows.get(rows.size()-1).getDate();
		for(int i = 0; i < rows.size(); i++) {
			writer.writeRecord(rows.get(i).toValuesArray());
		}
		writer.close();
		rows = new Vector<IBHistoricalRow>();
		fireOnHandled(lastDate);
	}
	
	private void fireOnHandled(String lastDate) {
		dispatcher.dispatch(new IBRespHandledEvent(onHandled, lastDate));
	}

	
}
