package ru.prolib.aquila.web.utils.httpclient;

import org.apache.http.client.methods.CloseableHttpResponse;

import ru.prolib.aquila.web.utils.WUInvalidResponseException;

@Deprecated
public class ResponseValidatorStub implements ResponseValidator {
	private static final ResponseValidatorStub instance;
	
	static {
		instance = new ResponseValidatorStub();
	}
	
	public static ResponseValidator getInstance() {
		return instance;
	}
	
	private ResponseValidatorStub() {
		
	}

	@Override
	public void validateResponse(CloseableHttpResponse response)
			throws WUInvalidResponseException
	{

	}

}
