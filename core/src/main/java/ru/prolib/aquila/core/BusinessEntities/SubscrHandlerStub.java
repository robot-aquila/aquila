package ru.prolib.aquila.core.BusinessEntities;

import java.util.concurrent.CompletableFuture;

public class SubscrHandlerStub implements SubscrHandler {
	private final CompletableFuture<Boolean> confirm;
	
	public SubscrHandlerStub(boolean result) {
		confirm = new CompletableFuture<>();
		confirm.complete(result);
	}
	
	public SubscrHandlerStub() {
		this(true);
	}

	@Override
	public void close() {
		
	}

	@Override
	public CompletableFuture<Boolean> getConfirmation() {
		return confirm;
	}

}
