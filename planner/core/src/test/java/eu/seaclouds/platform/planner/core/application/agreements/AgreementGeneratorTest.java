package eu.seaclouds.platform.planner.core.application.agreements;

import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Scanner;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringInfo;
import it.polimi.tower4clouds.rules.MonitoringRules;

public class AgreementGeneratorTest {


    private MockWebServer server = new MockWebServer();

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        server = new MockWebServer();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void testGenerateAgreementId() throws Exception {
        server.enqueue(new MockResponse().setBody(
                readFile("/eu/seaclouds/planner/core/application/agreements/sla-create-template-response.json")));
        server.start();
        URL url = server.url("/").url();


        // Load a sample adp
        String adp = readFile("/generated_adp.yml");

        // Generate an empty set of monitoring rules
        MonitoringInfo monitoring = new MonitoringInfo(new MonitoringRules(), adp);

        AgreementGenerator generator = new AgreementGenerator(url.toString());
        String result = generator.generateAgreeemntId(adp, monitoring);

        assertNotNull(result);
    }

    @Test
    public void testGenerateGetAgreement() throws Exception {
        server.enqueue(new MockResponse().
                setBody(readFile("/eu/seaclouds/planner/core/application/agreements/sla-create-agreement-response.xml")));
        server.start();
        URL url = server.url("/").url();

        AgreementGenerator generator = new AgreementGenerator(url.toString());
        String result = generator.getAgreement("templateId");

        assertNotNull(result);
    }

    private String readFile(String resourcePath) throws FileNotFoundException {
        File f = new File(this.getClass().getResource(resourcePath).getFile());
        Scanner scanner = new Scanner(f);
        String json = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return json;
    }

}
