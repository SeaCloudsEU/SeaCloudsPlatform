package eu.seaclouds.monitor.monitoringdamgenerator;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Host;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Module;
import eu.seaclouds.monitor.monitoringdamgenerator.dcgenerators.JavaAppDcGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.dcgenerators.MODACloudsDcGenerator;

public class DcGeneratorTests {
    private static Logger logger = LoggerFactory
            .getLogger(DcGeneratorTests.class);
    
    private static final String TEST_HOST = "test_host";
    private static final String TEST_MODULE = "test_module";
    private static final String TEST_MONITORING_MANAGER_IP = "127.0.0.1";
    private static final int TEST_MONITORING_MANAGER_PORT = 8170;
    private static final String TEST_MONITORING_INFLUXDB_IP = "127.0.0.1";
    private static final int TEST_MONITORING_INFLUXDB_PORT = 8083;
    private static final String JAVA_APP_ID="javaAppDc";
    private static final String PROPERTIES="properties";
    private static final String ENV_VARS="env";
    private static final String MODACLOUDS_DC_ID = "modacloudsDc";
    private static final String SYNC_PERIOD = "10";
    private static final String KEEP_ALIVE = "25";
    private static final String  IAAS_DEPLOYMENT = "IaaS";


    @SuppressWarnings("unchecked")
    @Test
    public void javaAppDcGeneratorTest(){
        
        JavaAppDcGenerator testGenerator = new JavaAppDcGenerator();
        Module testModule = new Module();
        Host testHost = new Host();

        testHost.setHostName(TEST_HOST);
        testModule.setModuleName(TEST_MODULE);
        testModule.setHost(testHost);
        testModule.setJavaApp(true);
        
        testGenerator.addDataCollector(testModule, TEST_MONITORING_MANAGER_IP, TEST_MONITORING_MANAGER_PORT,
        		TEST_MONITORING_INFLUXDB_IP, TEST_MONITORING_INFLUXDB_PORT);
        
        Assert.assertNotNull(testModule);
        Assert.assertNotNull(testModule.getDataCollector());
        Assert.assertEquals(testModule.getDataCollector().size(), 1);     
        Map<String,Object> dataCollector = testModule.getDataCollector().get(0);

        Assert.assertNotNull(dataCollector.get(JAVA_APP_ID + "_" + testModule.getModuleName()));
        Map<String,Object> dcSpecification = (Map<String, Object>) dataCollector.get(JAVA_APP_ID + "_" + testModule.getModuleName());

        
        Assert.assertNotNull(dcSpecification.get(PROPERTIES));
        Map<String,Object> properties = (Map<String, Object>) dcSpecification.get(PROPERTIES);
        
        Assert.assertNotNull(properties.get(ENV_VARS));
        List<Map<String,Object>> env = (List<Map<String, Object>>) properties.get(ENV_VARS);
        
        for(Map<String,Object> var: env){
            for(String key: var.keySet()){
                switch (key) {
                case "MODACLOUDS_TOWER4CLOUDS_MANAGER_IP":
                    Assert.assertEquals(var.get(key), TEST_MONITORING_MANAGER_IP);
                    break;

                case "MODACLOUDS_TOWER4CLOUDS_MANAGER_PORT":
                    Assert.assertEquals(var.get(key), String.valueOf(TEST_MONITORING_MANAGER_PORT));
                    break;
        
                case "MODULE_ID":
                    Assert.assertEquals(var.get(key), testModule.getModuleName());
                    break;
                    
                default:
                    logger.error("Error: found a not valid environmental variable for the tested data collector.");
                    throw new IllegalStateException("Error: found a not valid environmental variable for the tested data collector.");
                }
            } 
        }
        


    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void modacloudsDcGeneratorTest(){
        
        MODACloudsDcGenerator testGenerator = new MODACloudsDcGenerator();
        Module testModule = new Module();
        Host testHost = new Host();

        testHost.setHostName(TEST_HOST);
        testHost.setDeploymentType(DeploymentType.IaaS);
        testModule.setModuleName(TEST_MODULE);
        testModule.setHost(testHost);
        
        
        testGenerator.addDataCollector(testModule, TEST_MONITORING_MANAGER_IP, TEST_MONITORING_MANAGER_PORT,
        		TEST_MONITORING_INFLUXDB_IP, TEST_MONITORING_INFLUXDB_PORT);
        
        Assert.assertNotNull(testModule);
        Assert.assertNotNull(testModule.getDataCollector());
        Assert.assertEquals(testModule.getDataCollector().size(), 1);     
        Map<String,Object> dataCollector = testModule.getDataCollector().get(0);
        
        Assert.assertNotNull(dataCollector.get(MODACLOUDS_DC_ID + "_" + testModule.getModuleName()));
        Map<String,Object> dcSpecification = (Map<String, Object>) dataCollector.get(MODACLOUDS_DC_ID + "_" + testModule.getModuleName());

        
        Assert.assertNotNull(dcSpecification.get(PROPERTIES));
        Map<String,Object> properties = (Map<String, Object>) dcSpecification.get(PROPERTIES);
        
        Assert.assertNotNull(properties.get(ENV_VARS));
        List<Map<String,Object>> env = (List<Map<String, Object>>) properties.get(ENV_VARS);
        
        for(Map<String,Object> var: env){
            for(String key: var.keySet()){
                switch (key) {
                case "MODACLOUDS_TOWER4CLOUDS_MANAGER_IP":
                    Assert.assertEquals(var.get(key), TEST_MONITORING_MANAGER_IP);
                    break;

                case "MODACLOUDS_TOWER4CLOUDS_MANAGER_PORT":
                    Assert.assertEquals(var.get(key), String.valueOf(TEST_MONITORING_MANAGER_PORT));
                    break;
                    
                case "MODACLOUDS_TOWER4CLOUDS_DC_SYNC_PERIOD":
                    Assert.assertEquals(var.get(key), SYNC_PERIOD);
                    break;
                    
                case "MODACLOUDS_TOWER4CLOUDS_RESOURCES_KEEP_ALIVE_PERIOD":
                    Assert.assertEquals(var.get(key), KEEP_ALIVE);
                    break;
                    
                case "MODACLOUDS_TOWER4CLOUDS_INTERNAL_COMPONENT_TYPE":
                    Assert.assertEquals(var.get(key), testModule.getModuleName());
                    break;
                    
                case "MODACLOUDS_TOWER4CLOUDS_INTERNAL_COMPONENT_ID":
                    Assert.assertEquals(var.get(key), testModule.getModuleName() + "_ID");
                    break;
                    
                case "MODACLOUDS_TOWER4CLOUDS_VM_TYPE":
                    Assert.assertEquals(var.get(key), testHost.getHostName());
                    break;
                    
                case "MODACLOUDS_TOWER4CLOUDS_VM_ID":
                    Assert.assertEquals(var.get(key), testHost.getHostName() + "_ID");
                    break;
                    
                default:
                    logger.error("Error: found a not valid environmental variable for the tested data collector.");
                    throw new IllegalStateException("Error: found a not valid environmental variable for the tested data collector.");
                }
            } 
        }
        


    }

}
