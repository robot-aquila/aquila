package ru.prolib.aquila.exante.rh;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.UnsupportedMessageType;
import quickfix.field.SecurityRequestResult;
import quickfix.fix44.BusinessMessageReject;
import quickfix.fix44.Message;
import quickfix.fix44.SecurityList;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.exante.XResponseHandler;
import ru.prolib.aquila.exante.XSymbol;
import ru.prolib.aquila.exante.XSymbolRepository;

public class SecurityListHandler implements XResponseHandler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SecurityListHandler.class);
	}
	
	private final EditableTerminal terminal;
	private final XSymbolRepository symbols;
	private boolean firstResponse;
	private int expectedSymbols, receivedSymbols;
	
	public SecurityListHandler(EditableTerminal terminal, XSymbolRepository symbols) {
		this.terminal = terminal;
		this.symbols = symbols;
		this.firstResponse = true;
		this.expectedSymbols = 0;
		this.receivedSymbols = 0;
	}

	@Override
	public boolean onMessage(Message message) throws
		FieldNotFound,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType
	{
		SecurityList response = (SecurityList) message;
		int result = response.getSecurityRequestResult().getValue();
		if ( result != SecurityRequestResult.VALID_REQUEST ) {
			logger.error("Not valid request: {}", response);
			return true;
		}
		if ( firstResponse ) {
			expectedSymbols = response.getTotNoRelatedSym().getValue();
			firstResponse = false;
		}
		if ( response.isSetNoRelatedSym() ) {
			int symbol_count = response.getNoRelatedSym().getValue();
			receivedSymbols += symbol_count;
			SecurityList.NoRelatedSym symbol_group = new SecurityList.NoRelatedSym();
			Instant current_time = terminal.getCurrentTime();
			for ( int i = 1; i <= symbol_count; i ++ ) {
				response.getGroup(i, symbol_group);
				processSymbol(symbol_group, current_time);
			}
		}
		return receivedSymbols >= expectedSymbols;
	}

	@Override
	public void onReject(BusinessMessageReject message) throws
		FieldNotFound,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType
	{
		logger.error("Request rejected: {}", message);
	}

	@Override
	public void close() {
		
	}

	private void processSymbol(SecurityList.NoRelatedSym symbol_group, Instant current_time) throws FieldNotFound {
		String x_symbol = symbol_group.getSymbol().getValue();
		String x_security_id = symbol_group.getSecurityID().getValue();
		String x_exchange_id = null;
		String x_cfi_code = symbol_group.getCFICode().getValue();
		String x_currency = null;

		if ( symbol_group.isSetSecurityExchange() ) {
			x_exchange_id = symbol_group.getSecurityExchange().getValue();
		}
		if ( symbol_group.isSetCurrency() ) {
			x_currency = symbol_group.getCurrency().getValue();
		}
		
		SymbolType a_type = null;
		switch ( x_cfi_code ) {
		case "EXXXXX":
			if ( x_exchange_id == null
			  || x_currency == null
			  || ! x_security_id.equals(x_symbol + "." + x_exchange_id) )
			{
				return;
			}
			a_type = SymbolType.STOCK;
			break;
		case "MRCXXX":
			// we need this (FOREX) to process positions
			a_type = SymbolType.CURRENCY;
			break;
		default:
			return;	
		}
		
		Symbol a_symbol = new Symbol(x_symbol, x_exchange_id, x_currency, a_type);
		DeltaUpdateBuilder builder = new DeltaUpdateBuilder()
				.withTime(current_time)
				.withToken(SecurityField.DISPLAY_NAME, x_symbol)
				.withToken(SecurityField.LOT_SIZE, of(1L))
				.withToken(SecurityField.TICK_SIZE, of("0.01"))
				.withToken(SecurityField.TICK_VALUE, ofUSD2("-1"))
				.withToken(SecurityField.SETTLEMENT_PRICE, of("-1"));
		if ( symbol_group.isSetNoInstrAttrib() ) {
			int attr_count = symbol_group.getNoInstrAttrib().getValue();
			SecurityList.NoRelatedSym.NoInstrAttrib attr_group = new SecurityList.NoRelatedSym.NoInstrAttrib();
			for ( int i = 1; i <= attr_count; i ++ ) {
				symbol_group.getGroup(i, attr_group);
				String attr_val = attr_group.getInstrAttribValue().getValue();
				switch ( attr_group.getInstrAttribType().getValue() ) {
				case 500:
				case 503:
					try {
						builder.withToken(SecurityField.TICK_SIZE, of(attr_val));
					} catch ( Throwable t ) {
						logger.error("Conversion error: ", t);
					}
					break;
				case 504:
					try {
						builder.withToken(SecurityField.LOT_SIZE, of(attr_val));
					} catch ( Throwable t ) {
						logger.error("Conversion error: ", t);
					}
				}
			}
		}
		symbols.register(a_symbol, new XSymbol(x_symbol, x_security_id, x_exchange_id, x_cfi_code, x_currency));
		EditableSecurity security = terminal.getEditableSecurity(a_symbol);
		security.consume(builder.buildUpdate());
	}
	
}
