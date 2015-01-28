/**
 * Пакет содержит компоненты событийной системы.
 * <p>
 * Класс {@link ru.prolib.aquila.core.EventTypeImpl EventTypeImpl} в тандеме с
 * {@link ru.prolib.aquila.core.EventDispatcherImpl EventDispatcherImpl}
 * позволяют блокировать генерацию событий путем использования стандартных
 * блокировок Java. Это может быть полезным, когда объект сначала подписывается
 * на определенное событие, а затем выполняет код, который обязательно должен
 * быть выполнен до поступления события.
 * <p>
 * 2012-06-11<br>
 * $Id: package-info.java 302 2012-11-05 04:02:02Z whirlwind $
 */
package ru.prolib.aquila.core;