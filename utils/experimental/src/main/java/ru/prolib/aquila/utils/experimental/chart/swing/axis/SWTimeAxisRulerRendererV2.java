package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.utils.experimental.chart.ChartConstants;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisRulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.axis.GridLinesSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.PreparedRuler;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerRendererID;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.RLabel;

/**
 * Данная реализация не использует в своей работе параметры таймфрейма.
 * Работа основана на определении приоритетов отображения меток для
 * каждой категории в зависимости от расположения на временной оси
 * и отношении с соседними категориями. Алгоритм работы следующий:
 * для каждой видимой категории определяется приоритет отображения.
 * Приоритет зависит не только от значения категории но и от временного
 * отрезка, по отношению к предыдущей категории. Так, например если
 * начало торговой сессии в 10:00, а закрытие предыдущим днем в 23:50,
 * то десятичасовая метка отображается с повышенным приоритетом,
 * символизирующим новый день. Таким образом десяти часам нового дня
 * соответствует метка дня месяца, а не десяти часов. Хотя в обчыном
 * случае две соседние часовые метки имеют одинаковый часовой приоритет.
 * В итоге метки отображаются таким образом, что бы наблюдателю было
 * легче ориентироваться на временной оси: от меток большого масштаба
 * к маньшему в зависимости от доступных возможностей области
 * отображения. 
 * <pre>
 * Таблица приоритетов (0 - высший, 17 - низший):
 * Приоритет | Формат метки* | Описание
 *  0        |  YYYY         | начало тысячелетия
 *  1        |  YYYY         | начало столетия
 *  2        |  YYYY         | начало десятилетия
 *  3        |  YYYY         | начало года
 *  4        |  MMM          | начало месяца
 *  5        |  NN           | начало декады месяца (1, 11, 21)
 *  6        |  NN           | начало суток (полночь)
 *  7        |  HHh          | начало 12-ти часовых интервалов внутри дня
 *  8        |  HHh          | начало 6-ти часовых интервалов внутри дня
 *  9        |  HHh          | начало 3-х часовых интервалов внутри дня
 * 10        |  HHh          | начало часа
 * 11        |  :mm          | начало получаса
 * 12        |  :mm          | начало четвертей часа
 * 13        |  :mm          | начало десятиминуток
 * 14        |  :mm          | начало пятиминуток
 * 15        |  :mm          | начало минуты
 * 16        |  ss"          | начало секунды
 * 17        |  .SSS         | миллисекунда
 * </pre>
 * * - формат меток указан ориентировочно. Окончательное представление зависит
 * от реализации форматтера меток.
 * <p>
 * Приоритет может быть повышен в двух случаях:
 * <ul>
 * <li>1 - время точно указывает на границу соответствующего интервала</li>
 * <li>2 - между временем рассматриваемой категории и предыдущей находится
 *     граница вышестоящего интервала. Например, между 10:03 и 10:07 находится
 *     граница интервала 10:05 в связи с чем категории 10:07 назначается
 *     повышенная 14 категория, вместо типичной 15. Таким образом приоритет
 *     будет повышаться до тех пор, пока текущая категория не окажется в одном
 *     интервале с предшествующей.</li>
 * </ul> 
 */
public class SWTimeAxisRulerRendererV2 implements CategoryAxisRulerRenderer {
	public static final Font DEFAULT_FONT = ChartConstants.LABEL_FONT;
	
	static class Prioritizer {
		private static final Prioritizer instance = new Prioritizer();
		
		public static Prioritizer getInstance() {
			return instance;
		}
		
		private int getDecadeOfMonth(LocalDate date) {
			int decade = 3;
			int dom = date.getDayOfMonth();
			if ( dom < 11 ) {
				decade = 1;
			} else if ( dom < 21 ) {
				decade = 2;
			}
			return decade;
		}
		
