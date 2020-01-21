package ru.prolib.aquila.core.BusinessEntities;

import java.util.concurrent.CompletableFuture;

public interface SubscrHandler {
	
	/**
	 * Close subscription. Subsequent calls have no effect.
	 */
	void close();
	
	/**
	 * Get confirmation.
	 * <p>
	 * When service processed subscription request it sets confirmation value.
	 * True means all OK. False will be set in case if by some reason request
	 * cannot be done and subscription not established. Closing the handler will
	 * work despite of confirmation value. Using confirmation is useful to make
	 * it synchronous style when work should be proceed when subscription was
	 * actually made to avoid possible data loss.
	 * <p>
	 * @return confirmation
	 */
	CompletableFuture<Boolean> getConfirmation();
}
