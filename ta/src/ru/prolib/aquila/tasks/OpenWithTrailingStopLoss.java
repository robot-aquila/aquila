package ru.prolib.aquila.tasks;

import java.util.Observable;
import java.util.Observer;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;

public class OpenWithTrailingStopLoss
	extends TaskUsesLocator implements Observer
{
	protected LimitOrder open;
	protected TrailingStopLoss stop;

	public OpenWithTrailingStopLoss(ServiceLocator locator) {
		super(locator);
	}
	
	/**
	 * Установить продолжительность открытия позы в барах.
	 * 
	 * @param bars
	 * @return
	 */
	public synchronized OpenWithTrailingStopLoss openNotLongerThan(int bars) {
		open.notLongerThan(bars);
		return this;
	}
	
	/**
	 * Установить цену открытия позиции.
	 * 
	 * @param price
	 * @return
	 */
	public synchronized OpenWithTrailingStopLoss openPrice(double price) {
		open.price(price);
		return this;
	}
	
	/**
	 * Установить начальную цену заявки, активируемой по защитному стопу.
	 * 
	 * @param price
	 * @return
	 */
	public synchronized OpenWithTrailingStopLoss stopPrice(double price) {
		stop.price(price);
		return this;
	}
	
	public synchronized OpenWithTrailingStopLoss stopSpread(int spread) {
		stop.spread(spread);
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

	@Override
	public synchronized void cancel() {
		if ( started() ) {
			stop.cancel();
			open.cancel();
			setCancelled();
		}
	}

	@Override
	public synchronized void update(Observable o, Object arg) {
		if ( o == open ) {
			open.deleteObserver(this);
			if ( open.completed() ) {
				stop.addObserver(this);
				stop.start();
				if ( stop.completed() ) {
					setCompleted();
				} else if ( stop.cancelled() ) {
					setCancelled();
				}
			} else {
				setCancelled();
			}
		} else if ( o == stop ) {
			// Если мы здесь, значит
			// 1) стоп-лосс исполнился и закрыл позицию
			// 2) стоп-лосс был отменен по внешнему сигналу 
			stop.deleteObserver(this);
			if ( stop.completed() ) {
				setCompleted(); // закрылись по стопу
			} else {
				setCancelled(); // стоп-сняли, но раз был стоп, значит
								// была открытая позиция. Надо закрывать.
			}
			// Независимо от того, что произошло, на всякий случай
			// запускаем процедуру принудительного закрытия позиции.
			// Если позиция закрыта, то ничего не произойдет.
			
			// TODO: Нельзя так делать. В тестовом режиме фигачит подряд две
			// продажи, так как стейт портфеля не успевает обновиться
			//new CloseImmediately(locator).start();
		}
	}

}
