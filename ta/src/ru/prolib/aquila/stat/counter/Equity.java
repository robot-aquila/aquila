package ru.prolib.aquila.stat.counter;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.PortfolioState;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;
import ru.prolib.aquila.ta.ds.MarketData;

/**
 * Расчитывает величину собственного капитала в деньгах портфеля.
 * Перерасчет выполняется каждый раз при образовании бара в MarketData.
 * После перерасчета уведомляет наблюдателей (фактически на каждом новом баре).
 */
public class Equity extends Observable implements Counter<Double>,Observer {
	private static final Logger logger = LoggerFactory.getLogger(Equity.class);
	private Double value = null;
	protected MarketData data = null;
	protected PortfolioState state = null;
	
	public Equity() {
		super();
	}

	@Override
	public Double getValue() {
		return value;
	}

	@Override
	public void startService(ServiceLocator locator) throws CounterException {
		if ( data != null ) {
			throw new CounterServiceAlreadyStartedException();
		}
		try {
			state = locator.getPortfolioState();
			data = locator.getMarketData();
		} catch ( ServiceLocatorException e ) {
			state = null;
			throw new CounterException(e.getMessage(), e); 
		}
		data.addObserver(this);
	}

	@Override
	public void stopService() throws CounterException {
		if ( data != null ) {
			data.deleteObserver(this);
			data = null;
			state = null;
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if ( arg0 == data ) {
			try {
				value = state.getTotalMoney();
				setChanged();
				notifyObservers();
			} catch ( PortfolioException e ) {
				error(e);
				value = null;
			}
		}
	}

	//@Override
	//public void reset() {
	//	if ( state != null ) {
	//		try {
	//			value = state.getTotalMoney();
	//		} catch ( PortfolioException e ) {
	//			error(e);
	//			value = null;
	//		}
	//	} else {
	//		value = null;
	//	}
	//}
	
	private void error(Exception e) {
		if ( logger.isDebugEnabled() ) {
			logger.error("Could not obtain equity", e);
		} else {
			logger.error("Could not obtain equity: {}", e.getMessage());
		}
	}

}
