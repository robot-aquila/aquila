echo Starting up...
echo off
setLocal EnableDelayedExpansion
set CP="
for /R ./lib %%f in (*.jar) do (
    set CP=!CP!;%%f
)
set CP=!CP!"
rem ==========================
rem If you got troubles:
rem -verbose 
rem -Dsun.java2d.d3d=false
rem Alternatively, set the J2D_D3D environment variable to 'false' prior to
rem starting your application (or set it globally).
rem more info http://www.oracle.com/technetwork/java/javase/6u10-142936.html
rem ==========================
java -cp %CP% -Xms64m -Xmx512m -Djava.library.path=bin ru.prolib.aquila.ui.Main
echo Finished
