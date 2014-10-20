package model.exceptions;

/**
 * @author MBarrientos
 */
public class MetricNotFoundException extends MonitorRuntimeException{

    public MetricNotFoundException(String metric){
        super("Metric not found: " + metric);
    }
}
