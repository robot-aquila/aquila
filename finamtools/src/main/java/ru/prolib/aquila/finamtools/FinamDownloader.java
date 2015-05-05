package ru.prolib.aquila.finamtools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.openqa.selenium.*;

public class FinamDownloader implements AutoCloseable {
	private final WebDriver driver;
	private final DownloadDriver downloadDriver;
	private boolean initialRequestIsMade = false;
	
	public FinamDownloader(WebDriver driver, DownloadDriver downloadDriver) {
		super();
		this.driver = driver;
		this.downloadDriver = downloadDriver;
	}
	
	public FinamDownloader initialRequest() {
		if ( initialRequestIsMade == false ) {
			driver.get("http://www.finam.ru/profile/moex-akcii/gazprom/export/");
			initialRequestIsMade = true;
		}
		return this;
	}
	
	public File download() throws IOException {
		initialRequest();
		File destination = File.createTempFile("finam-", ".txt");
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
	
	public FinamDownloader selectDateFrom(LocalDate date) {
		initialRequest();
		setDate("issuer-profile-export-from", date);
		return this;
	}
	
	public FinamDownloader selectDateTo(LocalDate date) {
		initialRequest();
		setDate("issuer-profile-export-to", date);
		return this;
	}
	
	public FinamDownloader selectDate(LocalDate date) {
		return selectDateFrom(date)
				.selectDateTo(date);
	}
	
	public FinamDownloader selectQuote(String quoteName) {
		initialRequest();
		driver.findElement(By.id("issuer-profile-header"))
			.findElement(By.className("finam-ui-quote-selector-quote"))
			.findElement(By.className("finam-ui-quote-selector-arrow"))
			.click();
		selectLink(getQuoteSelectorOwner(), quoteName);
		return this;
	}
	
	public FinamDownloader selectMarket(String marketName) {
		initialRequest();
		driver.findElement(By.id("issuer-profile-header"))
			.findElement(By.className("finam-ui-quote-selector-market"))
			.findElement(By.className("finam-ui-quote-selector-arrow"))
			.click();
		selectLink(getMarketSelectorOwner(), marketName);
		return this;
	}
	
	public FinamDownloader selectPeriod(String periodName) {
		initialRequest();
		driver.findElement(By.id("issuer-profile-export-first-row"))
			.findElement(By.className("finam-ui-controls-select"))
			.findElement(By.className("finam-ui-controls-select-arrow"))
			.click();
		selectLink(getPeriodSelectorOwner(), periodName);
		return this;
	}
	
	public FinamDownloader selectPeriodTick() {
		return selectPeriod("тики");
	}
	
	private FinamDownloader selectFileExt(String extName) {
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
				date.getMonthOfYear() - 1); // january is 0 for finam
		selectLink(driver.findElement
			(By.xpath("//table[@class='ui-datepicker-calendar']/tbody")),
				date.getDayOfMonth());
	}
	
	private void selectLink(WebElement owner, int expectedValue) {
		selectLink(owner, Integer.toString(expectedValue));
	}
	
	private void selectLink(WebElement owner, String expectedValue) {
		List<WebElement> elements = owner.findElements(By.tagName("a"));
		if ( elements.size() == 0 ) {
			throw new FinamDownloaderException("Hyperlinks were not found");
		}
		for  ( WebElement e : elements ) {
			if ( expectedValue.equals(e.getText().trim()) ) {
				e.click();
				return;
			}
		}
		throw new FinamDownloaderException
			("Couldn't find an appropriate hyperlink: " + expectedValue);
	}
	
	private void selectOption(WebElement owner, int expectedValue) {
		selectOption(owner, Integer.toString(expectedValue));
	}
	
	private void selectOption(WebElement owner, String expectedValue) {
		List<WebElement> elements = owner.findElements(By.tagName("option"));
		if ( elements.size() == 0 ) {
			throw new FinamDownloaderException("Options were not found");
		}
		for  ( WebElement e : elements ) {
			String actualValue = e.getAttribute("value");
			if ( expectedValue.equals(actualValue) ) {
				e.click();
				return;
			}
		}
		throw new FinamDownloaderException
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

	public void close() throws Exception {
		driver.close();		
	}

}
