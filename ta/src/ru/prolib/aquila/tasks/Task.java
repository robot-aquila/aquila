package ru.prolib.aquila.tasks;

import java.util.Observer;

import ru.prolib.aquila.util.Observable;

/**
 * Интерфейс абстрактной задачи.
 * 
 * Задача может быть в одном из следующих состояний:
 * {@link #PENDING} - задача была создана, но не запущена в работу;
 * {@link #STARTED} - задача была запущена, но исполнение еще не привело
 * к определенному результату. 
 * {@link #COMPLETED} - задача успешно выполнена.
 * {@link #CANCELLED} - задача была принудительно отменена, либо достигнуты
 * условия, не позволяющие выполнить задачу.
 * 
 * Каждая задача является наблюдаемым объектом. Уведомление наблюдателям
 * отправляется в тот момент, когда задача была исполнена или отменена. При
 * изменении статуса задачи на {@link #COMPLETED} или {@link #CANCELLED}
 * список обозревателей автоматически очищается.
 */
public interface Task extends Observable {
	public static final int PENDING		= 0;
	public static final int STARTED		= 1;
	public static final int COMPLETED	= 2;
	public static final int CANCELLED	= 3;
	
	/**
	 * Является ли задача готовой к исполнению.
	 * 
	 * @return true - задача готова к исполнению, false - уже исполняется,
	 * завершена или отменена.
	 */
	public boolean pending();
	
	/**
	 * Является ли задача завершенной.
	 * 
	 * @return true - задача исполнена, false - не запущена или отменена
	 */
	public boolean completed();
	
	/**
	 * Является ли задача отмененной.
	 * 
	 * @return true - задача отменена, false - не запущена или исполнена
	 */
	public boolean cancelled();
	
	/**
	 * Является ли задача запущенной на выполнение.
	 * 
	 * @return true - задача исполняется, false - задача не запущена, исполнена
	 * или отменена
	 */
	public boolean started();
	
	/**
	 * Запустить задачу на исполнение
	 * 
	 * Не имеет эффекта, если задача уже запущена, отменена или исполнена.
	 */
	public void start();
	
	/**
	 * Отменить задачу
	 * 
	 * Не имеет эффекта, если задача уже отменена или исполнена.
	 */
	public void cancel();
	
	/**
	 * Установить обозревателя по исполнению
	 * 
	 * @param observer обозреватель, который будет уведомлен, если статус
	 * текущей задачи изменится на {@link #COMPLETED}. Если на момент вызова
	 * текущая задача исполнена, то указанный обозреватель будет уведомлен
	 * немедленно.
	 */
	public void onComplete(Observer observer);
	
	/**
	 * Установить обозревателя по исполнению
	 * 
	 * @param observer обозреватель, который будет уведомлен, если статус
	 * текущей задачи изменится на {@link #CANCELLED}. Если на момент вызова
	 * текущая задача отменена, то указанный обозреватель будет уведомлен
	 * немедленно.
	 */
	public void onCancel(Observer observer);
	
	public void onCompleteCancel(Task task);

	public void onCancelCancel(Task task);

}
