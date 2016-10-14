package ru.prolib.aquila.data.storage.file;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.data.DataFormatException;

/**
 * Plain-text multiline format of a delta-update.
 * <p>
 * The record format:
 * <pre>
 * F:YYYY-MM-DDTHH:mm:ss.SSS
 * N1:value1
 * NX:valueX
 * </pre>
 * Where F is 1 - if the update is a full snapshot, 0 - otherwise<br>
 * N1...NX - token of values<br>
 * value1...valueX - URL-encoded string representation of appropriate token values<br>
 * An new line indicates the end of the record.
 * <p>
 */
public class PtmlDeltaUpdatePacker {
	private static final String LE = "\n";
	private static final String ES = ":";
	private static final String ENC = "UTF-8";
	private final PtmlDeltaUpdateConverter converter;
	
	public PtmlDeltaUpdatePacker(PtmlDeltaUpdateConverter converter) {
		this.converter = converter;
	}

	public String toString(DeltaUpdate update) throws DataFormatException {
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(update.isSnapshot() ? 1 : 0)
				.append(ES)
				.append(update.getTime().toString())
				.append(LE);
			Iterator<Map.Entry<Integer, Object>> it = update.getContents().entrySet().iterator();
			while ( it.hasNext() ) {
				Map.Entry<Integer, Object> entry = it.next();
				int token = entry.getKey();
				buffer.append(token)
					.append(ES)
					.append(URLEncoder.encode(converter.toString(token, entry.getValue()), ENC))
					.append(LE);
			}
			return buffer.toString();
		} catch ( UnsupportedEncodingException e ) {
			throw new DataFormatException(e);
		}
	}

	public DeltaUpdate toUpdate(String record) throws DataFormatException {
		String[] lines = StringUtils.split(record, LE);
		if ( lines.length < 2 ) {
			throw new DataFormatException("At least two lines expected: " + record);
		}
		// Parse header
		String[] headLines = StringUtils.splitByWholeSeparatorPreserveAllTokens(lines[0], ES, 2);
		if ( headLines.length != 2 ) {
			throw new DataFormatException("Unexpected header format: " + lines[0]);
		}
		boolean isSnapshot = false;
		if ( "1".equals(headLines[0]) ) {
			isSnapshot = true;
		} else if ( ! "0".equals(headLines[0]) ) {
			throw new DataFormatException("Invalid sanpshot marker: " + headLines[0]);
		}
		Instant updateTime = null;
		try {
			updateTime = Instant.parse(headLines[1]);
		} catch ( DateTimeParseException e ) {
			throw new DataFormatException("Invalid update time: " + headLines[1]);
		}
		// Parse tokens
		Map<Integer, Object> contents = new HashMap<>();
		for ( int i = 1; i < lines.length; i ++ ) {
			String[] entryLines = StringUtils.splitByWholeSeparatorPreserveAllTokens(lines[i], ES, 2);
			if ( entryLines.length != 2 ) {
				throw new DataFormatException("Token not found: " + lines[i]);
			}
			try {
				int token = Integer.valueOf(entryLines[0]);
				contents.put(token, converter.toObject(token, URLDecoder.decode(entryLines[1], ENC)));
			} catch ( UnsupportedEncodingException e ) {
				throw new DataFormatException(e);
			} catch ( NumberFormatException e ) {
				throw new DataFormatException("Invalid token format: " + entryLines[0]);
			}
		}
		return new DeltaUpdate(updateTime, isSnapshot, contents);
	}

}
