/*
 *  Copyright 2014 SeaClouds
 *  Contact: SeaClouds
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package eu.seaclouds.platform.planner.aamwriter;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import eu.seaclouds.platform.planner.aamwriter.modelaam.Aam;
import eu.seaclouds.platform.planner.aamwriter.modeldesigner.DGraph;

public class AamWriter {

    private Translator translator = new Translator();
    
    public static void main(String[] args) {
        
        if (args.length == 0) {
            System.err.println("Usage: aamwriter <topology.json>");
            System.exit(1);
        }
        String path = args[0];
        
        AamWriter writer = new AamWriter();
        JSONObject root;
        try {
            root = (JSONObject) Utils.loadJson(path);
        } catch (ParseException | IOException e) {
            
            throw new AamWriterException(e);
        }
        String yaml = writer.writeAam(root);
        System.out.println(yaml);
    }

    public String writeAam(String json) {
        
        JSONObject root = (JSONObject) JSONValue.parse(json);
        String yaml = writeAam(root);
        return yaml;
    }

    private String writeAam(JSONObject root) {
        DGraph graph = new DGraph(root);
        Aam aam = translator.translate(graph);
        
        String yaml = dumpYaml(aam);
        return yaml;
    }
    
    private String dumpYaml(Aam aam) {
        DumperOptions options = new DumperOptions();
        options.setLineBreak(DumperOptions.LineBreak.getPlatformLineBreak());
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yamlParser = new Yaml(options);
        
        String yamlString = yamlParser.dump(aam);
        return yamlString;
    }
    
}
