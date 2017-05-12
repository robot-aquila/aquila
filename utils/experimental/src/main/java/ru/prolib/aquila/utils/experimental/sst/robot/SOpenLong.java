package ru.prolib.aquila.utils.experimental.sst.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMExitAction;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;

public class SOpenLong extends BasicState implements SMExitAction {
	public static final String EOK = Const.E_OK;
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SOpenLong.class);
	}

	private Order order;
	
	public SOpenLong(RobotData data) {
		super(data);
		registerExit(EOK);
		setExitAction(this);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Security security = data.getSecurity();
		FDecimal price = security.getUpperPriceLimit();
		if ( price == null ) {
			logger.error("Upper price limit not defined");
			return getExit(EER);
		}
		Portfolio portfolio = data.getPortfolio();
		CalcUtils cu = new CalcUtils();
		long contracts = (long)portfolio.getEquity()
				.multiply(FDecimal.of2(data.getConfig().getShare()))
				.subtract(cu.getSafe(portfolio.getPosition(data.getSymbol()).getCurrentPrice()))
				.divide(cu.getLastPrice(security))
				.withScale(0)
				.doubleValue();
		logger.debug("Contracts: {}", contracts);
		if ( contracts == 0 ) {
			return getExit(EOK);
		}
		order = data.getTerminal().createOrder(data.getAccount(), data.getSymbol(),
				OrderAction.BUY, contracts, price);
		triggers.add(newExitOnEvent(order.onFailed(), EER));
		triggers.add(newExitOnEvent(order.onFilled(), EOK));
		triggers.add(newExitOnEvent(order.onCancelled(), EOK));
		try {
			data.getTerminal().placeOrder(order);
		} catch ( OrderException e ) {
			logger.error("Order failed: ", e);
			return getExit(EER);
		}
		return null;
	}

	@Override
	public void exit() {
		if ( order != null && ! order.getStatus().isFinal() ) {
			try {
				data.getTerminal().cancelOrder(order);
			} catch ( OrderException e ) {
				logger.error("Cancel failed: ", e);
			}
		}
		order = null;
	}

}
