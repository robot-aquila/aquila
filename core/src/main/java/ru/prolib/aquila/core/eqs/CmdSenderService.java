package ru.prolib.aquila.core.eqs;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ru.prolib.aquila.core.EventQueueStats;
import ru.prolib.aquila.core.FlushIndicator;
import ru.prolib.aquila.core.eque.EventQueueService;

public class CmdSenderService implements EventQueueService {
	private final LinkedBlockingQueue<Cmd> cmdQueue;
	private final long timeout;
	
	public CmdSenderService(LinkedBlockingQueue<Cmd> cmd_queue, long timeout_millis) {
		this.cmdQueue = cmd_queue;
		this.timeout = timeout_millis;
	}
	
	public CmdSenderService(LinkedBlockingQueue<Cmd> cmd_queue) {
		this(cmd_queue, 5000L);
	}
	
	public long getTimeout() {
		return timeout;
	}
	
	private <T extends Cmd> T send(T cmd) {
		try {
			cmdQueue.put(cmd);
		} catch ( Exception e ) {
			throw new CompletionException("Unexpected exception", e);
		}
		return cmd;
	}

	@Override
	public FlushIndicator createIndicator() {
		try {
			return send(new CmdRequestIndicator()).getResult().get(timeout, TimeUnit.MILLISECONDS);
		} catch ( InterruptedException|ExecutionException|TimeoutException e ) {
			throw new CompletionException("Unexpected exception", e);
		}
	}
	
	@Override
	public EventQueueStats getStats() {
		try {
			return send(new CmdRequestStats()).getResult().get(timeout, TimeUnit.MILLISECONDS);
		} catch ( InterruptedException|ExecutionException|TimeoutException e ) {
			throw new CompletionException("Unexpected exception", e);
		}
	}

	@Override
	public void eventEnqueued() {
		send(new CmdAddCount(1L, null, null));
	}

	@Override
	public void eventSent() {
		send(new CmdAddCount(null, 1L, null));
	}

	@Override
	public void eventDispatched() {
		send(new CmdAddCount(null, null, 1L));
	}

	@Override
	public void addPreparingTime(long nanos) {
		send(new CmdAddTime(nanos, null, null));
	}

	@Override
	public void addDispatchingTime(long nanos) {
		send(new CmdAddTime(null, nanos, null));
	}

	@Override
	public void addDeliveryTime(long nanos) {
		send(new CmdAddTime(null, null, nanos));
	}

	@Override
	public void eventDispatched(long preparing_time, long dispatching_time) {
		send(new CmdAddCount(null, null, 1L));
		send(new CmdAddTime(preparing_time, dispatching_time, null));
	}

	@Override
	public void shutdown() {
		send(new CmdShutdown());
	}

}
