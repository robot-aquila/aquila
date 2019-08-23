package ru.prolib.aquila.exante.rh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.UnsupportedMessageType;
import quickfix.field.CFICode;
import quickfix.field.LongQty;
import quickfix.field.MsgType;
import quickfix.field.SecurityExchange;
import quickfix.field.SecurityID;
import quickfix.field.ShortQty;
import quickfix.field.TotalNetValue;
import quickfix.fix44.BusinessMessageReject;
import quickfix.fix44.Message;
import ru.prolib.aquila.exante.XAccountSummaryMessages;
import ru.prolib.aquila.exante.XResponseHandler;

public class AccountSummaryTestHandler implements XResponseHandler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(AccountSummaryTestHandler.class);
	}
	
	private boolean firstResponse;
	private int expectedPositions, receivedPositions;
	
	private String account, currency, totalNet, usedMargin;
	
	public AccountSummaryTestHandler() {
		this.firstResponse = true;
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
		case XAccountSummaryMessages.MSGTYPE_SUMMARY_RESPONSE:
			processResponse(message);
			return receivedPositions >= expectedPositions;
		case XAccountSummaryMessages.MSGTYPE_SUMMARY_REJECT:
			String args[] = {
					message.getString(XAccountSummaryMessages.TAG_ACCOUNT_REQUEST_ID),
					message.getString(1), // account code
					message.getString(58), // reject reason
					message.getString(XAccountSummaryMessages.TAG_REJECT_REASON)
			};
			logger.error("Request rejected: id={} account={} text={} rej_reason={}", args);
			return true;
		default:
			throw new IllegalArgumentException("Unknown message type: " + msg_type.getValue());
		}
	}
	
	private void processResponse(Message message) throws FieldNotFound {
		if ( firstResponse ) {
			expectedPositions = message.getInt(XAccountSummaryMessages.TAG_NUM_REPORTS);
			account = message.getString(1);
			currency = message.getString(XAccountSummaryMessages.TAG_ACCOUNT_CURRENCY);
			firstResponse = false;
		}
		receivedPositions ++;
		
		if ( message.isSetField(TotalNetValue.FIELD) ) {
			totalNet = message.getString(TotalNetValue.FIELD);
		}
		if ( message.isSetField(XAccountSummaryMessages.TAG_USED_MARGIN) ) {
			usedMargin = message.getString(XAccountSummaryMessages.TAG_USED_MARGIN);
		}
		String na = "N/A";
		String pnl = na, val = na, c_pnl = na, c_val = na, exchange_id = na;
		if ( message.isSetField(XAccountSummaryMessages.TAG_PROFIT_AND_LOSS) ) {
			pnl = message.getString(XAccountSummaryMessages.TAG_PROFIT_AND_LOSS);
		}
		if ( message.isSetField(XAccountSummaryMessages.TAG_VALUE) ) {
			val = message.getString(XAccountSummaryMessages.TAG_VALUE);
		}
		if ( message.isSetField(XAccountSummaryMessages.TAG_CONVERTED_PROFIT_AND_LOSS) ) {
			c_pnl = message.getString(XAccountSummaryMessages.TAG_CONVERTED_PROFIT_AND_LOSS);
		}
		if ( message.isSetField(XAccountSummaryMessages.TAG_CONVERTED_VALUE) ) {
			c_val = message.getString(XAccountSummaryMessages.TAG_CONVERTED_VALUE);
		}
		if ( message.isSetField(new SecurityExchange()) ) {
			exchange_id = message.getField(new SecurityExchange()).getValue();
		}
		
		Object args[] = {
				message.getField(new SecurityID()).getValue(),
				exchange_id,
				message.getField(new LongQty()).getValue(),
				message.getField(new ShortQty()).getValue(),
				pnl,
				val,
				c_pnl,
				c_val,
				message.getString(CFICode.FIELD)
		};
		logger.debug("security_id={} exchange={} long={} short={} pnl={} val={} c.pnl={} c.val={} cfi={}", args);	
	}

	@Override
	public void onReject(BusinessMessageReject message) throws
		FieldNotFound,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType
	{
		
	}

	@Override
	public void close() {
		String args[] = {
				account,
				currency,
				totalNet,
				usedMargin
		};
		logger.debug("request done: account={} currency={} total_net={} used_margin={}", args);
	}

}
