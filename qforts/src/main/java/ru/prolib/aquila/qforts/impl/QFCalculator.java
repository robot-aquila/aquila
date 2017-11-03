package ru.prolib.aquila.qforts.impl;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderField;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.PortfolioField;
import ru.prolib.aquila.core.BusinessEntities.Position;
import ru.prolib.aquila.core.BusinessEntities.PositionField;
import ru.prolib.aquila.core.BusinessEntities.Security;

public class QFCalculator {
	private static final CDecimal ZERO = CDecimalBD.ZERO;
	private static final CDecimal ZERO_MONEY2 = CDecimalBD.ZERO_RUB2;
	private static final CDecimal ZERO_MONEY5 = CDecimalBD.ZERO_RUB5;
	private final QFCalcUtils utils;
	
	public QFCalculator(QFCalcUtils utils) {
		this.utils = utils;
	}
	
	public QFCalculator() {
		this(new QFCalcUtils());
	}
	
	private QFPortfolioChangeUpdate updateEquityAndFreeMargin(QFPortfolioChangeUpdate update) {
		return update.setFinalEquity(update.getFinalBalance().add(update.getFinalProfitAndLoss()))
			.setFinalFreeMargin(update.getFinalEquity().subtract(update.getFinalUsedMargin()));
	}
	
	private QFPortfolioChangeUpdate setInitialValues(Portfolio portfolio, QFPortfolioChangeUpdate update) {
		return update
			.setInitialBalance(portfolio.getCDecimal(PortfolioField.BALANCE, ZERO_MONEY2))
			.setInitialEquity(portfolio.getCDecimal(PortfolioField.EQUITY, ZERO_MONEY2))
			.setInitialFreeMargin(portfolio.getCDecimal(PortfolioField.FREE_MARGIN, ZERO_MONEY2))
			.setInitialProfitAndLoss(portfolio.getCDecimal(PortfolioField.PROFIT_AND_LOSS, ZERO_MONEY2))
			.setInitialUsedMargin(portfolio.getCDecimal(PortfolioField.USED_MARGIN, ZERO_MONEY2))
			.setInitialVarMargin(portfolio.getCDecimal(QFPortfolioField.QF_VAR_MARGIN, ZERO_MONEY5))
			.setInitialVarMarginClose(portfolio.getCDecimal(QFPortfolioField.QF_VAR_MARGIN_CLOSE, ZERO_MONEY5))
			.setInitialVarMarginInter(portfolio.getCDecimal(QFPortfolioField.QF_VAR_MARGIN_INTER, ZERO_MONEY5));
	}
	
	public QFPortfolioChangeUpdate changePosition(Portfolio portfolio,
			Security security, CDecimal volume, CDecimal price)
	{
		Position pos = portfolio.getPosition(security.getSymbol());
		QFPositionChangeUpdate pu = utils.changePosition(pos, volume, price);
		QFPortfolioChangeUpdate update = new QFPortfolioChangeUpdate(pu.getAccount());
		setInitialValues(portfolio, update)
			.setPositionUpdate(pu)
			.setChangeBalance(CDecimalBD.ZERO_RUB2)
			.setChangeProfitAndLoss(pu.getChangeProfitAndLoss())
			.setChangeUsedMargin(pu.getChangeUsedMargin())
			.setChangeVarMargin(pu.getChangeVarMargin())
			.setChangeVarMarginClose(pu.getChangeVarMarginClose())
			.setChangeVarMarginInter(pu.getChangeVarMarginInter());
		return updateEquityAndFreeMargin(update);
	}
	
	public QFPortfolioChangeUpdate updateByMarket(Portfolio portfolio) {
		QFPortfolioChangeUpdate update = new QFPortfolioChangeUpdate(portfolio.getAccount());
		CDecimal cUsMgn = ZERO_MONEY2, cVarMgn = ZERO_MONEY5, cPL = ZERO_MONEY2;
		for ( Position pos : portfolio.getPositions() ) {
			if ( pos.getCDecimal(PositionField.CURRENT_VOLUME, ZERO).compareTo(ZERO) != 0L ) {
				QFPositionChangeUpdate pu = utils.refreshByCurrentState(pos);
				update.setPositionUpdate(pu);
				cUsMgn = cUsMgn.add(pu.getChangeUsedMargin());
				cVarMgn = cVarMgn.add(pu.getChangeVarMargin());
				cPL = cPL.add(pu.getChangeProfitAndLoss());
			}
		}
		setInitialValues(portfolio, update)
			.setChangeBalance(ZERO_MONEY2)
			.setChangeProfitAndLoss(cPL)
			.setChangeUsedMargin(cUsMgn)
			.setChangeVarMargin(cVarMgn)
			.setChangeVarMarginClose(ZERO_MONEY5)
			.setChangeVarMarginInter(ZERO_MONEY5);
		return updateEquityAndFreeMargin(update);
	}
	
	public QFPortfolioChangeUpdate updateMargin(Position position) {
		QFPortfolioChangeUpdate update = new QFPortfolioChangeUpdate(position.getAccount())
			.setChangeBalance(ZERO_MONEY2)
			.setChangeProfitAndLoss(ZERO_MONEY2)
			.setChangeUsedMargin(ZERO_MONEY2)
			.setChangeVarMargin(ZERO_MONEY5)
			.setChangeVarMarginClose(ZERO_MONEY5)
			.setChangeVarMarginInter(ZERO_MONEY5);
		setInitialValues(position.getPortfolio(), update);
		if ( position.getCDecimal(PositionField.CURRENT_VOLUME, ZERO).compareTo(ZERO) != 0L ) {
			QFPositionChangeUpdate pu = utils.refreshByCurrentState(position);
			update.setPositionUpdate(pu)
				.setChangeUsedMargin(pu.getChangeUsedMargin())
				.setChangeVarMargin(pu.getChangeVarMargin())
				.setChangeProfitAndLoss(pu.getChangeProfitAndLoss());
		}
		return updateEquityAndFreeMargin(update);
	}
	
