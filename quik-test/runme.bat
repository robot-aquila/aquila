echo off
setlocal
SET CP=%CD%\bin;%CD%/../build/aquila-core.jar
SET CP=%CP%;%CD%/../build/aquila-dde.jar
SET CP=%CP%;%CD%/../build/aquila-dde-utils.jar
SET CP=%CP%;%CD%/../build/aquila-dde-jddesvr.jar
SET CP=%CP%;%CD%/../build/aquila-quik.jar
SET CP=%CP%;%CD%/../build/aquila-t2q.jar
SET CP=%CP%;%CD%/../build/aquila-t2q-jqt.jar
SET CP=%CP%;%CD%/../contrib/commons-lang3-3.1.jar
SET CP=%CP%;%CD%/../contrib/slf4j-api-1.6.4.jar
SET CP=%CP%;%CD%/../contrib/slf4j-log4j12-1.6.4.jar
SET CP=%CP%;%CD%/../contrib/log4j-1.2.16.jar
SET CP=%CP%;%CD%/../contrib/jddesvr.jar
SET CP=%CP%;%CD%/../contrib/JQTrans.jar
SET CP=%CP%;
echo %CP%

SET FILE="DdeDump"

mkdir bin
javac -cp %CP% -d bin src/test/%FILE%.java
java -cp %CP% -Djava.library.path=%CD%/../contrib/bin test.%FILE%
