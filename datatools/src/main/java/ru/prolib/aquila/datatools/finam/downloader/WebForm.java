package ru.prolib.aquila.datatools.finam.downloader;

import java.io.Closeable;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebForm implements Closeable {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(WebForm.class);
	}
	
	private final WebDriver driver;
	private final Downloader downloadDriver;
	private boolean initialRequestIsMade = false;
	
	public static WebForm createFirefoxDownloader() {
    	FirefoxDownloader driver = new FirefoxDownloader();
    	return new WebForm(driver.getWebDriver(), driver);
	}
	
	public static WebForm createHtmlUnitDownloader() {
    	HtmlUnitDownloader driver = new HtmlUnitDownloader();
    	return new WebForm(driver.getWebDriver(), driver);
	}
	
	public WebForm(WebDriver driver, Downloader downloadDriver) {
		super();
		this.driver = driver;
		this.downloadDriver = downloadDriver;
	}
	
	public WebForm initialRequest() {
		if ( initialRequestIsMade == false ) {
			driver.get("http://www.finam.ru/profile/moex-akcii/gazprom/export/");
			initialRequestIsMade = true;
		}
		return this;
	}
	
	public File download() throws DownloaderException {
		initialRequest();
		File destination;
		try {
			destination = File.createTempFile("finam-", ".txt");
		} catch ( Exception e ) {
			throw new DownloaderException("Error creating temp file", e);
		}
		destination.delete();
		selectFileName(FilenameUtils.removeExtension(destination.getName()));
		selectFileExt(".txt");
		return downloadDriver.download(this, destination.getName());
	}
	
	public WebElement getSubmitButton() {
		return driver.findElement(By.xpath(getSubmitButtonXPath()));
	}
	
	public String getSubmitButtonXPath() {
		return "//div[@id=\"issuer-profile-export-button\"]/button";
	}
	
	public WebForm selectDateFrom(LocalDate date) {
		initialRequest();
		setDate("issuer-profile-export-from", date);
		return this;
	}
	
	public WebForm selectDateTo(LocalDate date) {
		initialRequest();
		setDate("issuer-profile-export-to", date);
		return this;
	}
	
	public WebForm selectDate(LocalDate date) {
		return selectDateFrom(date)
				.selectDateTo(date);
	}
	
	public WebForm selectQuote(String quoteName) {
		initialRequest();
		driver.findElement(By.id("issuer-profile-header"))
			.findElement(By.className("finam-ui-quote-selector-quote"))
			.findElement(By.className("finam-ui-quote-selector-arrow"))
			.click();
		selectLink(getQuoteSelectorOwner(), quoteName);
		return this;
	}
	
	public WebForm selectMarket(String marketName) {
		initialRequest();
		driver.findElement(By.id("issuer-profile-header"))
			.findElement(By.className("finam-ui-quote-selector-market"))
			.findElement(By.className("finam-ui-quote-selector-arrow"))
			.click();
		selectLink(getMarketSelectorOwner(), marketName);
		return this;
	}
	
	public WebForm selectPeriod(String periodName) {
		initialRequest();
		driver.findElement(By.id("issuer-profile-export-first-row"))
			.findElement(By.className("finam-ui-controls-select"))
			.findElement(By.className("finam-ui-controls-select-arrow"))
			.click();
		selectLink(getPeriodSelectorOwner(), periodName);
		return this;
	}
	
	public WebForm selectPeriodTick() {
		return selectPeriod("тики");
	}

	public WebForm selectFileFormat(String formatName) {
		initialRequest();
		driver.findElement(By.id("issuer-profile-export-fileformat-row"))
			.findElement(By.className("finam-ui-controls-select"))
			.findElement(By.className("finam-ui-controls-select-arrow"))
			.click();
		selectLink(getFileFormatSelectorOwner(), formatName);
		return this;
	}

	/**
	 * Select output file format.
	 * <p>
	 * Select "DATE, TIME, LAST, VOL, ID" file format.
	 * This preset will work only for tick data interval which should be
	 * currently selected on the page.
	 * <p>
	 * @return this object
	 */
	public WebForm selectFileFormat_TimePriceVolId() {
		return selectFileFormat("DATE, TIME, LAST, VOL, ID");
	}
	
	/**
	 * Select output file format.
	 * <p>
	 * Select "DATE, TIME, LAST, VOL" file format.
	 * This preset will work only for tick data interval which should be
	 * currently selected on the page.
	 * <p>
	 * @return this object
	 */
	public WebForm selectFileFormat_TimePriceVol() {
		return selectFileFormat("DATE, TIME, LAST, VOL");
	}
	
	private WebForm selectFileExt(String extName) {
		initialRequest();
		driver.findElement(By.id("issuer-profile-export-second-row"))
			.findElement(By.className("finam-ui-controls-select"))
			.findElement(By.className("finam-ui-controls-select-arrow"))
			.click();
		selectLink(getFileExtSelectorOwner(), extName);
		return this;
	}
	
	private void selectFileName(String fileName) {
		WebElement filename = driver.findElement
				(By.id("issuer-profile-export-file-name"));
		filename.sendKeys(StringUtils.repeat("\b", 50) + fileName);
	}
	
	private void setDate(String inputId, LocalDate date) {
		driver.findElement(By.id(inputId)).click();
		selectOption(driver.findElement(By.className("ui-datepicker-year")),
				date.getYear());
		selectOption(driver.findElement(By.className("ui-datepicker-month")),
				date.getMonth().getValue());
		selectLink(driver.findElement
			(By.xpath("//table[@class='ui-datepicker-calendar']/tbody")),
				date.getDayOfMonth());
	}
	
	private void selectLink(WebElement owner, int expectedValue) {
		selectLink(owner, Integer.toString(expectedValue));
	}
	
	private void selectLink(WebElement owner, String expectedValue)
		throws WebFormException
	{
		List<WebElement> elements = owner.findElements(By.tagName("a"));
		if ( elements.size() == 0 ) {
			throw new WebFormException("Hyperlinks were not found");
		}
		for  ( WebElement e : elements ) {
			if ( expectedValue.equals(e.getText().trim()) ) {
				e.click();
				return;
			}
		}
		throw new WebFormException
			("Couldn't find an appropriate hyperlink: " + expectedValue);
	}
	
	private void selectOption(WebElement owner, int expectedValue) {
		selectOption(owner, Integer.toString(expectedValue));
	}
	
	private void selectOption(WebElement owner, String expectedValue) {
		List<WebElement> elements = owner.findElements(By.tagName("option"));
		if ( elements.size() == 0 ) {
			throw new WebFormException("Options were not found");
		}
		for  ( WebElement e : elements ) {
			String actualValue = e.getAttribute("value");
			if ( expectedValue.equals(actualValue) ) {
				e.click();
				return;
			}
		}
		throw new WebFormException
			("Couldn't find an appropriate option: " + expectedValue);
	}
	
	private WebElement getFinamDropDown(int index) {
		return driver.findElements(By.className("finam-ui-dropdown-list"))
				.get(index);
	}
	
	private WebElement getMarketSelectorOwner() {
		return getFinamDropDown(0);
	}
	
	private WebElement getQuoteSelectorOwner() {
		return getFinamDropDown(1);
	}
	
	private WebElement getPeriodSelectorOwner() {
		return getFinamDropDown(2);
	}
	
	private WebElement getFileExtSelectorOwner() {
		return getFinamDropDown(3);
	}
	
	private WebElement getFileFormatSelectorOwner() {
		return getFinamDropDown(8);
	}

	public void close() {
		try {
			driver.close();
		} catch ( Exception e ) {
			logger.warn("Exception while closing web form: ", e);
		}
	}

}
