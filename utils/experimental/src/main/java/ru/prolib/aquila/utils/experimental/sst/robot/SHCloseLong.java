package ru.prolib.aquila.utils.experimental.sst.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMExitAction;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;

public class SHCloseLong extends BasicStateHandler implements SMExitAction {
	public static final String EOK = Const.E_OK;
	public static final String EOPN = Const.E_OPEN;
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SHCloseLong.class);
	}
	
	private Order order;

	public SHCloseLong(RobotState data) {
		super(data);
		registerExit(EOK);
		registerExit(EOPN);
		setExitAction(this);
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Security security = data.getSecurity();
		CDecimal price = security.getLowerPriceLimit();
		if ( price == null ) {
			logger.error("Lower price limit not defined");
			return getExit(EER);
		}
		Portfolio portfolio = data.getPortfolio();
		CalcUtils cu = new CalcUtils();
		CDecimal contracts = cu.getSafe(portfolio.getPosition(data.getSymbol()).getCurrentVolume());
		if ( contracts.compareTo(CDecimalBD.ZERO) <= 0 ) {
			return getExit(EOK);
		}
		order = data.getTerminal().createOrder(data.getAccount(), data.getSymbol(),
				OrderAction.SELL, contracts, price);
		triggers.add(newExitOnEvent(order.onFailed(), EER));
		triggers.add(newExitOnEvent(order.onFilled(), EOK));
		triggers.add(newExitOnEvent(order.onCancelled(), EOK));
		triggers.add(newExitOnEvent(data.getMarketSignal().onBullish(), EOPN));
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
