package eu.seaclouds.platform.planner.aamwriter;

public enum MonitoringMetrics {

    APP_AVAILABLE("AppAvailable", ""),
    AVERAGE_RESPONSE_TIME("AverageResponseTime", "ms"),
    AVERAGE_THROUGHPUT("AverageThroughput", "req_per_min");
    
    private String metricName;
    private String unit;
    
    MonitoringMetrics(String metricName, String unit) {
        this.metricName = metricName;
        this.unit = unit;
    }
    
    public String getMetricName() {
        return metricName;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public static MonitoringMetrics from(String metricName) {
        if (metricName == null) {
            throw new NullPointerException("metricName cannot be null");
        }
        for (MonitoringMetrics item : MonitoringMetrics.values()) {
            if (metricName.equals(item.getMetricName())) {
                return item;
            }
        }
        throw new IllegalArgumentException("Not found MonitoringMetrics for '" + metricName + "'");
    }
}
