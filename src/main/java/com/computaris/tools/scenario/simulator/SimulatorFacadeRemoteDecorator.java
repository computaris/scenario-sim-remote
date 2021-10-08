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

import com.opencloud.scenariosimulator.thirdparty.guava.common.common.collect.Maps;
import com.opencloud.tools.scenario.definition.ScenarioRecognitionException;
import com.opencloud.tools.scenario.schema.validation.InvalidScenarioException;
import com.opencloud.tools.scenario.simulator.management.ScenarioBindingsDescription;
import com.opencloud.tools.scenario.simulator.management.SimulatorConfigurationException;
import com.opencloud.tools.scenario.simulator.management.SimulatorException;
import com.opencloud.tools.scenario.simulator.management.SimulatorFacade;
import com.opencloud.tools.scenario.simulator.monitoring.DialogStatsSnapshot;
import com.opencloud.tools.scenario.simulator.monitoring.SessionOutcome;
import com.opencloud.tools.scenario.simulator.monitoring.SessionStatusSnapshot;
import com.opencloud.tools.scenario.simulator.protocol.ProtocolAdaptorException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywordOverload;
import org.robotframework.javalib.annotation.RobotKeywords;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Actual implementation of SimulatorFacade for remote Robot keywords.
 */
@RobotKeywords
public class SimulatorFacadeRemoteDecorator extends BaseSimulatorFacadeRemoteDecorator {
    protected static final String LIB_PREFIX = "ScenSim";

    public SimulatorFacadeRemoteDecorator(SimulatorFacade simulatorFacade, Logger log) {
        super(simulatorFacade, log);
    }

    @RobotKeyword(LIB_PREFIX + "SetEndpointAddress")
    @ArgumentNames({"endpointName", "addressString"})
    public void scenSimSetEndpointAddress(String endpointName, String addressString) throws SimulatorConfigurationException {
        setEndpointAddress(endpointName, addressString);
    }

    @RobotKeyword(LIB_PREFIX + "GetEndpointNames")
    @ArgumentNames({})
    public Collection<String> scenSimGetEndpointNames() {
        return getEndpointNames();
    }

    @RobotKeyword(LIB_PREFIX + "GetSchemaNames")
    @ArgumentNames({})
    public Collection<String> scenSimGetSchemaNames() {
        return getSchemaNames();
    }

    @RobotKeyword(LIB_PREFIX + "GetSchemaInfos")
    @ArgumentNames({})
    public Collection<Map<String, String>> scenSimGetSchemaInfos() {
        return getSchemaInfos();
    }

    @RobotKeyword(LIB_PREFIX + "GetProtocolAdaptorTypes")
    @ArgumentNames({})
    public Collection<String> scenSimGetProtocolAdaptorTypes() {
        return getProtocolAdaptorTypes();
    }

    @RobotKeyword(LIB_PREFIX + "GetProtocolAdaptorTypeInfos")
    @ArgumentNames({})
    public Collection<Map<String, String>> scenSimGetProtocolAdaptorTypeInfos() {
        return getProtocolAdaptorTypeInfos();
    }

    @RobotKeyword(LIB_PREFIX + "GetProtocolAdaptorTypeForSchema")
    @ArgumentNames({})
    public String scenSimGetProtocolAdaptorTypeForSchema(String schema) throws SimulatorConfigurationException {
        return getProtocolAdaptorTypeForSchema(schema);
    }

    @RobotKeyword(LIB_PREFIX + "CreateLocalEndpoint")
    @ArgumentNames({"endpointName", "protocolAdaptorType", "properties", "schemas"})
    public void scenSimCreateLocalEndpoint(String endpointName, String protocolAdaptorType, String properties, String schemas)
            throws ProtocolAdaptorException, IllegalStateException, IOException, SimulatorConfigurationException {

        log.info("CreateLocalEndpoint : endPointName=" + endpointName + ", protocolAdaptorType=" + protocolAdaptorType + ", properties=" + properties + ", schemas=" + schemas);

        List<String> schemasAsList = new ArrayList<>(Arrays.asList(schemas.split(",")));
        Properties props = new Properties();
        props.load(new StringReader(properties));

        createLocalEndpoint(endpointName, protocolAdaptorType, Maps.fromProperties(props), schemasAsList);
    }

