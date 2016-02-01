package eu.seaclouds.monitor.datacollector;

import it.polimi.tower4clouds.data_collector_library.DCAgent;
import it.polimi.tower4clouds.manager.api.ManagerAPI;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.monitor.datacollector.metrics.ModuleAvailability;
import eu.seaclouds.monitor.datacollector.metrics.NuroMetric;
import it.polimi.tower4clouds.model.data_collectors.DCDescriptor;
import it.polimi.tower4clouds.model.ontology.InternalComponent;
import it.polimi.tower4clouds.model.ontology.Resource;

/**
 * A Registry keeps track of all the resources 
 * for which one of the provided metrics need to be monitored.
 * It is implemented as a singleton that initializes the collecting 
 * of each metric with an empty set of resources
 * and allow to dynamically add a new resource for a specific metric,
 * by re-initializing the collecting of such metric
 * with the updated set of resources.
 * @author micheleguerriero
 *
 */
public class Registry {

    private static final Logger logger = LoggerFactory
            .getLogger(Registry.class);

    private Set<Metric> providedMetrics;

    private DCAgent dcAgent;

    private boolean registryInitialized = false;
    private boolean monitoringStarted = false;

    protected static final Registry _INSTANCE = new Registry();

    public static Integer CONFIG_SYNC_PERIOD = null;
    public static Integer KEEP_ALIVE = null;
    private static final int DEFAULT_CONFIG_SYNC_PERIOD = 30;
    private static Map<Resource,URL> resources = new HashMap<Resource, URL>();

    protected Registry() {
    }

    /**
     * This method is used to initialized the collecting of each metric.
     * It initializes a DCAgent with the manager_ip and manager_port parameters
     * in order to communicate with Tower 4Clouds.
     * It then build a DCDescriptor with the list of all the provided metrics
     * and the set of monitored resources for each provided metric.
     * It then start the DCAgent, which enact the collecting of each metric
     * and communicate to Tower 4Clouds the set of monitored resources 
     * for each provided metric.
     * @param dc_sync_period
     * @param resources_keep_alive_period
     * @param manager_ip
     * @param manager_port
     */
    public void init(String dc_sync_period, String  resources_keep_alive_period, String manager_ip, String manager_port) {

        if (registryInitialized)
            throw new RuntimeException("Registry was already initialized");

        if (dc_sync_period != null) {
            CONFIG_SYNC_PERIOD = Integer.parseInt(dc_sync_period);
        }

        if (resources_keep_alive_period != null) {
            KEEP_ALIVE = Integer.parseInt(resources_keep_alive_period);
        }

        // Build metrics
        providedMetrics = buildProvidedMetrics();

        // Build the DCAgent
        dcAgent = new DCAgent(new ManagerAPI(manager_ip, Integer.parseInt(manager_port)));

        // Add observers of metrics to the DCAgent
        for (Metric metric : providedMetrics) {
            logger.debug("Added metric {} as observer of dcagent",
                    metric.getMonitoredMetric());
            dcAgent.addObserver(metric);
        }

        // Build the DCDescriptor
        DCDescriptor dcDescriptor = new DCDescriptor();

        dcDescriptor.addMonitoredResources(getProvidedMetrics(), getResources());
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

    private Set<String> getProvidedMetrics() {

        Set<String> metricsNames = new HashSet<String>();
        for (Metric metric : providedMetrics) {
            metricsNames.add(metric.getMonitoredMetric());
        }

        return metricsNames;
    }

    private Set<Metric> buildProvidedMetrics() {

        Set<Metric> metrics = new HashSet<Metric>();
        Metric toAdd;

        toAdd = new NuroMetric();
        toAdd.setMonitoredMetric("NUROServerLastMinuteAverageRunTime");
        metrics.add(toAdd);
        
        toAdd = new NuroMetric();
        toAdd.setMonitoredMetric("NUROServerLastMinuteAverageThroughput");
        metrics.add(toAdd);
        
        toAdd = new NuroMetric();
        toAdd.setMonitoredMetric("NUROServerLastMinutePlayerCount");
        metrics.add(toAdd);
        
        toAdd = new NuroMetric();
        toAdd.setMonitoredMetric("NUROServerLastMinuteRequestCount");
        metrics.add(toAdd);
        
        toAdd = new NuroMetric();
        toAdd.setMonitoredMetric("NUROServerLastTenSecondsAverageRunTime");
        metrics.add(toAdd);
        
        toAdd = new NuroMetric();
        toAdd.setMonitoredMetric("NUROServerLastTenSecondsAverageThroughput");
        metrics.add(toAdd);
        
        toAdd = new NuroMetric();
        toAdd.setMonitoredMetric("NUROServerLastTenSecondsPlayerCount");
        metrics.add(toAdd);
        
        toAdd = new NuroMetric();
        toAdd.setMonitoredMetric("NUROServerLastTenSecondsRequestCount");
        metrics.add(toAdd);
        
        toAdd = new ModuleAvailability();
        toAdd.setMonitoredMetric("PaaSModuleAvailability");
        metrics.add(toAdd);

        return metrics;
    }

    /**
     * This method returns the set of monitored resources.
     * @return a Set of Resource that are monitored.
     */
    public Set<Resource> getResources() {      
        return resources.keySet();
    }
  
    /**
     * This method returns the URL associated to a monitored resources.
     * @ param r a monitored Resource.
     * @return a String representation of the URL of the input Resource.
     */
    public String getResourceUrl(Resource r) {      
        return resources.get(r).toString();
    }
    
    /**
     * This method allow to add a new monitored resource to the Registry.
     * @param type the Type of the new resource to be added.
     * @param id the id of the new resource to be added.
     * @param url the String representation of the url of the new resource to be added.
     */
    public static void addResource(String type, String id, String url){
        
        
        //add the new resource to the list of the managed resources
        logger.info("Adding the following new resource to the Data Collector Descriptor: {}, {}", type, id);
        try {
            resources.put(new InternalComponent(type,id), new URL(url));
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e.getCause());
        }
        
        logger.info("Currently managed resources...");
        
        for(Resource r: _INSTANCE.getResources()){
            logger.info(r.getType() + " " + r.getId() + "\n");
        }
        
        // re-Build the DCDescriptor
        DCDescriptor dcDescriptor = new DCDescriptor();

        dcDescriptor.addMonitoredResources(_INSTANCE.getProvidedMetrics(), _INSTANCE.getResources());
        dcDescriptor.addResources(_INSTANCE.getResources());

        dcDescriptor
                .setConfigSyncPeriod(CONFIG_SYNC_PERIOD != null ? CONFIG_SYNC_PERIOD
                        : DEFAULT_CONFIG_SYNC_PERIOD);

        dcDescriptor.setKeepAlive(KEEP_ALIVE != null ? KEEP_ALIVE
                : (DEFAULT_CONFIG_SYNC_PERIOD + 15));
        
        logger.info("Setting the new DCDescriptor...");
        
        _INSTANCE.dcAgent.setDCDescriptor(dcDescriptor);
        
        //re-start the monitoring
        _INSTANCE.monitoringStarted = false;
        startMonitoring();

        
    }

    public static void initialize(String dc_sync_period, String  resources_keep_alive_period, String manager_ip, String manager_port) {
        _INSTANCE.init(dc_sync_period, resources_keep_alive_period, manager_ip, manager_port);
    }

    public static void startMonitoring() {
        _INSTANCE.start();
    }

    public static void stopMonitoring() {
        _INSTANCE.stop();
    }
}
