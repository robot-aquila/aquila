package ru.prolib.aquila.web.utils;

import org.openqa.selenium.WebElement;

public interface WebElementTransformer<T> {
	
	public T transform(WebElement element) throws DataExportException;

}
