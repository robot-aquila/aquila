package ru.prolib.aquila.core.BusinessEntities;

import java.time.LocalDateTime;

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
		@Override public LocalDateTime getCurrentTime() { return null; }
		@Override public TaskHandler schedule(Runnable task, LocalDateTime time)
			{ return null; }
		@Override public TaskHandler
			schedule(Runnable task, LocalDateTime firstTime, long period)
			{ return null; }
		@Override public TaskHandler
			schedule(Runnable task, long delay) { return null; }
		@Override public TaskHandler
			schedule(Runnable task, long delay, long period) { return null; }
		@Override public TaskHandler
			scheduleAtFixedRate(Runnable task, LocalDateTime firstTime, long period)
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
