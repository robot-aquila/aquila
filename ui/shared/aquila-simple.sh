#!/bin/sh
echo "Starting up..."
CP=""
for FILE in `ls -1 lib`; do
    CP="$CP:lib/$FILE"
done
echo "$CP"
java -cp "$CP" -Xms64m -Xmx512m -Djava.library.path=bin ru.prolib.aquila.ui.Main
echo "Finished"



