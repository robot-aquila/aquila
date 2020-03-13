package ru.prolib.aquila.qforts.impl;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.OrderExecution;
import ru.prolib.aquila.core.BusinessEntities.OrderExecutionImpl;
import ru.prolib.aquila.core.BusinessEntities.OrderField;
import ru.prolib.aquila.core.BusinessEntities.PortfolioField;
import ru.prolib.aquila.core.BusinessEntities.PositionField;
import ru.prolib.aquila.core.concurrency.Lockable;
import ru.prolib.aquila.core.concurrency.MultilockBuilderBE;

public class QFAssembler {
	
	public Lockable createMultilock(MultilockBuilderBE builder) {
		return builder.buildLock();
	}
	
	public void update(EditableOrder order, QFOrderExecutionUpdate update, long executionID) {
		OrderExecution execution = new OrderExecutionImpl(order.getTerminal(),
				executionID, update.getExecutionExternalID(), update.getExecutionSymbol(),
				update.getExecutionAction(), update.getExecutionOrderID(),
				update.getExecutionTime(), update.getExecutionPrice(),
				update.getExecutionVolume(), update.getExecutionValue());
		try {
			order.addExecution(execution);
		} catch ( OrderException e ) {
			throw new IllegalStateException("Execution already exists: " + executionID);
		}
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.CURRENT_VOLUME, update.getFinalCurrentVolume())
			.withToken(OrderField.EXECUTED_VALUE, update.getFinalExecutedValue())
			.withToken(OrderField.TIME_DONE, update.getFinalizationTime())
			.withToken(OrderField.STATUS, update.getFinalStatus())
			.buildUpdate());		
		order.fireExecution(execution);
	}
	
	public void update(EditableOrder order, QFOrderStatusUpdate update) {
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.TIME_DONE, update.getFinalizationTime())
			.withToken(OrderField.STATUS, update.getFinalStatus())
			.withToken(OrderField.SYSTEM_MESSAGE, update.getSystemMessage())
			.buildUpdate());
	}
	
	private void update(EditablePosition position, QFPositionChangeUpdate update) {
		position.consume(new DeltaUpdateBuilder()
			.withToken(PositionField.CURRENT_PRICE, update.getFinalCurrentPrice())
			.withToken(PositionField.CURRENT_VOLUME, update.getFinalVolume())
			.withToken(PositionField.OPEN_PRICE, update.getFinalOpenPrice())
			.withToken(PositionField.PROFIT_AND_LOSS, update.getFinalProfitAndLoss())
			.withToken(PositionField.USED_MARGIN, update.getFinalUsedMargin())
			.withToken(QFPositionField.QF_VAR_MARGIN, update.getFinalVarMargin())
			.withToken(QFPositionField.QF_VAR_MARGIN_CLOSE, update.getFinalVarMarginClose())
			.withToken(QFPositionField.QF_VAR_MARGIN_INTER, update.getFinalVarMarginInter())
			.withToken(QFPositionField.QF_TICK_VALUE, update.getFinalTickValue())
			.buildUpdate());
	}
	
	public void update(EditablePortfolio portfolio, QFPortfolioChangeUpdate update) {
		for ( QFPositionChangeUpdate pu : update.getPositionUpdates() ) {
			update(portfolio.getEditablePosition(pu.getSymbol()), pu);
		}
		portfolio.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.CURRENCY, update.getFinalBalance().getUnit())
			.withToken(PortfolioField.BALANCE, update.getFinalBalance())
			.withToken(PortfolioField.EQUITY, update.getFinalEquity())
			.withToken(PortfolioField.FREE_MARGIN, update.getFinalFreeMargin())
			.withToken(PortfolioField.PROFIT_AND_LOSS, update.getFinalProfitAndLoss())
			.withToken(PortfolioField.USED_MARGIN, update.getFinalUsedMargin())
			.withToken(QFPortfolioField.QF_VAR_MARGIN, update.getFinalVarMargin())
			.withToken(QFPortfolioField.QF_VAR_MARGIN_CLOSE, update.getFinalVarMarginClose())
			.withToken(QFPortfolioField.QF_VAR_MARGIN_INTER, update.getFinalVarMarginInter())
			.buildUpdate());
	}

}
