package ru.prolib.aquila.transaq.xml;

import static ru.prolib.aquila.transaq.entity.SecurityUpdate1.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.CandleKind;
import ru.prolib.aquila.transaq.entity.Market;
import ru.prolib.aquila.transaq.entity.SecField;
import ru.prolib.aquila.transaq.entity.SecType;
import ru.prolib.aquila.transaq.entity.SecurityUpdate1;
import ru.prolib.aquila.transaq.entity.SecurityBoardParams;

public class Parser {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(Parser.class);
	}
	
	/**
	 * Check value is not null or throw an exception.
	 * <p>
	 * @param value - value to check
	 * @param attr_name - property name to use for exception message
	 * @throws XMLStreamException value is null
	 */
	void checkNotNull(Object value, String attr_name) throws XMLStreamException {
		if ( value == null ) {
			throw new XMLStreamException("Value undefined: " + attr_name);
		}
	}
	
	private String getAttribute(XMLStreamReader reader, String attr_id) throws XMLStreamException {
		 return reader.getAttributeValue(null, attr_id);
	}
	
	private int getAttributeInt(XMLStreamReader reader, String attr_id) throws XMLStreamException {
		String str_val = getAttribute(reader, attr_id);
		try {
			return str_val == null ? null : Integer.parseInt(str_val);
		} catch ( NumberFormatException e ) {
			throw new XMLStreamException("Cannot parse int: " + str_val, e);
		}
	}
	
	private boolean getAttributeBool(XMLStreamReader reader, String attr_id, String true_string)
			throws XMLStreamException
	{
		String str_val = getAttribute(reader, attr_id);
		return str_val.equals(true_string);
	}
	
	private boolean getAttributeBool(XMLStreamReader reader, String attr_id) throws XMLStreamException {
		return getAttributeBool(reader, attr_id, "true");
	}
	
	private CDecimal readDecimal(XMLStreamReader reader) throws XMLStreamException {
		return CDecimalBD.of(readCharacters(reader));
	}
	
	private String readCharacters(XMLStreamReader reader) throws XMLStreamException {
		StringBuilder result = new StringBuilder();
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
				case XMLStreamReader.CHARACTERS:
				case XMLStreamReader.CDATA:
					result.append(reader.getText());
					break;
				case XMLStreamReader.END_ELEMENT:
					return result.toString();
			}
		}
		throw new XMLStreamException("Premature end of file");
    }
	
	private int readInt(XMLStreamReader reader) throws XMLStreamException {
		String str_integer = readCharacters(reader);
		try {
			return Integer.parseInt(str_integer);
		} catch ( NumberFormatException e ) {
			throw new XMLStreamException("Cannot parse int: " + str_integer, e);
		}
	}
	
	private Market readMarket(XMLStreamReader reader) throws XMLStreamException {
		String str_id = reader.getAttributeValue(null, "id");
		String str_name = readCharacters(reader).trim();
		if ( str_name.length() == 0 ) {
			str_name = str_id + " [N/A]";
		}
		try {
			return new Market(Integer.parseInt(str_id), str_name);
		} catch ( NumberFormatException e ) {
			throw new XMLStreamException("Cannot parse ID: " + str_id, e);
		}
	}
	
	public List<Market> readMarkets(XMLStreamReader reader) throws XMLStreamException {
		List<Market> result = new ArrayList<>();
		while ( reader.hasNext() ) {
        	switch ( reader.next() ) {
        	case XMLStreamReader.START_ELEMENT:
        		switch ( reader.getLocalName() ) {
        		case "market":
        			result.add(readMarket(reader));
        			break;
        		}
        		break;
        	case XMLStreamReader.END_ELEMENT:
        	case XMLStreamReader.END_DOCUMENT:
        		return result;
        	}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	private Board readBoard(XMLStreamReader reader) throws XMLStreamException {
		String str_code = reader.getAttributeValue(null, "id");
		String str_name = null;
		int market = -1, type = -1;
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "name":
					str_name = readCharacters(reader);
					break;
				case "market":
					market = readInt(reader);
					break;
				case "type":
					type = readInt(reader);
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				if ( str_name == null || str_name.length() == 0 ) {
					str_name = str_code + " [N/A]";
				}
				return new Board(str_code, str_name, market, type);
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public List<Board> readBoards(XMLStreamReader reader) throws XMLStreamException {
		List<Board> result = new ArrayList<>();
		while ( reader.hasNext() ) {
        	switch ( reader.next() ) {
        	case XMLStreamReader.START_ELEMENT:
        		switch ( reader.getLocalName() ) {
        		case "board":
        			result.add(readBoard(reader));
        			break;
        		}
        		break;
        	case XMLStreamReader.END_ELEMENT:
        	case XMLStreamReader.END_DOCUMENT:
        		return result;
        	}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	private CandleKind readCandleKind(XMLStreamReader reader) throws XMLStreamException {
		int id = -1, period = -1;
		String name = null;
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "id":
					id = readInt(reader);
					break;
				case "period":
					period = readInt(reader);
					break;
				case "name":
					name = readCharacters(reader);
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				if ( name == null || name.length() == 0 ) {
					name = id + " [N/A]";
				}
				return new CandleKind(id, period, name);
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public List<CandleKind> readCandleKinds(XMLStreamReader reader) throws XMLStreamException {
		List<CandleKind> result = new ArrayList<>();
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "kind":
					result.add(readCandleKind(reader));
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
			case XMLStreamReader.END_DOCUMENT:
				return result;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	private DeltaUpdate readSecurity(XMLStreamReader reader) throws XMLStreamException {
		DeltaUpdateBuilder builder = new DeltaUpdateBuilder()
				.withToken(SecField.SECID, getAttributeInt(reader, "secid"))
				.withToken(SecField.ACTIVE, getAttributeBool(reader, "active"));
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "seccode":
					builder.withToken(SecField.SECCODE, readCharacters(reader));
					break;
				case "instrclass":
					builder.withToken(SecField.SECCLASS, readCharacters(reader));
					break;
				case "board":
					builder.withToken(SecField.DEFAULT_BOARDCODE, readCharacters(reader));
					break;
				case "market":
					builder.withToken(SecField.MARKETID, readInt(reader));
					break;
				case "shortname":
					builder.withToken(SecField.SHORT_NAME, readCharacters(reader));
					break;
				case "decimals":
					builder.withToken(SecField.DECIMALS, readInt(reader));
					break;
				case "minstep":
					builder.withToken(SecField.MINSTEP, readDecimal(reader));
					break;
				case "lotsize":
					builder.withToken(SecField.LOTSIZE, readDecimal(reader));
					break;
				case "point_cost":
					builder.withToken(SecField.POINT_COST, readDecimal(reader));
					break;
				case "opmask":
					int opmask = 0;
					opmask |= getAttributeBool(reader, "usecredit", "yes") ? OPMASK_USECREDIT : 0;
					opmask |= getAttributeBool(reader, "bymarket", "yes") ? OPMASK_BYMARKET : 0;
					opmask |= getAttributeBool(reader, "nosplit", "yes") ? OPMASK_NOSPLIT : 0;
					opmask |= getAttributeBool(reader, "fok", "yes") ? OPMASK_FOK : 0;
					opmask |= getAttributeBool(reader, "ioc", "yes") ? OPMASK_IOC : 0;
					reader.nextTag();
					builder.withToken(SecField.OPMASK, opmask);
					break;
				case "sectype":
					String str_sec_type = readCharacters(reader);
					SecType sec_type;
					try {
						sec_type = SecType.valueOf(str_sec_type);
					} catch ( IllegalArgumentException e ) {
						String msg = e.getMessage();
						if ( msg != null && msg.startsWith("No enum constant") ) {
							logger.warn("Security type constant is undefined: " + str_sec_type);
							sec_type = SecType.QUOTES;
						} else {
							throw e;
						}
					}
					builder.withToken(SecField.SECTYPE, sec_type);
					break;
				case "sec_tz":
					builder.withToken(SecField.SECTZ, readCharacters(reader));
					break;
				case "quotestype":
					builder.withToken(SecField.QUOTESTYPE, readInt(reader));
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				return builder.buildUpdate();
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public List<DeltaUpdate> readSecurities(XMLStreamReader reader) throws XMLStreamException {
		List<DeltaUpdate> result = new ArrayList<>();
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "security":
					result.add(readSecurity(reader));
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
			case XMLStreamReader.END_DOCUMENT:
				return result;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}

}
