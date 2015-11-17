/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.seaclouds.common.tosca;

import alien4cloud.model.components.CSARDependency;
import alien4cloud.model.topology.Topology;
import alien4cloud.tosca.model.ArchiveRoot;
import alien4cloud.tosca.parser.ParsingException;
import alien4cloud.tosca.parser.ParsingResult;
import alien4cloud.tosca.parser.ToscaParser;
import alien4cloud.tosca.serializer.VelocityUtil;
import com.google.common.base.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ToscaSerializer {

    static Logger log = LoggerFactory.getLogger(ToscaSerializer.class);

    public final static String TEMPLATE_VERSION = "1.0.0-SNAPSHOT";
    private static Supplier<ToscaParser> p = new Supplier<ToscaParser>() {
        @Override
        public ToscaParser get() {
            return new ToscaParserSupplier().get();
        }
    };

    public static String toTOSCA(Topology topology, String author, String name, String description) {

        topology.getDependencies().add(new CSARDependency("tosca-normative-types", "1.0.0.wd03-SNAPSHOT"));

        Map<String, Object> velocityCtx = new HashMap<>();
        velocityCtx.put("topology", topology);
        velocityCtx.put("template_name", name);
        velocityCtx.put("template_version", TEMPLATE_VERSION);
        velocityCtx.put("template_author", author);
        velocityCtx.put("template_description", description);

        StringWriter writer = new StringWriter();
        try {
            VelocityUtil.generate("topology-1_0_0_wd03.yml.vm", writer, velocityCtx);
        }catch(IOException e){
            throw new RuntimeException("Exception generating the yaml");
        }
        return writer.toString();
    }

    public static ParsingResult<ArchiveRoot> fromTOSCA(Path filePath) throws ParsingException {
        ToscaParser parser = p.get();
        ParsingResult<ArchiveRoot> parsingResult = parser.parseFile(filePath);
        return parsingResult;
    }

    public static ParsingResult<ArchiveRoot> fromTOSCA(String yaml) throws ParsingException, IOException {
        log.debug("Parsing: \n" + yaml);
        File tempFile = File.createTempFile("toscayamlFile", null);
        BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
        bw.write(yaml);
        bw.close();

        ToscaParser parser = p.get();
        ParsingResult<ArchiveRoot> parsingResult = parser.parseFile(Paths.get(tempFile.getAbsolutePath()));

        tempFile.delete();

        return parsingResult;
    }

}
