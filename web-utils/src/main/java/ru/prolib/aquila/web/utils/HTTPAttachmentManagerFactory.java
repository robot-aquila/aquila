package ru.prolib.aquila.web.utils;

import org.openqa.selenium.WebDriver;

import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentManager;

public interface HTTPAttachmentManagerFactory {
	
	HTTPAttachmentManager createAttachmentManager(WebDriver driver);

}
