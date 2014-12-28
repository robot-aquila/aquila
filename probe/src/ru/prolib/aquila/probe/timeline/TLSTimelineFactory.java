package ru.prolib.aquila.probe.timeline;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import org.joda.time.Interval;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.sm.*;
import ru.prolib.aquila.core.utils.KW;

/**
 * Фабрика хронологий.
 */
public class TLSTimelineFactory {
	private final EventSystem es;
	
	public TLSTimelineFactory(EventSystem es) {
		super();
		this.es = es;
	}
	
	public TLSTimeline produce(Interval interval) {
		CountDownLatch started = new CountDownLatch(1);
		TLEventQueue evtQue = new TLEventQueue(interval);
		TLEventSources evtSrc = new TLEventSources();
		
		
		TLSTimeline timeline = new TLSTimeline(new TLCmdQueue(), evtQue,
			new TLSStrategy(evtSrc, evtQue),
			new TLSEventDispatcher(es), evtSrc);
		
		SMState pause	= new TLASPause(timeline),
				run		= new TLASRun(timeline),
				finish	= new TLASFinish(timeline);
		Map<KW<SMExit>, SMState> tr = new HashMap<KW<SMExit>, SMState>();
		tr.put(new KW<SMExit>(pause.getExit(TLASPause.EEND)), finish);
		tr.put(new KW<SMExit>(pause.getExit(TLASPause.ERUN)), run);
		tr.put(new KW<SMExit>(run.getExit(TLASRun.EEND)), finish);
		tr.put(new KW<SMExit>(run.getExit(TLASRun.EPAUSE)), pause);
		tr.put(new KW<SMExit>(finish.getExit(TLASFinish.EOK)), SMState.FINAL);
	
		SMStateMachine sm = new SMStateMachine(pause, tr);
		sm.setDebug(true);
		Thread thread = new Thread(new TLSThreadWorker(started, timeline, sm));
		thread.setDaemon(true);
		thread.start();
		try {
			started.await();
		} catch ( InterruptedException e ) {
			throw new TLInterruptionsNotAllowedException(e);
		}
		return timeline;
	}

}
