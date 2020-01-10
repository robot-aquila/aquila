package ru.prolib.aquila.core.eqs;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.eque.EventQueueService;

/**
 * Just delegate commands to specified service.
 */
public class CmdProcessorDelegate implements Runnable {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(CmdProcessorDelegate.class);
	}
	
	private final LinkedBlockingQueue<Cmd> cmdQueue;
	private final EventQueueService service;
	
	public CmdProcessorDelegate(LinkedBlockingQueue<Cmd> cmd_queue, EventQueueService service) {
		this.cmdQueue = cmd_queue;
		this.service = service;
	}
	
	private boolean runSafe(Cmd cmd) throws Exception {
		switch ( cmd.getType() ) {
		case ADD_COUNT:
			{
				CmdAddCount c = (CmdAddCount) cmd;
				if ( c.enqueued != null ) {
					service.eventEnqueued();
				}
				if ( c.sent != null ) {
					service.eventSent();
				}
				if ( c.dispatched != null ) {
					service.eventDispatched();
				}
			}
			break;
		case ADD_TIME:
			{
				CmdAddTime c = (CmdAddTime) cmd;
				if ( c.preparing != null ) {
					service.addPreparingTime(c.preparing);
				}
				if ( c.dispatching != null ) {
					service.addDispatchingTime(c.dispatching);
				}
				if ( c.delivery != null ) {
					service.addDeliveryTime(c.delivery);
				}
			}
			break;
		case CREATE_INDICATOR:
			{
				CmdRequestIndicator c = (CmdRequestIndicator) cmd;
				c.result.complete(service.createIndicator());
			}
			break;
		case GET_STATS:
			{
				CmdRequestStats c = (CmdRequestStats) cmd;
				c.result.complete(service.getStats());
			}
			break;
		case SHUTDOWN:
			service.shutdown();
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		Cmd cmd;
		try {
			while ( (cmd = cmdQueue.take()) != null ) {
				try {
					if ( runSafe(cmd) ) {
						break;
					}
				} catch ( Exception e ) {
					logger.error("Error processing command: ", e);
				}
			}
		} catch ( InterruptedException e ) {
			logger.error("Interrupted: ", e);
			Thread.currentThread().interrupt();
		}
	}

}
