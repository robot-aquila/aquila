/**
 * Импорт данных из терминалом QUIK через DDE.
 * <p>
 * Данный пакет содержит классы, обеспечивающие механизм доступа к таблицам DDE
 * в локальной среде исполнения. Классы пакета решают несколько задач. Прежде
 * всего, это первичная адаптация данных в приемлимый для обработки вид. Кроме
 * того, пакет реализует механизм кеширования данных таблиц и таким образом в
 * каждый отдельный момент времени программе доступен самый полный и актуальный
 * набор данных, полученных от QUIK.
 * <p>
 * Поскольку порядок поступления данных по DDE неопределен, алгоритм обработки
 * данных не может опираться на последовательность поступления данных и просто
 * конвертировать ряд таблицы в атрибуты соответствующего объекта бизнес-модели.
 * Это может привести к несогласованному состоянию.
 * <p>
 * Например, время сведения заявки расчитывается по последней сделке заявки и
 * может быть выполнено только если исполненное количество заявки из таблицы
 * заявок и суммарное количество по сделкам заявки из таблицы сделок совпадают.
 * Если же заявка была отменена без исполнения (нет ни одной сделки), то время
 * отмены заявки берется из соответствующего поля таблицы заявок.
 * <p>
 * Если не проверять на согласованность данных в двух таблицах, то может
 * возникнуть ситуация, когда наблюдатели заявки начнут обрабатывать событие о
 * сведении в тот момент, когда средняя цена исполнения и исполненный объем по
 * заявке еще не будут корректно расчитаны, так как обновление таблицы сделок
 * еще не было получено и обработано. Таким образом, для перевода заявки из
 * одного статуса в другой требуется доступ к данным сразу двух таблиц.
 * <p>
 * Механизм кеширования данных позволяет подписываться на изменения одной
 * или нескольких таблиц и таким образом реагировать именно в тот момент,
 * когда данных становится достаточно для выполнения расчетов.
 */
package ru.prolib.aquila.quik.dde;