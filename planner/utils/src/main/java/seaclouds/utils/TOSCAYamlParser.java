package seaclouds.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TOSCAYamlParser {

    static Logger log = LoggerFactory.getLogger(TOSCAYamlParser.class);

    Map<String, Object> TOSCAdefinitions;

    public TOSCAYamlParser(String topologyFile) throws FileNotFoundException {
        Yaml userYaml = new Yaml();
        TOSCAdefinitions = (Map) userYaml.load(topologyFile);
    }

    public TOSCAYamlParser(InputStream userInput) {

        Yaml userYaml = new Yaml();

        //TODO: add try-catch block (userInput can be invalid)
        TOSCAdefinitions = (Map) userYaml.load(userInput);

        //TODO: check if empty or not valid

        //TODO: check if it is a valid TOSCA file
    }

    public void writeYaml() {
        Yaml definitionsYaml = new Yaml();
        String output = definitionsYaml.dump(TOSCAdefinitions);
        StringWriter writer = new StringWriter();
        definitionsYaml.dump(TOSCAdefinitions, writer);
        //log.info(writer.toString());
    }

    public String getYaml() {
        Yaml definitionsYaml = new Yaml();
        return definitionsYaml.dump(TOSCAdefinitions);
    }

    public Map<String, Object> getTOSCAdefinitions() {
        return TOSCAdefinitions;
    }

    public void setTOSCAdefinitions(Map<String, Object> toscadefinitions) {
        TOSCAdefinitions = toscadefinitions;
    }

    public String getVersion() {
        //TODO: check existence?
        String keyname = "tosca_definitions_version";
        return (String) TOSCAdefinitions.get(keyname);
    }

    public String getNamespace() {
        //TODO:
        return null;
    }

    public String getTemplateName() {
        String keyname = "template_name";
        if (!TOSCAdefinitions.containsKey(keyname)) {
            return null;
        } else {
            return (String) TOSCAdefinitions.get(keyname);
        }
    }

    public String getTemplateAuthor() {
        String keyname = "template_author";
        if (!TOSCAdefinitions.containsKey(keyname)) {
            return null;
        } else {
            return (String) TOSCAdefinitions.get(keyname);
        }
    }

    public String getTemplateVersion() {
        String keyname = "template_version";
        if (!TOSCAdefinitions.containsKey(keyname)) {
            return null;
        } else {
            return (String) TOSCAdefinitions.get(keyname);
        }
    }

    public String getDescription() {
        String keyname = "description";
        if (!TOSCAdefinitions.containsKey(keyname)) {
            return null;
        } else {
            return (String) TOSCAdefinitions.get(keyname);
        }
    }

    public String getImports() {
        //TODO:
        return null;
    }

    public String getInputs() {
        //TODO:
        return null;
    }

    public Map<String, Object> getNodeTemplates() {
        String keyname = "node_templates";
        if (!TOSCAdefinitions.containsKey(keyname)) {
            // log.info("This keyname has not been defined.");
            return null;
        } else {
            return (Map<String, Object>) TOSCAdefinitions.get(keyname);
        }
    }

    public void setNodeTemplates(Map<String, Object> nodeTemplatesList) {
        TOSCAdefinitions.put("node_templates", nodeTemplatesList);
    }

    public Map<String, Object> getNodeTemplate(String name) {
        Map<String, Object> nodeTemplates = getNodeTemplates();
        if (nodeTemplates.containsKey(name)) {
            Map<String, Object> nodeTemplate = new LinkedHashMap<String, Object>();
            nodeTemplate.put(name, nodeTemplates.get(name));
            return nodeTemplate;

        } else {
            return null;
        }

    }

    public void setNodeTemplate(String name, Object value) {
        //TODO: check if input is valid
        //retrieve node templates list
        Map<String, Object> nodeTemplatesList = this.getNodeTemplates();
        //update node templates list
        nodeTemplatesList.put(name, value);
        //update old node templates list in TOSCAdefinitions
        TOSCAdefinitions.put("node_templates", nodeTemplatesList);
    }

    public Map<String, Object> getNodeTypes() {
        String keyname = "node_types";
        if (!TOSCAdefinitions.containsKey(keyname)) {
            log.info("This keyname has not been defined.");
            return null;
        } else {
            return (Map<String, Object>) TOSCAdefinitions.get(keyname);
        }
    }

    public String getRelationshipTypes() {
        //TODO:
        log.info("Warning: this function has not been defined yet");
        return null;
    }

    public String getCapabilityTypes() {
        //TODO:
        log.info("Warning: this function has not been defined yet");
        return null;
    }

    public String getArtifactTypes() {
        //TODO:
        log.info("Warning: this function has not been defined yet");
        return null;
    }

    public String getOutputs() {
        //TODO:
        log.info("Warning: this function has not been defined yet");
        return null;
    }

    public String getGroups() {
        //TODO:
        log.info("Warning: this function has not been defined yet");
        return null;
    }

    //TODO: functions to modify the status of the definitions file


    //TODO I think it will be better something like getNodeTemplateIds or similar
    public Set<String> getNodeTemplateKeyNames() {
        Set<String> result = null;
        if (getNodeTemplates() != null) {
            result = this.getNodeTemplates().keySet();
        }
        return result;
    }

    public List getNodeTemplateRequirements(String key){
        List result = null;
        Map<String, Object> TnodeTemplate = getNodeTemplate(key);
        if (TnodeTemplate!=null) {
            result = (List) ((Map)TnodeTemplate.get(key)).get("requirements");
        }
        return result;
    }

}