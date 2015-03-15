package ru.prolib.aquila.t2q.jqt;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.JQTrans.JQTransHandler;
import ru.prolib.aquila.JQTrans.JQTransServer;
import ru.prolib.aquila.JQTrans.QTransOrderStatus;
import ru.prolib.aquila.JQTrans.QTransTradeStatus;
import ru.prolib.aquila.t2q.T2QConnStatus;
import ru.prolib.aquila.t2q.T2QHandler;
import ru.prolib.aquila.t2q.T2QOrder;
import ru.prolib.aquila.t2q.T2QTrade;
import ru.prolib.aquila.t2q.T2QTransStatus;

/**
 * Адаптер {@link ru.prolib.aquila.JQTrans.JQTransHandler JQTransHandler}
 * к обработчику типа {@link ru.prolib.aquila.t2q.T2QHandler T2QHandler}.
 * <p>
 * Информация о статусе подключения дублируется в лог в режиме INFO.
 * Информация о транзакциях дуюлируется в лог в режиме DEBUG.
 * <p>
 * 2013-01-31<br>
 * $Id: JQTHandler.java 576 2013-03-14 12:07:25Z whirlwind $
 */
public class JQTHandler extends JQTransHandler {
	private static final Logger logger;
	private final T2QHandler handler;
	private JQTransServer server;
	
