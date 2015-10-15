package eu.seaclouds.monitor.nuroDcTest;

import it.polimi.tower4clouds.model.ontology.InternalComponent;
import it.polimi.tower4clouds.model.ontology.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import eu.seaclouds.monitor.nuroDc.metrics.NUROServerLastMinuteAverageRunTime;
import eu.seaclouds.monitor.nuroDc.metrics.NUROServerLastMinuteAverageThroughput;
import eu.seaclouds.monitor.nuroDc.metrics.NUROServerLastMinutePlayerCount;
import eu.seaclouds.monitor.nuroDc.metrics.NUROServerLastMinuteRequestCount;
import eu.seaclouds.monitor.nuroDc.metrics.NUROServerLastTenSecondsAverageRunTime;
import eu.seaclouds.monitor.nuroDc.metrics.NUROServerLastTenSecondsAverageThroughput;
import eu.seaclouds.monitor.nuroDc.metrics.NUROServerLastTenSecondsPlayerCount;
import eu.seaclouds.monitor.nuroDc.metrics.NUROServerLastTenSecondsRequestCount;

public class NuroDcGetSampleTest {

    private static final Logger logger = LoggerFactory
            .getLogger(NuroDcGetSampleTest.class);

    private static final double EXPECTED_LAST_MINUTE_AVERAGE_RUNTIME = 0.0084509995062027;
    private static final int EXPECTED_LAST_MINUTE_PLAYER_COUNT = 10;
    private static final int EXPECTED_LAST_MINUTE_REQUEST_COUNT = 213;
    private static final double EXPECTED_LAST_MINUTE_AVERAGE_THROUGHPUT = EXPECTED_LAST_MINUTE_REQUEST_COUNT / 60;

    private static final double EXPECTED_LAST_TEN_SECONDS_AVERAGE_RUNTIME = 0.0086382230122884;
    private static final int EXPECTED_LAST_TEN_SECONDS_PLAYER_COUNT = 3;
    private static final int EXPECTED_LAST_TEN_SECONDS_REQUEST_COUNT = 39;
    private static final double EXPECTED_LAST_TEN_SECONDS_AVERAGE_THROUGHPUT = EXPECTED_LAST_TEN_SECONDS_REQUEST_COUNT / 10;

    @Test
    public void testNUROServerLastMinuteAverageRunTime() throws Exception {

        NUROServerLastMinuteAverageRunTime metric = new NUROServerLastMinuteAverageRunTime();
        metric.setResponse(InputExample.EXAMPLE_INPUT);
        Resource testResource = new InternalComponent("NuroApplication",
                "NuroApplication_id");
        double sample = metric.getSample(testResource).doubleValue();
        logger.info("NUROServerLastMinuteAverageRunTime test value="
                + EXPECTED_LAST_MINUTE_AVERAGE_RUNTIME + ", sample value="
                + sample);
        Assert.assertTrue(sample == EXPECTED_LAST_MINUTE_AVERAGE_RUNTIME);

    }

    @Test
    public void testNUROServerLastMinuteAverageThroughput() throws Exception {

        NUROServerLastMinuteAverageThroughput metric = new NUROServerLastMinuteAverageThroughput();
        metric.setResponse(InputExample.EXAMPLE_INPUT);
        Resource testResource = new InternalComponent("NuroApplication",
                "NuroApplication_id");
        double sample = metric.getSample(testResource).doubleValue();
        logger.info("NUROServerLastMinuteAverageThroughput test value="
                + EXPECTED_LAST_MINUTE_AVERAGE_THROUGHPUT + ", sample value="
                + sample);
        Assert.assertTrue(sample == EXPECTED_LAST_MINUTE_AVERAGE_THROUGHPUT);

    }

