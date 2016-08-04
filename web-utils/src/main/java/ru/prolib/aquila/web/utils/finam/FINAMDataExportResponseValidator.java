package ru.prolib.aquila.web.utils.finam;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;

import ru.prolib.aquila.web.utils.InvalidResponseException;
import ru.prolib.aquila.web.utils.httpclient.ResponseValidator;

public class FINAMDataExportResponseValidator implements ResponseValidator {

	@Override
	public void validateResponse(CloseableHttpResponse response)
			throws InvalidResponseException
	{
		int statusCode = response.getStatusLine().getStatusCode();
		if ( statusCode != 200 ) {
			throw new InvalidResponseException("Unexpected HTTP status code: " + statusCode);
		}
		Header header = response.getFirstHeader("content-type");
		if ( header == null ) {
			throw new InvalidResponseException("Header Content-Type not exists");
		}
		String contentType = header.getValue();
		if ( ! "finam/expotfile".equals(contentType) ) {
			throw new InvalidResponseException("Unexpected Content-Type: " + contentType);
		}
		header = response.getFirstHeader("content-disposition");
		if ( header == null ) {
			throw new InvalidResponseException("Header Content-Disposition not exists");
		}
		String contentDisposition = header.getValue();
		if ( ! contentDisposition.startsWith("attachment;") ) {
			throw new InvalidResponseException("Unexpected Content-Disposition: " + contentDisposition);
		}
	}

}
