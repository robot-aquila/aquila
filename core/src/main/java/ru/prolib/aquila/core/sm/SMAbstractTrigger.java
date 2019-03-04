package ru.prolib.aquila.core.sm;

public abstract class SMAbstractTrigger implements SMTrigger {
	protected final SMInput input;
	protected SMTriggerRegistry proxy;
	protected boolean activated, closed;
	
	public SMAbstractTrigger(SMInput input) {
		this.input = input;
	}
	
	public SMAbstractTrigger() {
		this(null);
	}
	
	public SMInput getInput() {
		return input;
	}
	
	public SMTriggerRegistry getProxy() {
		return proxy;
	}

	/**
	 * Check state and activate trigger if needed.
	 * <p>
	 * This method checks possibility of activation and makes mandatory activation steps.
	 * In case if activated, derived class should execute specific activation procedures.
	 * If activation is not possible, derived class should skip specific activation steps.
	 * <p>
	 * From the point of design there is possible that trigger will be deactivated after
	 * this check but before derived class code performed its own activation steps. This
	 * case deactivation will be called prior to activation finish. And some specific data
	 * may go inconsistent state (for example subscribed for event forever). To avoid
	 * trigger inconsistency, derived class can use synchronization on trigger instance
	 * (this). But this can case deadlock issues in case of calling another code.
	 * <p>
	 * Actually this case is not a big deal. Because only one thread can access
	 * state machine instance at same time. Thus, any other thread unable to enter other
	 * state simultaneously with trigger activation which performed at state enter phase.
	 * So, just don't care. Use synchronization in derived class only if you sure it'll
	 * cause no problems.
	 * <p>
	 * @param registry - trigger registry
	 * @return true if trigger was activated, false otherwise
	 */
	protected synchronized boolean tryActivate(SMTriggerRegistry registry) {
		if ( activated || closed ) {
			return false;
		}
		proxy = registry;
		activated = true;
		return true;
	}

	/**
	 * Check state and deactivate trigger if needed.
	 * <p>
	 * @return true if trigger was deactivated, false otherwise
	 */
	protected synchronized boolean tryDeactivate() {
		if ( ! closed ) {
			proxy = null;
			closed = true;	
		}
		if ( activated ) {
			activated = false;
			return true;
		} else {
			return false;
		}
	}
	
	protected void dispatch(Object data) {
		SMTriggerRegistry proxy_copy;
		synchronized ( this ) {
			if ( closed || ! activated ) {
				return;
			}
			proxy_copy = proxy;
		}
		if ( input == null ) {
			proxy_copy.input(data);
		} else {
			proxy_copy.input(input, data);
		}
	}

}
