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
			new TLSStrategy(new TLSIntrgStrategy(evtSrc, evtQue), evtQue),
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
	
		TLSThread thread = new TLSThread(new TLSThreadWorker(started, timeline,
				new SMStateMachine(pause, tr)));
		timeline.setStarter(new TLSThreadStarter(started, thread));
		
		return timeline;
	}

}