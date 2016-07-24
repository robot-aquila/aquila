package ru.prolib.aquila.finam.tools.web.utils;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import ru.prolib.aquila.finam.tools.web.DataExportException;
import ru.prolib.aquila.finam.tools.web.ErrorClass;

/**
 * Utility class to search WebElement using one or more subsequent queries.
 */
public class SearchWebElement {
	private WebDriver driver;
	private WebElement currentElement;
	
	/**
	 * Constructor with driver as starting point.
	 * <p>
	 * @param driver - web driver
	 */
	public SearchWebElement(WebDriver driver) {
		this.driver = driver;
	}
	
	/**
	 * Constructor with element as starting point.
	 * <p>
	 * @param element - web element
	 */
	public SearchWebElement(WebElement element) {
		this.currentElement = element;
	}
	
	/**
	 * Find next element.
	 * <p>
	 * Find child element of the currently selected element.
	 * It may be web driver in case of first query or a result of previous query.
	 * <p>
	 * @param by - search criteria
	 * @return this
	 * @throws DataExportException - Web driver exception thrown
	 */
	public SearchWebElement find(By by) throws DataExportException {
		try {
			currentElement = currentElement == null ?
					driver.findElement(by) : currentElement.findElement(by);
			return this;
		} catch ( NoSuchElementException e ) {
			throw errForm("Element not found: " + by, e);
		} catch ( WebDriverException e ) {
			throw errDriver("WebDriver exception", e);
		}
	}
	
	/**
	 * Find next element.
	 * <p>
	 * Search all elements down and select one by index.
	 * If no one element found or no element with such index then exception will be thrown.
	 * <p>
	 * @param by - search criteria
	 * @param index - element index
	 * @return this
	 * @throws DataExportException - element not found or web driver exception thrown
	 */
	public SearchWebElement find(By by, int index) throws DataExportException {
		List<WebElement> list = findAll(by);
		if ( index >= list.size() ) {
			throw errForm("Element [" + index + "] not found: " + by);
		}
		currentElement = list.get(index);
		return this;
	}
	
	/**
	 * Find element with attribute value.
	 * <p>
	 * Search all elements down and select the first one which has an attribute with the specified value.
	 * <p>
	 * @param by - search criteria
	 * @param attributeName - attribute name
	 * @param expectedAttributeValue - expected attribute value
	 * @return this
	 * @throws DataExportException - element not found or web driver exception thrown
	 */
	public SearchWebElement findWithAttributeValue(By by, String attributeName,
			String expectedAttributeValue) throws DataExportException
	{
		for ( WebElement e : findAll(by) ) {
			String actualValue = e.getAttribute("value");
			if ( expectedAttributeValue.equals(actualValue) ) {
				currentElement = e;
				return this;
			}
		}
		throw errForm("Element with [" + attributeName + "=" + expectedAttributeValue + "] not found: " + by);
	}
	
	/**
	 * Find element with text.
	 * <p>
	 * Search all elements down and select the first one which contains the expected text.
	 * Note: the text means {@link org.openqa.selenium.WebElement#getText()} value.
	 * <p>
	 * @param by - search criteria
	 * @param expectedText - expected text
	 * @return this
	 * @throws DataExportException - element not found or web driver exception thrown
	 */
	public SearchWebElement findWithText(By by, String expectedText)
			throws DataExportException
	{
		for ( WebElement e : findAll(by) ) {
			String actualText = e.getText();
			if ( expectedText.equals(actualText) ) {
				currentElement = e;
				return this;
			}
		}
		throw errForm("Element with text [" + expectedText + "] not found: " + by);
	}
	
	/**
	 * Get currently selected element.
	 * <p>
	 * @return selected element
	 */
	public WebElement get() {
		return currentElement;
	}
	
	/**
	 * Click on currently selected element.
	 * <p>
	 * @return this
	 * @throws DataExportException - Web driver exception thrown
	 */
	public SearchWebElement click() throws DataExportException {
		try {
			currentElement.click();
			return this;
		} catch ( WebDriverException e ) {
			throw errDriver(e);
		}
	}
	
	/**
	 * Set currently selected element checked or unchecked.
	 * <p>
	 * @param checked - checked or unchecked
	 * @return this
	 * @throws DataExportException
	 */
	public SearchWebElement setChecked(boolean checked) throws DataExportException {
		try {
			if ( checked != currentElement.isSelected() ) {
				return click();
			}
			return this;
		} catch ( WebDriverException e ) {
			throw errDriver(e);
		}
	}
	
	/**
	 * Find all elements.
	 * <p>
	 * @param by - search criteria
	 * @return list of elements
	 * @throws DataExportException - Web driver exception thrown
	 */
	public List<WebElement> findAll(By by) throws DataExportException {
		try {
			return currentElement == null ?
					driver.findElements(by) : currentElement.findElements(by);
		} catch ( WebDriverException e ) {
			throw errDriver(e);
		}
	}
	
	/**
	 * Find all elements and transform them to something else.
	 * <p>
	 * @param by - search criteria
	 * @param transformer - element transformer
	 * @return list of transformed objects
	 * @throws DataExportException - Web driver exception thrown
	 */
	public <T> List<T> transformAll(By by, WebElementTransformer<T> transformer)
			throws DataExportException
	{
		List<T> list = new ArrayList<>();
		for ( WebElement element : findAll(by) ) {
			list.add(transformer.transform(element));
		}
		return list;
	}
	
	/**
	 * Find all, transform to something else and click on specific element.
	 * <p>
	 * @param by - search criteria
	 * @param transformer - element transformer
	 * @param index - element index to click
	 * @return list of transformed objects
	 * @throws DataExportException - element not found or web driver exception thrown
	 */
	public <T> List<T> transformAllAndClick(By by, WebElementTransformer<T> transformer, int index)
			throws DataExportException
	{
		List<T> list = new ArrayList<>();
		List<WebElement> elements = findAll(by);
		if ( index >= elements.size() ) {
			throw errForm("Element [" + index + "] not found: " + by);
		}
		for ( WebElement element : elements ) {
			list.add(transformer.transform(element));
		}
		elements.get(index).click();
		return list;
	}
	
	private DataExportException errForm(String msg, Throwable t) {
		return new DataExportException(ErrorClass.WEB_FORM, msg, t);
	}
	
	private DataExportException errForm(String msg) {
		return errForm(msg, null);
	}
	
	private DataExportException errDriver(String msg, Throwable t) {
		return new DataExportException(ErrorClass.WEB_DRIVER, msg, t);
	}
	
	private DataExportException errDriver(Throwable t) {
		return errDriver("WebDriver exception", t);
	}

}
