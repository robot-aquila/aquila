/**
 * Автоматы состояний.
 * <p>
 * Предложенная архитектура ориентирована на создание автоматов активных
 * состояний. В данном контексте, состояние - это не просто некий
 * классифицирующий маркер, но и все связанные с этим состоянием програмные
 * элементы: выходы, триггеры, входы, функции и данные. Каждое отдельное
 * состояние может быть разработано независимым от других состояний. При этом,
 * состояния могут быть связаны опосредованно общими данными, которые легко
 * выносятся за интерфейс в обособленную абстракцию. 
 * <p>
 * Например, состояние использует параметры, рассчитанные в процессе работы
 * предшествующего состояния. Оба состояния обмениваются данными через
 * хранилище-посредника. Образована связь на уровне данных, но не состояний.
 * Таким образом, множество различных автоматов может быть получено путем
 * комбинации различных состояний.
 * <p>
 * Данная реализация так же позволяет организовывать автоматы как с внешней
 * подачей данных (когда обработка данных инициируется извне), так и реагирующих
 * на внешние события (с помощью триггеров, проксирующих асинхронные события на
 * вход).
 * <p>
 * 
 * <h2>Некоторые особенности архитектуры</h2>
 * <p>
 * <s><b>Генерировать выходной сигнал в момент входа нельзя.</b><p>
 * Это сделано для того, что бы снизить количество нехарактерных использований
 * данной абстракции. Если позволить определять выход уже на входе, то возникает
 * соблазн плодить состояния на &quot;каждый чих&quot;. Такой подход с точки
 * зрения облегчения разработки самих состояний, несомненно, плюс. Но на
 * сборке автомата, структуру которого желательно видеть целиком, это скажется
 * негативно. В связи с этим, создание состояния с единственным выходом, переход
 * из которого осуществляется просто по факту входа в это состояние не
 * приветствуется.
 * <p>
 * <b>Хотя,</b> с другой стороны, рассмотрим ситуацию, когда в промежуток
 * времени между выходом из предыдущего состояния и входом в новое происходит
 * нечто, что исключает отработку нового состояния в нормальном режиме: когда
 * выход осуществляется только в результате анализа поступающих данных.
 * Например, в предыдущем состоянии была выставления заявка на покупку. 
 * Если заявка была исполнена в момент перехода, то в рамках автомата этот факт
 * можно определить, в лучшем случае, в процедуре входа в следующем состоянии.
 * Но если архитектура не способствует решению подобных ситуаций (а их можно
 * назвать типовыми), эффективность подобного решения под вопросом.  
 * <p>
 * </s>
 * TODO: здесь подумать, может какой-то опциональный выход типа Error сделать.
 * Плодить выходы для данной ситуации не хочется. Было бы здорово, что бы по
 * факту подписки создавался новый выход. Как это можно сделать автоматически,
 * пока идей нет.
 * <p>
 * В случае необходимости выполнить некую последовательность действий,
 * результатом выполнения которой молжет быть либо успех, либо фатальная ошибка
 * (исключение), не следует использовать создание нового состояния. Вместо
 * этого, такую последовательность следует скрывать за вызовом метода другого
 * объекта. Возникновение исключения на этом уровне всегда будет приводить к
 * останову конечного автомата. Если же речь идет об исключениях, которые
 * используются для ветвления алгоритма, то в этом случае нужно создавать
 * соответствующий выход.
 */
package ru.prolib.aquila.core.sm;