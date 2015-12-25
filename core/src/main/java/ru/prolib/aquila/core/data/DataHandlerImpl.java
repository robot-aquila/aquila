package ru.prolib.aquila.core.data;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DataHandlerImpl extends Observable implements DataHandler {
	protected final Lock lock;
	private final String descriptor;
	private DataHandlerState state;
	
	public DataHandlerImpl(String descriptor, DataHandlerState initialState, Lock lock) {
		super();
		this.descriptor = descriptor;
		this.state = initialState;
		this.lock = lock;
	}
	
	public DataHandlerImpl(String descriptor, DataHandlerState initialState) {
		this(descriptor, initialState, new ReentrantLock());
	}
	
	public DataHandlerImpl(String descriptor) {
		this(descriptor, DataHandlerState.PENDING);
	}
	
	@Override
	public DataHandlerState getState() {
		lock.lock();
		try {
			return state;
		} finally {
			lock.unlock();
		}
	}
	
	public void setState(DataHandlerState newState) {
		lock.lock();
		try {
			if ( newState != state ) {
				setChanged();
				state = newState;
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public String getDescriptor() {
		return descriptor;
	}

	@Override
	public void close() {
		
	}

	@Override
	public void lock() {
		lock.lock();
	}

	@Override
	public void unlock() {
		lock.unlock();
	}
	
	@Override
	public void addObserver(Observer o) {
		lock.lock();
		try {
			super.addObserver(o);
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void deleteObserver(Observer o) {
		lock.lock();
		try {
			super.deleteObserver(o);
		} finally {
			lock.unlock();
		}
	}

}
