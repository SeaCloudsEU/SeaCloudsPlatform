package core;

import metrics.Metric;
import model.Module;
import model.exceptions.MonitorConnectorException;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Created by Adrian on 15/10/2014.
 */
public interface Connector {

    public List<Metric> getAvailableMetrics();
    public <T> T getValue(Metric<T> type) throws MonitorConnectorException;
}
