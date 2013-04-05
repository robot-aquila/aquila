package ru.prolib.aquila.ta.ds;

import java.util.Date;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.util.AlignDate;

/**
 * Группировщик баров.
 * Группирует бары по времени. Для определения времени группировки использует
 * объект класса {@ling ru.prolib.aquila.util.AlignDate}. По завершению
 * очередного бара, делегирует добавление бара объекту класса {@link BarWriter},
 * указанному в конструкторе. 
 */
public class BarWriterGrouper implements BarWriter {
	private final AlignDate aligner;
	private final BarWriter target;
	private Candle cur = null;

	public BarWriterGrouper(AlignDate aligner, BarWriter target) {
		super();
		this.aligner = aligner;
		this.target = target;
	}
	
	public AlignDate getDateAligner() {
		return aligner;
	}
	
	public BarWriter getTargetBarWriter() {
		return target;
	}

	@Override
	public boolean addBar(Candle bar) throws BarWriterException {
		Date barTime = aligner.align(bar.getTime());
		if ( cur == null ) {
			cur = new Candle(barTime, bar.getOpen(), bar.getHigh(), bar.getLow(),
						  bar.getClose(), bar.getVolume());
		} else if ( barTime.equals(cur.getTime()) ) {
			cur.addCandle(bar);
		} else {
			target.addBar(cur);
			cur = new Candle(barTime, bar.getOpen(), bar.getHigh(), bar.getLow(),
					  bar.getClose(), bar.getVolume());
			return true;
		}
		return false;
	}

	@Override
	public boolean flush() throws BarWriterException {
		if ( cur == null ) {
			return false;
		}
		target.addBar(cur);
		target.flush();
		return true;
	}

}
