package ru.prolib.aquila.finam.tools.web.utils;

import org.openqa.selenium.WebElement;

import ru.prolib.aquila.finam.tools.web.DataExportException;

public interface WebElementTransformer<T> {
	
	public T transform(WebElement element) throws DataExportException;

}