	public QFPortfolioChangeUpdate changeBalance(Portfolio portfolio, CDecimal value) {
		QFPortfolioChangeUpdate update = new QFPortfolioChangeUpdate(portfolio.getAccount())
			.setChangeBalance(value)
			.setChangeProfitAndLoss(ZERO_MONEY2)
			.setChangeUsedMargin(ZERO_MONEY2)
			.setChangeVarMargin(ZERO_MONEY5)
			.setChangeVarMarginClose(ZERO_MONEY5)
			.setChangeVarMarginInter(ZERO_MONEY5);
		setInitialValues(portfolio, update);
		return updateEquityAndFreeMargin(update);
	}
	
	public QFOrderExecutionUpdate executeOrder(Order order,
			CDecimal volume,
			CDecimal price)
	{
		QFOrderExecutionUpdate update = new QFOrderExecutionUpdate()
			.setInitialCurrentVolume(order.getCDecimal(OrderField.CURRENT_VOLUME, ZERO))
			.setInitialExecutedValue(order.getCDecimal(OrderField.EXECUTED_VALUE, ZERO_MONEY2))
			.setInitialStatus(order.getStatus())
			.setChangeCurrentVolume(volume.negate())
			.setChangeExecutedValue(utils.priceToMoney(order.getSecurity(), volume, price))
			.setFinalStatus(order.getStatus());
		update.setExecutionAction(order.getAction())
			.setExecutionOrderID(order.getID())
			.setExecutionPrice(price)
			.setExecutionSymbol(order.getSymbol())
			.setExecutionTime(order.getTerminal().getCurrentTime()) // No prob with concurrency
			.setExecutionValue(update.getChangeExecutedValue().abs())
			.setExecutionVolume(volume);
		if ( update.getFinalCurrentVolume().compareTo(ZERO) <= 0 ) {
			update.setFinalStatus(OrderStatus.FILLED)
				.setFinalizationTime(update.getExecutionTime());
		}
		return update;
	}
	
	public QFOrderStatusUpdate updateOrderStatus(Order order,
			OrderStatus status, String systemMessage)
	{
		QFOrderStatusUpdate update = new QFOrderStatusUpdate()
			.setInitialStatus(order.getStatus())
			.setFinalStatus(status)
			.setSystemMessage(systemMessage);
		if ( status.isFinal() ) {
			update.setFinalizationTime(order.getTerminal().getCurrentTime()); // No prob with concurrency
		}
		return update;
	}

	public QFPortfolioChangeUpdate midClearing(Portfolio portfolio) {
		QFPortfolioChangeUpdate update = new QFPortfolioChangeUpdate(portfolio.getAccount());
		CDecimal cUsMgn = ZERO_MONEY2, fVarMgnI = ZERO_MONEY5, cPL = ZERO_MONEY2;
		for ( Position pos : portfolio.getPositions() ) {
			if ( pos.getCDecimal(PositionField.CURRENT_VOLUME, ZERO).compareTo(ZERO) != 0 ) {
				QFPositionChangeUpdate pu = utils.midClearing(pos);
				update.setPositionUpdate(pu);
				cUsMgn = cUsMgn.add(pu.getChangeUsedMargin());
				fVarMgnI = fVarMgnI.add(pu.getChangeVarMarginInter());
				cPL = cPL.add(pu.getChangeProfitAndLoss());
			}
		}
		setInitialValues(portfolio, update)
			.setChangeBalance(ZERO_MONEY2)
			.setChangeProfitAndLoss(cPL)
			.setChangeUsedMargin(cUsMgn)
			.setFinalVarMargin(ZERO_MONEY5)
			.setFinalVarMarginClose(ZERO_MONEY5)
			.setFinalVarMarginInter(fVarMgnI);
		return updateEquityAndFreeMargin(update);
	}
	
	public QFPortfolioChangeUpdate clearing(Portfolio portfolio) {
		QFPortfolioChangeUpdate update = new QFPortfolioChangeUpdate(portfolio.getAccount());
		CDecimal cUsMgn = ZERO_MONEY2, cBal = ZERO_MONEY2;
		for ( Position pos : portfolio.getPositions() ) {
			if ( pos.getCDecimal(PositionField.CURRENT_VOLUME, ZERO).compareTo(ZERO) != 0L ) {
				QFPositionChangeUpdate pu = utils.clearing(pos);
				update.setPositionUpdate(pu);
				cUsMgn = cUsMgn.add(pu.getChangeUsedMargin());
				cBal = cBal.add(pu.getChangeBalance());
			}
		}
		setInitialValues(portfolio, update)
			.setChangeBalance(cBal)
			.setChangeUsedMargin(cUsMgn)
			.setFinalProfitAndLoss(ZERO_MONEY2)
			.setFinalVarMargin(ZERO_MONEY5)
			.setFinalVarMarginClose(ZERO_MONEY5)
			.setFinalVarMarginInter(ZERO_MONEY5);
		return updateEquityAndFreeMargin(update);
	}
}
