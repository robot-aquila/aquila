package ru.prolib.aquila.web.utils.finam.segstor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.segstor.SegmentMetaData;
import ru.prolib.aquila.web.utils.finam.datasim.FinamCsvL1UpdateReader;

public class FinamL1UpdateSegmentMetaData implements SegmentMetaData {
	private final File file;
	private final Symbol symbol;
	
	public FinamL1UpdateSegmentMetaData(File file, Symbol symbol) {
		this.file = file;
		this.symbol = symbol;
	}

	@Override
	public String getPath() {
		return file.getAbsolutePath();
	}

	@Override
	public String getHashCode() {
		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(file), 1024);
			return StringUtils.upperCase(DigestUtils.md5Hex(is));
		} catch ( Exception e ) {
			throw new IllegalStateException("Unexpected exception: ", e);
		} finally {
			if ( is != null ) {
				IOUtils.closeQuietly(is);
			}
		}
	}

	@Override
	public Instant getUpdateTime() {
		return Instant.ofEpochMilli(file.lastModified());
	}

	@Override
	public long getNumberOfElements() {
		try ( CloseableIterator<L1Update> it = new FinamCsvL1UpdateReader(symbol, file) ) {
			long n = 0L;
			while ( it.next() ) {
				n ++;
			}
			return n;
		} catch ( IOException e ) {
			throw new IllegalStateException("Unexpected exception: ", e);
		}
	}

}
