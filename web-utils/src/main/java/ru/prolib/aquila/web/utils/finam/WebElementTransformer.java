package ru.prolib.aquila.web.utils.finam;

import org.openqa.selenium.WebElement;

import ru.prolib.aquila.web.utils.DataExportException;

public interface WebElementTransformer<T> {
	
	public T transform(WebElement element) throws DataExportException;

}
