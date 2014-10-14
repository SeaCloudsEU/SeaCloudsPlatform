package core;

import brooklyn.rest.api.SensorApi;
import brooklyn.rest.client.BrooklynApi;
import brooklyn.rest.domain.SensorSummary;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import metrics.BrooklynMetricLanguage;
import metrics.Metric;
import metrics.MetricCatalog;
import model.Module;
import model.exceptions.MonitorConnectorException;


import java.util.List;

/**
 * @author MBarrientos
 */
public class BrooklynConnector implements Connector {

    private Module module;
    private BrooklynApi endpoint;
    private final static BrooklynMetricLanguage

    // Metrics available in every module
    private List<Metric> availableMetrics;


    private static final MetricCatalog METRIC_CATALOG = MetricCatalog.getInstance();


    private void  updateAvailableMetrics() throws MonitorConnectorException {

        List<SensorSummary> sensorSummaries = this.endpoint.getSensorApi().list(module.getParentApplication().getId(), module.getId());

        // It returns null if some error happened
        if (sensorSummaries == null)
            throw new MonitorConnectorException("Unable to fetch sensor catalog from Brooklyn for " + module);

        for(SensorSummary sensor : sensorSummaries){
            Metric metricFound = METRICS_MAPPING.inverse().get(sensor.getName());

            if(metricFound != null){
                availableMetrics.add(metricFound);
            }else if (sensor.getName().startsWith(MetricCatalog.RUNTIME_METRIC_PREFIX)) {
                try {
                    Metric<?> metric = METRIC_CATALOG.add(sensor.getName(), sensor.getDescription(), Class.forName(sensor.getType()));
                    METRICS_MAPPING.put(metric, sensor.getName());
                    this.availableMetrics.add(metric);
                } catch (ClassNotFoundException e) {
                    throw new MonitorConnectorException("Unable to parse custom sensor type for " + sensor.getName() + " with type " + sensor.getType());
                }
            }else{
              // Brooklyn exposes module setup as sensors, so ignoring it from now.
            }
        }

    }

    public BrooklynConnector(Module module, String endpoint) throws MonitorConnectorException {
        this.module = module;
        this.endpoint = new BrooklynApi(endpoint);
        this.updateAvailableMetrics();
    }

    private List<Metric> getModuleMetricsFromBrooklyn(){ //throws MonitorConnectorException {
        ArrayList<Metric> result = Lists.newArrayList();
        SensorApi sensorApi = this.endpoint.getSensorApi();
        List<SensorSummary> summaryList = sensorApi.list(module.getParentApplication().getId(), module.getId());
        for(SensorSummary s: summaryList){

        }

        return result;
    }

    @Override
    public List<Metric> getAvailableMetrics() {
        return availableMetrics;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValue(Metric<T> type)  throws MonitorConnectorException{

        if(!availableMetrics.contains(type))
            throw new MonitorConnectorException(type.getId() + " metric doesn't exist in " + module.getId() + " module");

        T result = (T) endpoint.getSensorApi().get(module.getParentApplication().getId(), module.getId(), METRICS_MAPPING.get(type), false);

        // It returns null if some error happened
        if (result == null)
            throw new MonitorConnectorException("Unable to fetch " + type + " sensor from " + module);

        return result;
    }


}
