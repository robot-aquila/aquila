package ru.prolib.aquila.web.utils.jbd;

import org.openqa.selenium.WebDriver;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;

import ru.prolib.aquila.web.utils.HTTPAttachmentManagerFactory;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentManager;

public class JBDAttachmentManagerFactory implements HTTPAttachmentManagerFactory {

	@Override
	public HTTPAttachmentManager createAttachmentManager(WebDriver driver) {
		return new JBDAttachmentManager((JBrowserDriver) driver);
	}

}