		private int getSelfPriority(Instant curr, ZoneId zoneID) {
			ZonedDateTime curr_zdt = curr.atZone(zoneID);
			long curr_ms = curr_zdt.getLong(ChronoField.MILLI_OF_DAY);
			long curr_sec = curr_zdt.getLong(ChronoField.SECOND_OF_DAY);
			long curr_min = curr_zdt.getLong(ChronoField.MINUTE_OF_DAY);
			//long curr_ms = curr.toEpochMilli();
			//long curr_sec = curr_ms / 1000;
			//long curr_min = curr_sec / 60;
			
			if ( curr_ms % 1000 != 0 ) {
				return 17;
			}
			if ( curr_sec % 60 != 0 ) {
				return 16;
			}
			if ( curr_min % 5 != 0 ) {
				return 15;
			}
			
			// It can be any: /10, /15, /30, 1h, 3h, 6h, 12h, 1d, etc...
			if ( curr_min % 60 == 0 ) {
				// It's start of an hour				
				long curr_hour = curr_min / 60;
				if ( curr_hour == 0 ) {
					
					if ( curr_zdt.getDayOfMonth() == 1 ) {
						if ( curr_zdt.getMonthValue() == 1 ) {
							if ( curr_zdt.getYear() % 1000 == 0 ) {
								return 0;
							} else if ( curr_zdt.getYear() % 100 == 0 ) {
								return 1;
							} else if ( curr_zdt.getYear() % 10 == 0 ) {
								return 2;
							}
							return 3;
						
						} else {
							return 4;
							
						}
						
					} else if ( curr_zdt.getDayOfMonth() == 11
					  || curr_zdt.getDayOfMonth() == 21 )
					{
						return 5;
						
					}
					
					return 6;
				
				} else if ( curr_hour % 12 == 0 ) {
					return 7;
					
				} else if ( curr_hour % 6 == 0 ) {
					return 8;
					
				} else if ( curr_hour % 3 == 0 ) {
					return 9;
					
				}
				return 10;
				
			} else if ( curr_min % 30 == 0 ) {
				// It's half of an hour (can't be start of an hour there)
				return 11;
				
			} else if ( curr_min % 15 == 0 ) {
				// It's quarter of an hour (and can't be half of an hour there)
				return 12;
				
			} else if ( curr_min % 10 == 0 ) {
				// It's 10 minutes period (can't be quarter of an hour)
				return 13;
				
			} else {
				// It can be only 5 min period
				return 14;
				
			}
		}
		
		public int getPriority(Instant prev, Instant curr, ZoneId zoneID) {
			int self_prio = getSelfPriority(curr, zoneID);
			if ( prev == null ) {
				return self_prio;
			}
			
			ZonedDateTime curr_zdt = curr.atZone(zoneID);
			//long curr_ms = curr_zdt.getLong(ChronoField.MILLI_OF_DAY);
			long curr_sec = curr_zdt.getLong(ChronoField.SECOND_OF_DAY);
			long curr_min = curr_zdt.getLong(ChronoField.MINUTE_OF_DAY);
			//long curr_ms = curr.toEpochMilli();
			//long curr_sec = curr_ms / 1000;
			//long curr_min = curr_sec / 60;

			ZonedDateTime prev_zdt = prev.atZone(zoneID);
			//long prev_ms = prev_zdt.getLong(ChronoField.MILLI_OF_DAY);
			long prev_sec = prev_zdt.getLong(ChronoField.SECOND_OF_DAY);
			long prev_min = prev_zdt.getLong(ChronoField.MINUTE_OF_DAY);
			//long prev_ms = prev.toEpochMilli();
			//long prev_sec = prev_ms / 1000;
			//long prev_min = prev_sec / 60;
			
			//System.out.println("curr_ms=" + curr_ms + " curr_sec=" + curr_sec + " curr_min=" + curr_min);
			//System.out.println("prev_ms=" + prev_ms + " prev_sec=" + prev_sec + " prev_min=" + prev_min);
			
			long prev_x, curr_x;

			LocalDate prev_ld = prev_zdt.toLocalDate();
			LocalDate curr_ld = curr_zdt.toLocalDate();
			
			prev_x = prev_ld.getYear() / 1000; curr_x = curr_ld.getYear() / 1000;
			if ( prev_x != curr_x ) {
				return 0;
			}
			
			prev_x = prev_ld.getYear() / 100; curr_x = curr_ld.getYear() / 100;
			if ( prev_x != curr_x ) {
				return 1;
			}
			
			prev_x = prev_ld.getYear() / 10; curr_x = curr_ld.getYear() / 10;
			if ( prev_x != curr_x ) {
				return 2;
			}
			
			if ( prev_ld.getYear() != curr_ld.getYear() ) {
				return 3;
			}
			
			if ( prev_ld.getMonthValue() != curr_ld.getMonthValue() ) {
				return 4;
			}
			
			prev_x = getDecadeOfMonth(prev_ld);
			curr_x = getDecadeOfMonth(curr_ld);
			if ( prev_x != curr_x ) {
				return 5;
			}
			
			if ( prev_ld.getDayOfMonth() != curr_ld.getDayOfMonth() ) {
				return 6;
			}
			
			LocalTime prev_lt = prev_zdt.toLocalTime();
			LocalTime curr_lt = curr_zdt.toLocalTime();
			
			prev_x = prev_lt.getHour() / 12; curr_x = curr_lt.getHour() / 12;
			if ( prev_x != curr_x ) {
				return 7;
			}
			
			prev_x = prev_lt.getHour() / 6; curr_x = curr_lt.getHour() / 6;
			if ( prev_x != curr_x ) {
				return 8;
			}
			
			prev_x = prev_lt.getHour() / 3; curr_x = curr_lt.getHour() / 3;
			if ( prev_x != curr_x ) {
				return 9;
			}
			
			prev_x = prev_min / 60; curr_x = curr_min / 60;
			if ( prev_x != curr_x ) {
				return 10;
			}
			
			prev_x = prev_min / 30; curr_x = curr_min / 30;
			if ( prev_x != curr_x ) {
				return 11;
			}
			
			prev_x = prev_min / 15; curr_x = curr_min / 15;
			if ( prev_x != curr_x ) {
				return 12;
			}
			
			prev_x = prev_min / 10; curr_x = curr_min / 10;
			if ( prev_x != curr_x ) {
				return 13;
			}
			
			prev_x = prev_min / 5; curr_x = curr_min / 5;
			if ( prev_x != curr_x ) {
				return 14;
			}

			if ( prev_min != curr_min ) {
				return 15;
			}
			
			if ( prev_sec != curr_sec ) {
				return 16;
			}
			
			return Math.min(17, self_prio);
		}
		
	}
	
