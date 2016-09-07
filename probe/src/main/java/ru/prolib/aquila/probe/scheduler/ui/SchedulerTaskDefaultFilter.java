package ru.prolib.aquila.probe.scheduler.ui;

import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.probe.scheduler.SchedulerTask;

/**
 * The filter to show or hide "all" tasks which weren't recognized by other filters.
 */
public class SchedulerTaskDefaultFilter implements SchedulerTaskFilter {
	private boolean enabled = true;

	@Override
	public MsgID getTitle() {
		return ProbeMsg.STD_DEFAULT_FILTER;
	}

	@Override
	public boolean isIncluded(SchedulerTask handler) {
		return enabled;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enable) {
		this.enabled = enable;
	}

	@Override
	public boolean isSettingSupported() {
		return false;
	}

	@Override
	public void showSettingsDialog() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String formatTitle(SchedulerTask handler) {
		return handler.toString();
	}

}
