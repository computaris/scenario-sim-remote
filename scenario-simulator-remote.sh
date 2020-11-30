#!/bin/sh

#-------------------------------
# Starts the scenario simulator remote.
#
#-------------------------------

fail() { echo "$*" >&2; exit 1 ; }

CLASSPATH="${SIMULATOR_HOME}" # For log4j config
for library in `find "${SIMULATOR_HOME}/lib" -name '*.jar'|grep -v 'scenario-editor*.jar'`
do
    CLASSPATH="$CLASSPATH:$library"
done

. "$SIMULATOR_HOME/config_variables"

java $OPTIONS -classpath "$CLASSPATH" \
  -Dcom.opencloud.scenario-packs="${SIMULATOR_HOME}/protocols" \
  -Dcom.opencloud.simulator-log-directory="${SIMULATOR_HOME}/logs/" \
  -Dcom.opencloud.simulator-log-name="simulator.log" \
  com.computaris.tools.scenario.simulator.ScenarioSimulatorMain "$@"
