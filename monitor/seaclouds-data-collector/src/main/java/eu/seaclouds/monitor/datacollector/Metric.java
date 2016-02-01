package eu.seaclouds.monitor.datacollector;

import it.polimi.tower4clouds.data_collector_library.DCAgent;
import it.polimi.tower4clouds.model.ontology.Resource;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.Uninterruptibles;

public abstract class Metric implements Observer {

    private static final Logger logger = LoggerFactory.getLogger(Metric.class);
    private static final String APPLICATION_USER_PARAMETER ="applicationUser";
    private static final String APPLICATION_PASSWORD_PARAMETER ="applicationPassword";
    private static final String SAMPLING_TIME_PARAMETER ="samplingTime";

    private DCAgent dcAgent;

    private String monitoredMetric;
        
    private final Map<String, Timer> timerPerResourceId = new ConcurrentHashMap<String, Timer>();
    private final Map<String, Integer> samplingTimePerResourceId = new ConcurrentHashMap<String, Integer>();
    
    private static final int DEFAULT_SAMPLING_TIME = 5;

    protected void send(Number value, Resource resource) {
        if (dcAgent != null) {
            logger.info("Sending monitoring datum: " + resource.getType() + "," + resource.getId() + "," + this.getMonitoredMetric() + "," + value);
            dcAgent.send(resource, this.getMonitoredMetric(), value);
        } else {
            logger.warn("Monitoring is not required, data won't be sent");
        }
    }

    private boolean shouldMonitor(Resource resource) {
        if (dcAgent == null) {
            logger.error("{}: DCAgent was null", this.toString());
            return false;
        }
        return dcAgent.shouldMonitor(resource, this.getMonitoredMetric());
    }

    @Override
    public void update(Observable o, Object arg) {
        this.dcAgent = (DCAgent) o;

        for (Resource resource : Registry._INSTANCE.getResources()) {
            if (shouldMonitor(resource)) {
                int newSamplingTime = getSamplingTime(resource);
                if (timerPerResourceId.containsKey(resource.getId())
                        && samplingTimePerResourceId.get(resource.getId()) != newSamplingTime) {
                    timerPerResourceId.remove(resource.getId()).cancel();
                }
                if (!timerPerResourceId.containsKey(resource.getId())) {
                    Timer timer = new Timer();
                    timerPerResourceId.put(resource.getId(), timer);
                    samplingTimePerResourceId.put(resource.getId(),
                            newSamplingTime);
                    String url = Registry._INSTANCE.getResourceUrl(resource);
                    String user = getApplicationUser(resource);
                    String password = getApplicationPassword(resource);
                    createTask(timer, resource, newSamplingTime, url, user, password);
                }
            } else {
                Timer timer = timerPerResourceId.remove(resource.getId());
                if (timer != null)
                    timer.cancel();
                samplingTimePerResourceId.remove(resource.getId());
            }
        }
    }

    private String getApplicationUser(Resource resource){
        try {
            if(getParameters(resource).get(APPLICATION_USER_PARAMETER) != null){
                return getParameters(resource).get(APPLICATION_USER_PARAMETER);
            }else{
                return new String();
            }
        } catch (Exception e) {
            logger.error("Error while reading the applicationUser", e);
            throw new IllegalStateException("Error while reading the applicationUser",e.getCause());
        }    
    }
    
    private String getApplicationPassword(Resource resource){
        try {
            if(getParameters(resource).get(APPLICATION_PASSWORD_PARAMETER) != null){
                return getParameters(resource).get(APPLICATION_PASSWORD_PARAMETER);
            }else{
                return new String();
            }        
        } catch (Exception e) {
            logger.error("Error while reading the applicationPassword", e);
            throw new IllegalStateException("Error while reading the applicationPassword",e.getCause());
        }    
    }
    
    private int getSamplingTime(Resource resource) {
        if (getParameters(resource) == null
                || getParameters(resource).get(SAMPLING_TIME_PARAMETER) == null)
            return DEFAULT_SAMPLING_TIME;
        try {
            return Integer
                    .parseInt(getParameters(resource).get(SAMPLING_TIME_PARAMETER));
        } catch (Exception e) {
            logger.error("Error while reading the sampling time", e);
            return DEFAULT_SAMPLING_TIME;
        }
    }

    private void createTask(Timer timer, Resource resource, int samplingTime, String applicationUrl, String user, String password) {
        timer.scheduleAtFixedRate(new MetricSender(resource, applicationUrl, user, password), 0,
                samplingTime * 1000);
    }

    private class MetricSender extends TimerTask {
        private Resource resource;
        private String applicationUrl;
        private String user;
        private String password;

        public MetricSender(Resource resource, String applicationUrl, String user, String password) {
            this.resource = resource;
            this.applicationUrl = applicationUrl;
            this.user = user;
            this.password = password;
        }

        @Override
        public void run() {
            logger.info("Getting Sample...");
            long first = System.currentTimeMillis();
            boolean sampleRetrieved = false;

            do {
                try {
                    send(getSample(applicationUrl, user, password), resource);
                    sampleRetrieved = true;
                    logger.info("Sample retrieved and sent in: "
                            + (System.currentTimeMillis() - first) + "ms");
                } catch (Exception ex) {
                    logger.warn("Unable to get sample (ID:" + resource.getId()
                            + " - " + ex.getMessage());
                    Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS);
                }
            } while (!sampleRetrieved);

        }

    }

    public abstract Number getSample(String applicationUrl, String user, String password) throws Exception;
    
    protected Map<String, String> getParameters(Resource resource) {
        if (this.dcAgent != null)
            return this.dcAgent.getParameters(resource, this.getMonitoredMetric());
        return null;
    }

    public String getMonitoredMetric() {
        return monitoredMetric;
    }

    public void setMonitoredMetric(String monitoredMetric) {
        this.monitoredMetric = monitoredMetric;
    }
}
