package ru.prolib.aquila.exante.rh;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.UnsupportedMessageType;
import quickfix.field.ExecType;
import quickfix.field.LeavesQty;
import quickfix.field.OrdRejReason;
import quickfix.field.Text;
import quickfix.fix44.BusinessMessageReject;
import quickfix.fix44.Message;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.OrderField;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.exante.XAccountService;
import ru.prolib.aquila.exante.XResponseHandler;

public class OrderHandler implements XResponseHandler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(OrderHandler.class);
	}
	
	private final EditableOrder order;
	private final XAccountService accountService;
	
	public OrderHandler(EditableOrder order, XAccountService account_service) {
		this.order = order;
		this.accountService = account_service;
	}

	@Override
	public boolean onMessage(Message message) throws
		FieldNotFound,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType
	{
		logger.debug("Execution report of order: {} type {}", order.getID(), message.getString(ExecType.FIELD));
		if ( order.getStatus().isFinal() ) {
			logger.warn("Order already in final status: {} status {}", order.getID(), order.getStatus());
			return true;
		}
		
		CDecimal cur_vol = of(message.getString(LeavesQty.FIELD));
		DeltaUpdateBuilder builder = new DeltaUpdateBuilder()
				.withToken(OrderField.CURRENT_VOLUME, cur_vol);
		switch ( message.getChar(ExecType.FIELD) ) {
		case ExecType.ORDER_STATUS:
		case ExecType.PENDING_NEW:
		case ExecType.PENDING_CANCEL:
			break;
		case ExecType.NEW:
			builder.withToken(OrderField.STATUS, OrderStatus.ACTIVE);
			break;
		case ExecType.TRADE:
			if ( cur_vol.compareTo(CDecimalBD.ZERO) > 0 ) {
				builder.withToken(OrderField.STATUS, OrderStatus.ACTIVE);
			} else {
				builder.withToken(OrderField.STATUS, OrderStatus.FILLED)
					.withToken(OrderField.TIME_DONE, getCurrentTime());
			}
			accountService.rescheduleIfAllowed(1000L);
			break;
		case ExecType.CANCELED:
			builder.withToken(OrderField.STATUS, OrderStatus.CANCELLED)
				.withToken(OrderField.TIME_DONE, getCurrentTime());
			break;
		case ExecType.REJECTED:			
			String reason = "N/A";
			if ( message.isSetField(Text.FIELD) ) {
				reason = message.getString(Text.FIELD);
			}
			if ( message.isSetField(OrdRejReason.FIELD) ) {
				reason += String.format(" [%s]", message.getString(OrdRejReason.FIELD));
			}
			builder.withToken(OrderField.STATUS, OrderStatus.REJECTED)
				.withToken(OrderField.TIME_DONE, getCurrentTime())
				.withToken(OrderField.SYSTEM_MESSAGE, reason);
			break;
		}
		order.consume(builder.buildUpdate());
		return order.getStatus().isFinal();
	}

	@Override
	public void onReject(BusinessMessageReject message) throws
		FieldNotFound,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType
	{
		logger.debug("Order rejected: {}", message);
		reject("Reason unknown [FIXME]");

	}

	@Override
	public void close() {
		logger.debug("Handler of order closed: {}", order.getID());
	}
	
	private Instant getCurrentTime() {
		return order.getTerminal().getCurrentTime();
	}
	
	private void reject(String reason) {
		order.consume(new DeltaUpdateBuilder()
				.withToken(OrderField.STATUS, OrderStatus.REJECTED)
				.withToken(OrderField.SYSTEM_MESSAGE, reason)
				.withToken(OrderField.TIME_DONE, getCurrentTime())
				.buildUpdate());
	}

}
