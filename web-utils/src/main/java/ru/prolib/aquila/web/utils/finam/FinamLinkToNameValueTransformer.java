package ru.prolib.aquila.web.utils.finam;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.WebElement;

import ru.prolib.aquila.web.utils.DataExportException;
import ru.prolib.aquila.web.utils.ErrorClass;

/**
 * Transforms a hyperlink from Data Export Web Form of FINAM site to a name-value pair.
 * The links MUST have the "value" attribute.
 * It MAY not work with hyperlinks from other sites!
 */
public class FinamLinkToNameValueTransformer implements WebElementTransformer<NameValuePair> {

	@Override
	public NameValuePair transform(WebElement element) throws DataExportException {
		String value = element.getAttribute("value");
		String text = element.getText();
		if ( value == null ) {
			throw new DataExportException(ErrorClass.WEB_FORM,
				"The link does not have a [value] attribute: " + text);
		}
		return new BasicNameValuePair(text, value);
	}

}
