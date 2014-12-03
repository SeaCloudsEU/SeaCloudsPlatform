package seaclouds;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.io.*;

/**
 * Created by Jose on 27/11/14.
 */
public class SimplePlannerTest {

    String topologyFile;
    InputStream topologyInputFile = null;
    Planner planner=null;

    public SimplePlannerTest() throws FileNotFoundException {
        setUp();
    }

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(SimplePlannerTest.class);
    }

    public void setUp() throws FileNotFoundException {
        topologyFile =getClass().getClassLoader().getResource("nuroCase.yaml").getFile();
        planner = new Planner(topologyFile);
    }

    @Test
    public void plan() throws IOException {
        planner.plan();
    }



}
