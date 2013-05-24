package ru.prolib.aquila.ib.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvWriter;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventDispatcher;
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
	private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	private long lastRecordDate = 0;
	
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
			try {
				IBEventHistoricalData e = (IBEventHistoricalData) evt;
				IBHistoricalRow row = e.getRow();
				if(row.isLast()) {					
						writeToFile();
					
				}else {
					if(lastRecordDate == 0 || 
							lastRecordDate <= (format.parse(row.getDate()).getTime()/1000)) 
					{
						rows.add(row);
					}
				}
			} catch (IOException e1) {					
				logger.error(e1.getMessage());
			} catch (ParseException e1) {
				logger.error(e1.getMessage());
			}
		}		
	}
	
	private void writeToFile() throws IOException, ParseException {
		logger.debug("Records recieved: {}", rows.size());
		CsvWriter writer = new CsvWriter(new FileWriter(destFile, true), ',');
		String lastDate = rows.get(rows.size()-1).getDate();
		for(int i = 0; i < rows.size(); i++) {
			writer.writeRecord(rows.get(i).toValuesArray());
		}
		writer.close();
		rows = new Vector<IBHistoricalRow>();
		lastRecordDate = (long) format.parse(lastDate).getTime()/1000;
		fireOnHandled(lastDate);
	}
	
	private void fireOnHandled(String lastDate) {
		dispatcher.dispatch(new IBRespHandledEvent(onHandled, lastDate));
	}

	
}
