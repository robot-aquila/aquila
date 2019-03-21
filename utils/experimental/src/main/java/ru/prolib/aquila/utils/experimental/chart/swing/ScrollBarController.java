package ru.prolib.aquila.utils.experimental.chart.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.data.ObservableSeries;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;

/**
 * Scrolling controller of the bar chart panel.
 * <p>
 * Performs following control:<br>
 * <li>Chart scrolling and scroll bar handling based on a shared axis viewport state</li>
 * <li>Automatic repainting based on timer and updates coming from categories</li>
 */
public class ScrollBarController implements AdjustmentListener, ActionListener, EventListener {
	private final Timer timer;
	private JPanel rootPanel;
	private JScrollBar scrollBar;
	private JCompAutoScrollButton autoScrollButton;
	private ObservableSeries<?> categories;
	private CategoryAxisDisplayMapper mapper;
	private CategoryAxisViewport viewport;
	private boolean autoScrollEnabled = true, autoRepaintEnabled, autoRepaintChange;
	private int lastNumberOfCategories;
	
	public ScrollBarController() {
		this.timer = new Timer(100, this);
		setAutoRepaint(true);
	}
	
	/**
	 * Enable or disable auto repaint based on categories update events.
	 * <p>
	 * @param enabled - enable or disable
	 */
	public synchronized void setAutoRepaint(boolean enabled) {
		if ( autoRepaintEnabled != enabled ) {
			autoRepaintEnabled = enabled;
			if ( enabled ) {
				autoRepaintChange = false;
				timer.start();
			} else {
				timer.stop();
			}
		}
	}
	
	/**
	 * Set the root panel just to repaint when needed.
	 * <p>
	 * @param rootPanel - panel instance
	 */
	public synchronized void setRootPanel(JPanel rootPanel) {
		this.rootPanel = rootPanel;
	}
	
	public synchronized void setAutoScroll(boolean autoScroll) {
		autoScrollEnabled = autoScroll;
		adjustAutoScrollButton();
		adjustScrollBar();
	}
	
	public synchronized void setAutoScrollButton(JCompAutoScrollButton autoScrollButton) {
		if ( this.autoScrollButton != null ) {
			this.autoScrollButton.removeActionListener(this);
		}
		this.autoScrollButton = autoScrollButton;
		this.autoScrollButton.addActionListener(this);
		adjustAutoScrollButton();
	}
	
	public synchronized void setScrollBar(JScrollBar scrollBar) {
		if ( this.scrollBar != null ) {
			this.scrollBar.removeAdjustmentListener(this);
		}
		this.scrollBar = scrollBar;
		this.scrollBar.addAdjustmentListener(this);
		adjustScrollBar();
	}

	public synchronized void setCategories(ObservableSeries<?> categories) {
		// To provide an automatic repainting it should be subscribed on every
		// update. Subscribing just for length update does not work this case.
		if ( this.categories != null ) {
			this.categories.onUpdate().removeListener(this);
		}
		this.categories = categories;
		this.categories.onUpdate().addListener(this);
		this.lastNumberOfCategories = categories.getLength();
		adjustScrollBar();
	}
	
	public synchronized ObservableSeries<?> getCategories() {
		return categories;
	}
	
	public synchronized void setDisplayMapper(CategoryAxisDisplayMapper mapper) {
		this.mapper = mapper;
		adjustScrollBar();
	}
	
	public synchronized void setViewport(CategoryAxisViewport viewport) {
		this.viewport = viewport;
	}
	
	/**
	 * Called when scroll bar value changed.
	 */
	@Override
	public synchronized void adjustmentValueChanged(AdjustmentEvent e) {
		if ( autoScrollEnabled ) {
			return;
		}
		adjustViewport();
		rootPanel.repaint();
	}

	@Override
	public synchronized void actionPerformed(ActionEvent e) {
		if ( e.getSource() == autoScrollButton ) {
			autoScrollEnabled = !autoScrollEnabled;
			adjustAutoScrollButton();
			adjustScrollBar();
			adjustViewport();
			rootPanel.repaint();
		} else if ( e.getSource() == timer ) {
			if ( autoRepaintChange ) {
				autoRepaintChange = false;
				rootPanel.repaint();
			}
		}
	}

	/**
	 * Called when categories length updated.
	 */
	@Override
	public void onEvent(Event event) {
		ObservableSeries<?> c = null;
		synchronized ( this ) {
			if ( viewport == null ) {
				return;
			}
			c = categories;
		}
		int number = c.getLength();
		boolean adj = false;
		synchronized ( this ) {
			if ( number != lastNumberOfCategories ) {
				lastNumberOfCategories = number;
				adj = true;
			}
			if ( autoRepaintEnabled ) {
				autoRepaintChange = true;
			}
		}
		if ( adj ) {
			adjustViewport();
		}
	}
	
	private synchronized void adjustViewport() {
		if ( ! SwingUtilities.isEventDispatchThread() ) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					adjustViewport();
				}
			});
		} else if ( categories != null ) {
			int number = categories.getLength();
			synchronized ( viewport ) {
				Integer pNumBars = viewport.getPreferredNumberOfBars();
				if ( pNumBars == null ) {
					// If preferred number of bars is not specified then try to fill out whole chart
					viewport.setCategoryRangeByFirstAndNumber(0, number);
				} else if ( autoScrollEnabled || number < pNumBars ) {
					viewport.setCategoryRangeByLastAndNumber(number - 1, pNumBars);
				} else {
					int max = scrollBar.getMaximum(),
						first = scrollBar.getValue(),
						numVisible = scrollBar.getVisibleAmount();
					// If the scroll bar is adjusted just use it.
					// Otherwise skip viewport adjustment until scroll bar adjusted.
					if ( max == number ) {
						viewport.setCategoryRangeByFirstAndNumber(first, numVisible);
					}
				}
			}
		}
	}
	
	private synchronized void adjustScrollBar() {
		if ( ! SwingUtilities.isEventDispatchThread() ) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					adjustScrollBar();
				}
			});
		} else {
			if ( categories == null || mapper == null ) {
				scrollBar.setEnabled(false);
				scrollBar.setValues(0, 0, 0, 0);
			} else {
				scrollBar.setEnabled(!autoScrollEnabled);
				scrollBar.setValues(mapper.getFirstVisibleCategory(),
					mapper.getNumberOfVisibleCategories(), 0, categories.getLength());
			}
		}
	}
	
	private synchronized void adjustAutoScrollButton() {
		if ( ! SwingUtilities.isEventDispatchThread() ) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					adjustAutoScrollButton();
				}
			});
		} else {
			autoScrollButton.setAutoScroll(autoScrollEnabled);
		}
	}

}
