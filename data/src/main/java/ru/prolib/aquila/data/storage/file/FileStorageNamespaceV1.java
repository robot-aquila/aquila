package ru.prolib.aquila.data.storage.file;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.IdUtils;
import ru.prolib.aquila.data.storage.DatedSymbol;

/**
 * The first version of the file storage namespace.
 * <p>
 * Directory structure:<p>
 * At level 1) first two characters of hexMD5 hash on symbol string representation</br>
 * At level 2) safe encoded symbol string representation</br>
 * At level 3) four digits of year</br>
 * At level 4) two digits of month</br>
 */
public class FileStorageNamespaceV1 implements FileStorageNamespace {
	private static final FilenameFilter LEVEL1_FILTER = new FilenameFilter() {
		@Override public boolean accept(File dir, String name) {
			return name.length() == 2
				&& name.matches("^[A-Z\\d]{2}$")
				&& new File(dir, name).isDirectory();
		}
	};
	private final IdUtils idUtils = new IdUtils();
	private final File root;
	
	public FileStorageNamespaceV1(File root) {
		this.root = root;
	}
	
	/**
	 * Get the storage root directory.
	 * <p>
	 * @return the root directory
	 */
	public File getRootDirectory() {
		return root;
	}

	@Override
	public File getDirectory(DatedSymbol descr) {
		String FS = File.separator;
		Symbol symbol = descr.getSymbol();
		LocalDate date = descr.getDate();
		return new File(root, StringUtils.upperCase(DigestUtils.md5Hex(symbol.toString()).substring(0, 2))
				+ FS + idUtils.getSafeSymbolId(symbol)
				+ FS + String.format("%04d", date.getYear())
				+ FS + String.format("%02d", date.getMonthValue()));
	}

	@Override
	public File getDirectoryForWriting(DatedSymbol descr) throws IOException {
		File path = getDirectory(descr);
		FileUtils.forceMkdir(path);
		return path;
	}

	@Override
	public Set<Symbol> scanForSymbols() throws IOException {
		final Set<Symbol> symbols = new HashSet<>();
		final FilenameFilter leve2Filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				try {
					symbols.add(idUtils.toSymbol(name));
				} catch ( IllegalArgumentException e ) { }
				return false;
			}
		};
		for ( String x : root.list(LEVEL1_FILTER) ) {
			new File(root, x).list(leve2Filter);
		}
		return symbols;
	}

}
