package ru.prolib.aquila.utils.experimental.chart.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.Timer;

import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.data.ObservableSeries;
import ru.prolib.aquila.ui.SwingEvent;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewportController;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewportImpl;

/**
 * Scrolling controller of the bar chart panel.
 * <p>
 * Количество отображаемых баров влияет на размер ползунка и, как следствие, на
 * позицию этого ползунка. Но у нас привязка к последнему бару. В этом случае
 * точность длины ползунка в барах является критичной. Однако, контроллер ничего
 * не знает о параметрах отображения. Фактическое количество отображаемых баров
 * может отличаться от желаемого как в большую, так и в меньшую стороны.
 * Необходимо дополнительно учитывать количество видимых баров, которое
 * устанавливается извне вызовом метода {@link #updateNumberOfVisibleBars(int)}.
 * Размер ползунка в барах определяется с использованием параметров в следующем
 * порядке приоритета:
 * 
 * 		1) длина последовательности
 * 		2) предпочитаемое количество баров для отображения
 * 		3) фактическое количество видимых баров
 * 
 * Обрабатываемые события
 *  - [Таймер] - перерисовка при наличии изменений
 *  - [Изменение длины последовательности] - смещение ползунка, если
 *    включен автоскрол и перерисовка
 *  - [Обновление последовательности] - установка признака изменений
 *  - [Клик на автоскрол] - активация/деактивация кнопки, при включении
 *    автоскрола сместить ползунок в конец и перерисовать
 *  - [Изменение позиции ползунка] - смещение окна просмотра
 *    и перерисовка
 *  - [Изменение желаемого количества отображаемых баров] - при
 *    изменении размера ползунка перерисовать
 *  - [Изменение фактического количества отображаемых баров] - при изменении
 *    размера ползунка перерисовать
 */
