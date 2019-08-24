package ru.prolib.aquila.exante.rh;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.UnsupportedMessageType;
import quickfix.field.LongQty;
import quickfix.field.MsgType;
import quickfix.field.SecurityID;
import quickfix.field.ShortQty;
import quickfix.field.TotalNetValue;
import quickfix.fix44.BusinessMessageReject;
import quickfix.fix44.Message;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.PortfolioField;
import ru.prolib.aquila.core.BusinessEntities.PositionField;
import ru.prolib.aquila.exante.XAccountSummaryMessages;
import ru.prolib.aquila.exante.XResponseHandler;
import ru.prolib.aquila.exante.XSymbolRepository;

public class AccountSummaryHandler implements XResponseHandler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(AccountSummaryHandler.class);
	}
	
	public static final String CURRENCY = "EUR";
	
	static class Entry {
		private final String security_id;
		private final CDecimal volume, pl, value;
		
		Entry(String security_id, CDecimal volume, CDecimal pl, CDecimal value) {
			this.security_id = security_id;
			this.volume = volume;
			this.pl = pl;
			this.value = value;
		}

	}
	
	private final EditableTerminal terminal;
	private final XSymbolRepository symbols;
	private final LinkedHashMap<String, Entry> positions;
	private boolean firstResponse;
	private int expectedPositions, receivedPositions;
	private String account;
	//private String currency;
	private CDecimal totalNetValue, usedMargin;

	public AccountSummaryHandler(EditableTerminal terminal, XSymbolRepository symbols) {
		this.terminal = terminal;
		this.symbols = symbols;
		this.positions = new LinkedHashMap<>();
		this.firstResponse = true;
		this.totalNetValue = of("-1.00", CURRENCY);
		this.usedMargin = of("0.00", CURRENCY);
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
			//currency = message.getString(XAccountSummaryMessages.TAG_ACCOUNT_CURRENCY);
			firstResponse = false;
		}
		receivedPositions ++;
		
		if ( message.isSetField(TotalNetValue.FIELD) ) {
			totalNetValue = of(message.getString(TotalNetValue.FIELD));
		}
		if ( message.isSetField(XAccountSummaryMessages.TAG_USED_MARGIN) ) {
			usedMargin = of(message.getString(XAccountSummaryMessages.TAG_USED_MARGIN));
		}
		
		CDecimal pl = of(0L), value = of(0L);
		if ( message.isSetField(XAccountSummaryMessages.TAG_PROFIT_AND_LOSS) ) {
			pl = of(message.getString(XAccountSummaryMessages.TAG_PROFIT_AND_LOSS));
		}
		if ( message.isSetField(XAccountSummaryMessages.TAG_VALUE) ) {
			value = of(message.getString(XAccountSummaryMessages.TAG_VALUE));
		}
		CDecimal lv = of(message.getString(LongQty.FIELD));
		CDecimal sv = of(message.getString(ShortQty.FIELD));
		CDecimal volume = of(0L);
		if ( lv.compareTo(ZERO) > 0 ) {
			volume = lv;
		} else if ( sv.compareTo(ZERO) > 0 ) {
			volume = sv.multiply(-1L);
		}
		String security_id = message.getString(SecurityID.FIELD);
		positions.put(security_id, new Entry(security_id, volume, pl, value));
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
		Instant current_time = terminal.getCurrentTime();
		EditablePortfolio portfolio = terminal.getEditablePortfolio(new Account(account));
		Entry main_entry = positions.remove(CURRENCY);
		CDecimal profit_and_loss = of(0L);
		for ( Entry entry : positions.values() ) {
			EditablePosition position = portfolio.getEditablePosition(symbols.getSymbol(entry.security_id));
			position.consume(new DeltaUpdateBuilder()
					.withTime(current_time)
					.withToken(PositionField.CURRENT_VOLUME, entry.volume)
					.withToken(PositionField.CURRENT_PRICE, of("-1"))
					.withToken(PositionField.OPEN_PRICE, of("-1"))
					.withToken(PositionField.PROFIT_AND_LOSS, entry.pl.withUnit(CURRENCY))
					.withToken(PositionField.USED_MARGIN, entry.value.withUnit(CURRENCY))
					.buildUpdate());
			profit_and_loss = profit_and_loss.add(entry.pl);
			logger.debug("Position of {} updated: {}", entry.security_id, position.getContents());
		}
		if ( main_entry != null ) {
			CDecimal balance = main_entry.value.withUnit(CURRENCY).withScale(2);
			CDecimal equity = totalNetValue.withUnit(CURRENCY).withScale(2);
			profit_and_loss = profit_and_loss.withUnit(CURRENCY).withScale(2);
			CDecimal used_margin = usedMargin.withUnit(CURRENCY).withScale(2);
			CDecimal free_margin = equity.subtract(used_margin);
			portfolio.consume(new DeltaUpdateBuilder()
				.withTime(current_time)
				.withToken(PortfolioField.CURRENCY, CURRENCY)
				.withToken(PortfolioField.BALANCE, balance)
				.withToken(PortfolioField.EQUITY, equity)
				.withToken(PortfolioField.PROFIT_AND_LOSS, profit_and_loss)
				.withToken(PortfolioField.USED_MARGIN, used_margin)
				.withToken(PortfolioField.FREE_MARGIN, free_margin)
				.buildUpdate());
		}
		
	}

}
