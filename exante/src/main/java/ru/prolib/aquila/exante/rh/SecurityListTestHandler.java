package ru.prolib.aquila.exante.rh;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.UnsupportedMessageType;
import quickfix.field.NoRelatedSym;
import quickfix.field.SecurityID;
import quickfix.field.SecurityRequestResult;
import quickfix.field.TotNoRelatedSym;
import quickfix.fix44.BusinessMessageReject;
import quickfix.fix44.Message;
import quickfix.fix44.SecurityList;
import ru.prolib.aquila.exante.XResponseHandler;

public class SecurityListTestHandler implements XResponseHandler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SecurityListTestHandler.class);
	}
	
	private final Set<String> symbols = new HashSet<>();
	private boolean first_response = true;
	private long expected_total_symbols = 0, responses = 0;

	@Override
	public synchronized boolean onMessage(Message message) throws
		FieldNotFound,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType
	{
		//logger.debug("Class: {}", message.getClass());
		//System.out.println(message);
		SecurityList response = (SecurityList) message;
		int result = response.getSecurityRequestResult().getValue();
		if ( result != SecurityRequestResult.VALID_REQUEST ) {
			return true;
		}
		
		//System.out.print(".");
		responses ++;
		TotNoRelatedSym total_count = new TotNoRelatedSym();
		NoRelatedSym count = new NoRelatedSym();
		boolean has_total = false;
		if ( message.isSetField(TotNoRelatedSym.FIELD) ) {
			message.getField(total_count);
			has_total = true;
		}
		message.getField(count);
		SecurityList.NoRelatedSym symbol_group = new SecurityList.NoRelatedSym();			
		if ( first_response && has_total ) {
			first_response = false;
			expected_total_symbols = total_count.getValue();
			logger.debug("Expected total: {}", expected_total_symbols);
		}
		
		//logger.debug("Local count: {}", count.getValue());
		for ( int i = 1; i <= count.getValue(); i ++ ) {
			message.getGroup(i, symbol_group);
			SecurityID exante_id = symbol_group.getSecurityID();
			//logger.debug(exante_id.toString());
			symbols.add(exante_id.toString());
		}
		
		//logger.debug("SRR: {} CNT: {}", r.getValue(), count.getValue());
		
		if ( expected_total_symbols > symbols.size() ) {
			return false;
		}
		
		logger.debug("expected count = actual count!!!");
		logger.debug("total chunks: {}", responses);
		logger.debug("total symbols: {}", expected_total_symbols);
		//logger.debug("symbols: {}", symbols);
		
		List<String> x = new ArrayList<>();
		int xx = 0;
		for ( String sym : symbols ) {
			x.add(sym);
			xx ++;
			if ( xx >= 50 ) {
				break;
			}
		}
		System.out.println(x);
		//logger.debug("security list response {}", session_id);
		return true;
	}

	@Override
	public void onReject(BusinessMessageReject message) throws
		FieldNotFound,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType
	{
		// TODO Auto-generated method stub
		logger.error("Rejected: {}", message);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		logger.debug("closed");
	}

}
