package ru.prolib.aquila.data;

/**
 * State of data feed subscription.
 */
public class DFSubscrState {
	protected DFSubscrStatus status;

	public DFSubscrState() {
		this.status = DFSubscrStatus.NOT_SUBSCR;
	}
	
	public DFSubscrState(DFSubscrStatus status) {
		this.status = status;
	}

	/**
	 * Get feed current status.
	 * <p>
	 * @return current status
	 */
	public DFSubscrStatus getStatus() {
		return status;
	}
	
	protected void throwCannotSwitchTo(DFSubscrStatus new_status) {
		throw new IllegalStateException("Cannot switch from " + status + " to " + new_status);
	}

	public void switchTo(DFSubscrStatus new_status) throws IllegalStateException, IllegalArgumentException {
		switch ( status ) {
		case NOT_SUBSCR:
		{
			switch ( new_status ) {
			case NOT_SUBSCR:
			case PENDING_SUBSCR:
			case NOT_AVAILABLE:
				break;
			default:
				throwCannotSwitchTo(new_status);
			}
			break;
		}
		case SUBSCR:
		{
			switch ( new_status ) {
			case NOT_SUBSCR: // Possible in some cases. For example: connection lost
			case SUBSCR:
			case PENDING_UNSUBSCR:
			case NOT_AVAILABLE:
				break;
			default:
				throwCannotSwitchTo(new_status);
			}
			break;
		}
		case PENDING_SUBSCR:
		{
			switch ( new_status ) {
			case SUBSCR:
			case NOT_SUBSCR:
			case NOT_AVAILABLE:
			case PENDING_SUBSCR:
				break;
			default:
				throwCannotSwitchTo(new_status);
			}
			break;
		}
		case PENDING_UNSUBSCR:
		{
			switch ( new_status ) {
			case SUBSCR:
			case NOT_SUBSCR:
			case NOT_AVAILABLE:
			case PENDING_UNSUBSCR:
				break;
			default:
				throwCannotSwitchTo(new_status);
			}
			break;
		}
		case NOT_AVAILABLE:
		{
			switch ( new_status ) {
			case NOT_AVAILABLE:
				break;
			default:
				throwCannotSwitchTo(new_status);
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Unsupported target status: " + new_status);		
		}
		status = new_status;
	}		

}
