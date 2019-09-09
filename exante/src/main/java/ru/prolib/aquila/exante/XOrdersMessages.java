package ru.prolib.aquila.exante;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;

import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.ClOrdID;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Price;
import quickfix.field.SecurityID;
import quickfix.field.SecurityIDSource;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
import quickfix.fix44.BusinessMessageReject;
import quickfix.fix44.Message;
import quickfix.fix44.NewOrderSingle;
import quickfix.fix44.OrderCancelRequest;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderType;

public class XOrdersMessages {
	private final XMessageDispatcher dispatcher;
	private final XRepo repo;
	private final XSymbolRepository symbols;
	
	XOrdersMessages(XSymbolRepository symbols, XMessageDispatcher dispatcher, XRepo repo) {
		this.symbols = symbols;
		this.dispatcher = dispatcher;
		this.repo = repo;
		
		repo.setDebug(true);
	}
	
	public XOrdersMessages(XSymbolRepository symbols, XMessageDispatcher dispatcher) {
		this(symbols, dispatcher, new XRepo());
	}
	
	public XOrdersMessages(XSymbolRepository symbols, SessionID session_id) {
		this(symbols, new XMessageDispatcher(session_id));
	}
	
	private Double toDouble(CDecimal value) {
		return Double.valueOf(value.toString());
	}
	
	public void newOrderSingle(Order order, XResponseHandler handler) {
		String request_id = repo.newRequest(Long.toUnsignedString(order.getID()), handler);
		NewOrderSingle request = new NewOrderSingle();
		request.setField(new ClOrdID(request_id));
		request.setField(new Side(order.getAction() == OrderAction.BUY ? Side.BUY : Side.SELL));
		request.setField(new TransactTime(LocalDateTime.ofInstant(order.getTime(), ZoneOffset.UTC)));
		if ( order.getType() == OrderType.MKT ) {
			request.setField(new OrdType(OrdType.MARKET));
		} else
		if ( order.getType() == OrderType.LMT ) {
			request.setField(new OrdType(OrdType.LIMIT));
			request.setField(new Price(toDouble(order.getPrice())));
		} else {
			throw new IllegalArgumentException("Unsupported order type: " + order.getType());
		}
		request.setField(new TimeInForce(TimeInForce.DAY));
		request.setField(new OrderQty(toDouble(order.getInitialVolume())));
		
		XSymbol x_sym = symbols.getBySymbol(order.getSymbol());
		request.setField(new SecurityIDSource("111"));
		request.setField(new SecurityID(x_sym.getSecurityID()));
		request.setField(new Symbol(x_sym.getSecurityID()));
		dispatcher.send(request);
	}
	
	public void cancelOrder(Order order, XResponseHandler handler) {
		String str_order_id = Long.toUnsignedString(order.getID());
		String request_id = repo.newRequest(
				str_order_id + "-C-" + ThreadLocalRandom.current().nextInt(100, 1000),
				handler
			);
		OrderCancelRequest request = new OrderCancelRequest();
		request.setField(new ClOrdID(request_id));
		request.setField(new OrigClOrdID(Long.toUnsignedString(order.getID())));
		request.setField(new Side(order.getAction() == OrderAction.BUY ? Side.BUY : Side.SELL));
		request.setField(new TransactTime(LocalDateTime.ofInstant(order.getTime(), ZoneOffset.UTC)));
		request.setField(new OrderQty(toDouble(order.getInitialVolume())));
		
		XSymbol x_sym = symbols.getBySymbol(order.getSymbol());
		request.setField(new SecurityIDSource("111"));
		request.setField(new SecurityID(x_sym.getSecurityID()));
		request.setField(new Symbol(x_sym.getSecurityID()));
		dispatcher.send(request);
	}
	
	public void approve(NewOrderSingle message) throws
		IllegalStateException,
		FieldNotFound,
		DoNotSend
	{
		repo.approve(message.getClOrdID().getValue(), message);
	}
	
	public void rejected(BusinessMessageReject message) throws
		FieldNotFound,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType,
		IllegalStateException
	{
		repo.rejected(message);
	}

	public void response(Message message) throws
		FieldNotFound,
		IllegalStateException,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType
	{
		repo.response(message.getField(new ClOrdID()).getValue(), message);
	}

}
