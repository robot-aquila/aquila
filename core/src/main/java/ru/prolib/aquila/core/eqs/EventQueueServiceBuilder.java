package ru.prolib.aquila.core.eqs;

import java.util.concurrent.LinkedBlockingQueue;

import ru.prolib.aquila.core.eqs.legacy.EventQueueServiceLegacy;
import ru.prolib.aquila.core.eque.EventQueueService;

public class EventQueueServiceBuilder {
	
	public EventQueueService createLegacyService() {
		return new EventQueueServiceLegacy();
	}
	
	public EventQueueService delegateToSeparateThread(EventQueueService service, String thread_name) {
		LinkedBlockingQueue<Cmd> cmd_queue = new LinkedBlockingQueue<>();
		Thread service_thread = new Thread(new CmdProcessorDelegate(cmd_queue, service));
		service_thread.setDaemon(true);
		service_thread.setName(thread_name);
		service_thread.start();
		return new CmdSenderService(cmd_queue);
	}
	
	public EventQueueService delegateToSeparateThread(String thread_name) {
		return delegateToSeparateThread(new EventQueueServiceLegacy(), thread_name);
	}

}
