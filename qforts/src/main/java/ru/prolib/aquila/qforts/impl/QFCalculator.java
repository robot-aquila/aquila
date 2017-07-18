package ru.prolib.aquila.qforts.impl;

import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderField;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.PortfolioField;
import ru.prolib.aquila.core.BusinessEntities.Position;
import ru.prolib.aquila.core.BusinessEntities.PositionField;
import ru.prolib.aquila.core.BusinessEntities.Security;

public class QFCalculator {
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
			.setInitialBalance(portfolio.getMoneyOrZero2(PortfolioField.BALANCE, FMoney.RUB))
			.setInitialEquity(portfolio.getMoneyOrZero2(PortfolioField.EQUITY, FMoney.RUB))
			.setInitialFreeMargin(portfolio.getMoneyOrZero2(PortfolioField.FREE_MARGIN, FMoney.RUB))
			.setInitialProfitAndLoss(portfolio.getMoneyOrZero2(PortfolioField.PROFIT_AND_LOSS, FMoney.RUB))
			.setInitialUsedMargin(portfolio.getMoneyOrZero2(PortfolioField.USED_MARGIN, FMoney.RUB))
			.setInitialVarMargin(portfolio.getMoneyOrZero(QFPortfolioField.QF_VAR_MARGIN, 5, FMoney.RUB))
			.setInitialVarMarginClose(portfolio.getMoneyOrZero(QFPortfolioField.QF_VAR_MARGIN_CLOSE, 5, FMoney.RUB))
			.setInitialVarMarginInter(portfolio.getMoneyOrZero(QFPortfolioField.QF_VAR_MARGIN_INTER, 5, FMoney.RUB));
	}
	
	public QFPortfolioChangeUpdate changePosition(Portfolio portfolio,
			Security security, long volume, FDecimal price)
	{
		Position pos = portfolio.getPosition(security.getSymbol());
		QFPositionChangeUpdate pu = utils.changePosition(pos, volume, price);
		QFPortfolioChangeUpdate update = new QFPortfolioChangeUpdate(pu.getAccount());
		setInitialValues(portfolio, update)
			.setPositionUpdate(pu)
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setChangeProfitAndLoss(pu.getChangeProfitAndLoss())
			.setChangeUsedMargin(pu.getChangeUsedMargin())
			.setChangeVarMargin(pu.getChangeVarMargin())
			.setChangeVarMarginClose(pu.getChangeVarMarginClose())
			.setChangeVarMarginInter(pu.getChangeVarMarginInter());
		return updateEquityAndFreeMargin(update);
	}
	
	public QFPortfolioChangeUpdate updateByMarket(Portfolio portfolio) {
		QFPortfolioChangeUpdate update = new QFPortfolioChangeUpdate(portfolio.getAccount());
		FMoney cUsMgn = FMoney.ZERO_RUB2, cVarMgn = FMoney.ZERO_RUB5, cPL = FMoney.ZERO_RUB2;
		for ( Position pos : portfolio.getPositions() ) {
			if ( pos.getLongOrZero(PositionField.CURRENT_VOLUME) != 0L ) {
				QFPositionChangeUpdate pu = utils.refreshByCurrentState(pos);
				update.setPositionUpdate(pu);
				cUsMgn = cUsMgn.add(pu.getChangeUsedMargin());
				cVarMgn = cVarMgn.add(pu.getChangeVarMargin());
				cPL = cPL.add(pu.getChangeProfitAndLoss());
			}
		}
		setInitialValues(portfolio, update)
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setChangeProfitAndLoss(cPL)
			.setChangeUsedMargin(cUsMgn)
			.setChangeVarMargin(cVarMgn)
			.setChangeVarMarginClose(FMoney.ZERO_RUB5)
			.setChangeVarMarginInter(FMoney.ZERO_RUB5);
		return updateEquityAndFreeMargin(update);
	}
	
	public QFPortfolioChangeUpdate updateMargin(Position position) {
		QFPortfolioChangeUpdate update = new QFPortfolioChangeUpdate(position.getAccount())
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setChangeProfitAndLoss(FMoney.ZERO_RUB2)
			.setChangeUsedMargin(FMoney.ZERO_RUB2)
			.setChangeVarMargin(FMoney.ZERO_RUB5)
			.setChangeVarMarginClose(FMoney.ZERO_RUB5)
			.setChangeVarMarginInter(FMoney.ZERO_RUB5);
		setInitialValues(position.getPortfolio(), update);
		if ( position.getLongOrZero(PositionField.CURRENT_VOLUME) != 0L ) {
			QFPositionChangeUpdate pu = utils.refreshByCurrentState(position);
			update.setPositionUpdate(pu)
				.setChangeUsedMargin(pu.getChangeUsedMargin())
				.setChangeVarMargin(pu.getChangeVarMargin())
				.setChangeProfitAndLoss(pu.getChangeProfitAndLoss());
		}
		return updateEquityAndFreeMargin(update);
	}
	
	public QFPortfolioChangeUpdate changeBalance(Portfolio portfolio,
			FMoney value)
	{
		QFPortfolioChangeUpdate update = new QFPortfolioChangeUpdate(portfolio.getAccount())
			.setChangeBalance(value)
			.setChangeProfitAndLoss(FMoney.ZERO_RUB2)
			.setChangeUsedMargin(FMoney.ZERO_RUB2)
			.setChangeVarMargin(FMoney.ZERO_RUB5)
			.setChangeVarMarginClose(FMoney.ZERO_RUB5)
			.setChangeVarMarginInter(FMoney.ZERO_RUB5);
		setInitialValues(portfolio, update);
		return updateEquityAndFreeMargin(update);
	}
	
	public QFOrderExecutionUpdate executeOrder(Order order, long volume,
			FDecimal price)
	{
		QFOrderExecutionUpdate update = new QFOrderExecutionUpdate()
			.setInitialCurrentVolume(order.getLongOrZero(OrderField.CURRENT_VOLUME))
			.setInitialExecutedValue(order.getMoneyOrZero2(OrderField.EXECUTED_VALUE, FMoney.RUB))
			.setInitialStatus(order.getStatus())
			.setChangeCurrentVolume(-volume)
			.setChangeExecutedValue(utils.priceToMoney(order.getSecurity(), volume, price))
			.setFinalStatus(order.getStatus());
		update.setExecutionAction(order.getAction())
			.setExecutionOrderID(order.getID())
			.setExecutionPrice(price)
			.setExecutionSymbol(order.getSymbol())
			.setExecutionTime(order.getTerminal().getCurrentTime()) // No prob with concurrency
			.setExecutionValue(update.getChangeExecutedValue().abs())
			.setExecutionVolume(volume);
		if ( update.getFinalCurrentVolume() <= 0L ) {
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
		FMoney cUsMgn = FMoney.ZERO_RUB2, fVarMgnI = FMoney.ZERO_RUB5, cPL = FMoney.ZERO_RUB2;
		for ( Position pos : portfolio.getPositions() ) {
			if ( pos.getLongOrZero(PositionField.CURRENT_VOLUME) != 0L ) {
				QFPositionChangeUpdate pu = utils.midClearing(pos);
				update.setPositionUpdate(pu);
				cUsMgn = cUsMgn.add(pu.getChangeUsedMargin());
				fVarMgnI = fVarMgnI.add(pu.getChangeVarMarginInter());
				cPL = cPL.add(pu.getChangeProfitAndLoss());
			}
		}
		setInitialValues(portfolio, update)
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setChangeProfitAndLoss(cPL)
			.setChangeUsedMargin(cUsMgn)
			.setFinalVarMargin(FMoney.ZERO_RUB5)
			.setFinalVarMarginClose(FMoney.ZERO_RUB5)
			.setFinalVarMarginInter(fVarMgnI);
		return updateEquityAndFreeMargin(update);
	}
	
	public QFPortfolioChangeUpdate clearing(Portfolio portfolio) {
		QFPortfolioChangeUpdate update = new QFPortfolioChangeUpdate(portfolio.getAccount());
		FMoney cUsMgn = FMoney.ZERO_RUB2, cBal = FMoney.ZERO_RUB2;
		for ( Position pos : portfolio.getPositions() ) {
			if ( pos.getLongOrZero(PositionField.CURRENT_VOLUME) != 0L ) {
				QFPositionChangeUpdate pu = utils.clearing(pos);
				update.setPositionUpdate(pu);
				cUsMgn = cUsMgn.add(pu.getChangeUsedMargin());
				cBal = cBal.add(pu.getChangeBalance());
			}
		}
		setInitialValues(portfolio, update)
			.setChangeBalance(cBal)
			.setChangeUsedMargin(cUsMgn)
			.setFinalProfitAndLoss(FMoney.ZERO_RUB2)
			.setFinalVarMargin(FMoney.ZERO_RUB5)
			.setFinalVarMarginClose(FMoney.ZERO_RUB5)
			.setFinalVarMarginInter(FMoney.ZERO_RUB5);
		return updateEquityAndFreeMargin(update);
	}
}