	static {
		logger = LoggerFactory.getLogger(JQTHandler.class);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param handler типовой обработчик
	 */
	public JQTHandler(T2QHandler handler) {
		super();
		this.handler = handler;
	}
	
	/**
	 * Получить типовой обработчик.
	 * <p>
	 * @return типовой обработчик
	 */
	public T2QHandler getCommonHandler() {
		return handler;
	}
	
	/**
	 * Установить экземпляр сервера.
	 * <p>
	 * @param server сервер
	 */
	public synchronized void setServer(JQTransServer server) {
		this.server = server;
	}
	
	/**
	 * Получить экземпляр сервера.
	 * <p>
	 * @return сервер
	 */
	public synchronized JQTransServer getServer() {
		return server;
	}
	
	@Override
	public void OnConnectionStatus(int connEvent, int errCode, String errMsg) {
		Object args[] = { connEvent, errCode, errMsg };
		logger.info("OnConnectionStatus: evt={}, errCode={}, errMsg={}", args);
		T2QConnStatus status = null;
		switch ( connEvent ) {
		case  8: status = T2QConnStatus.QUIK_CONN; break;
		case  9: status = T2QConnStatus.QUIK_DISC; break;
		case 10: status = T2QConnStatus.DLL_CONN; break;
		case 11: status = T2QConnStatus.DLL_DISC; break;
		}
		handler.OnConnStatus(status);
	}
	
	@Override
	public void OnTransactionReply(int resultCode, int errCode, int replyCode,
			long transId, long orderId, String replyMsg)
	{
		//if ( logger.isDebugEnabled() ) {
		//	Object args[] = { resultCode, errCode, replyCode,
		//			transId, orderId, replyMsg };
		//	logger.debug("OnTransactionReply: resultCode={}, errCode={}, "
		//		+ "replyCode={}, transId={}, orderId={}, replyMsg={}", args);
		//}
		T2QTransStatus status = T2QTransStatus.ERR_UNK;
		if ( resultCode != 0 ) {
			status = T2QTransStatus.ERR_NOK;
		} else {
			switch ( replyCode ) {
			case  0: status = T2QTransStatus.SENT; break;
			case  1: status = T2QTransStatus.RECV; break;
			case  2: status = T2QTransStatus.ERR_CON; break;
			case  3: status = T2QTransStatus.DONE; break;
			case  4: status = T2QTransStatus.ERR_TSYS; break;
			case  5: status = T2QTransStatus.ERR_REJ; break;
			case  6: status = T2QTransStatus.ERR_LIMIT; break;
			case 10: status = T2QTransStatus.ERR_UNSUPPORTED; break;
			case 11: status = T2QTransStatus.ERR_AUTH; break;
			case 12: status = T2QTransStatus.ERR_TIMEOUT; break;
			case 13: status = T2QTransStatus.ERR_CROSS; break;
			}
		}
		handler.OnTransReply(status, transId,
				orderId == 0 ? null : orderId,
				replyMsg);
	}
	
	@Override
	public void OnOrderStatus(QTransOrderStatus args) {
		if ( server == null ) {
			logger.error("Server instance not specified");
			return;
		}
		long orderDescriptor = args.getOrderDescriptor();
		handler.OnOrderStatus(new T2QOrder(args.getMode(),
				args.getTransId(), args.getOrderId(),
				args.getClassCode(), args.getSecCode(), args.getPrice(),
				args.getBalance(), args.getValue(), args.getIsSell(),
				args.getStatus(),
				server.getOrderFirmId(orderDescriptor),
				server.getOrderClientCode(orderDescriptor),
				server.getOrderAccount(orderDescriptor),
				server.getOrderQty(orderDescriptor),
				server.getOrderDate(orderDescriptor),
				server.getOrderTime(orderDescriptor),
				server.getOrderActivationTime(orderDescriptor),
				server.getOrderWithdrawTime(orderDescriptor),
				server.getOrderExpiry(orderDescriptor),
				server.getOrderAccruedInt(orderDescriptor),
				server.getOrderYield(orderDescriptor),
				server.getOrderUid(orderDescriptor),
				server.getOrderUserId(orderDescriptor),
				server.getOrderBrokerRef(orderDescriptor)));
	}
	
	@Override
	public void OnTradeStatus(QTransTradeStatus args) {
		if ( server == null ) {
			logger.error("Server instance not specified");
			return;
		}
		long tradeDescriptor = args.getTradeDescriptor();
		handler.OnTradeStatus(new T2QTrade(args.getMode(),
				args.getId(), args.getOrderId(),
				args.getClassCode(), args.getSecCode(), args.getPrice(),
				args.getQty(), args.getValue(), args.getIsSell(),
				server.getTradeDate(tradeDescriptor),
				server.getTradeSettleDate(tradeDescriptor),
				server.getTradeTime(tradeDescriptor),
				server.getTradeIsMarginal(tradeDescriptor),
				server.getTradeAccruedInt(tradeDescriptor),
				server.getTradeYield(tradeDescriptor),
				server.getTradeTsCommission(tradeDescriptor),
				server.getTradeClearingCenterCommission(tradeDescriptor),
				server.getTradeExchangeCommission(tradeDescriptor),
				server.getTradeTradingSystemCommission(tradeDescriptor),
				server.getTradePrice2(tradeDescriptor),
				server.getTradeRepoRate(tradeDescriptor),
				server.getTradeRepoValue(tradeDescriptor),
				server.getTradeRepo2Value(tradeDescriptor),
				server.getTradeAccruedInt2(tradeDescriptor),
				server.getTradeRepoTerm(tradeDescriptor),
				server.getTradeStartDiscount(tradeDescriptor),
				server.getTradeLowerDiscount(tradeDescriptor),
				server.getTradeUpperDiscount(tradeDescriptor),
				server.getTradeBlockSecurities(tradeDescriptor),
				server.getTradeCurrency(tradeDescriptor),
				server.getTradeSettleCurrency(tradeDescriptor),
				server.getTradeSettleCode(tradeDescriptor),
				server.getTradeAccount(tradeDescriptor),
				server.getTradeBrokerRef(tradeDescriptor),
				server.getTradeClientCode(tradeDescriptor),
				server.getTradeUserId(tradeDescriptor),
				server.getTradeFirmId(tradeDescriptor),
				server.getTradePartnerFirmId(tradeDescriptor),
				server.getTradeExchangeCode(tradeDescriptor),
				server.getTradeStationId(tradeDescriptor)));
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == JQTHandler.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		JQTHandler o = (JQTHandler) other;
		return new EqualsBuilder()
			.append(handler, o.handler)
			.append(server, o.server)
			.isEquals();
	}

}