    @RobotKeyword(LIB_PREFIX + "CreateLocalEndpointWithConfigurationFile")
    @ArgumentNames({"endpointName", "protocolAdaptorType", "properties", "fileContent", "propertyNameForConfigurationFile", "schemas"})
    public void scenSimCreateLocalEndpointWithConfigurationFile(String endpointName, String protocolAdaptorType, String properties, String fileContent, String propertyNameForConfigurationFile, String schemas)
            throws ProtocolAdaptorException, IllegalStateException, IOException, SimulatorConfigurationException {

        File tempFile = createTmpFile(fileContent, "xml");

        try {
            log.info("CreateLocalEndpoint : endPointName=" + endpointName + ", protocolAdaptorType=" + protocolAdaptorType + ", properties=" + properties + ", schemas=" + schemas);
            List<String> schemasAsList = new ArrayList<>(Arrays.asList(schemas.split(",")));

            Properties props = new Properties();
            props.load(new StringReader(properties));
            props.setProperty(propertyNameForConfigurationFile, tempFile.getCanonicalPath());

            createLocalEndpoint(endpointName, protocolAdaptorType, Maps.fromProperties(props), schemasAsList);

        } finally {
            removeTempFile(tempFile);
        }
    }

    @RobotKeyword(LIB_PREFIX + "BindRole")
    @ArgumentNames({"roleName", "endpointName", "=dialogName", "=configName"})
    public void scenSimBindRole(String roleName, String endpointName, String dialogName, String configName) throws SimulatorConfigurationException {
        bindRole(roleName, endpointName, dialogName, configName);
    }

    @RobotKeywordOverload
    public void scenSimBindRole(String roleName, String endpointName, String dialogName) throws SimulatorConfigurationException {
        bindRole(roleName, endpointName, dialogName, null);
    }

    @RobotKeywordOverload
    public void scenSimBindRole(String roleName, String endpointName) throws SimulatorConfigurationException {
        bindRole(roleName, endpointName, null, null);
    }

    @RobotKeyword(LIB_PREFIX + "Load")
    @ArgumentNames({"scenarioFile", "configName"})
    public ScenarioBindingsDescription scenSimLoad(String scenarioFile, String configName)
            throws IOException, ScenarioRecognitionException, SimulatorConfigurationException, InvalidScenarioException {
        log.info("load: scen=" + scenarioFile + ", config=" + configName);
        return load(scenarioFile, configName);
    }

    @RobotKeyword(LIB_PREFIX + "LoadDataSet")
    @ArgumentNames({"dataSetName", "csvFileContent"})
    public void scenSimLoadDataSet(String dataSetName, String csvFilePath) throws IOException, SimulatorConfigurationException {

        File tempFile = createTmpFile(csvFilePath, "csv");

        try {
            loadDataSet(dataSetName, tempFile.getCanonicalPath());
        } finally {
            removeTempFile(tempFile);
        }

    }

    @RobotKeyword(LIB_PREFIX + "BindTable")
    @ArgumentNames({"tableName", "dataSetName", "configName"})
    public void scenSimBindTable(String tableName, String dataSetName, String configName) throws SimulatorConfigurationException {
        bindTable(tableName, dataSetName, configName);
    }

    @RobotKeywordOverload
    public void scenSimBindTable(String tableName, String dataSetName) throws SimulatorConfigurationException {
        bindTable(tableName, dataSetName, null);
    }

    @RobotKeyword(LIB_PREFIX + "LoadNoConfig")
    @ArgumentNames({"scenarioContent"})
    public ScenarioBindingsDescription scenSimloadNoConfig(String scenarioContent)
            throws IOException, ScenarioRecognitionException, SimulatorConfigurationException, InvalidScenarioException {
        log.info("loadNoConfig: scen=" + scenarioContent);

        File tempFile = createTmpFile(scenarioContent, "scen");

        try {
            return load(tempFile.getCanonicalPath(), null);
        } finally {
            removeTempFile(tempFile);
        }
    }

    private void removeTempFile(File tempFile) {
        FileUtils.deleteQuietly(tempFile);
    }


    private File createTmpFile(String fileContent, String fileExtension) throws IOException {
        File tempFile = File.createTempFile(fileExtension, null);

        FileUtils.writeStringToFile(tempFile, fileContent, Charset.defaultCharset(), false);
        return tempFile;
    }

    @RobotKeyword(LIB_PREFIX + "GetConfigurationDescription")
    @ArgumentNames({})
    public String scenSimGetConfigurationDescription() {
        return getConfigurationDescription();
    }

