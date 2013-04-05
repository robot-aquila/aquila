package ru.prolib.aquila.tasks;

import java.util.Observable;
import java.util.Observer;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;

public abstract class TargetedWithFixedStopLoss
	extends TaskUsesLocator implements Observer
{

	protected LimitOrder open;
	protected LimitOrder close;
	protected StopLoss stop;

	public TargetedWithFixedStopLoss(ServiceLocator locator) {
		super(locator);
	}

	/**
	 * Установить продолжительность открытия позы в барах.
	 * 
	 * @param bars
	 * @return
	 */
	public synchronized TargetedWithFixedStopLoss openNotLongerThan(int bars) {
		open.notLongerThan(bars);
		return this;
	}

	/**
	 * Установить цену открытия позиции.
	 * 
	 * @param price
	 * @return
	 */
	public synchronized TargetedWithFixedStopLoss openPrice(double price) {
		open.price(price);
		return this;
	}

	/**
	 * Установить продолжительность закрытия позы в барах.
	 * 
	 * @param bars
	 * @return
	 */
	public synchronized TargetedWithFixedStopLoss closeNotLongerThan(int bars) {
		close.notLongerThan(bars);
		return this;
	}

	/**
	 * Установить цену закрытия позиции.
	 * 
	 * @param target
	 * @return
	 */
	public synchronized TargetedWithFixedStopLoss closePrice(double target) {
		close.price(target);
		return this;
	}

	/**
	 * Установить цену реализации по стоп-лоссу.
	 * 
	 * @param stopPrice
	 * @return
	 */
	public synchronized TargetedWithFixedStopLoss stopPrice(double stopPrice) {
		stop.price(stopPrice);
		return this;
	}

	@Override
	public synchronized void start() {
		if ( ! pending() ) {
			return;
		}
		setStarted();
		open.addObserver(this);
		open.start();
	}

	/**
	 * Отменить задачу
	 * 
	 * Если на момент вызова есть открытая позиция, то она будет принудительно
	 * закрыта. Все активные заявки будут сняты. Если задача не запущена, то
	 * ничего не произойдет. 
	 */
	@Override
	public synchronized void cancel() {
		if ( started() ) {
			stop.cancel();
			close.cancel();
			open.cancel();
			setCancelled();
		}
	}

	@Override
	public synchronized void update(Observable o, Object arg) {
		if ( o == open ) {
			if ( open.completed() ) {
				close.addObserver(this);
				close.start();
				if ( close.started() ) {
					// Заявка на закрытие позы активирована,
					// теперь активируем стоп-лосс
					stop.addObserver(this);
					stop.start();
				} else if ( close.completed() ) {
					// Закрытие позиции произошло немедленно,
					// даже стоп-лосс выставлять не пришлось.
					setCompleted();
				}
			} else {
				setCancelled();
			}
		} else {
			// Если мы здесь, значит:
			// 1) стоп-лосс исполнился и закрыл позицию
			// 2) лимитная заявка на закрытие позиции выполнена
			// 3) лимитная заявка на закрытие позиции снята. Снята она может
			//	  быть в случае, если закончилось время удержания позиции.
			//	  Во всех случаях надо чекать позицию и закрывать, если есть
			//	  остатки.
			
			// Нет смысла слушать заявки дальше,
			// так как все решится прямо сейчас.
			stop.deleteObserver(this);
			close.deleteObserver(this);
			if ( o == close ) {
				stop.cancel(); // если стоп не снят, то снимем
				if ( close.completed() ) {
					setCompleted(); // все отлично - успешный трейд
				} else {
					setCancelled(); // цель не достигнута, но еще есть шанс
									// выйти в ноль
				}
			} else if ( o == stop ) {
				close.cancel(); // если лимитная не снята, то снимаем
				if ( stop.completed() ) {
					setCompleted(); // закрылись по стопу
				} else {
					setCancelled(); // стоп-сняли, но раз был стоп, значит
									// была открытая позиция. Надо закрывать.
				}
			}
			// Независимо от того, что произошло, на всякий случай
			// запускаем процедуру принудительного закрытия позиции.
			// Если позиция закрыта, то ничего не произойдет.
			
			// TODO: нельзя так делать. см. трейлинг стоп
			//new CloseImmediately(locator).start();
		}
	}

}