    @Test
    public void testNUROServerLastMinutePlayerCount() throws Exception {

        NUROServerLastMinutePlayerCount metric = new NUROServerLastMinutePlayerCount();
        metric.setResponse(InputExample.EXAMPLE_INPUT);
        Resource testResource = new InternalComponent("NuroApplication",
                "NuroApplication_id");
        int sample = metric.getSample(testResource).intValue();
        logger.info("NUROServerLastMinutePlayerCount test value="
                + EXPECTED_LAST_MINUTE_PLAYER_COUNT + ", sample value="
                + sample);
        Assert.assertTrue(sample == EXPECTED_LAST_MINUTE_PLAYER_COUNT);

    }

    @Test
    public void testNUROServerLastMinuteRequestCount() throws Exception {

        NUROServerLastMinuteRequestCount metric = new NUROServerLastMinuteRequestCount();
        metric.setResponse(InputExample.EXAMPLE_INPUT);
        Resource testResource = new InternalComponent("NuroApplication",
                "NuroApplication_id");
        int sample = metric.getSample(testResource).intValue();
        logger.info("NUROServerLastMinuteRequestCount test value="
                + EXPECTED_LAST_MINUTE_REQUEST_COUNT + ", sample value="
                + sample);
        Assert.assertTrue(sample == EXPECTED_LAST_MINUTE_REQUEST_COUNT);

    }

    @Test
    public void testNUROServerLastTenSecondsAverageRunTime() throws Exception {

        NUROServerLastTenSecondsAverageRunTime metric = new NUROServerLastTenSecondsAverageRunTime();
        metric.setResponse(InputExample.EXAMPLE_INPUT);
        Resource testResource = new InternalComponent("NuroApplication",
                "NuroApplication_id");
        double sample = metric.getSample(testResource).doubleValue();
        logger.info("NUROServerLastTenSecondsAverageRunTime test value="
                + EXPECTED_LAST_TEN_SECONDS_AVERAGE_RUNTIME + ", sample value="
                + sample);
        Assert.assertTrue(sample == EXPECTED_LAST_TEN_SECONDS_AVERAGE_RUNTIME);

    }

    @Test
    public void testNUROServerLastTenSecondsAverageThroughput()
            throws Exception {

        NUROServerLastTenSecondsAverageThroughput metric = new NUROServerLastTenSecondsAverageThroughput();
        metric.setResponse(InputExample.EXAMPLE_INPUT);
        Resource testResource = new InternalComponent("NuroApplication",
                "NuroApplication_id");
        double sample = metric.getSample(testResource).doubleValue();
        logger.info("NUROServerLastTenSecondsAverageThroughput test value="
                + EXPECTED_LAST_TEN_SECONDS_AVERAGE_THROUGHPUT
                + ", sample value=" + sample);
        Assert.assertTrue(sample == EXPECTED_LAST_TEN_SECONDS_AVERAGE_THROUGHPUT);

    }

    @Test
    public void testNUROServerLastTenSecondsPlayerCount() throws Exception {

        NUROServerLastTenSecondsPlayerCount metric = new NUROServerLastTenSecondsPlayerCount();
        metric.setResponse(InputExample.EXAMPLE_INPUT);
        Resource testResource = new InternalComponent("NuroApplication",
                "NuroApplication_id");
        int sample = metric.getSample(testResource).intValue();
        logger.info("NUROServerLastTenSecondsPlayerCount test value="
                + EXPECTED_LAST_TEN_SECONDS_PLAYER_COUNT + ", sample value="
                + sample);
        Assert.assertTrue(sample == EXPECTED_LAST_TEN_SECONDS_PLAYER_COUNT);

    }

    @Test
    public void testNUROServerLastTenSecondsRequestCount() throws Exception {

        NUROServerLastTenSecondsRequestCount metric = new NUROServerLastTenSecondsRequestCount();
        metric.setResponse(InputExample.EXAMPLE_INPUT);
        Resource testResource = new InternalComponent("NuroApplication",
                "NuroApplication_id");
        int sample = metric.getSample(testResource).intValue();
        logger.info("NUROServerLastTenSecondsRequestCount test value="
                + EXPECTED_LAST_TEN_SECONDS_REQUEST_COUNT + ", sample value="
                + sample);
        Assert.assertTrue(sample == EXPECTED_LAST_TEN_SECONDS_REQUEST_COUNT);

    }
}
