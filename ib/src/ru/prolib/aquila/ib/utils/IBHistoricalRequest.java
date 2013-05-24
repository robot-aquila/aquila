package ru.prolib.aquila.ib.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;

/**
 * $Id$
 */
public class IBHistoricalRequest implements EventListener {

	private static final Logger logger = LoggerFactory.getLogger(IBHistoricalRequest.class);
	public static int rqId = 222;
	
	private IBClientHistoricalRq client;
	private IBHistoricalRequestParams params;
	
	private long endDate;
	private long currDate;
	private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	private int step = 10*24*60*60;	
	
	private boolean finished = false;
	
	public IBHistoricalRequest(IBClientHistoricalRq client, 
			IBHistoricalRequestParams params) throws ParseException 
	{
		this.client = client;
		this.params = params;
		init();
	}
	
	public void execute() {
		doRequest();
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.EventListener#onEvent(ru.prolib.aquila.core.Event)
	 */
	@Override
	public void onEvent(Event event) {
		if(event instanceof IBRespHandledEvent) {
			try {				
				if(finished) {
					logger.info("Request completed");
					finished = false;
				}else {
					Thread.sleep(10000);
					calculateRequest(((IBRespHandledEvent) event).getLastDate());
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
	}
	
	private void calculateRequest(String dateTime) throws ParseException {		
		long time = (long) (format.parse(dateTime).getTime()/1000);
		currDate = time + step;
		if(endDate < currDate) {
			currDate = endDate;
			finished = true;
		}
		doRequest();
	}
	
	private void doRequest() {
		Date date = new Date((long) currDate *1000);
		logger.debug("Request data from: {}", date);
		client.reqHistoricalData(rqId, params.getContract(), format.format(date), 
				"10 D", params.getBarSize(), params.getWhatToShow(), 
				params.getUseRTH(), 1);
		rqId++;
	}
	
	private void init() throws ParseException {
		endDate = (long) (format.parse(params.getEndDateTime()).getTime()/1000);
		long duration = params.getDuration()*24*60*60;
		currDate = endDate - duration + step;
	}
}
