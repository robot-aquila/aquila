esource interpreted as Document but transferred with MIME type finam/expotfile: "

http://195.128.78.52/SPFB.RTS_160714_160714-23.txt?market=14&em=17455&code=SPFB.RTS&dtf=1&tmf=1&MSOR=1&mstime=on&mstimever=1&sep=1&sep2=1&datf=9&at=1

http://195.128.78.52/APTK_160715_160715.txt?market=1&em=13855&code=APTK&appЕ&cn=APTK&dtf=1&tmf=1&MSOR=1&mstime=on&mstimever=1&sep=1&sep2=1&datf=9&at=1

curl "http://195.128.78.52/BANE_160715_160714.txt?market=1&em=81757&code=BANE&apply=0&df=15&mf=6&yf=2016&from=15.07.2016&dt=14&mt=6&yt=2016&to=14.07.2016&p=1&f=BANE_160715_160714&e=.txt&cn=BANE&dtf=1&tmf=1&MSOR=1&mstime=on&mstimever=1&sep=1&sep2=1&datf=9&at=1" -H "Accept-Encoding: gzip, deflate, lzma, sdch" -H "Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4" -H "Upgrade-Insecure-Requests: 1" -H "User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36 OPR/38.0.2220.41" -H "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8" -H "Referer: http://www.finam.ru/profile/moex-akcii/bashneft-ank-ao/export/?market=1&em=81757&code=BANE&apply=0&df=15&mf=6&yf=2016&from=15.07.2016&dt=14&mt=6&yt=2016&to=14.07.2016&p=1&f=BANE_160715_160714&e=.txt&cn=BANE&dtf=1&tmf=1&MSOR=1&mstime=on&mstimever=1&sep=1&sep2=1&datf=9&at=1" -H "Connection: keep-alive" --compressed


http://195.128.78.52/SPFB.BR-10.16_160715_160713.csv?market=14&em=454403&code=SPFB.BR-10.16&apply=0&df=15&mf=6&yf=2016&from=15.07.2016&dt=13&mt=6&yt=2016&to=13.07.2016&p=1&f=SPFB.BR-10.16_160715_160713&e=.csv&cn=SPFB.BR-10.16&dtf=1&tmf=1&MSOR=1&mstime=on&mstimever=1&sep=1&sep2=1&datf=9&at=1

http://195.128.78.52/BR-10.16_160715_160713.csv?market=14&em=454403&code=SPFB.BR-10.16&apply=0&df=15&mf=6&yf=2016&from=15.07.2016&dt=13&mt=6&yt=2016&to=13.07.2016&p=1&f=BR-10.16_160715_160713&e=.csv&cn=BR-10.16&dtf=1&tmf=1&MSOR=1&mstime=on&mstimever=1&sep=1&sep2=1&datf=9&at=1


market = √руппа данных
em = Finam ID инструмента
code = SPFB.—имвол дл€ ‘ќ–“— или —имвол дл€ акций
apply = 0 ???
df = день мес€ца с
mf = мес€ц с (начина€ с нул€)
yf = год с
from = дата с в формате DD.MM.YYYY
dt = день мес€ца по
mt = мес€ц по (начина€ с нул€)
yt = год по
to = дата по в формате DD.MM.YYYY
p = 1 - тики, 2 - минуты, 3 - 5мин, 4 - 10мин, 5 - 15мин, 6 - 30мин, 7 - 1час, 8 - 1день, 9 - 1недел€
f = им€ выходного файла (без расширени€)
e = .csv или .txt
cn = им€ контракта (произвольное поле в форме)
dtf = формат даты ??? 1 - ггггммдд, 
tmf = формат времени ??? 1 - ччммсс
MSOR=1 ???
mstime=on ???
mstimever=1 ???
sep=1 ??? разделитель полей
sep2=1 ??? разделитель разр€дов
datf=9 формат строк DATE, TIME, LAST, VOL
at=1 добавить заголовок файла




GET /SPFB.BR-10.16_160715_160713.csv?market=14&em=454403&code=SPFB.BR-10.16&apply=0&df=15&mf=6&yf=2016&from=15.07.2016&dt=13&mt=6&yt=2016&to=13.07.2016&p=1&f=SPFB.BR-10.16_160715_160713&e=.csv&cn=SPFB.BR-10.16&dtf=1&tmf=1&MSOR=1&mstime=on&mstimever=1&sep=1&sep2=1&datf=9&at=1 HTTP/1.1
Host: 195.128.78.52
Connection: keep-alive
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36 OPR/38.0.2220.41
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
Referer: http://www.finam.ru/profile/mosbirzha-fyuchersy/br-10-16-brv6/export/?market=14&em=454403&code=BRV6&apply=0&df=13&mf=6&yf=2016&from=13.07.2016&dt=13&mt=6&yt=2016&to=13.07.2016&p=1&f=BRV6_160713_160713&e=.csv&cn=BRV6&dtf=1&tmf=1&MSOR=1&mstime=on&mstimever=1&sep=3&sep2=1&datf=9&at=1
Accept-Encoding: gzip, deflate, lzma, sdch
Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4

