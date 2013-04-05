package ru.prolib.aquila.ChaosTheory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Каркас робота.
 *
 * Вынесен в отдельный класс, что бы легче было создавать сколь угодно много
 * инстансов. Можно подсунуть каждому свои настройки в сервис-локаторе и
 * запускать параллельно в целях теста стратегий.
 * 
 * Все настройки робота определяются через Properies, получаемые по запросу
 * {@link ServiceLocator#getProperties()}. Ниже перечислены доступные
 * параметры:
 * 
 * Наименование		Тип		Обяз.	Описание
 * -----------------------------------------------------------------------------
 * StrategyClass	String	Да		Полное имя класса стратегии. Класс стратегии
 * 									должен быть наследником класса
 * 									{@link TradingStrategy}. Для инстанцирования
 * 									будет использован конструктор с аргументами
 * 									как в базовом классе.
 * 
 * RiskManagerClass	String	Да		Полное имя класса риск-менеджера. Этот
 * 									класс должен быть наследником класса
 * 									{@link RiskManager}. Для инстанцирования
 * 									будет использован конструктор с аргументами,
 * 									как в базовом классе.
 * 
 * EmergTimeout		long	Нет		Таймаут в миллисекундах для операций
 * 									экстренного закрытия позиции. По умолчанию
 * 									10 секунд на каждую операцию.
 */
public class RobotImpl implements Robot {
	private static final Logger logger = LoggerFactory.getLogger(RobotImpl.class);
	public static final String PROPNAME_STRATEGY_CLASS = "StrategyClass";
	public static final String PROPNAME_RISK_MANAGER_CLASS = "RiskManagerClass";
	public static final String PROPNAME_EMERG_TIMEOUT = "EmergTimeout";
	public static final String PROPNAME_UPDATE_SIGNAL = "UpdateSignal";
	public static final String PROP_DRIVER_SLIPPAGE = "Driver.Slippage";
	
	private final ServiceLocator locator;
	private PortfolioDriver driver;
	private TradingStrategy strategy;
	
	public RobotImpl(ServiceLocator locator) {
		super();
		this.locator = locator;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Robot#init()
	 */
	@Override
	public void init() throws Exception {
		Props props = locator.getProperties();
		long timeout = props.getInt(PROPNAME_EMERG_TIMEOUT, 10000);
		Portfolio port = locator.getPortfolio();
		Asset asset = port.getAsset();
		RiskManager rm = new RiskManagerSimpleLimit(locator.getPortfolioState(),
				props.getInt("RiskManager.MaxPos", 1));
		rm = new RiskManagerMultiplier(rm,
				props.getInt("RiskManager.Multiplier", 1));
		driver = new PortfolioDriver(port, asset, rm, timeout);
		driver.setSlippage(props.getInt(PROP_DRIVER_SLIPPAGE, 1));
		strategy = (TradingStrategy)Class
			.forName(props.getString(PROPNAME_STRATEGY_CLASS))
			.getDeclaredConstructor(ServiceLocator.class, PortfolioDriver.class)
			.newInstance(locator, driver);
		strategy.prepare();
		locator.getMarketData().prepare();
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Robot#pass()
	 */
	@Override
	public void pass() throws Exception {
		locator.getMarketData().update();
		strategy.nextPass();
		showResult();
		logger.debug("Pass finished {}\n",
				locator.getMarketData().getTime().get());
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ChaosTheory.Robot#clean()
	 */
	@Override
	public void clean() {
		strategy.clean();
	}
	
	private void showResult() {
		try {
			PortfolioState state = locator.getPortfolioState();
			Object args[] = {
					state.getPosition(),
					state.getMoney(),
					state.getVariationMargin(),
					state.getInitialMargin(),
					state.getTotalMoney()
			};
			logger.info("Position: {}, Money: {}, Var.Margin: {}, " +
					"Init.Margin: {}, Total: {}", args);
		} catch ( Exception e ) {
			logger.error("Couldn't calculate financial result", e);
		}
	}
	
}