	public interface LabelFormatter {
		
		/**
		 * Get text representation of the instant.
		 * <p>
		 * @param priority - label priority
		 * @param time - the time instant
		 * @return text representation of label
		 */
		String getLabelText(int priority, Instant time);
		
	}
	
	static class LabelFormatterImpl implements LabelFormatter {
		private final DateTimeFormatter fYear, fMonth, fDayOfMonth,
			fHour, fMinute, fSecond, fMillisecond;
		
		public LabelFormatterImpl(ZoneId zoneID, Locale locale) {
			fYear = DateTimeFormatter.ofPattern("yyyy", locale).withZone(zoneID);
			fMonth = DateTimeFormatter.ofPattern("MMM", locale).withZone(zoneID);
			fDayOfMonth = DateTimeFormatter.ofPattern("dd", locale).withZone(zoneID);
			fHour = DateTimeFormatter.ofPattern("H'h'", locale).withZone(zoneID);
			fMinute = DateTimeFormatter.ofPattern(":mm", locale).withZone(zoneID);
			fSecond = DateTimeFormatter.ofPattern("ss'\"'", locale).withZone(zoneID);
			fMillisecond = DateTimeFormatter.ofPattern(".SSS", locale).withZone(zoneID);
		}
		
		public LabelFormatterImpl(ZoneId zoneID) {
			this(zoneID, Locale.US);
		}

		@Override
		public String getLabelText(int priority, Instant time) {
			switch ( priority ) {
			case 0:
			case 1:
			case 2:
			case 3:
				return fYear.format(time);
			case 4:
				return fMonth.format(time);
			case 5:
			case 6:
				return fDayOfMonth.format(time);
			case 7:
			case 8:
			case 9:
			case 10:
				return fHour.format(time);
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
				return fMinute.format(time);
			case 16:
				return fSecond.format(time);
			case 17:
				return fMillisecond.format(time);
			default:
				throw new IllegalArgumentException("Unsupported priority: " + priority);
			}
		}
		
	}
	
	private final String id;
	private final Prioritizer prioritizer;
	private final SWRendererCallbackCA callback;
	private Font labelFont;
	private TSeries<Instant> categories;
	
