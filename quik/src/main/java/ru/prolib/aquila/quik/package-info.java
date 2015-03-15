/**
 * Реализация терминала QUIK.
 * <p>
 * Настройки экспорта таблиц:
 * <p>
 * Общие для всех таблиц:<br>
 * <b>С заголовками столбцов</b> - включена<br>
 * <b>Формальные заголовки</b> - включена<br>
 * <b>Выводить пустые ячейки вместо нулей</b> - ВЫключена<br>
 * <p>
 * <b>ВНИМАНИЕ:</b> QUIK->Настройки->Основные, Программа->Экспорт данных.
 * Установить "При выдаче целиком таблицы, секунд" значение 120. Иначе высока
 * вероятность остановки передачи данных на всех-сделках. В соседнем поле
 * указать таймаут для строки 60 секунд.
 * <p>
 * Идентификация атрибутов выполняется по заголовку. Это очень удобно с точки
 * зрения пользователя: он имеет возможность просто добавить все имеющиеся
 * атрибуты в таблицу и не заморачиваться порядком следования колонок, как в
 * случае идентификации по номеру. В связи с этим, в экспорте обязательно
 * включение галки С заголовками столбцов. Это правило справедливо
 * для всех таблиц.
 * <p>
 * Идентификация выполняется по формальным заголовкам. Так что для всех таблиц 
 * необходимо включать галку Формальные заголовки.
 * <p>
 * Нет никакой возможности определить является ли нулевое значение актуальным
 * значение атрибута или это недоступное значение. Но если включить галку
 * Выводить пустые ячейки вместо нулей, то для тех значений, для которых
 * нулевые значения имеют смысл (например, текущая позиция, вариационная маржа,
 * и т.п.), так же будут передаваться пустые ячейки, что приведет к
 * некорректному отражению данных в объектах терминала. По этому, галка
 * Выводить пустые ячейки вместо нулей должна быть выключена для всех
 * таблиц. 
 * <p>
 * <b>ИНСТРУМЕНТЫ</b>: Из-за отсутствия в большинстве таблиц полей, позволяющих
 * сформировать полный дескриптор инструмента, невозможно напрямую сопоставить
 * ряд такой таблицы с инструментом. Для решения этой задачи используется два
 * способа определения инструмента:<br>
 * 1) по паре код инструмента и код класса для таблиц заявок и сделок<br>
 * 2) по краткому наименованию для таблиц позиций<br>
 * <p>
 * Для обеспечения этой возможности дескрипторы инструментов предварительно
 * регистрируются в специальном хранилище, которое обеспечивает доступ к
 * дескриптору по краткому наименованию или паре кодов инструмента и класса
 * инструмента. 
 * <p>
 * <b>ПОРТФЕЛИ и СЧЕТА: </b> Терминал создает отдельный портфель для каждой
 * строки из таблицы ограничений по клиентским счетам (фьючерсы) (далее -
 * портфель по деривативам). Портфель по деривативам идентифицируется по коду
 * фирмы и коду торгового счета.
 * <p>
 * Портфели по бумагам. Здесь момент не проработан. Счет должен создаваться
 * для каждого торгового счета, разделение по которым в таблицы портфелей
 * по бумагам отсутствует. Портфели по бумагам идентифицируются по комбинации
 * кода фирмы, кода клиента и кода торгового счета. При этом для определенной
 * категории портфелей, разделяются с другими портфелями. Здесь нужно
 * изворачиваться по поводу разделяемого несколькими портфелями баланса.  
 * <p>
 * <b>ПРИМЕЧАНИЯ:</b> замечено непонятное поведение для таблицы портфелей по
 * бумагам. В моменты между открытием и закрытием позиций приходят некорректные
 * данные о балансе и доступных средствах. Ощущение такое, что суммируется или
 * вычитается не вовремя и в этом несогласованном состоянии передается
 * DDE-серверу. Например, средства = 2600, при открытии лонга LHOH по рыночной
 * получаем тик средства 400, после чего возвращается на 2600. При закрытии
 * наоборот: увеличивается почти в два раза на несколько тиков, затем
 * нормализуется. Все ничего, если не вешать на эти данные условные
 * срабатывания. Где еще аналогичные глюки вылезут, неизвестно. 
 * <p>
 * <b>ЗАЯВКИ:</b> Таблицы стоп-заявок, заявок и сделок хранят информацию о
 * соответствующих объектах за целый день. Но терминал может быть запущен в
 * любое время дня и в этом случае, генерация полноги цикла событий по ранним
 * объектам может дать нежелательную реакцию. В связи с этим, события по
 * заявкам, стоп-заявкам и сделкам с временем раньше времени запуска ассемблера
 * особым образом фильтруются. Детали генерации событий по заявкам,
 * стоп-заявкам и сделкам описаны в пакете
 * {@link ru.prolib.aquila.quik.assembler}.  
 * <p>
 * 2012-09-06<br>
 * $Id$
 */
package ru.prolib.aquila.quik;