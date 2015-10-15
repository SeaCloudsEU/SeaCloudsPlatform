package eu.seaclouds.monitor.reconfigDc;

import it.polimi.tower4clouds.data_collector_library.DCAgent;
import it.polimi.tower4clouds.manager.api.ManagerAPI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.brooklyn.rest.client.BrooklynApi;
import org.apache.brooklyn.rest.domain.ApplicationSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.seaclouds.monitor.reconfigDc.metrics.IsAppOnFire;
import it.polimi.tower4clouds.model.data_collectors.DCDescriptor;
import it.polimi.tower4clouds.model.ontology.InternalComponent;
import it.polimi.tower4clouds.model.ontology.Resource;

public class Registry {

    private static final Logger logger = LoggerFactory
            .getLogger(Registry.class);

    private Properties dcProperties;

    private Map<String, Resource> resourceById;

    private Set<Metric> applicationMetrics;

    private DCAgent dcAgent;

    private boolean registryInitialized = false;
    private boolean monitoringStarted = false;

    protected static final Registry _INSTANCE = new Registry();

    public static Integer CONFIG_SYNC_PERIOD = null;
    public static Integer KEEP_ALIVE = null;
    private static final int DEFAULT_CONFIG_SYNC_PERIOD = 30;

    protected Registry() {
    }

    public void init(String managerIP, int managerPort, Properties dcProperties) {

        if (registryInitialized)
            throw new RuntimeException("Registry was already initialized");

        this.dcProperties = dcProperties;

        resourceById = buildResources();

        if (this.dcProperties.get(DCProperties.DC_SYNC_PERIOD) != null) {
            CONFIG_SYNC_PERIOD = Integer.parseInt(this.dcProperties.get(
                    DCProperties.DC_SYNC_PERIOD).toString());
        }

        if (this.dcProperties.get(DCProperties.RESOURCES_KEEP_ALIVE_PERIOD) != null) {
            KEEP_ALIVE = Integer.parseInt(this.dcProperties.get(
                    DCProperties.RESOURCES_KEEP_ALIVE_PERIOD).toString());
        }

        // Build metrics
        applicationMetrics = buildDeployerMetrics();

        // Build the DCAgent
        dcAgent = new DCAgent(new ManagerAPI(managerIP, managerPort));

        // Add observers of metrics to the DCAgent
        for (Metric metric : applicationMetrics) {
            logger.debug("Added metric {} as observer of dcagent",
                    metric.getName());
            dcAgent.addObserver(metric);
        }

        // Build the DCDescriptor
        DCDescriptor dcDescriptor = new DCDescriptor();
        dcDescriptor
                .addMonitoredResources(getDeployerMetrics(), getResources());
        dcDescriptor.addResources(getResources());

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

    private Map<String, Resource> buildResources() {
        Map<String, Resource> map = new HashMap<String, Resource>();
        BrooklynApi deployer;

        if (dcProperties.getProperty(DCProperties.DEPLOYER_INSTANCE_USERNAME) != null
                & dcProperties
                        .getProperty(DCProperties.DEPLOYER_INSTANCE_PASSWORD) != null)
            deployer = new BrooklynApi(
                    "http://"
                            + dcProperties
                                    .getProperty(DCProperties.DEPLOYER_INSTANCE_IP)
                            + ":"
                            + dcProperties
                                    .getProperty(DCProperties.DEPLOYER_INSTANCE_PORT)
                            + "/",
                    dcProperties
                            .getProperty(DCProperties.DEPLOYER_INSTANCE_USERNAME),
                    dcProperties
                            .getProperty(DCProperties.DEPLOYER_INSTANCE_PASSWORD));
        else
            System.out.println(dcProperties
                    .getProperty(DCProperties.DEPLOYER_INSTANCE_IP)
                    + ":"
                    + dcProperties
                            .getProperty(DCProperties.DEPLOYER_INSTANCE_PORT));
        deployer = new BrooklynApi("http://"
                + dcProperties.getProperty(DCProperties.DEPLOYER_INSTANCE_IP)
                + ":"
                + dcProperties.getProperty(DCProperties.DEPLOYER_INSTANCE_PORT)
                + "/", "", "");

        List<ApplicationSummary> apps = deployer.getApplicationApi().list(null);
        for (ApplicationSummary app : apps) {
            map.put(app.getId(),
                    buildResource(app.getId() + "_id", app.getId()));
            logger.info("added: " + app.getId());
        }

        return map;
    }

    private Set<String> getDeployerMetrics() {

        Set<String> metricsNames = new HashSet<String>();
        for (Metric metric : applicationMetrics) {
            metricsNames.add(metric.getName());
        }

        return metricsNames;
    }

    private Set<Metric> buildDeployerMetrics() {

        Set<Metric> metrics = new HashSet<Metric>();

        Metric isAppOnFire = new IsAppOnFire();
        metrics.add(isAppOnFire);

        for (Metric m : metrics) {
            m.setDeployerInstanceIp((String) dcProperties
                    .get(DCProperties.DEPLOYER_INSTANCE_IP));
            m.setDeployerInstancePort((String) dcProperties
                    .get(DCProperties.DEPLOYER_INSTANCE_PORT));
            m.setDeployerUser((String) dcProperties
                    .get(DCProperties.DEPLOYER_INSTANCE_USERNAME));
            m.setDeployerPassword((String) dcProperties
                    .get(DCProperties.DEPLOYER_INSTANCE_PASSWORD));
        }

        return metrics;
    }

    public Set<Resource> getResources() {
        return new HashSet<Resource>(resourceById.values());
    }

    public static void initialize(String managerIP, int managerPort,
            Properties dcProperties) {
        _INSTANCE.init(managerIP, managerPort, dcProperties);
    }

    public static void startMonitoring() {
        _INSTANCE.start();
    }

    public static void stopMonitoring() {
        _INSTANCE.stop();
    }
}
