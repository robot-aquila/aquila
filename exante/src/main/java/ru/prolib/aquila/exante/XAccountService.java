package ru.prolib.aquila.exante;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.exante.rh.AccountSummaryHandler;

public class XAccountService {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(XAccountService.class);
	}

	/**
	 * Position refresh interval in milliseconds.
	 */
	public static final long DEFAULT_REFRESH_INTERVAL = 3000L;
	
	public static class TimerTask implements Runnable {
		private final XAccountService service;
		
		public TimerTask(XAccountService service) {
			this.service = service;
		}

		@Override
		public void run() {
			service.queryIfAllowed();
		}
		
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != TimerTask.class ) {
				return false;
			}
			TimerTask o = (TimerTask) other;
			return new EqualsBuilder()
					.append(o.service, service)
					.build();
		}
		
	}
	
	private final Lock lock = new ReentrantLock();
	private final XAccountSummaryMessages accountSummaryMessages;
	private final XSymbolRepository symbols;
	private final long refreshInterval;
	private EditableTerminal terminal;
	private boolean requestInProgress;
	private TaskHandler timerTask;
	
	public XAccountService(
			XAccountSummaryMessages account_summary_messages,
			XSymbolRepository symbols,
			long refresh_interval
		)
	{
		this.accountSummaryMessages = account_summary_messages;
		this.symbols = symbols;
		this.refreshInterval = refresh_interval;
	}
	
	public XAccountService(
			XAccountSummaryMessages account_summary_messages,
			XSymbolRepository symbols
		)
	{
		this(account_summary_messages, symbols, DEFAULT_REFRESH_INTERVAL);
	}
	
	public void start(EditableTerminal term) {
		lock.lock();
		try {
			if ( terminal != null ) {
				logger.warn("Service already started");
				throw new IllegalStateException("Service already started");
			}
			terminal = term;
			requestInProgress = false;
			timerTask = null;
			query();
		} finally {
			lock.unlock();
		}
	}
	
	public void stop() {
		lock.lock();
		try {
			terminal = null;
			requestInProgress = false;
			if ( timerTask != null ) {
				timerTask.cancel();
				timerTask = null;
			}
		} finally {
			lock.unlock();
		}
	}
	
	public void rescheduleIfAllowed(long delay) {
		lock.lock();
		try {
			if ( requestInProgress == false && terminal != null ) {
				if ( timerTask != null ) {
					timerTask.cancel();
				}
				timerTask = terminal.schedule(new TimerTask(this), delay);
				//logger.debug("account query scheduled: {} ms.", delay);
			}
		} finally {
			lock.unlock();
		}
		
	}
	
	private void rescheduleIfAllowed() {
		rescheduleIfAllowed(refreshInterval);
	}
	
	public void queryIfAllowed() {
		lock.lock();
		try {
			if ( requestInProgress == false && terminal != null ) {
				query();
			}
		} finally {
			lock.unlock();
		}
	}
	
	private void query() {
		lock.lock();
		try {
			accountSummaryMessages.query(new AccountSummaryHandler(terminal, symbols) {
				@Override
				public void close() {
					super.close();
					//logger.debug("account query done");
					lock.lock();
					try {
						requestInProgress = false;
						rescheduleIfAllowed();
					} finally {
						lock.unlock();
					}
				}
			});
			requestInProgress = true;
		} finally {
			lock.unlock();
		}
	}
	
}