	public SWTimeAxisRulerRendererV2(String id,
			Font labelFont,
			TSeries<Instant> categories,
			Prioritizer prioritizer,
			SWRendererCallbackCA callback)
	{
		this.id = id;
		this.prioritizer = prioritizer;
		this.callback = callback;
		this.labelFont = labelFont;
		this.categories = categories;		
	}
	
	public SWTimeAxisRulerRendererV2(String id,
			Font labelFont,
			TSeries<Instant> categories)
	{
		this(id,
			labelFont,
			categories,
			Prioritizer.getInstance(),
			SWTimeAxisRulerRendererCallback.getInstance());
	}
	
	public SWTimeAxisRulerRendererV2(String id,
			Font labelFont)
	{
		this(id, labelFont, null);
	}
	
	public SWTimeAxisRulerRendererV2(String id,
			TSeries<Instant> categories)
	{
		this(id, DEFAULT_FONT, categories);
	}
	
	public SWTimeAxisRulerRendererV2(String id) {
		this(id, new TSeriesImpl<Instant>(ZTFrame.M1));
	}
	
	public Font getLabelFont() {
		return labelFont;
	}
	
	public void setLabelFont(Font labelFont) {
		this.labelFont = labelFont;
	}
	
	public TSeries<Instant> getCategories() {
		return categories;
	}
	
	public void setCategories(TSeries<Instant> categories) {
		this.categories = categories;
	}
	
	@Override
	public String getID() {
		return id;
	}
	
	private FontMetrics getFontMetrics(Object device) {
		return ((Graphics2D) device).getFontMetrics(labelFont);
	}
	
	public int getLabelWidth(String labelText, FontMetrics fontMetrics) {
		return fontMetrics.stringWidth(labelText) + 5;
	}
	
	public int getLabelHeight(String labelText, FontMetrics fontMetrics) {
		return fontMetrics.getHeight() + 5;
	}

	@Override
	public int getMaxLabelWidth(Object device) {
		// Possible variants:
		// 2018 <-- MAX
		// Oct
		// 20
		// 14h
		// :23
		// 15"
		// .222 <-- MAX
		return getLabelWidth("XXXX", getFontMetrics(device));
	}

	@Override
	public int getMaxLabelHeight(Object device) {
		return getLabelHeight("X", getFontMetrics(device));
	}
	
	/**
	 * Build priority map of visible categories.
	 * <p>
	 * @param f - index of the first visible category
	 * @param n - number of visible categories
	 * @param categories - categories
	 * @return array of priorities
	 * @throws ValueException - an error occurred
	 */
	private Integer[] getPriorityMap(int f, int n, TSeries<Instant> categories)
		throws ValueException
	{
		Instant prev_time = null;
		ZoneId zoneID = categories.getTimeFrame().getZoneID();
		Integer[] prio_map = new Integer[n];
		for ( int j = 0; j < n; j ++ ) {
			int i = f + j;
			// Do not check category for null. Category cannot be null!
			Instant curr_time = categories.get(i);
			prio_map[j] = prioritizer.getPriority(prev_time, curr_time, zoneID);
			prev_time = curr_time;
		}
		return prio_map;
	}
	
	/**
	 * Get center point of a label.
	 * <p>
	 * @param lbar - segment of label's bar
	 * @return coordinate of center of label
	 */
	private int getLabelCoord(Segment1D lbar) {
		return lbar.getStart() + lbar.getLength() / 2;
	}

