package ru.prolib.aquila.core.BusinessEntities;

import org.joda.time.DateTime;

/**
 * Вспомогательные классы для тестирования элементов планировщика задач.
 */
public class SchedulerTestComponents {

	/**
	 * Для тестирования процедур сравнения.
	 */
	static class MyRunnable implements Runnable {
		@Override public void run() { }
		@Override public boolean equals(Object other) { return true; }
	}
	
	/**
	 * Для тестирования процедур сравнения.
	 */
	static class MyScheduler implements Scheduler {
		@Override public DateTime getCurrentTime() { return null; }
		@Override public TaskHandler schedule(Runnable task, DateTime time)
			{ return null; }
		@Override public TaskHandler
			schedule(Runnable task, DateTime firstTime, long period)
			{ return null; }
		@Override public TaskHandler
			schedule(Runnable task, long delay) { return null; }
		@Override public TaskHandler
			schedule(Runnable task, long delay, long period) { return null; }
		@Override public TaskHandler
			scheduleAtFixedRate(Runnable task, DateTime firstTime, long period)
			{ return null; }
		@Override public TaskHandler
			scheduleAtFixedRate(Runnable task, long delay, long period)
			{ return null; }
		@Override public void cancel(Runnable task) { }
		@Override public boolean scheduled(Runnable task) {	return false; }
		@Override public TaskHandler
			getTaskHandler(Runnable task) { return null; }
		@Override public boolean equals(Object other) { return true; }
	}
	

}
