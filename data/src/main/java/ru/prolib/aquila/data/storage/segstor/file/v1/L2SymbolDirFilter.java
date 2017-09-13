package ru.prolib.aquila.data.storage.segstor.file.v1;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.StrCoder;

/**
 * Level-2 cache directory filter for symbol subdirectories.
 * <p>
 * This filter is applicable to scan level-1 directories.
 * Level-2 represents list of symbol directories.
 * This filter decodes all valid subdirectories which are point to valid symbol.
 * All symbols found during the scan may be obtained through appropriate method.
 * Same filter instance may be used to scan several level-1 directories.
 */
public class L2SymbolDirFilter implements FilenameFilter {
	private static final StrCoder coder = StrCoder.getInstance();
	private final Set<Symbol> symbols;
	
	public L2SymbolDirFilter() {
		symbols = new HashSet<>();
	}

	@Override
	public boolean accept(File dir, String name) {
		if ( ! new File(dir, name).isDirectory() ) {
			return false;
		}
		try {
			Symbol symbol = new Symbol(coder.decode(name));
			symbols.add(symbol);
			return true;
		} catch ( IllegalArgumentException e ) {
			return false;
		}
	}
	
	/**
	 * Get all symbols found during scan.
	 * <p>
	 * @return symbol set
	 */
	public Set<Symbol> getFoundSymbols() {
		return symbols;
	}

}
