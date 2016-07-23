Параметры формы

market = Группа данных
em = Finam ID инструмента
code = SPFB.Символ для ФОРТС или Символ для акций (в реальности не важно, ни на что не влияет)
apply = 0 ??? всегда ноль?
df = день месяца с
mf = месяц с (начиная с нуля)
yf = год с
from = дата с в формате DD.MM.YYYY
dt = день месяца по
mt = месяц по (начиная с нуля)
yt = год по
to = дата по в формате DD.MM.YYYY
p = 1 - тики, 2 - минуты, 3 - 5мин, 4 - 10мин, 5 - 15мин, 6 - 30мин, 7 - 1час, 8 - 1день, 9 - 1неделя
f = имя выходного файла (без расширения)
e = .csv или .txt
cn = имя контракта (произвольное поле в форме)
dtf = формат даты 1 - ггггммдд, 2 - ггммдд, 3 - ддммгг, 4 - дд/мм/гг, 5 - мм/дд/гг
tmf = формат времени 1 - ччммсс, 2 - ччмм, 3 - чч:мм:сс, 4 - чч:мм
MSOR= 0 - время начала свечи, 1 - окончания свечи
mstimever=0 время московское выкл или mstime=on&mstimever=1 когда время московское выбрано 
sep= разделитель полей 1 - запятая, 2 - точка, 3 - точка с запятой, 4 - табуляция, 5 - пробел
sep2= разделитель разрядов 1 - нет, 2 - точка, 3 - запятая, 4 - пробел, 5 - кавычка
datf= формат строк 
	// Только для свечей
	1 - TICKER, PER, DATE, TIME, OPEN, HIGH, LOW, CLOSE, VOL
	2 - TICKER, PER, DATE, TIME, OPEN, HIGH, LOW, CLOSE
	3 - TICKER, PER, DATE, TIME, CLOSE, VOL
	4 - TICKER, PER, DATE, TIME, CLOSE
	5 - DATE, TIME, OPEN, HIGH, LOW, CLOSE, VOL
	// Только для тиков
	6 - TICKER, PER, DATE, TIME, LAST, VOL
	7 - TICKER, DATE, TIME, LAST, VOL
	8 - TICKER, DATE, TIME, LAST
	9 - DATE, TIME, LAST, VOL
	10 - DATE, TIME, LAST
	11 - DATE, TIME, LAST, VOL, ID
at=1 добавить заголовок файла или не добавлять параметр, если выключено
fsp=1 заполнять периоды без сделок или не добавлять параметр, если выключено

Request example:

GET /SPFB.RTS-9.16_160701_160701.csv?market=14&em=420493&code=SPFB.RTS-9.16&apply=0&df=1&mf=6&yf=2016&from=01.07.2016&dt=1&mt=6&yt=2016&to=01.07.2016&p=1&f=SPFB.RTS-9.16_160701_160701&e=.csv&cn=SPFB.RTS-9.16&dtf=1&tmf=1&MSOR=0&mstimever=0&sep=1&sep2=1&datf=9&at=1 HTTP/1.1
Host: 195.128.78.52
Connection: keep-alive
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36 OPR/38.0.2220.41
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
Referer: http://www.finam.ru/profile/mosbirzha-fyuchersy/rts-9-16-riu6/export/?market=14&em=420493&code=RIU6&apply=0&df=1&mf=5&yf=2016&from=01.06.2016&dt=1&mt=5&yt=2016&to=01.06.2016&p=1&f=RIU6_160601_160601&e=.csv&cn=RIU6&dtf=1&tmf=1&MSOR=0&mstimever=0&sep=1&sep2=1&datf=9&at=1
Accept-Encoding: gzip, deflate, lzma, sdch
Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4

Response example:

HTTP/1.1 200 OK
Cache-Control: private
Date: Sat, 23 Jul 2016 08:19:51 GMT
Transfer-Encoding: chunked
Content-Type: finam/expotfile
Server: Microsoft-IIS/6.0
X-Powered-By: ASP.NET
Content-Disposition: attachment; filename="SPFB.RTS-9.16_160701_160701.csv"
Content-Encoding: gzip
Vary: Accept-Encoding



