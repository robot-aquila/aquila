package ru.prolib.aquila.test;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.OrderException;
import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.PortfolioOrders;
import ru.prolib.aquila.ChaosTheory.PortfolioState;
import ru.prolib.aquila.ChaosTheory.PortfolioTimeoutException;

/**
 * Состояние портфеля для тестирования стратегий.
 * Принимает уведомления о новых заявках от хранилища заявок.
 * Выполняет рассчет позиции и вариационной маржи на основе исполнения заявок.
 * Рыночные заявки исполняются по цене закрытия текущего бара.
 * Наблюдаемый объект. Уведомляет наблюдателей при смене баланса и текущей
 * позиции.
 */
public class TestPortfolioState extends Observable
	implements PortfolioState,Observer
{
	static private final Logger logger = LoggerFactory.getLogger(TestPortfolioState.class);
	protected PortfolioOrders orders;
	private final TestPortfolioStateController ctrl;
	private final Asset asset;
	
	public TestPortfolioState(Asset asset) {
		this(asset, new TestPortfolioStateControllerImpl());
	}
	
	public TestPortfolioState(Asset asset, TestPortfolioStateController ctrl) {
		super();
		this.ctrl = ctrl;
		this.asset = asset;
	}
	
	public Asset getAsset() {
		return asset;
	}
	
	public TestPortfolioStateController getController() {
		return ctrl;
	}
	
	public void setMoney(double money) throws PortfolioException {
		ctrl.setMoney(money);
	}
	
	public void setPosition(int position) throws PortfolioException {
		ctrl.setPosition(position);
	}
	
	/**
	 * Начать обслуживание
	 * 
	 * Вызов этого метода определяет источники данных и уведомлений. Выполняется
	 * подписка на уведомления соответствующих объектов. Если работа уже начата,
	 * то выбрасывается исключение.
	 * 
	 * @param orders объект доступа к заявкам
	 * @throws PortfolioException обслуживание уже начато
	 */
	public synchronized void startService(PortfolioOrders orders)
		throws PortfolioException
	{
		if ( this.orders != null ) {
			throw new PortfolioException("Service already started");
		}
		this.orders = orders;
		asset.addObserver(this);
		orders.addObserver(this);
	}
	
	/**
	 * Прекратить обслуживание
	 * 
	 * Этот метод отменяет подписку на соответствующие объекты и сбрасывает
	 * внутренние атрибуты, указывающие на них. Если на момент вызова
	 * обслуживание не было начато, то ничего не происходит.
	 * 
	 * @throws PortfolioException
	 */
	public synchronized void stopService() throws PortfolioException {
		asset.deleteObserver(this);
		if ( orders != null ) {
			orders.deleteObserver(this);
			orders = null;
		}
	}

	@Override
	public synchronized double getMoney() {
		return ctrl.getMoney();
	}

	@Override
	public synchronized int getPosition() {
		return ctrl.getPosition();
	}
	
	@Override
	public double getVariationMargin() throws PortfolioException {
		return ctrl.getVariationMargin();
	}

	@Override
	public double getInitialMargin() throws PortfolioException {
		return ctrl.getInitialMargin();
	}

	@Override
	public void waitForNeutralPosition(long timeout)
			throws PortfolioTimeoutException, PortfolioException,
			InterruptedException
	{
		if ( getPosition() != 0 ) {
			throw new PortfolioTimeoutException();
		}
	}

	@Override
	public synchronized void update(Observable o, Object arg) {
		if ( o == orders ) {
			Order order = (Order)arg;
			if ( order.isMarketOrder() || order.isLimitOrder() ) {
				order.addObserver(this);
			}
		} else if ( o instanceof Order ) {
			String msg = "Change position failed";
			try {
				checkOrder((Order) o);
			} catch ( PortfolioException e ) {
				error(msg, e);
			} catch ( OrderException e ) {
				error(msg, e);
			}
		} else if ( o == asset ) {
			if ( arg == Asset.EVENT_CLEARING ) {
				try {
					ctrl.closePeriod();
					ctrl.openPeriod(asset);
				} catch ( PortfolioException e ) {
					error("Open period failed", e);
				}
			}
		} else {
			logger.error("Unknown observable instance: {}", o);
		}
	}
	
	private void checkOrder(Order order)
		throws PortfolioException,OrderException
	{
		if ( order.getStatus() != Order.FILLED ) {
			return;
		}
		if ( order.isMarketOrder() ) {
			if ( order.isBuy() ) {
				ctrl.changePosition(order.getQty());
			} else {
				ctrl.changePosition(-order.getQty());
			}
		} else if ( order.isLimitOrder() ) {
			if ( order.isBuy() ) {
				ctrl.changePosition(order.getQty(), order.getPrice());
			} else {
				ctrl.changePosition(-order.getQty(), order.getPrice());
			}
		} else {
			logger.warn("Unexpected order type: {}", order);
		}
		setChanged();
		notifyObservers();
		order.deleteObserver(this);
	}
	
	private void error(String msg, Exception e) {
		Object params[] = null;
		if ( logger.isDebugEnabled() ) {
			params = new Object[] { msg, e.getMessage(), e };
		} else {
			params = new Object[] { msg, e.getMessage() };
		}
		logger.error("{}: {}", params);
	}

	@Override
	public double getTotalMoney() throws PortfolioException {
		return ctrl.getMoney() + ctrl.getInitialMargin() +
			ctrl.getVariationMargin();
	}

}
