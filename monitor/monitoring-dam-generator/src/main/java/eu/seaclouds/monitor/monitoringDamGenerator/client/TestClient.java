package eu.seaclouds.monitor.monitoringDamGenerator.client;

import it.polimi.tower4clouds.rules.MonitoringRule;
import it.polimi.tower4clouds.rules.MonitoringRules;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.json.JSONArray;
import org.yaml.snakeyaml.Yaml;

import eu.seaclouds.monitor.monitoringDamGenerator.service.MonitoringDamGenerator;

public class TestClient {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        InputStream is;

        try {
            is = new FileInputStream("resources/webchatADP.yml");

            MonitoringDamGenerator service = new MonitoringDamGenerator();

            String response = service.receiveData(is);

            is.close();

            JSONArray jsonArray = new JSONArray(response);
            Yaml yaml = new Yaml();
            
            System.out.println(jsonArray);

            for (int i = 0; i < jsonArray.length(); i++) {

                Map<String, Object> moduleElement = (Map<String, Object>) yaml
                        .load(jsonArray.getString(i));

                System.out.println(jsonArray.get(i));
                System.out.println("------------------------");

                for (String module : moduleElement.keySet()) {
                    System.out.println(module);
                    System.out.println("------------------");
                    System.out.println(moduleElement.get(module));
                    System.out.println("------------------");

                    Map<String, Object> fields = (Map<String, Object>) moduleElement
                            .get(module);

                    for (String field : fields.keySet()) {
                        if (field.equals("members")) {
                            List<String> members = (List<String>) fields
                                    .get(field);

                            for (String member : members) {
                                System.out.println(member);
                                System.out.println("------------------");
                            }
                        } else if (field.equals("policies")) {
                            List<Map<String, Object>> policies = (List<Map<String, Object>>) fields
                                    .get(field);

                            for (Map<String, Object> policy : policies) {
                                for (String key : policy.keySet()) {

                                    if(key.equals("monitoringRules")){
                                        JAXBContext context;
                                        context = JAXBContext
                                                .newInstance("it.polimi.tower4clouds.rules");
                                        Unmarshaller unmarshaller = context.createUnmarshaller();
                                        InputStream stream = new ByteArrayInputStream(policy.get(key).toString().getBytes());
                                        MonitoringRules rules=(MonitoringRules) unmarshaller.unmarshal(stream);
                                        
                                        for(MonitoringRule rule: rules.getMonitoringRules()){
                                            System.out.println(rule.getId());
                                        }
                                    } else if(key.equals("deploymentScript")){
                                        System.out.println(policy.get(key));
                                    }
                                }
                            }
                        }
                    }

                }

            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
