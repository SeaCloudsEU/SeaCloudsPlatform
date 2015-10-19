package eu.seaclouds.monitor.nuroDc;

import it.polimi.tower4clouds.data_collector_library.DCAgent;
import it.polimi.tower4clouds.manager.api.ManagerAPI;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.monitor.nuroDc.metrics.NUROServerLastMinuteAverageRunTime;
import eu.seaclouds.monitor.nuroDc.metrics.NUROServerLastMinuteAverageThroughput;
import eu.seaclouds.monitor.nuroDc.metrics.NUROServerLastMinutePlayerCount;
import eu.seaclouds.monitor.nuroDc.metrics.NUROServerLastMinuteRequestCount;
import eu.seaclouds.monitor.nuroDc.metrics.NUROServerLastTenSecondsAverageRunTime;
import eu.seaclouds.monitor.nuroDc.metrics.NUROServerLastTenSecondsAverageThroughput;
import eu.seaclouds.monitor.nuroDc.metrics.NUROServerLastTenSecondsPlayerCount;
import eu.seaclouds.monitor.nuroDc.metrics.NUROServerLastTenSecondsRequestCount;
import it.polimi.tower4clouds.model.data_collectors.DCDescriptor;
import it.polimi.tower4clouds.model.ontology.InternalComponent;
import it.polimi.tower4clouds.model.ontology.Resource;

public class Registry {

    private static final Logger logger = LoggerFactory
            .getLogger(Registry.class);

    private Properties dcProperties;

    private Set<Metric> nuroMetrics;

    private DCAgent dcAgent;

    private boolean registryInitialized = false;
    private boolean monitoringStarted = false;

    protected static final Registry _INSTANCE = new Registry();

    public static Integer CONFIG_SYNC_PERIOD = null;
    public static Integer KEEP_ALIVE = null;
    private static final int DEFAULT_CONFIG_SYNC_PERIOD = 30;

    protected Registry() {
    }

    public void init(Properties dcProperties) {

        if (registryInitialized)
            throw new RuntimeException("Registry was already initialized");

        this.dcProperties = dcProperties;

        if (this.dcProperties.get(DCProperties.DC_SYNC_PERIOD) != null) {
            CONFIG_SYNC_PERIOD = Integer.parseInt(this.dcProperties.get(
                    DCProperties.DC_SYNC_PERIOD).toString());
        }

        if (this.dcProperties.get(DCProperties.RESOURCES_KEEP_ALIVE_PERIOD) != null) {
            KEEP_ALIVE = Integer.parseInt(this.dcProperties.get(
                    DCProperties.RESOURCES_KEEP_ALIVE_PERIOD).toString());
        }

        // Build metrics
        nuroMetrics = buildNuroMetrics();

        // Build the DCAgent
        dcAgent = new DCAgent(new ManagerAPI(this.dcProperties.get(
                DCProperties.MANAGER_IP).toString(),
                Integer.parseInt(this.dcProperties.get(
                        DCProperties.MANAGER_PORT).toString())));

        // Add observers of metrics to the DCAgent
        for (Metric metric : nuroMetrics) {
            logger.debug("Added metric {} as observer of dcagent",
                    metric.getName());
            dcAgent.addObserver(metric);
        }

        // Build the DCDescriptor
        DCDescriptor dcDescriptor = new DCDescriptor();
        dcDescriptor
                .addMonitoredResource(
                        getNuroMetrics(),
                        buildResource(
                                this.dcProperties
                                        .getProperty(DCProperties.INTERNAL_COMPONENT_ID),
                                this.dcProperties
                                        .getProperty(DCProperties.INTERNAL_COMPONENT_TYPE)));
        dcDescriptor.addResource(buildResource(this.dcProperties
                .getProperty(DCProperties.INTERNAL_COMPONENT_ID),
                this.dcProperties
                        .getProperty(DCProperties.INTERNAL_COMPONENT_TYPE)));

        dcDescriptor
                .setConfigSyncPeriod(CONFIG_SYNC_PERIOD != null ? CONFIG_SYNC_PERIOD
                        : DEFAULT_CONFIG_SYNC_PERIOD);

        dcDescriptor.setKeepAlive(KEEP_ALIVE != null ? KEEP_ALIVE
                : (DEFAULT_CONFIG_SYNC_PERIOD + 15));

        dcAgent.setDCDescriptor(dcDescriptor);
        registryInitialized = true;

    }

