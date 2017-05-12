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

public class SCloseLong extends BasicState implements SMExitAction {
	public static final String EOK = Const.E_OK;
	public static final String EOPN = Const.S_OPEN;
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SCloseLong.class);
	}
	
	private Order order;

	public SCloseLong(RobotData data) {
		super(data);
		registerExit(EOK);
		registerExit(EOPN);
		setExitAction(this);
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Security security = data.getSecurity();
		FDecimal price = security.getLowerPriceLimit();
		if ( price == null ) {
			logger.error("Lower price limit not defined");
			return getExit(EER);
		}
		Portfolio portfolio = data.getPortfolio();
		CalcUtils cu = new CalcUtils();
		long contracts = cu.getSafe(portfolio.getPosition(data.getSymbol()).getCurrentVolume());
		if ( contracts <= 0L ) {
			return getExit(EOK);
		}
		order = data.getTerminal().createOrder(data.getAccount(), data.getSymbol(),
				OrderAction.SELL, contracts, price);
		triggers.add(newExitOnEvent(order.onFailed(), EER));
		triggers.add(newExitOnEvent(order.onFilled(), EOK));
		triggers.add(newExitOnEvent(order.onCancelled(), EOK));
		triggers.add(newExitOnEvent(data.getSignal().onBullish(), EOPN));
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
