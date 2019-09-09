package ru.prolib.aquila.exante.rh;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.UnsupportedMessageType;
import quickfix.field.ExecType;
import quickfix.field.MsgType;
import quickfix.fix44.BusinessMessageReject;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.Message;
import quickfix.fix44.OrderCancelReject;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderField;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.exante.XResponseHandler;

public class OrderCancelHandler implements XResponseHandler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(OrderCancelHandler.class);
	}
	
	private final Order order;
	
	public OrderCancelHandler(Order order) {
		this.order = order;
	}

	@Override
	public boolean onMessage(Message message) throws
		FieldNotFound,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType
	{
		MsgType msg_type = new MsgType();
		message.getHeader().getField(msg_type);
		switch ( msg_type.getValue() ) {
		case ExecutionReport.MSGTYPE:
			onExecutionReport((ExecutionReport) message);
			break;
		case OrderCancelReject.MSGTYPE:
			onOrderCancelReject((OrderCancelReject) message);
			break;
		default:
			throw new IllegalArgumentException("Unsupported message type: " + msg_type.getValue());
		}
		return true;
	}
	
	private Instant getCurrentTime() {
		return order.getTerminal().getCurrentTime();
	}
	
	private void onExecutionReport(ExecutionReport message) throws
		FieldNotFound
	{
		logger.debug("Execution report of order: {} type {}", order.getID(), message.getString(ExecType.FIELD));
		if ( order.getStatus().isFinal() ) {
			logger.warn("Order already in final status: {} status {}", order.getID(), order.getStatus());
			return;
		}
		DeltaUpdateBuilder builder = new DeltaUpdateBuilder();
		switch ( message.getChar(ExecType.FIELD) ) {
		case ExecType.CANCELED:
			order.consume(builder.withToken(OrderField.STATUS, OrderStatus.CANCELLED)
				.withToken(OrderField.TIME_DONE, getCurrentTime())
				.buildUpdate());
			break;
		}
	}
	
	private void onOrderCancelReject(OrderCancelReject message) {
		logger.warn("Order cancel reject: {}", message);
	}

	@Override
	public void onReject(BusinessMessageReject message) throws
		FieldNotFound,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType
	{
		logger.error("Order cancel rejected: {}", message);
	}

	@Override
	public void close() {
		
	}

}
