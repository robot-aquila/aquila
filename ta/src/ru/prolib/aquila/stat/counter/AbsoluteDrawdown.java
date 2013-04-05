package ru.prolib.aquila.stat.counter;

import java.util.Observable;
import java.util.Observer;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;

/**
 * 2012-02-02
 * $Id: AbsoluteDrawdown.java 197 2012-02-05 20:21:19Z whirlwind $
 * 
 * AbsoluteDrawdown - разница между последним максимумом и текущим значением.
 * 
 * Расчитывает текущее абсолютное значение просадки по счетчику.
 * Выполняет перерасчет при изменении значения счетчика.
 * Уведомляет наблюдателей об изменении каждый раз при перерасчете (фактически
 * каждый раз при обновлении счетчика).
 * 
 * Данный счетчик может использоваться например для расчета просадки портфеля.
 * Для этого нужно использовать экземпляр класса в комплексе с {@link Equity}.
 */
public class AbsoluteDrawdown extends Observable
	implements Observer,Counter<Double>
{
	private final Max counterMax;
	private final Sub drawdown;
	
	public AbsoluteDrawdown(Counter<Double> counter) {
		super();
		counterMax = new Max(counter);
		drawdown = new Sub(counterMax, counter);
	}

	@Override
	public Double getValue() {
		return drawdown.getValue();
	}

	@Override
	public void startService(ServiceLocator locator) throws CounterException {
		drawdown.addObserver(this);
		drawdown.startService(locator);
		counterMax.startService(locator);
	}

	@Override
	public void stopService() throws CounterException {
		counterMax.stopService();
		drawdown.stopService();
		drawdown.deleteObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		setChanged();
		notifyObservers();
	}

}
