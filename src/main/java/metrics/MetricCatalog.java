package metrics;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import sun.plugin2.gluegen.runtime.CPU;

import java.util.List;
import java.util.Map;


/**
 * Created by Adrian on 15/10/2014.
 */
public class MetricCatalog {


    public final static String RUNTIME_METRIC_PREFIX = "custom.metrics";
    private Map<String, Metric> availableMetrics;
    private static MetricCatalog instance;

    private MetricCatalog(){
        availableMetrics = Maps.newHashMap();

        // Java-based platforms (VM Metrics)
        availableMetrics.put("java.metrics.heap.used", new Metric<>("java.metrics.heap.used", "Current heap size (bytes)", Long.class));
        availableMetrics.put("java.metrics.heap.init", new Metric<>("java.metrics.heap.init", "Initial heap size (bytes)", Long.class));
        availableMetrics.put("java.metrics.heap.committed", new Metric<>("java.metrics.heap.committed", "Commited heap size (bytes)", Long.class));
        availableMetrics.put("java.metrics.heap.max", new Metric<>("java.metrics.heap.max", "Max heap size (bytes)", Long.class));
        availableMetrics.put("java.metrics.nonheap.used", new Metric<>("java.metrics.nonheap.used", "Current non-heap size (bytes)", Long.class));
        availableMetrics.put("java.metrics.threads.current", new Metric<>("java.metrics.threads.current", "Current number of threads", Integer.class));
        availableMetrics.put("java.metrics.threads.max", new Metric<>("java.metrics.threads.max", "Peak number of threads", Integer.class));
        availableMetrics.put("java.metrics.starttime", new Metric<>("java.metrics.starttime", "Start time of Java process (UTC)", Long.class));
        availableMetrics.put("java.metrics.uptime", new Metric<>("java.metrics.uptime", "Uptime of Java process (millis, elapsed since start)", Long.class));
        availableMetrics.put("java.metrics.processCpuTime.total", new Metric<>("java.metrics.processCpuTime.total", "Process CPU time (total millis since start)", Double.class));
        availableMetrics.put("java.metrics.processCpuTime.fraction.last", new Metric<>("java.metrics.processCpuTime.fraction.last", "Fraction of CPU time used, reported by JVM (percentage, last datapoint)", Double.class));
        availableMetrics.put("java.metrics.processCpuTime.fraction.windowed", new Metric<>("java.metrics.processCpuTime.fraction.windowed", "Fraction of CPU time used, reported by JVM (percentage, over time window)", Double.class));
        availableMetrics.put("java.metrics.processors.available", new Metric<>("java.metrics.processors.available", "number of processors available to the Java virtual machine", Integer.class));
        availableMetrics.put("java.metrics.physical.memory",  new Metric<>("java.metrics.physical.memory", "Fraction of CPU time used, reported by JVM (percentage, over time window)", Double.class));
        availableMetrics.put("java.metrics.systemload.average",  new Metric<>("java.metrics.systemload.average", "average system load", Double.class));
        availableMetrics.put("java.metrics.processors.available", new Metric<>("java.metrics.processors.available", "number of processors available to the Java virtual machine", Integer.class));
        availableMetrics.put("java.metrics.physicalmemory.total",  new Metric<>("java.metrics.physicalmemory.total", "The physical memory available to the operating system", Long.class));
        availableMetrics.put("java.metrics.physicalmemory.free",  new Metric<>("java.metrics.physicalmemory.free", "The free memory available to the operating system", Long.class));


        //TODO: Add remaining metrics
    }

    public static MetricCatalog getInstance(){
        if(instance == null)
            instance = new MetricCatalog();

        return instance;
    }

    public <T> Metric add(String id, String description, Class<T> type){
        Metric<T> newMetric = new Metric<>(id,description,type);

        if(!availableMetrics.containsKey(id))
            availableMetrics.put(id, newMetric);

        return newMetric;
    }

    public Metric getMetric(String metric) {
       return availableMetrics.get(metric);
    }

    // TODO: Classify metrics by type (java, db...). Maybe with a filter by id


}
