package eu.seaclouds.monitor.monitoringdamgeneratorservice;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringDamGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringInfo;

@Path("/")
public class Service {

    static Logger log = LoggerFactory.getLogger(Service.class);

    private static String monitoringManagerIp;
    private static String monitoringManagerPort;

    private static String influxdbIp;
    private static String influxdbPort;

    private static Map<String, MonitoringInfo> monitoringInfoByApplication = new HashMap<String, MonitoringInfo>();

    public Service(String monitorUrl, String monitorPort, String influxIp, String influxPort) {
        monitoringManagerIp = monitorUrl;
        monitoringManagerPort = monitorPort;

        influxdbIp = influxIp;
        influxdbPort = influxPort;

    }

    @POST
    @Path("/generateDamMonitoringInfo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public ApplicationId generateMonitoringInfo(String adp) {

        MonitoringDamGenerator monDamGen;

        try {
            monDamGen = new MonitoringDamGenerator(
                    new URL("http://" + monitoringManagerIp + ":" + monitoringManagerPort + ""),
                    new URL("http://" + influxdbIp + ":" + influxdbPort + ""));

            String generatedApplicationId = UUID.randomUUID().toString();
            MonitoringInfo generated = monDamGen.generateMonitoringInfo(adp);

            monitoringInfoByApplication.put(generatedApplicationId, generated);

            return new ApplicationId(generatedApplicationId);
        } catch (MalformedURLException e) {
            log.error(e.getMessage());
        }

        return null;

    }

    /**
     * @param applicationId
     *            the id of an application previously obtained calling the
     *            /generateDamMonitoringInfo endpoint
     * @return all the monitoring rules that need to be installed into Tower
     *         4Cloud
     */
    @GET
    @Path("/{id}/monitoringRules/")
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationRules getMonitoringRulesByApplication(@PathParam("id") String applicationId) {

        StringWriter sw = new StringWriter();

        JAXBContext context;
        try {
            context = JAXBContext.newInstance("it.polimi.tower4clouds.rules");
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
            marshaller.marshal(monitoringInfoByApplication.get(applicationId).getApplicationMonitoringRules(), sw);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new ApplicationRules(sw.toString());
    }

    /**
     * @param applicationId
     *            the id of an application previously obtained calling the
     *            /generateDamMonitoringInfo endpoint
     * @return the list of all the node templates that need to be added to the
     *         DAM in order to deploy the data collectors associated which
     *         applicationId
     */
    @GET
    @Path("/{id}/generatedAdp")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneratedAdp getGeneratedAdp(@PathParam("id") String applicationId) {

        return new GeneratedAdp(monitoringInfoByApplication.get(applicationId).getReturnedAdp());
    }

}