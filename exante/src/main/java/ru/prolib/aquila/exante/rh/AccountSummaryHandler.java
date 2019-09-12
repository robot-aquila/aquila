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
import quickfix.field.CFICode;
import quickfix.field.LongQty;
import quickfix.field.MsgType;
import quickfix.field.SecurityID;
import quickfix.field.ShortQty;
//import quickfix.field.Symbol;
import quickfix.field.TotalNetValue;
import quickfix.fix44.BusinessMessageReject;
import quickfix.fix44.Message;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.PortfolioField;
import ru.prolib.aquila.core.BusinessEntities.PositionField;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.exante.XAccountSummaryMessages;
import ru.prolib.aquila.exante.XResponseHandler;
import ru.prolib.aquila.exante.XSymbolRepository;

public class AccountSummaryHandler implements XResponseHandler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(AccountSummaryHandler.class);
	}
	
	static class Entry {
		private final String security_id, symbol, cfi;
		public final CDecimal volume, pl, value, conv_pl, conv_value;
		
		Entry(String security_id,
			  String symbol,
			  String cfi,
			  CDecimal volume,
			  CDecimal pl,
			  CDecimal value,
			  CDecimal conv_pl,
			  CDecimal conv_value)
		{
			this.security_id = security_id;
			this.symbol = symbol;
			this.cfi = cfi;
			this.volume = volume;
			this.pl = pl;
			this.value = value;
			this.conv_pl = conv_pl;
			this.conv_value = conv_value;
		}

	}
	
	private final EditableTerminal terminal;
	private final XSymbolRepository symbols;
	private final LinkedHashMap<String, Entry> positions;
	private boolean firstResponse;
	private int expectedPositions, receivedPositions;
	private String account;
	private String currency;
	private CDecimal totalNetValue, usedMargin;

	public AccountSummaryHandler(
			EditableTerminal terminal,
			XSymbolRepository symbols)
	{
		this.terminal = terminal;
		this.symbols = symbols;
		this.positions = new LinkedHashMap<>();
		this.firstResponse = true;
		this.totalNetValue = of("-1.00");
		this.usedMargin = of("0.00");
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
		
		// TODO: Best place to check that this part is final and make it
		// allowed sending next requests to refresh positions. Some day.
		
		if ( message.isSetField(TotalNetValue.FIELD) ) {
			totalNetValue = of(message.getString(TotalNetValue.FIELD));
		}
		if ( message.isSetField(XAccountSummaryMessages.TAG_USED_MARGIN) ) {
			usedMargin = of(message.getString(XAccountSummaryMessages.TAG_USED_MARGIN));
		}
		
		CDecimal pl = ZERO, value = ZERO, c_pl = ZERO, c_value = ZERO;
		if ( message.isSetField(XAccountSummaryMessages.TAG_PROFIT_AND_LOSS) ) {
			pl = of(message.getString(XAccountSummaryMessages.TAG_PROFIT_AND_LOSS));
		}
		if ( message.isSetField(XAccountSummaryMessages.TAG_VALUE) ) {
			value = of(message.getString(XAccountSummaryMessages.TAG_VALUE));
		}
		
		if ( message.isSetField(XAccountSummaryMessages.TAG_CONVERTED_PROFIT_AND_LOSS) ) {
			c_pl = of(message.getString(XAccountSummaryMessages.TAG_CONVERTED_PROFIT_AND_LOSS));
		}
		if ( message.isSetField(XAccountSummaryMessages.TAG_CONVERTED_VALUE) ) {
			c_value = of(message.getString(XAccountSummaryMessages.TAG_CONVERTED_VALUE));
		}
		
		CDecimal lv = of(message.getString(LongQty.FIELD));
		CDecimal sv = of(message.getString(ShortQty.FIELD));
		CDecimal volume = ZERO;
		if ( lv.compareTo(ZERO) > 0 ) {
			volume = lv;
		} else if ( sv.compareTo(ZERO) > 0 ) {
			volume = sv.multiply(-1L);
		}
		String security_id = message.getString(SecurityID.FIELD);
		String symbol = message.getString(quickfix.field.Symbol.FIELD);
		String cfi = message.getString(CFICode.FIELD);
		positions.put(security_id, new Entry(security_id, symbol, cfi, volume, pl, value, c_pl, c_value));
		if ( logger.isDebugEnabled() ) {
			//Object args[] = { security_id, cfi, volume, pl, c_pl, value, c_value, totalNetValue, usedMargin };
			//logger.debug("Received: sec_id={} cfi={} vol={} pl={} cpl={} val={} cval={} net={} us.mgn={}", args);
			//logger.debug("From source message: {}", message);
		}
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
		CDecimal total_pl = of(0L);
		Symbol symbol = null;
		for ( Entry entry : positions.values() ) {
			if ( entry.security_id.equals(entry.symbol) && entry.cfi.equals("MRCXXX") ) {
				symbol = getCashSecurity(entry.symbol).getSymbol();
			} else {
				try {
					symbol = symbols.getSymbol(entry.security_id);
				} catch ( IllegalArgumentException e ) {
					logger.error("Error processing position entry: ", e);
					continue;
				}
			}
			EditablePosition position = portfolio.getEditablePosition(symbol);
			position.consume(new DeltaUpdateBuilder()
					.withTime(current_time)
					.withToken(PositionField.CURRENT_VOLUME, entry.volume)
					.withToken(PositionField.CURRENT_PRICE, entry.conv_value.withUnit(currency))
					.withToken(PositionField.OPEN_PRICE, of("-1").withUnit(currency))
					.withToken(PositionField.PROFIT_AND_LOSS, entry.conv_pl.withUnit(currency))
					.withToken(PositionField.USED_MARGIN, of("-1").withUnit(currency))
					.buildUpdate());
			total_pl = total_pl.add(entry.conv_pl);
			//logger.debug("Position of {} updated: {}", entry.security_id, position.getContents());
		}

		Security m_sec = getCashSecurity(currency);
		int m_scale = m_sec.getScale();
		total_pl = total_pl.withUnit(currency).withScale(m_scale);
		CDecimal equity = totalNetValue.withUnit(currency).withScale(m_scale);
		CDecimal balance = equity.subtract(total_pl).withScale(m_scale);
		CDecimal used_margin = usedMargin.withUnit(currency).withScale(m_scale);
		CDecimal free_margin = equity.subtract(used_margin).withScale(m_scale);

		portfolio.consume(new DeltaUpdateBuilder()
			.withTime(current_time)
			.withToken(PortfolioField.CURRENCY, currency)
			.withToken(PortfolioField.BALANCE, balance)
			.withToken(PortfolioField.EQUITY, equity)
			.withToken(PortfolioField.PROFIT_AND_LOSS, total_pl)
			.withToken(PortfolioField.USED_MARGIN, used_margin)
			.withToken(PortfolioField.FREE_MARGIN, free_margin)
			.buildUpdate());
		
	}
	
	private Symbol getCashSymbol(String currency_code) {
		return new Symbol(currency_code, null, currency_code, SymbolType.CURRENCY);
	}
	
	private Security getCashSecurity(String currency_code) {
		Symbol symbol = getCashSymbol(currency_code);
		if ( ! terminal.isSecurityExists(symbol) ) {
			return createCashSecurity(symbol);
		} else {
			return getSecurity(symbol);
		}
	}
	
	//private EditableSecurity createCashSecurity(String currency_code) {
	//	return createCashSecurity(getCashSymbol(currency_code));
	//}
	
	private EditableSecurity createCashSecurity(Symbol symbol) {
		// force create special security
		EditableSecurity security = terminal.getEditableSecurity(symbol);
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.DISPLAY_NAME, "CASH of " + symbol.getCurrencyCode())
				.withToken(SecurityField.TICK_SIZE, of("0.0001"))
				.buildUpdate()); 
		return security;		
	}
	
	private Security getSecurity(Symbol symbol) {
		try {
			return terminal.getSecurity(symbol);
		} catch ( SecurityException e ) {
			throw new IllegalStateException(e);
		}
	}

}
