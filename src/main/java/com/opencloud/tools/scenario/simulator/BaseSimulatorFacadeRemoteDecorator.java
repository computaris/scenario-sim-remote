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

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.opencloud.tools.scenario.definition.ScenarioRecognitionException;
import com.opencloud.tools.scenario.schema.validation.InvalidScenarioException;
import com.opencloud.tools.scenario.simulator.management.ScenarioBindingsDescription;
import com.opencloud.tools.scenario.simulator.management.SimulatorConfigurationException;
import com.opencloud.tools.scenario.simulator.management.SimulatorException;
import com.opencloud.tools.scenario.simulator.management.SimulatorFacade;
import com.opencloud.tools.scenario.simulator.monitoring.DialogStatsSnapshot;
import com.opencloud.tools.scenario.simulator.monitoring.SessionLifecycleListener;
import com.opencloud.tools.scenario.simulator.monitoring.SessionMessageListener;
import com.opencloud.tools.scenario.simulator.monitoring.SessionOutcome;
import com.opencloud.tools.scenario.simulator.monitoring.SessionStatusSnapshot;
import com.opencloud.tools.scenario.simulator.protocol.ProtocolAdaptorException;

/**
 * Base implementation for SimulatorFacade. Proxies local calls for scenario simulator.
 */
public class BaseSimulatorFacadeRemoteDecorator implements SimulatorFacade {
    protected SimulatorFacade simulatorFacade;
    protected static Logger log;

    public BaseSimulatorFacadeRemoteDecorator(SimulatorFacade simulatorFacade, Logger log) {
        this.simulatorFacade = simulatorFacade;
        BaseSimulatorFacadeRemoteDecorator.log = log;
    }

    @Override
    public void setEndpointAddress(String endpointName, String addressString) throws SimulatorConfigurationException {
        simulatorFacade.setEndpointAddress(endpointName, addressString);
    }

    @Override
    public Collection<String> getEndpointNames() {
        return simulatorFacade.getEndpointNames();
    }

    @Override
    public Collection<String> getSchemaNames() {
        return simulatorFacade.getSchemaNames();
    }

    @Override
    public Collection<Map<String, String>> getSchemaInfos() {
        return simulatorFacade.getSchemaInfos();
    }

    @Override
    public Collection<String> getProtocolAdaptorTypes() {
        return simulatorFacade.getProtocolAdaptorTypes();
    }

    @Override
    public Collection<Map<String, String>> getProtocolAdaptorTypeInfos() {
        return simulatorFacade.getProtocolAdaptorTypeInfos();
    }

