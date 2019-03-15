package ru.prolib.aquila.core.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FlushControl {
	private final Lock lock;
	private final List<FlushController> controllers;
	
	FlushControl(List<FlushController> controllers) {
		this.lock = new ReentrantLock();
		this.controllers = controllers;
	}
	
	public FlushControl() {
		this(new ArrayList<>());
	}
	
	public void countUp() {
		lock.lock();
		try {
			for ( FlushController ctrl : controllers ) {
				ctrl.countUp();
			}
		} finally {
			lock.unlock();
		}
	}
	
	public void countDown() {
		lock.lock();
		try {
			Iterator<FlushController> it = controllers.iterator();
			while ( it.hasNext() ) {
				FlushController ctrl = it.next();
				if ( ctrl.countDown() ) {
					it.remove();
				}
			}
		} finally {
			lock.unlock();
		}
	}
	
	public FlushIndicator createIndicator() {
		FlushController ctrl = new FlushController();
		lock.lock();
		try {
			controllers.add(ctrl);
		} finally {
			lock.unlock();
		}
		return ctrl;
	}

}
