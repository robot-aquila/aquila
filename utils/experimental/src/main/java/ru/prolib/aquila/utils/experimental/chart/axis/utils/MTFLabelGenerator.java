package ru.prolib.aquila.utils.experimental.chart.axis.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import ru.prolib.aquila.utils.experimental.chart.Segment1D;

public class MTFLabelGenerator {
	private static final MTFLabelGenerator instance;
	
	static {
		instance = new MTFLabelGenerator();
	}
	
	public static MTFLabelGenerator getInstance() {
		return instance;
	}
	
	/**
	 * Get string template of the largest possible label.
	 * <p>
	 * @return string that represents a largest (in chars) possible label
	 */
	public static String getLargestLabelTemplate() {
		return "00000";
	}
	
	/**
	 * Get complete set of intraday labels for minute time frame.
	 * <p>
	 * @param tframePeriod - period of time frame in minutes
	 * @return list of labels
	 */
	public List<MTFLabel> getIntradayLabels(int tframePeriod) {
		// TODO: Add caching. Make the result readonly.
		DateTimeFormatter hLabelTpl = DateTimeFormatter.ofPattern("H'h'"),
						  mLabelTpl = DateTimeFormatter.ofPattern(":mm");
		int count = 1440 / tframePeriod;
		if ( 60 % tframePeriod != 0 ) {
			hLabelTpl = DateTimeFormatter.ofPattern("HH:mm");
			count ++;
		}
		int prevHour = -1;
		LocalTime currTime = LocalTime.of(0, 0);
		List<MTFLabel> labels = new ArrayList<>();
		for ( int i = 0; i < count; i ++ ) {
			MTFLabel label = null;
			if ( currTime.getHour() != prevHour ) {
				prevHour = currTime.getHour();
				label = new MTFLabel(currTime,
									 hLabelTpl.format(currTime),
									 true);
			} else {
				label = new MTFLabel(currTime,
									 mLabelTpl.format(currTime),
									 false);
			}
			labels.add(label);
			currTime = currTime.plusMinutes(tframePeriod);
		}
		return labels;
	}
	
	public List<MTFLabel> getIntradayLabels(int barSize,
			int tframePeriod,			
			MTFLabelSize labelSize)
	{
		List<MTFLabel> basicLabels = getIntradayLabels(tframePeriod);
		
		// Разобьем на два этапа:
		// 1) Первым делом, нужно "нарисовать" часовые метки.
		// 2) Оставшееся место используем для "рисования" внутричасовых меток.
		
		// Создадим карту, в которой для каждого часа будет храниться
		// информация об отрезке, занятом часовой меткой. Эти отрезки нельзя
		// использовать при "рисовании" минутных меток. Отсутствие отрезка в
		// соответствующей позиции означает, что часовая метка для этого часа
		// не рисуется. Если нет часовой метки, значит для внутричасовых тем
		// более недостаточно места и весь час можно пропустить.
		Segment1D usedSegments[] = new Segment1D[24];
		Segment1D lastUsedSegment = null;
		for ( int barIndex = 0; barIndex < basicLabels.size(); barIndex ++ ) {
			MTFLabel label = basicLabels.get(barIndex);
			if ( ! label.isHourBoundary() ) {
				continue;
			}
			int h = label.getTime().getHour();
			int barX = barIndex * barSize;
			// Проверим, не накладывается-ли координата начала отображения
			// на отрезок, занятый последней "отрисованной" меткой.
			if ( lastUsedSegment != null && lastUsedSegment.getEnd() >= barX ) {
				continue;
			}
			int size = labelSize.getVisibleSize(label.getText());
			usedSegments[h] = lastUsedSegment = new Segment1D(barX, size);
			if ( h == 23 ) {
				break;
			}
		}
		
		// Теперь создадим список меток, которые подлежат отрисовке
		List<MTFLabel> labels = new ArrayList<>();
		for ( int barIndex = 0; barIndex < basicLabels.size(); barIndex ++ ) {
			MTFLabel label = basicLabels.get(barIndex);
			int h = label.getTime().getHour();
			// Если часовая метка не "рисуется", то ничего не попадает в список
			if ( usedSegments[h] == null ) {
				continue;
			}
			if ( label.isHourBoundary() ) { 
				lastUsedSegment = usedSegments[h];
			} else {
				// Это внутричасовая метка. Она не должна перекрывать область
				// предыдущей метки и метки последующего часа.
				int barX = barIndex * barSize;
				if ( barX <= lastUsedSegment.getEnd() ) {
					continue;
				}
				int size = labelSize.getVisibleSize(label.getText());
				Segment1D labelSegment = new Segment1D(barX, size);
				if ( h < 23 ) {
					Segment1D nextUsedSegment = null;
					for ( int j = h + 1; j < 24; j ++ ) {
						nextUsedSegment = usedSegments[j];
					}
					if ( nextUsedSegment != null
					  && nextUsedSegment.getStart() <= labelSegment.getEnd() )
					{
							continue;
					}
					lastUsedSegment = labelSegment;
				}
			}
			labels.add(label);
		}
		return labels;
	}

}
