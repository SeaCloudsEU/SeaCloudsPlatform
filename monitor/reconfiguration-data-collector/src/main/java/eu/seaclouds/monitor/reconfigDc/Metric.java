package eu.seaclouds.monitor.reconfigDc;

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

    private DCAgent dcAgent;

    private String deployerInstanceIp;

    private String deployerInstancePort;

    private String deployerUser;

    private String deployerPassword;

    private final Map<String, Timer> timerPerResourceId = new ConcurrentHashMap<String, Timer>();
    private final Map<String, Integer> samplingTimePerResourceId = new ConcurrentHashMap<String, Integer>();

    private static final int DEFAULT_SAMPLING_TIME = 5;

    protected String getName() {
        return getClass().getSimpleName();
    }

    protected void send(Number value, Resource resource) {
        if (dcAgent != null) {
            dcAgent.send(resource, getName(), value);
        } else {
            logger.warn("Monitoring is not required, data won't be sent");
        }
    }

    private boolean shouldMonitor(Resource resource) {
        if (dcAgent == null) {
            logger.error("{}: DCAgent was null", this.toString());
            return false;
        }
        return dcAgent.shouldMonitor(resource, getName());
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
                    createTask(timer, resource, newSamplingTime);
                }
            } else {
                Timer timer = timerPerResourceId.remove(resource.getId());
                if (timer != null)
                    timer.cancel();
                samplingTimePerResourceId.remove(resource.getId());
            }
        }
    }

    private int getSamplingTime(Resource resource) {
        if (getParameters(resource) == null
                || getParameters(resource).get("samplingTime") == null)
            return DEFAULT_SAMPLING_TIME;
        try {
            return Integer
                    .parseInt(getParameters(resource).get("samplingTime"));
        } catch (Exception e) {
            logger.error("Error while reading the sampling time", e);
            return DEFAULT_SAMPLING_TIME;
        }
    }

    private void createTask(Timer timer, Resource resource, int samplingTime) {
        timer.scheduleAtFixedRate(new MetricSender(resource), 0,
                samplingTime * 1000);
    }

    private class MetricSender extends TimerTask {
        private Resource resource;

        public MetricSender(Resource resource) {
            this.resource = resource;
        }

        @Override
        public void run() {
            logger.info("Getting Sample...");
            long first = System.currentTimeMillis();
            boolean sampleRetrieved = false;

            do {
                try {
                    send(getSample(resource), resource);
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

    public abstract Number getSample(Resource resource) throws Exception;

    protected Map<String, String> getParameters(Resource resource) {
        if (this.dcAgent != null)
            return this.dcAgent.getParameters(resource, getName());
        return null;
    }

    public String getDeployerInstanceIp() {
        return deployerInstanceIp;
    }

    public void setDeployerInstanceIp(String deployerInstanceIp) {
        this.deployerInstanceIp = deployerInstanceIp;
    }

    public String getDeployerInstancePort() {
        return deployerInstancePort;
    }

    public void setDeployerInstancePort(String deployerInstancePort) {
        this.deployerInstancePort = deployerInstancePort;
    }

    public String getDeployerUser() {
        return deployerUser;
    }

    public void setDeployerUser(String deployerUser) {
        this.deployerUser = deployerUser;
    }

    public String getDeployerPassword() {
        return deployerPassword;
    }

    public void setDeployerPassword(String deployerPassword) {
        this.deployerPassword = deployerPassword;
    }
}
