package metrics;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
/**
 * Created by Adrian on 17/10/2014.
 */
public class BrooklynMetricLanguage  extends MetricLanguage{
    private static BrooklynMetricLanguage instance;
    private  BiMap<String, String> runtimeTranslations;

    private BrooklynMetricLanguage(){
        runtimeTranslations = HashBiMap.create();
    }

    public static BrooklynMetricLanguage getInstance() {
        if(instance == null)
            instance = new BrooklynMetricLanguage();

        return instance;
    }

    @Override
    public void addTranslation(String from, String to) {
        runtimeTranslations.put(from,to);
    }

    @Override
    // TODO: Get all the translations from MetricCatalog.PREDEFINED_METRICS
    public String getTranslation(String input) {
        MetricCatalog.PredefinedMetrics predefined_metric = MetricCatalog.PredefinedMetrics.getEnum(input);
        if(predefined_metric != null){
            switch(predefined_metric){
                case JAVA_HEAP_USED:
                    return "java.metrics.heap.used";
                case JAVA_HEAP_INIT:
                    return "java.metrics.heap.init";
                case JAVA_HEAP_COMMITED:
                    return "java.metrics.heap.commited";
                case JAVA_HEAP_MAX:
                    return "java.metrics.heap.max";
                case JAVA_NONHEAP_USED:
                    return "java.metrics.nonheap.used";
                case JAVA_THREADS_CURRENT:
                    return "java.metrics.threads.current";
                case JAVA_THREADS_MAX:
                    return "java.metrics.threads.max";
                case JAVA_TIME_START:
                    return "java.metrics.starttime";
                case JAVA_TIME_UPTIME:
                    return "java.metrics.uptime";
                case JAVA_TIME_PROCESSCPU_TOTAL:
                    return "java.metrics.processCpuTime.total";
                case JAVA_TIME_PROCESSCPU_FRACTION_LAST:
                    return "java.metrics.processCpuTime.fraction.last";
                case JAVA_TIME_PROCESSCPU_FRACTION_WINDOWED:
                    return "java.metrics.processCpuTime.fraction.last";
                case JAVA_PROCESSORS_AVAILABLE:
                    return "java.metrics.processors.available";
                case JAVA_SYSTEMLOAD_AVERAGE:
                    return "java.metrics.systemload.average";
                case JAVA_PHYSICAL_MEMORY_TOTAL:
                    return "java.metrics.pysicalmemory.total";
                case JAVA_PHYSICAL_MEMORY_FREE:
                    return "java.metrics.physicalmemory.free";

                case WEBAPP_REQUESTS_TOTAL:
                    return "webapp.reqs.total";
                case WEBAPP_REQUESTS_ERRORS:
                    return "webapp.reqs.errors";
                case WEBAPP_PROCESSING_TIME_TOTAL:
                    return "webapp.reqs.processingTime.total";
                case WEBAPP_PROCESING_TIME_MAX:
                    return "webapp.reqs.processingTime.max";
                case WEBAPP_PROCESSING_TIME_FRACTION_LAST:
                    return "webapp.reqs.processingTime.fraction.last";
                case WEBAPP_PROCESSING_TIME_FRACTION_WINDOWED:
                    return "webapp.reqs.processingTime.fraction.windowed";

                default:
                    throw new RuntimeException("BrooklynMetricLanguage not updated, please update " + this.getClass().getSimpleName());
            }
        }else{
            // The translation was created in runtime by custom metric.
            return  runtimeTranslations.get(input);

        }
    }

    @Override
    // TODO: Get all the translations from MetricCatalog.PREDEFINED_METRICS
    public String getInverseTranslation(String input) {
        switch(input){
            case "java.metrics.heap.used":
                return MetricCatalog.PredefinedMetrics.JAVA_HEAP_USED.getValue().getId();
            case "java.metrics.heap.init":
                return MetricCatalog.PredefinedMetrics.JAVA_HEAP_INIT.getValue().getId();
            case "java.metrics.heap.commited":
                return MetricCatalog.PredefinedMetrics.JAVA_HEAP_COMMITED.getValue().getId();
            case "java.metrics.heap.max":
                return MetricCatalog.PredefinedMetrics.JAVA_HEAP_MAX.getValue().getId();
            case "java.metrics.nonheap.used":
                return MetricCatalog.PredefinedMetrics.JAVA_NONHEAP_USED.getValue().getId();
            case "java.metrics.threads.current":
                return MetricCatalog.PredefinedMetrics.JAVA_THREADS_CURRENT.getValue().getId();
            case "java.metrics.threads.max":
                return MetricCatalog.PredefinedMetrics.JAVA_THREADS_MAX.getValue().getId();
            case "java.metrics.starttime":
                return MetricCatalog.PredefinedMetrics.JAVA_TIME_START.getValue().getId();
            case "java.metrics.uptime":
                return MetricCatalog.PredefinedMetrics.JAVA_TIME_UPTIME.getValue().getId();
            case "java.metrics.processCpuTime.total":
                return MetricCatalog.PredefinedMetrics.JAVA_TIME_PROCESSCPU_TOTAL.getValue().getId();
            case "java.metrics.processCpuTime.fraction.last":
                return MetricCatalog.PredefinedMetrics.JAVA_TIME_PROCESSCPU_FRACTION_LAST.getValue().getId();
            case "java.metrics.processCpuTime.fraction.last.windowed":
                return MetricCatalog.PredefinedMetrics.JAVA_TIME_PROCESSCPU_FRACTION_WINDOWED.getValue().getId();
            case "java.metrics.processors.available":
                return MetricCatalog.PredefinedMetrics.JAVA_PROCESSORS_AVAILABLE.getValue().getId();
            case "java.metrics.systemload.average":
                return MetricCatalog.PredefinedMetrics.JAVA_SYSTEMLOAD_AVERAGE.getValue().getId();
            case "java.metrics.pysicalmemory.total":
                return MetricCatalog.PredefinedMetrics.JAVA_PHYSICAL_MEMORY_TOTAL.getValue().getId();
            case "java.metrics.physicalmemory.free":
                return MetricCatalog.PredefinedMetrics.JAVA_PHYSICAL_MEMORY_FREE.getValue().getId();

            case "webapp.reqs.total":
                return MetricCatalog.PredefinedMetrics.WEBAPP_REQUESTS_TOTAL.getValue().getId();
            case "webapp.reqs.errors":
                return MetricCatalog.PredefinedMetrics.WEBAPP_REQUESTS_ERRORS.getValue().getId();
            case "webapp.reqs.processingTime.total":
                return MetricCatalog.PredefinedMetrics.WEBAPP_PROCESSING_TIME_TOTAL.getValue().getId();
            case "webapp.reqs.processingTime.max":
                return MetricCatalog.PredefinedMetrics.WEBAPP_PROCESING_TIME_MAX.getValue().getId();
            case "webapp.reqs.processingTime.fraction.last":
                return MetricCatalog.PredefinedMetrics.WEBAPP_PROCESSING_TIME_FRACTION_LAST.getValue().getId();
            case "webapp.reqs.processingTime.fraction.windowed":
                return MetricCatalog.PredefinedMetrics.WEBAPP_PROCESSING_TIME_FRACTION_WINDOWED.getValue().getId();

            default:
                // The translation was created in runtime by custom metric.
                return runtimeTranslations.inverse().get(input);

        }
    }


}
