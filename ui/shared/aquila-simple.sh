#!/bin/sh
echo "Starting up..."
CP=""
for FILE in `ls -1 lib`; do
    CP="$CP:lib/$FILE"
done
echo "$CP"
#     -Dswing.defaultlaf=com.sun.java.swing.plaf.gtk.GTKLookAndFeel \
#     -Dswing.defaultlaf=javax.swing.plaf.nimbus.NimbusLookAndFeel \
java -cp "$CP" -Xms64m -Xmx512m -Djava.library.path=bin \
    -Dswing.plaf.metal.controlFont="Arial-11" \
    -Dswing.plaf.metal.userFont="Arial-11" \
    ru.prolib.aquila.ui.Main
echo "Finished"