public class ScrollBarControllerV2 implements
	AdjustmentListener,
	ActionListener,
	CategoryAxisViewportController
{
	
	static class Viewport {
		private final int totalLen, windowPos, windowLen, knobPos, knobLen;
		private final boolean knobEnabled;
		
		/**
		 * Constructor.
		 * <p>
		 * @param totalLen - total length of space represented. Visible window
		 * is somewhere inside that space.
		 * @param windowPos - position of virtual window inside represented
		 * space. Position is an index of the first element inside the window.
		 * Position can be negative. This is possible then data is sticked to
		 * the end of window instead of start. In other words when chart start
		 * displaying at right and expends to the left. 
		 * @param windowLen - length of visible area window. Actually number of
		 * bars desired to display. It must be less or equal to length of
		 * virtual space or exception will thrown.
		 * @param knobPos - position of scroll bar knob. Position can't be
		 * negative, greater or equals than the total length.
		 * @param knobLen - length of knob. Number of elements which are
		 * actually visible. Sometimes it can differ with desired visible area.
		 * For example if drawing device cannot display all elements because of
		 * absence of space. This parameter must be less or equal to window of
		 * visible area or exception will thrown. Also knobPos + knobLen cannot
		 * be greater than the total length.
		 * @param knobEnabled - true if knob enabled, false otherwise. Knob is
		 * always defined and valid position and length but some cases it must
		 * be disabled for user usage. 
		 */
		public Viewport(int totalLen,
						int windowPos,
						int windowLen,
						int knobPos,
						int knobLen,
						boolean knobEnabled)
		{
			if ( knobLen > windowLen ) {
				throw new IllegalArgumentException(new StringBuilder()
						.append("Expected knob len <= ")
						.append(windowLen)
						.append(" but ")
						.append(knobLen)
						.toString());
			}
			if ( knobPos < 0 ) {
				throw new IllegalArgumentException(new StringBuilder()
						.append("Expected knob pos >= 0 but ")
						.append(knobPos)
						.toString());
			}
			int knobMax = knobPos + knobLen;
			if ( knobMax > totalLen ) {
				throw new IllegalArgumentException(new StringBuilder()
						.append("Expected knob pos + len <= ")
						.append(totalLen)
						.append(" but ")
						.append(knobMax)
						.toString());
			}
			this.totalLen = totalLen;
			this.windowPos = windowPos;
			this.windowLen = windowLen;
			this.knobPos = knobPos;
			this.knobLen = knobLen;
			this.knobEnabled = knobEnabled;
		}
		
		public int getTotalLen() {
			return totalLen;
		}
		
		public int getWindowPos() {
			return windowPos;
		}
		
		public int getWindowLen() {
			return windowLen;
		}
		
		public int getKnobPos() {
			return knobPos;
		}
		
		public int getKnobLen() {
			return knobLen;
		}
		
		public boolean isKnobEnabled() {
			return knobEnabled;
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != Viewport.class ) {
				return false;
			}
			Viewport o = (Viewport) other;
			return new EqualsBuilder()
					.append(o.totalLen, totalLen)
					.append(o.windowPos, windowPos)
					.append(o.windowLen, windowLen)
					.append(o.knobPos, knobPos)
					.append(o.knobLen, knobLen)
					.append(o.knobEnabled, knobEnabled)
					.build();
		}
		
		@Override
		public String toString() {
			return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
					.append("tl", totalLen)
					.append("wp", windowPos)
					.append("wl", windowLen)
					.append("kp", knobPos)
					.append("kl", knobLen)
					.append("ke", knobEnabled)
					.build();
		}
		
	}
	
	/**
	 * Трекер окна просмотра с привязкой к правой части. Крайняя правая позиция
	 * ползунка всегда совпадает с крайней правой позицией окна просмотра. Из
	 * этого следует, что максимальная позиция правой границы окна просмотра
	 * равна общей длине минус единицу. Позиция окна просмотра может сдвигаться
	 * влево в сторону отрицательных значений. Истиный размер ползунка
	 * определяется как минимальное из preferred, visible и total, а его позиция
	 * всегда положительна и в сумме с длиной не выходит за пределы области
	 * значений. Если область перекрытия пространства значений и окна просмотра
	 * меньше истиной длины ползунка, то длина ползунка равна длине области
	 * перекрытия.
	 */
	static class ViewportTracker {
		private Integer preferredLen, visibleLen;
		private boolean autoScroll, knobEnabled;
		private int totalLen, windowPos, windowLen, knobPos, knobLen;
		
		public ViewportTracker(boolean autoScroll) {
			this.autoScroll = autoScroll;
		}
		
		/**
		 * Get true length of the knob considering all properties.
		 * <p>
		 * Note that this call required {@link #preferredLen},
		 * {@link #visibleLen} and {@link #totalLen} properties contain correct
		 * values prior to call (possible undefined).
		 * <p>
		 * @return knob length
		 */
		private int getTrueKnobLen() {
			int kl = totalLen;
			if ( preferredLen != null ) {
				kl = Math.min(kl, preferredLen);
			}
			if ( visibleLen != null ) {
				kl = Math.min(kl, visibleLen);
			}
			return kl;
		}
		
		private int getTrueWindowLen() {
			return preferredLen == null ? totalLen : preferredLen;
		}
		
		private void update() {
			if ( autoScroll ) {
				knobEnabled = false;
				windowLen = getTrueWindowLen();
				windowPos = totalLen - windowLen;
				knobLen = getTrueKnobLen();
				knobPos = totalLen - knobLen;
				if ( knobPos < 0 ) {
					knobLen = totalLen;
					knobPos = 0;
				}
			} else {
				knobLen = getTrueKnobLen();
				if ( knobLen == totalLen ) {
					knobEnabled = false;
					knobPos = 0;
				} else {
					knobEnabled = true;
					if ( knobPos < 0 ) {
						knobPos = 0;
					} else if ( knobPos + knobLen > totalLen ) {
						knobPos = totalLen - knobLen;
					}
				}
				int knobEnd = knobPos + knobLen;
				windowLen = getTrueWindowLen();
				windowPos = knobEnd - windowLen;
			}
		}
		
		public boolean isAutoScroll() {
			return autoScroll;
		}
		
		public void setAutoScroll(boolean autoScroll) {
			this.autoScroll = autoScroll;
			update();
		}
		
		public boolean switchAutoScroll() {
			setAutoScroll(!autoScroll);
			return autoScroll;
		}
		
		public void setPreferredLen(Integer len) {
			this.preferredLen = len;
			update();
		}
		
		public Integer getPreferredLen() {
			return preferredLen;
		}
		
		public void setVisibleLen(Integer len) {
			this.visibleLen = len;
			update();
		}
		
		public void setTotalLen(int len) {
			totalLen = len;
			update();
		}
		
		public void setKnobPos(int pos) {
			if ( ! autoScroll ) {
				knobPos = pos;
				update();
			}
		}
		
		public Viewport getViewport() {
			return new Viewport(
					totalLen,
					windowPos,
					windowLen,
					knobPos,
					knobLen,
					knobEnabled
				);
		}
		
	}
	
	static class OnUpdateProxy implements EventListener {
		private final ScrollBarControllerV2 owner;
		
		OnUpdateProxy(ScrollBarControllerV2 owner) {
			this.owner = owner;
		}

		@Override
		public void onEvent(Event event) {
			if ( owner.hasCategories() ) {
				if ( owner.isAutoRepaintEnabled() ) {
					owner.setAutoRepaintChange(true);
				}
			}
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != OnUpdateProxy.class ) {
				return false;
			}
			return new EqualsBuilder()
					.append(((OnUpdateProxy) other).owner, owner)
					.build();
		}
		
	}
	
	static class OnLengthUpdateProxy implements EventListener {
		private final ScrollBarControllerV2 owner;
		
		OnLengthUpdateProxy(ScrollBarControllerV2 owner) {
			this.owner = owner;
		}

		@Override
		public void onEvent(Event event) {
			if ( owner.hasCategories() ) {
				owner.adjustAll();
			}
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != OnLengthUpdateProxy.class ) {
				return false;
			}
			return new EqualsBuilder()
					.append(((OnLengthUpdateProxy) other).owner, owner)
					.build();
		}
		
	}
	
	private final Timer timer;
	private final ViewportTracker vptracker;
	private final SwingEvent onLengthUpdateProxy, onUpdateProxy;
	private JPanel rootPanel;
	private JScrollBar scrollBar;
	private JCompAutoScrollButton autoScrollButton;
	private ObservableSeries<?> categories;
	private boolean autoRepaintEnabled, autoRepaintChange;
	
	/**
	 * Constructor.
	 * <p>
	 * For testing purposes only.
	 * <p>
	 * @param timer - timer instance
	 * @param viewport - viewport instance
	 * @param autoRepaint - enable or disable auto repaint
	 */
	ScrollBarControllerV2(Timer timer,
						  boolean autoRepaint,
						  ViewportTracker vptracker)
	{
		this.timer = timer;
		this.vptracker = vptracker;
		setAutoRepaint(autoRepaint);
		onLengthUpdateProxy = new SwingEvent(new OnLengthUpdateProxy(this));
		onUpdateProxy = new SwingEvent(new OnUpdateProxy(this));
	}
	
	public ScrollBarControllerV2(Timer timer) {
		this(timer, true, new ViewportTracker(true));
	}
	
	public ScrollBarControllerV2() {
		this.timer = new Timer(100, this);
		this.vptracker = new ViewportTracker(true);
		setAutoRepaint(true);
		onLengthUpdateProxy = new SwingEvent(new OnLengthUpdateProxy(this));
		onUpdateProxy = new SwingEvent(new OnUpdateProxy(this));
	}
	
	/**
	 * Get length update event listener proxy.
	 * <p>
	 * @return event listener proxy
	 */
	EventListener getOnLengthUpdateProxy() {
		return onLengthUpdateProxy;
	}
	
	/**
	 * Get update event listener proxy.
	 * <p>
	 * @return event listener proxy
	 */
	EventListener getOnUpdateProxy() {
		return onUpdateProxy;
	}
	
	/**
	 * Check there is pending updates.
	 * <p>
	 * @return true if has pending updates, false - no updates
	 */
	boolean isAutoRepaintChange() {
		return autoRepaintChange;
	}
	
	/**
	 * Set or reset pending changes mark.
	 * <p>
	 * @param change - true to set, false - to reset
	 */
	void setAutoRepaintChange(boolean change) {
		autoRepaintChange = change;
	}
	
	/**
	 * Check whether controlle has categories.
	 * <p>
	 * @return true if categories are defined
	 */
	boolean hasCategories() {
		return categories != null;
	}

	/**
	 * Set component of auto scroll button.
	 * <p>
	 * Note this instance is must defined even if scroll bar or auto scroll
	 * button not used. Just define and make them hidden. After all the button
	 * should be adjusted when all components defined. Call {@link #adjustAll()}
	 * to make proper initial state of all components.
	 * <p>
	 * @param autoScrollButton - component
	 */
	public void setAutoScrollButton(JCompAutoScrollButton autoScrollButton) {
		resetAutoScrollButton();
		this.autoScrollButton = autoScrollButton;
		this.autoScrollButton.setAutoScroll(vptracker.isAutoScroll());
		this.autoScrollButton.addActionListener(this);
	}

	/**
	 * Set component of scroll bar.
	 * <p>
	 * Note this instance must be defined even if scroll bar not used. Just
	 * define and make it not visible. After all the scroll bar should be
	 * adjusted when all components defined. Call {@link #adjustAll()}
	 * to make proper initial state of all components.
	 * <p>
	 * @param scrollBar - component
	 */
	public void setScrollBar(JScrollBar scrollBar) {
		resetScrollBar();
		this.scrollBar = scrollBar;
		this.scrollBar.addAdjustmentListener(this);
	}

	/**
	 * Set categories.
	 * <p>
	 * This instance must be defined to make controller work at all. It is
	 * possible that the series of categories not yet ready when controller
	 * should be started. That's OK and will work properly without this data
	 * series. But after categories has been set all other components require
	 * adjustment. You should call {@link #adjustAll()} method to make proper
	 * initialization of all components.
	 * <p>
	 * @param categories - the data series which represents categories
	 */
	public void setCategories(ObservableSeries<?> categories) {
		// To provide an automatic repainting it should be subscribed on every
		// update. Subscribing just for length update does not work this case.
		resetCategories();
		this.categories = categories;
		categories.onUpdate().addListener(onUpdateProxy);
		categories.onLengthUpdate().addListener(onLengthUpdateProxy);
	}
	
	/**
	 * Set the root panel just to repaint when needed.
	 * <p>
	 * @param rootPanel - panel instance
	 */
	public void setRootPanel(JPanel rootPanel) {
		this.rootPanel = rootPanel;
	}

	/**
	 * Enable or disable auto repaint based on categories update events.
	 * <p>
	 * @param enabled - enable or disable
	 */
	public void setAutoRepaint(boolean enabled) {
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
	 * Enable or disable autoscroll. This may cause repaint in case of slider
	 * moved.
	 * <p>
	 * @param autoScroll - true to enable or false to disable auto scroll feature 
	 */
	public void setAutoScroll(boolean autoScroll) {
		if ( autoScroll != vptracker.isAutoScroll() ) {
			vptracker.setAutoScroll(autoScroll);
			adjustAutoScroll();
		}
	}
	
	@Override
	public void setPreferredNumberOfBars(Integer number) {
		vptracker.setPreferredLen(number);
	}
	
	@Override
	public void updateNumberOfVisibleBars(int number) {
		vptracker.setVisibleLen(number);
	}
	
	public boolean isAutoRepaintEnabled() {
		return autoRepaintEnabled;
	}

	/**
	 * Called when scroll bar value changed.
	 */
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		// We can't return because this method called each time scrollbar
		// parameters changed. Even if it was changed programmatically.
		//if ( autoScrollEnabled ) {
		//	return;
		//}
		vptracker.setKnobPos(scrollBar.getValue());
		if ( ! adjustScrollBar() ) {
			repaint();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == autoScrollButton ) {
			vptracker.switchAutoScroll();
			adjustAutoScroll();
			
		} else if ( e.getSource() == timer ) {
			if ( autoRepaintChange ) {
				repaint();
			}

		}
	}
	
	void repaint() {
		// Keep this check for easier testing using real AWT classes
		if ( rootPanel != null ) {
			rootPanel.repaint();
		}
		autoRepaintChange = false;
	}
	
	/**
	 * Adjust slider position and auto scroll button using current parameters of 
	 * scroll bar and categories data. This method is intended to components
	 * adjustment while switching auto scroll mode. Will cause repaint if
	 * needed.
	 */
	private void adjustAutoScroll() {
		autoScrollButton.setAutoScroll(vptracker.isAutoScroll());
		adjustScrollBar();
	}
	
	/**
	 * Adjust scroll bar.
	 * <p>
	 * @return true if scroll bar values were changed and this will cause
	 * repaint. So if repaint was your goal then you should skip. 
	 */
	private boolean adjustScrollBar() {
		Viewport v = vptracker.getViewport();
		int cur_knob_pos = scrollBar.getValue(),
			cur_knob_len = scrollBar.getVisibleAmount(),
			cur_total_len = scrollBar.getMaximum(),
			new_knob_pos = v.getKnobPos(),
			new_knob_len = v.getKnobLen(),
			new_total_len = v.getTotalLen();
		if ( v.isKnobEnabled() != scrollBar.isEnabled() ) {
			scrollBar.setEnabled(v.isKnobEnabled());
		}
		if ( cur_knob_pos != new_knob_pos
		  || cur_knob_len != new_knob_len
		  || cur_total_len != new_total_len )
		{
			// Yes, it's called maximum but actually it's amount of elements
			scrollBar.setValues(new_knob_pos, new_knob_len, 0, new_total_len);
			return true;
		}
		return false;
	}
	
	/**
	 * Adjust all UI properties according to data model. Will cause repaint if
	 * needed (avoiding repaint not possible due to AWT logic). This method
	 * requires that all UI components defined. Categories can be undefined.
	 */
	public void adjustAll() {
		adjustAutoScroll();
	}
	
	@Override
	public CategoryAxisViewport getViewport() {
		vptracker.setTotalLen(hasCategories() ? categories.getLength() : 0);
		Viewport v = vptracker.getViewport();
		CategoryAxisViewportImpl viewport = new CategoryAxisViewportImpl();
		viewport.setPreferredNumberOfBars(vptracker.getPreferredLen());
		viewport.setCategoryRangeByFirstAndNumber(v.getWindowPos(), v.getWindowLen());
		return viewport;
	}
	
	private void resetAutoScrollButton() {
		if ( autoScrollButton != null ) {
			autoScrollButton.removeActionListener(this);
			autoScrollButton = null;
		}
	}
	
	private void resetScrollBar() {
		if ( scrollBar != null ) {
			scrollBar.removeAdjustmentListener(this);
			scrollBar = null;
		}
	}
	
	private void resetCategories() {
		if ( categories != null ) {
			categories.onUpdate().removeListener(onUpdateProxy);
			categories.onLengthUpdate().removeListener(onLengthUpdateProxy);
			categories = null;
		}
	}
	
	public void close() {
		resetAutoScrollButton();
		resetScrollBar();
		resetCategories();
	}

}
