/*
 * (C) Copyright 2020 Computaris International (http://computaris.com/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.computaris.tools.scenario.simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.opencloud.tools.scenario.simulator.ScenarioSimulatorFactory;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.robotframework.javalib.library.AnnotationLibrary;
import org.robotframework.remoteserver.RemoteServer;

import com.computaris.tools.scenario.simulator.SimulatorFacadeRemoteDecorator;
import com.opencloud.tools.scenario.simulator.management.SimulatorFacade;

public final class ScenarioSimulatorMain extends AnnotationLibrary {
    private static final String SIMULATOR_RHINOREMOTE_PORT = "com.computaris.robotremote.port";
    private static final String REMOTE_KEYWORD_DOCUMENTATION_EXPLANATION = "https://docs.opencloud.com/ocdoc/books/scenario-simulator/2.3.0/scenario-simulator-user-guide/managing-the-scenario-simulator/help-with-simulator-commands.html";
    private static final String REMOTE_LIBRARY_LOCATION = "com/computaris/tools/scenario/simulator/*.class";

    public ScenarioSimulatorMain() {
        super(REMOTE_LIBRARY_LOCATION);
    }

    @Override
    public String getKeywordDocumentation(String keywordName) {
        if (keywordName.equals("__intro__")) {
            return REMOTE_KEYWORD_DOCUMENTATION_EXPLANATION;
        } else {
            return super.getKeywordDocumentation(keywordName);
        }
    }

    private static void startRemoteServer(SimulatorFacade simulatorFacade, Logger log) throws Exception {
        RemoteServer.configureLogging();
        final String portNumber = System.getProperty(SIMULATOR_RHINOREMOTE_PORT, "8270");
        final RemoteServer server = new RemoteServer(Integer.parseInt(portNumber));
        server.putLibrary("/SimulatorFacade", simulatorFacade);
        log.info("Starting RhinoRemote server on port=" + portNumber);
        server.start();
    }

    public static void main(String[] args) throws Exception {
        Logger log = Logger.getLogger("simulator.main");
        try {
            log.info("Initialising the simulator...");
            final SimulatorFacade simulatorFacade = ScenarioSimulatorFactory.createSimulator();
            SimulatorFacadeRemoteDecorator simulatorFacadeRemote = new SimulatorFacadeRemoteDecorator(simulatorFacade, log);
            startRemoteServer(simulatorFacadeRemote, log);

        } catch (Throwable throwable) {
            if (log == null) {
                System.err.println("Exiting simulator due to an Exception or Error : " + throwable.getLocalizedMessage());
                throwable.printStackTrace();
            } else {
                log.error("Exiting due to the following:", throwable);
            }

            System.exit(1);
        }
    }

}
