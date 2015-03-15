package ru.prolib.aquila.quik;


import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;


public class ParadoxMontyHallTest {
	
	/**
	 * Селектор варианта.
	 */
	class Selector {
		/**
		 * Случайно выбрать один из вариантов.
		 * <p>
		 * @param variants
		 * @return номер варианта-1
		 */
		int random(int variants) {
			return (int) (Math.random() * (double)variants);
		}
	}
	
	Selector selector = new Selector();
	
	/**
	 * Дверь.
	 */
	class Door {
		// Автомобиль не может быть перемещен.
		private final boolean auto;
		private boolean opened = false;
		Door(boolean auto) {
			this.auto = auto;
		}
		
		public String toString() {
			return getClass().getSimpleName() + "["
				+ "opened=" + opened + ", auto=" + auto + "]";
		}
	}
	
	/**
	 * Все 3 двери.
	 */
	class DoorSet {
		private final Door doors[] = new Door[3];
		DoorSet(Door d1, Door d2, Door d3) {
			doors[0] = d1;
			doors[1] = d2;
			doors[2] = d3;
			int autos = 0;
			for ( int i = 0; i < doors.length; i ++ ) {
				if ( doors[i].auto ) {
					autos ++;
				}
			}
			if ( autos != 1 ) {
				throw new RuntimeException("Invalid doorset");
			}
		}
		
		/**
		 * Проверить, за этой ли дверью автомобиль.
		 * <p>
		 * @param door номер двери
		 * @return результат проверки
		 */
		public boolean isAuto(int door) {
			return doors[door].auto;
		}
		
		public String toString() {
			String result = "";
			for ( int i = 0; i < doors.length; i ++ ) {
				result += " " + doors[i].toString();
			}
			return result;
		}
		
	}
	
	/**
	 * Интерфейс стратегии поведения.
	 */
	interface Strategy {
		
		int choose();
		
		int choose2(DoorSet set, int firstChoice);
		
	}
	
	/**
	 * Стратегия, при которой игрок не меняет первоначальный выбор.
	 */
	class Keep implements Strategy {
		
		public int choose() {
			return selector.random(3);
		}

		@Override
		public int choose2(DoorSet set, int firstChoice) {
			return firstChoice;
		}
		
	}

	/**
	 * Стратегия, при которой игрок меняет первоначальный выбор.
	 */
	class Swap implements Strategy {

		@Override
		public int choose() {
			return selector.random(3);
		}

		@Override
		public int choose2(DoorSet set, int firstChoice) {
			// Должны остаться две двери. Определяем какие.
			List<Integer> closed = new Vector<Integer>();
			for ( int i = 0; i < set.doors.length; i ++ ) {
				if ( set.doors[i].opened == false ) {
					closed.add(i);
				}
			}
			// По условию, мы должны выбрать 1 из двух.
			// Если закрытыми остались не 2 двери, то это ошибка.
			if ( closed.size() != 2 ) {
				throw new RuntimeException("Invalid doorset");
			}
			// Теперь меняем.
			return closed.get(0) == firstChoice ? closed.get(1) : closed.get(0);
		}
		
	}
	
	/**
	 * Создать 3 двери, за одной из которых авто. 
	 */
	public DoorSet makeDoors() {
		Door doors[] = new Door[3];
		// Автомобиль только за одной дверью
		int whereAuto = selector.random(3);
		for ( int i = 0; i < doors.length; i ++ ) {
			doors[i] = new Door(i == whereAuto);
		}
		return new DoorSet(doors[0], doors[1], doors[2]);
	}

	/**
	 * Проверяем распределение вероятности выбора одной из дверей.
	 * Вероятность выбора должна быть одинаковой для каждой из дверей.
	 * <p>
	 * @throws Exception
	 */
	@Test
	public void ourSelectorWorksFine() throws Exception {
		final int rounds = 10000000;
		final double maxAllowedDeviation = 0.001d; // 0.1%
		int count[] = { 0, 0, 0 };
		for ( int i = 0; i < rounds; i ++ ) {
			int index = selector.random(3);
			count[index] ++;
		}
		double avg = (count[0] + count[1] + count[2]) / 3;
		for ( int i = 0; i < count.length; i ++ ) {
			double dev = Math.abs((count[i] - avg) / avg);
			assertTrue(dev <= maxAllowedDeviation);
		}
	}
	
	@Test
	public void testMontyHall() {
		final int rounds = 100000; // сколько раз пробуем для каждой стратегии 
		System.out.println("if I swap then " + test(rounds, new Swap()));
		System.out.println("if I keep then " + test(rounds, new Keep()));
	}
	
	/**
	 * Протестировать стратегию поведения.
	 * <p>
	 * @param rounds сколько раз тестировать правильность выбора
	 * @param strategy стратегия поведения
	 * @return количество "попаданий" в автомобили
	 */
	int test(int rounds, Strategy strategy) {
		int hits = 0;
		for ( int i = 0; i < rounds; i ++ ) {
			// Создаем двери.
			DoorSet set = makeDoors();
			// Выбираем первый раз
			int firstChoice = strategy.choose();
			// Теперь откроем одну из других дверей за которой нет автомобиля
			// и не мы выбрали. Тупо перебираем двери и добавляем те, которые не
			// попадают под условия открывания двери ведущим. 
			List<Integer> canOpen = new Vector<Integer>();
			for ( int k = 0; k < 3; k ++ ) {
				if ( k == firstChoice ) {
					// эту дверь мы не можем открыть, так как ее выбрали
				} else if ( set.doors[k].auto == true ) {
					// эту дверь мы не можем открыть, так как там авто
				} else {
					// а эту можем открыть
					canOpen.add(k);
				}
			}
			// ВОТ ОН ПАРАДОКС!!!!!! - у ведущего все же есть выбор
			if ( canOpen.size() > 1 ) {
				//System.out.println("First choice: " + firstChoice);
				//System.out.println("Doors:" + set);
				//System.out.println("Can open: " + canOpen);
				//System.out.println();
			}
			
			// Теперь откроем одну из доступных дверей
			int toOpen = canOpen.get(selector.random(canOpen.size()));
			set.doors[toOpen].opened = true;
			
			// Теперь теперь игрок меняет или не меняет свой выбор
			if ( set.isAuto(strategy.choose2(set, firstChoice)) ) {
				hits ++; // увеличиваем счетчик попаданий
			}
			
		}
		return hits;
	}

}
