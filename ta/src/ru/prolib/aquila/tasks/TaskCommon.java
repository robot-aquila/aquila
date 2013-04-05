package ru.prolib.aquila.tasks;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Шаблон типовой задачи.
 * 
 * 2012-02-13
 * $Id: TaskCommon.java 201 2012-04-03 14:45:43Z whirlwind $
 */
abstract public class TaskCommon extends Observable implements Task {
	private int status = PENDING;
	
	public TaskCommon() {
		super();
	}
	
	@Override
	final public boolean pending() {
		return status == PENDING;
	}
	
	@Override
	final public boolean completed() {
		return status == COMPLETED;
	}
	
	@Override
	final public boolean cancelled() {
		return status == CANCELLED;
	}

	@Override
	final public boolean started() {
		return status == STARTED;
	}
	
	/**
	 * Уведомить наблюдателей и очистить список.
	 */
	final private void notifyObserversAndClearList() {
		setChanged();
		notifyObservers();
		deleteObservers();
	}
	
	/**
	 * Установить результат выполнения задачи как отменено.
	 * Вызов не имеет эффекта, если статус не соответствует
	 * {@link Task#STARTED}.
	 */
	final protected void setCancelled() {
		getClassLogger().debug("Task cancelled: {}", this);
		status = CANCELLED;
		notifyObserversAndClearList();
	}

	/**
	 * Установить результат выполнения задачи как успешно исполнено.
	 * Вызов не имеет эффекта, если статус не соответствует
	 * {@link Task#STARTED}.
	 */
	final protected void setCompleted() {
		getClassLogger().debug("Task completed: {}", this);
		status = COMPLETED;
		notifyObserversAndClearList();
	}
	
	/**
	 * Установить результат выполнения задачи как запущена на исполнение.
	 * Вызов не имеет эффекта, если статус не соответствует
	 * {@link Task#PENDING}.
	 */
	final protected void setStarted() {
		if ( status == PENDING ) {
			getClassLogger().debug("Task started: {}", this);
			status = STARTED;
		}
	}

	@Override
	final public void addObserver(Observer o) {
		super.addObserver(o);
		if ( completed() || cancelled() ) {
			notifyObserversAndClearList();
		}
	}

	@Override
	final public void deleteObserver(Observer o) {
		super.deleteObserver(o);
	}

	@Override
	final public void deleteObservers() {
		super.deleteObservers();
	}

	@Override
	final public void onComplete(Observer observer) {
		addObserver(new CompleteDelegate(observer));
	}

	@Override
	final public void onCancel(Observer observer) {
		addObserver(new CancelDelegate(observer));
	}
	
	@Override
	final public void onCompleteCancel(Task task) {
		onComplete(new CancelOnUpdate(task));
	}
	
	@Override
	final public void onCancelCancel(Task task) {
		onCancel(new CancelOnUpdate(task));
	}
	
	/**
	 * Получить логгер финального класса иерархии.
	 * 
	 * @return
	 */
	final protected Logger getClassLogger() {
		return LoggerFactory.getLogger(getClass());
	}
	
	/**
	 * Вспомогательный метод для журналирования информации об исключении.
	 * 
	 * Выполняет полный дамп исключения в отладочном режиме логгера.
	 * 
	 * @param msg
	 * @param e
	 */
	final protected void debugException(String msg, Exception e) {
		Logger logger = getClassLogger();
		if ( logger.isDebugEnabled() ) {
			logger.error(msg + ": {}", e.getMessage(), e);
		} else {
			logger.error(msg + ": {}", e.getMessage());
		}
	}
	
	private static class CompleteDelegate implements Observer {
		private final Observer target;
		
		private CompleteDelegate(Observer o) {
			super();
			target = o;
		}

		@Override
		public void update(Observable o, Object arg) {
			Task task = (Task)o;
			if ( task.completed() ) {
				target.update(o, arg);
			}
		}
		
	}
	
	private static class CancelDelegate implements Observer {
		private final Observer target;
		
		private CancelDelegate(Observer o) {
			super();
			target = o;
		}

		@Override
		public void update(Observable o, Object arg) {
			Task task = (Task)o;
			if ( task.cancelled() ) {
				target.update(o, arg);
			}
		}
	}
	
	private static class CancelOnUpdate implements Observer {
		private final Task target;
		
		private CancelOnUpdate(Task task) {
			super();
			target = task;
		}

		@Override
		public void update(Observable o, Object arg) {
			target.cancel();
		}
		
	}

}
