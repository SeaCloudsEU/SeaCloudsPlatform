package eu.seaclouds.monitor.monitoringdamgenerator.adpparsing;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import eu.seaclouds.monitor.monitoringdamgenerator.DeploymentType;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Module;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.YAMLMonitorParser;

public class AdpParsingTest {
    
    private static final int EXPEXTED_NUMBER_OF_MODULES=3;
    
    @Test
    public void parsingTest() throws Exception {
        
        YAMLMonitorParser adpParser = new YAMLMonitorParser();
        byte[] encoded = Files.readAllBytes(Paths.get("resources/adp_example.yml"));

        List<Module> modules = adpParser.getModuleRelevantInfoFromAdp(new String(encoded, Charset.defaultCharset()));

        Assert.assertEquals(modules.size(), EXPEXTED_NUMBER_OF_MODULES);
        
        for(Module m: modules){
            switch (m.getModuleName()) {
            
            case "www":
                Assert.assertEquals(m.getAvailability(), 0.98);
                Assert.assertEquals(m.getResponseTime(), 2000.0);
                Assert.assertEquals(m.getLanguage(), "JAVA");
                Assert.assertEquals(m.getHost().getHostName(), "CloudFoundry");
                Assert.assertEquals(m.getHost().getDeploymentType(), DeploymentType.PaaS);

                break;
            
            case "webservices":
                Assert.assertEquals(m.getAvailability(), 0.96);
                Assert.assertEquals(m.getResponseTime(), 0.0);
                Assert.assertEquals(m.getLanguage(), "JAVA");
                Assert.assertEquals(m.getPort(), "8080");
                Assert.assertEquals(m.getHost().getHostName(), "Amazon_EC2_i2_xlarge_us_west_1");
                Assert.assertEquals(m.getHost().getDeploymentType(), DeploymentType.IaaS);
                break;
            
            case "db1":
                Assert.assertEquals(m.getAvailability(), 0.0);
                Assert.assertEquals(m.getResponseTime(), 0.0);
                Assert.assertEquals(m.getHost().getHostName(), "Amazon_EC2_i2_xlarge_us_west_2");
                Assert.assertEquals(m.getHost().getDeploymentType(), DeploymentType.IaaS);
                
                break;

            default:
                break;
            }
        }
    }


}