    @RobotKeyword(LIB_PREFIX + "GetConfigurationNames")
    @ArgumentNames({})
    public Collection<String> scenSimGetConfigurationNames() {
        return getConfigurationNames();
    }

    @RobotKeyword(LIB_PREFIX + "SetPreferredScenario")
    @ArgumentNames({"scenarios"})
    public void scenSimSetPreferredScenario(Map<String, Double> scenarios) throws SimulatorException {
        setPreferredScenario(scenarios);
    }

    @RobotKeyword(LIB_PREFIX + "SetPreferredScenario")
    @ArgumentNames({"scenarioName"})
    public void scenSimSetPreferredScenario(String scenarioName) throws SimulatorException {
        setPreferredScenario(scenarioName);
    }

    @RobotKeyword(LIB_PREFIX + "RemoveScenario")
    @ArgumentNames({"scenarioName"})
    public boolean scenSimRemoveScenario(String scenarioName) {
        return simulatorFacade.removeScenario(scenarioName);
    }

    @RobotKeyword(LIB_PREFIX + "GetScenarioNames")
    @ArgumentNames({})
    public Collection<String> scenSimGetScenarioNames() {
        return getScenarioNames();
    }

    @RobotKeyword(LIB_PREFIX + "GetInitiatingScenarioNames")
    @ArgumentNames({})
    public Collection<String> scenSimGetInitiatingScenarioNames() {
        return getInitiatingScenarioNames();
    }

    @RobotKeyword(LIB_PREFIX + "GetScenarioDescription")
    @ArgumentNames({"scenarioName"})
    public String scenSimGetScenarioDescription(String scenarioName) {
        return getScenarioDescription(scenarioName);
    }

    @RobotKeyword(LIB_PREFIX + "GetScenarioBindings")
    @ArgumentNames({"scenarioName"})
    public ScenarioBindingsDescription scenSimGetScenarioBindings(String scenarioName) {
        return getScenarioBindings(scenarioName);
    }

    @RobotKeyword(LIB_PREFIX + "RunSession")
    @ArgumentNames({"scenarioName"})
    public SessionOutcome scenSimRunSession(String scenarioName) throws SimulatorException {
        resetSessionAndDialogStats();
        return runSession(scenarioName, null);
    }

    @RobotKeyword(LIB_PREFIX + "VerifyStatus")
    public boolean scenSimVerifyStatus() throws SimulatorException {
        SessionStatusSnapshot sessionStatusSnapshot = getSessionStatsSnapshot();
        DialogStatsSnapshot dialogStatsSnapshot = getDialogStatsSnapshot();
        if (sessionStatusSnapshot.getNonMatchingSessionsCount() > 0 || dialogStatsSnapshot.getDialogsRejected() > 0) {
            return false;
        }
        return true;
    }

    @RobotKeyword(LIB_PREFIX + "StartGeneratingSessions")
    @ArgumentNames({})
    public boolean scenSimStartGeneratingSessions() {
        return startGeneratingSessions();
    }

    @RobotKeyword(LIB_PREFIX + "StopGeneratingSessions")
    @ArgumentNames({})
    public void scenSimStopGeneratingSessions() {
        stopGeneratingSessions();
    }

    @RobotKeyword(LIB_PREFIX + "SetSessionRate")
    @ArgumentNames({"sessionsPerSecond"})
    public void scenSimSetSessionRate(double paramDouble) {
        setSessionRate(paramDouble);
    }

    @RobotKeyword(LIB_PREFIX + "RampUpSessionRate")
    @ArgumentNames({"initialRate", "targetRate", "period"})
    public void scenSimRampUpSessionRate(double initialRate, double targetRate, int period) {
        rampUpSessionRate(initialRate, targetRate, period);
    }

    @RobotKeyword(LIB_PREFIX + "WaitUntilOperational")
    @ArgumentNames({"timeout"})
    public void scenSimWaitUntilOperational(int timeout) throws SimulatorException {
        waitUntilOperational(timeout);
    }

    @RobotKeyword(LIB_PREFIX + "QuitWithTimeout")
    @ArgumentNames({"timeout"})
    public void scenSimQuit(long timeout) {
        quit(timeout);
    }

    @RobotKeyword(LIB_PREFIX + "Quit")
    @ArgumentNames({})
    public void scenSimQuit() {
        quit();
    }

}
