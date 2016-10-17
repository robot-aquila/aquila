package ru.prolib.aquila.web.utils.moex;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.openqa.selenium.io.IOUtils;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;

/**
 * This class adapts a MOEX delta-update to a standard update of symbol.
 * The MOEX delta-update is a contract info update which was obtained via the
 * MOEX site. The standard update is an update which is used to update
 * {@link ru.prolib.aquila.core.BusinessEntities.EditableSecurity security}
 * model. Some source updates may be skipped if they will make no actual
 * changes.
 */
public class MoexContractSymbolUpdateReader implements CloseableIterator<DeltaUpdate> {
	private final CloseableIterator<DeltaUpdate> moexContractUpdates;
	private final MoexContractSymbolUpdateConverter updateConverter;
	private DeltaUpdate lastUpdate;
	private boolean closed = false;
	
	public MoexContractSymbolUpdateReader(CloseableIterator<DeltaUpdate> moexContractUpdates,
			MoexContractSymbolUpdateConverter updateConverter)
	{
		this.moexContractUpdates = moexContractUpdates;
		this.updateConverter = updateConverter;
	}
	
	public MoexContractSymbolUpdateReader(CloseableIterator<DeltaUpdate> moexContractUpdates) {
		this(moexContractUpdates, new MoexContractSymbolUpdateConverter());
	}

	@Override
	public void close() throws IOException {
		if ( ! closed ) {
			IOUtils.closeQuietly(moexContractUpdates);
			closed = true;
		}
	}

	@Override
	public boolean next() throws IOException {
		if ( closed ) {
			throw new IOException("Iterator closed");
		}
		DeltaUpdate last = lastUpdate;
		lastUpdate = null;
		for ( ;; ) {
			if ( ! moexContractUpdates.next() ) {
				return false; // No more updates - nothing to do
			}
			DeltaUpdate raw = moexContractUpdates.item();
			DeltaUpdate next = updateConverter.toSymbolUpdate(new DeltaUpdateBuilder()
				.withSnapshot(true)
				.withTime(raw.getTime())
				.withTokens(raw)
				.buildUpdate());
			// Make an update only if this is a first update or content is different
			if ( last == null || (next.hasContents() && ! last.getContents().equals(next.getContents())) ) {
				lastUpdate = next;
				return true;
			}
		}
	}

	@Override
	public DeltaUpdate item() throws IOException, NoSuchElementException {
		if ( closed ) {
			throw new IOException("Iterator closed");
		}
		if ( lastUpdate == null ) {
			throw new NoSuchElementException();
		}
		return lastUpdate;
	}

}
