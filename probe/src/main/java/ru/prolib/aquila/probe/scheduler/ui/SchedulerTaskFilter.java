package ru.prolib.aquila.probe.scheduler.ui;

import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.probe.scheduler.SchedulerTask;

public interface SchedulerTaskFilter {
	
	/**
	 * Get title of the filter.
	 * <p>
	 * The title is used to create menu entry.
	 * <p>
	 * @return the title
	 */
	public MsgID getTitle();
	
	/**
	 * Test that the task should be included to a result.
	 * <p>
	 * @param handler - the task handler
	 * @return true if the task should be included, false otherwise 
	 */
	public boolean isIncluded(SchedulerTask handler);
	
	/**
	 * Test that the filter is enabled.
	 * <p>
	 * @return true if enabled, false otherwise
	 */
	public boolean isEnabled();
	
	/**
	 * Enable or disable filter.
	 * <p>
	 * If filter disabled then it will give a false to task inclusion test.
	 * If it is enabled then result depends on current filter settings.   
	 * <p>
	 * @param enable - if true then enable filter, false otherwise
	 */
	public void setEnabled(boolean enable);
	
	/**
	 * Test that the filter supports additional settings dialog.
	 * <p>
	 * @return true if additional settings are supported, false otherwise
	 */
	public boolean isSettingSupported();
	
	/**
	 * Show additional settings dialog.
	 */
	public void showSettingsDialog();
	
	/**
	 * Get text representation of the task.
	 * <p>
	 * This method is called only if the task should be included. I.e. the
	 * filter recognized the task.
	 * <p>
	 * @param handler - the task handler
	 * @return short string representation of the task
	 */
	public String formatTitle(SchedulerTask handler);

}