    private void start() {
        if (!registryInitialized)
            throw new RuntimeException("Registry was not initialized");
        if (!monitoringStarted) {
            logger.info("Starting monitoring");
            dcAgent.stop();
            dcAgent.start();
            monitoringStarted = true;
        } else {
            logger.warn("Monitoring was already started");
        }
    }

    private void stop() {
        if (monitoringStarted) {
            logger.info("Stopping monitoring");
            dcAgent.stop();
            monitoringStarted = false;
        } else {
            logger.warn("Monitoring was not running");
        }
    }

    private Resource buildResource(String id, String type) {

        return new InternalComponent(type, id);

    }

    private Set<String> getNuroMetrics() {

        Set<String> metricsNames = new HashSet<String>();
        for (Metric metric : nuroMetrics) {
            metricsNames.add(metric.getName());
        }

        return metricsNames;
    }

    private Set<Metric> buildNuroMetrics() {

        Set<Metric> metrics = new HashSet<Metric>();

        Metric lastMinuteAvgRuntime = new NUROServerLastMinuteAverageRunTime();
        metrics.add(lastMinuteAvgRuntime);
        Metric lastMinuteAvgThr = new NUROServerLastMinuteAverageThroughput();
        metrics.add(lastMinuteAvgThr);
        Metric lastMinutePlayerCount = new NUROServerLastMinutePlayerCount();
        metrics.add(lastMinutePlayerCount);
        Metric lastMinuteRequestCount = new NUROServerLastMinuteRequestCount();
        metrics.add(lastMinuteRequestCount);
        Metric lastTenSecondsAvgRuntime = new NUROServerLastTenSecondsAverageRunTime();
        metrics.add(lastTenSecondsAvgRuntime);
        Metric lastTenSecondsAvgThr = new NUROServerLastTenSecondsAverageThroughput();
        metrics.add(lastTenSecondsAvgThr);
        Metric lastTenSecondsPlayerCount = new NUROServerLastTenSecondsPlayerCount();
        metrics.add(lastTenSecondsPlayerCount);
        Metric lastTenSecondsRequestCount = new NUROServerLastTenSecondsRequestCount();
        metrics.add(lastTenSecondsRequestCount);

        for (Metric m : metrics) {
            m.setNuroInstanceIp((String) dcProperties
                    .get(DCProperties.NURO_INSTANCE_IP));
            m.setNuroInstancePort((String) dcProperties
                    .get(DCProperties.NURO_INSTANCE_PORT));
            m.setNuroUser((String) dcProperties
                    .get(DCProperties.NURO_INSTANCE_USERNAME));
            m.setNuroPassword((String) dcProperties
                    .get(DCProperties.NURO_INSTANCE_PASSWORD));
        }

        return metrics;
    }

    public Set<Resource> getResources() {
        Set<Resource> toReturn = new HashSet<Resource>();
        toReturn.add(buildResource(this.dcProperties
                .getProperty(DCProperties.INTERNAL_COMPONENT_ID),
                this.dcProperties
                        .getProperty(DCProperties.INTERNAL_COMPONENT_TYPE)));
        return toReturn;
    }

    public static void initialize(Properties dcProperties) {
        _INSTANCE.init(dcProperties);
    }

    public static void startMonitoring() {
        _INSTANCE.start();
    }

    public static void stopMonitoring() {
        _INSTANCE.stop();
    }
}