    @Override
    public String getProtocolAdaptorTypeForSchema(String schema) throws SimulatorConfigurationException {
        return simulatorFacade.getProtocolAdaptorTypeForSchema(schema);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void createLocalEndpoint(String endpointName, String protocolAdaptorType, String propertiesFile, Collection<String> schemas)
            throws ProtocolAdaptorException, IllegalStateException, IOException, SimulatorConfigurationException {
        simulatorFacade.createLocalEndpoint(endpointName, protocolAdaptorType, propertiesFile, schemas);
    }

    @Override
    public void createLocalEndpoint(String localEndpointName, String protocolAdaptorType, Map<String, String> properties, Collection<String> schemas)
            throws ProtocolAdaptorException, IllegalStateException, SimulatorConfigurationException {
        simulatorFacade.createLocalEndpoint(localEndpointName, protocolAdaptorType, properties, schemas);
    }

    @Override
    public void bindRole(String roleName, String endpointName, String dialogName, String configName) throws SimulatorConfigurationException {
        simulatorFacade.bindRole(roleName, endpointName, dialogName, configName);
    }

    @Override
    public void loadDataSet(String dataSetName, String csvFilePath) throws IOException, SimulatorConfigurationException {
        simulatorFacade.loadDataSet(dataSetName, csvFilePath);
    }

    @Override
    public Collection<String> getDataSetNames() {
        return simulatorFacade.getDataSetNames();
    }

    @Override
    public void bindTable(String tableName, String dataSetName, String configName) throws SimulatorConfigurationException {
        simulatorFacade.bindTable(tableName, dataSetName, configName);
    }

    @Override
    public ScenarioBindingsDescription load(String scenarioFile, String configName)
            throws IOException, ScenarioRecognitionException, SimulatorConfigurationException, InvalidScenarioException {
        log.info("load: scen=" + scenarioFile + ", config=" + configName);
        return simulatorFacade.load(scenarioFile, configName);
    }

    @Override
    public String getConfigurationDescription() {
        return simulatorFacade.getConfigurationDescription();
    }

    @Override
    public Collection<String> getConfigurationNames() {
        return simulatorFacade.getConfigurationNames();
    }

    @Override
    public void setPreferredScenario(Map<String, Double> scenarios) throws SimulatorException {
        simulatorFacade.setPreferredScenario(scenarios);
    }

    @Override
    public void setPreferredScenario(String scenarioName) throws SimulatorException {
        simulatorFacade.setPreferredScenario(scenarioName);
    }

    @Override
    public boolean removeScenario(String scenarioName) {
        return simulatorFacade.removeScenario(scenarioName);
    }

    @Override
    public Collection<String> getScenarioNames() {
        return simulatorFacade.getScenarioNames();
    }

    @Override
    public Collection<String> getInitiatingScenarioNames() {
        return simulatorFacade.getInitiatingScenarioNames();
    }

    @Override
    public String getScenarioDescription(String scenarioName) {
        return simulatorFacade.getScenarioDescription(scenarioName);
    }

    @Override
    public ScenarioBindingsDescription getScenarioBindings(String scenarioName) {
        return simulatorFacade.getScenarioBindings(scenarioName);
    }

    @Override
    public String getConnectivityStatusSummary() {
        return simulatorFacade.getConnectivityStatusSummary();
    }

    @Override
    public SessionStatusSnapshot getSessionStatsSnapshot() {
        return simulatorFacade.getSessionStatsSnapshot();
    }

    @Override
    public void resetSessionAndDialogStats() {
        simulatorFacade.resetSessionAndDialogStats();
    }

    @Override
    public DialogStatsSnapshot getDialogStatsSnapshot() {
        return simulatorFacade.getDialogStatsSnapshot();
    }

    @Override
    public SessionOutcome runSession(String scenarioName, SessionMessageListener paramSessionMessageListener) throws SimulatorException {
        return simulatorFacade.runSession(scenarioName, paramSessionMessageListener);
    }

    @Override
    public boolean startGeneratingSessions() {
        return simulatorFacade.startGeneratingSessions();
    }

    @Override
    public void stopGeneratingSessions() {
        simulatorFacade.stopGeneratingSessions();
    }

    @Override
    public void setSessionRate(double paramDouble) {
        simulatorFacade.setSessionRate(paramDouble);
    }

    @Override
    public void rampUpSessionRate(double initialRate, double targetRate, int period) {
        simulatorFacade.rampUpSessionRate(initialRate, targetRate, period);
    }

    @Override
    public void addGlobalSessionLifecycleListener(SessionLifecycleListener sessionLifecycleListener) {
        simulatorFacade.addGlobalSessionLifecycleListener(sessionLifecycleListener);
    }

    @Override
    public void addGlobalSessionMessageListener(SessionMessageListener sessionMessageListener) {
        simulatorFacade.addGlobalSessionMessageListener(sessionMessageListener);
    }

    @Override
    public void waitUntilOperational(int timeout) throws SimulatorException {
        simulatorFacade.waitUntilOperational(timeout);
    }

    @Override
    public void quit(long timeout) {
        simulatorFacade.quit(timeout);
    }

    @Override
    public void quit() {
        simulatorFacade.quit();
    }
}