	@Override
	public PreparedRuler prepareRuler(CategoryAxisDisplayMapper mapper, Object device) {
		AxisDirection dir = mapper.getAxisDirection();
		if ( dir.isVertical() ) {
			throw new UnsupportedOperationException("Axis direction is not supported: " + dir);
		}
		LabelFormatter formatter = new LabelFormatterImpl(categories.getTimeFrame().getZoneID());
		FontMetrics fontMetrics = getFontMetrics(device);
		int f = mapper.getFirstVisibleCategory();
		int n = mapper.getNumberOfVisibleCategories();
		categories.lock();
		try {
			Integer[] prio_map = getPriorityMap(f, n, categories);
			String[] labels = new String[n];
			Integer[] coords = new Integer[n];
			
			int prio_lowest = Integer.MIN_VALUE, prio_highest = Integer.MAX_VALUE;
			for ( int i = 0; i < n; i ++ ) {
				int prio = prio_map[i];
				//System.out.println("DBG: I=" + i + " prio=" + prio);
				prio_lowest = Math.max(prio_lowest, prio);
				prio_highest = Math.min(prio_highest, prio);
			}
			//System.out.println("DBG: prio_lowest=" + prio_lowest + " prio_highest=" + prio_highest);
			
			// Перебираем от высшего приоритета (меньшие значения) к низшему (большие)
			for ( int prio_curr = prio_highest; prio_curr <= prio_lowest; prio_curr ++ ) {
				//System.out.println("DBG: processing prio " + prio_curr);
				for ( int i = 0; i < n; i ++ ) {
					int prio = prio_map[i];
					//System.out.println("DBG: I=" + i + " prio=" + prio);
					if ( prio < 0 || prio != prio_curr ) {
						continue;
					}
					Instant ltime = categories.get(f + i);
					String ltext = labels[i] = formatter.getLabelText(prio, ltime);
					int lwidth = getLabelWidth(ltext, fontMetrics);
					Segment1D lbar = mapper.toDisplay(f + i);
					int lcoord = coords[i] = getLabelCoord(lbar);
					// Начиная с lcoord в количестве lwidth пикселей
					// пространство будет занято меткой. Мы можем отобразить
					// метку, если внутри этого пространства нет метки с
					// более высоким приоритетом. Независимо от этого, все
					// метки низжего или равного приоритета расположенные
					// далее (правее), не могут быть отображены.
					Segment1D lsegment = new Segment1D(lcoord, lwidth);
					//System.out.println("DBG: I=" + i + " possible label @" + ltime + " text=" + ltext + " dim=" + lsegment);
					for ( int j = i + 1; j < n; j ++ ) {
						// Сначала определим, а есть ли вообще проблема?
						// Если центр очередного бара за пределами нужной
						// нам области, то проблемы нет и работу нужно
						// прекратить.
						Segment1D x_bar = mapper.toDisplay(f + j);
						int x_coord = getLabelCoord(x_bar);
						if ( lsegment.getEnd() < x_coord ) {
							break;
						}
						
						// Проблема есть - области перекрываются. Но если
						// на очереди метка с повышенным приоритетом,
						// то именно она должна быть отображена, а исходная
						// должна быть пропущена.
						int x_prio = prio_map[j];
						if ( x_prio < prio ) {
							prio_map[i] = -1;
							break;
						}
						
						// На очереди метка с таким же или низшим приорететом.
						// Такая метка должна быть пропущена.
						prio_map[j] = -1;
						
					}
				}
			}
			
			// Теперь у нас в labels тексты меток, а в prio_map находятся
			// значения > 0, когда метка должна быть отображена. На основе
			// этой информации формируем конечный список меток.
			List<RLabel> result = new ArrayList<>();
			for ( int i = 0; i < n; i ++ ) {
				if ( prio_map[i] < 0 ) {
					continue;
				}
				result.add(new RLabel(f + i, labels[i], coords[i]));
			}
			
			return new SWPreparedRulerCA(callback, mapper, result, labelFont);
		} catch ( Exception e ) {
			System.out.println("DBG: n=" + n + " f=" + f);
			System.out.println("DBG: plot=" + mapper.getPlot());
			System.out.println("DBG: first visible category (mapper): " + mapper.getFirstVisibleCategory());
			System.out.println("DBG: last visible category (mapper): " + mapper.getLastVisibleCategory());
			System.out.println("DBG: num visible categories (mapper): " + mapper.getNumberOfVisibleCategories());
			System.out.println("DBG: num visible bars (mapper): " + mapper.getNumberOfVisibleBars());
			throw new IllegalStateException("Unexpected exception: ", e);
		} finally {
			categories.unlock();
		}
	}

	@Override
	public RulerSetup createRulerSetup(RulerID rulerID) {
		return new SWTimeAxisRulerSetup(rulerID);
	}

	@Override
	public GridLinesSetup createGridLinesSetup(RulerRendererID rendererID) {
		return new GridLinesSetup(rendererID);
	}

}
