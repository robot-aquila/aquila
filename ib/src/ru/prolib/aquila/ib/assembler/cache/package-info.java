/**
 * Кэш данных IB API.
 * <p>
 * При работе с терминалом IB может возникнуть ситуация, когда обработка данных
 * не может быть выполнена в момент получения этих данных. Например, при
 * получении информации о позиции соответствующий позиции инструмент может
 * быть недоступен. То же самое касается заявок и сделок.
 * <p>
 * Для решения этой проблемы определенная часть данных, полученных через IB API,
 * кэшируется. Некоторые типы данных, такие как информация о контрактах и
 * позициях, остаются в кэше постоянно. Другие данные, такие как заявки и
 * сделки, находятся в кэше до тех пор, пока не будут обработаны и удалены явным
 * образом.
 * <p>
 */
package ru.prolib.aquila.ib.assembler.cache;