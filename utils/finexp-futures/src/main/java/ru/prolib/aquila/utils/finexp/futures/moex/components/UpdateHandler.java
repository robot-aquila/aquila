package ru.prolib.aquila.utils.finexp.futures.moex.components;

import java.io.Closeable;

import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.web.utils.WUWebPageException;

public interface UpdateHandler extends Closeable {

	public boolean execute() throws DataStorageException, WUWebPageException;
	
}
