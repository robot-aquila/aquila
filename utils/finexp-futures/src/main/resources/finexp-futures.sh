#!/bin/sh
### BEGIN INIT INFO
# Provides:          finexp-futures
# Required-Start:    $local_fs $network $named $time $syslog
# Required-Stop:     $local_fs $network $named $time $syslog
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Description:       Start export of FINAM/MOEX futures data
### END INIT INFO

PROG_PATH=/home/whirlwind/VirtualBox/shared/aquila
SCRIPT="/usr/bin/java -jar finexp-futures-0.14.3.jar -r data/finexp-futures-data --skip-integrity-test"
RUNAS=whirlwind
PIDFILE="${PROG_PATH}/finexp-futures.pid"
LOGFILE="${PROG_PATH}/finexp-futures.log"

start() {
	if [ -f "$PIDFILE" ] && kill -0 $(cat "${PIDFILE}"); then
		echo 'Service already running' >&2
		return 1
	fi
	echo 'Starting service...' >&2
	local CMD="$SCRIPT &> \"$LOGFILE\" & echo \$!"
	(cd "${PROG_PATH}" && exec su -c "$CMD" "$RUNAS" > "$PIDFILE")
	echo 'Service started' >&2
}

stop() {
	if [ ! -f "$PIDFILE" ] || ! kill -0 $(cat "${PIDFILE}"); then
		echo 'Service not running' >&2
		return 1
	fi
	echo 'Stopping service...' >&2
	kill -15 $(cat "$PIDFILE") && rm -f "$PIDFILE"
	echo 'Service stopped' >&2
}

case "$1" in
	start)
		start
		;;
	stop)
		stop
		;;
	restart)
		stop
		start
		;;
	*)
		echo "Usage: $0 {start|stop|restart}"
esac
