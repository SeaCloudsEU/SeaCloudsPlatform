package eu.seaclouds.monitor.nuroDc;

import it.polimi.tower4clouds.data_collector_library.DCAgent;
import it.polimi.tower4clouds.model.ontology.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Metric implements Observer {

    private static final Logger logger = LoggerFactory.getLogger(Metric.class);

    private DCAgent dcAgent;

    private String nuroInstanceIp;

    private String nuroInstancePort;

    private String nuroUser;

    private String nuroPassword;

    private String response;

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
                    try {
                        Thread.sleep(200);
                    } catch (Exception ex1) {
                        logger.error(ex.getMessage());
                    }
                }
            } while (!sampleRetrieved);

        }

    }

    protected String getResponse() {

        if (this.response == null) {

            HttpResponse response;
            HttpEntity responseEntity;
            InputStream stream;
            BufferedReader reader;
            StringBuilder out;

            HttpClient httpClient = HttpClientBuilder.create().build();
            String auth = getNuroUser() + ":" + getNuroPassword();
            String encodedAuth = Base64.encodeBase64String(auth.getBytes());

            HttpGet httpget = new HttpGet("http://" + getNuroInstanceIp() + ":"
                    + getNuroInstancePort() + "/sensor.php");
            httpget.addHeader("Authorization", "Basic " + encodedAuth);

            try {

                response = httpClient.execute(httpget);

                responseEntity = response.getEntity();
                stream = responseEntity.getContent();
                reader = new BufferedReader(new InputStreamReader(stream));
                out = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                reader.close();

                return out.toString();

            } catch (ClientProtocolException e) {
                logger.warn(e.getMessage());
                throw new IllegalStateException(e.getMessage(), e.getCause());
            } catch (IOException e) {
                logger.warn(e.getMessage());
                throw new IllegalStateException(e.getMessage(), e.getCause());
            }
        } else {
            return this.response;
        }

    }

    public void setResponse(String response) {
        this.response = response;
    }

    public abstract Number getSample(Resource resource) throws Exception;

    protected Map<String, String> getParameters(Resource resource) {
        if (this.dcAgent != null)
            return this.dcAgent.getParameters(resource, getName());
        return null;
    }

    public String getNuroInstanceIp() {
        return nuroInstanceIp;
    }

    public void setNuroInstanceIp(String nuroInstanceIp) {
        this.nuroInstanceIp = nuroInstanceIp;
    }

    public String getNuroInstancePort() {
        return nuroInstancePort;
    }

    public void setNuroInstancePort(String nuroInstancePort) {
        this.nuroInstancePort = nuroInstancePort;
    }

    public String getNuroUser() {
        return nuroUser;
    }

    public void setNuroUser(String nuroUser) {
        this.nuroUser = nuroUser;
    }

    public String getNuroPassword() {
        return nuroPassword;
    }

    public void setNuroPassword(String nuroPassword) {
        this.nuroPassword = nuroPassword;
    }
}
