package eu.seaclouds.platform.sla.generator;

import java.io.File;
import java.io.IOException;

import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import eu.seaclouds.platform.sla.generator.SlaInfo.SlaInfoBuilder;

public class SlaInfoBuilderTest {

    @Test
    public void testBuildFromDam() throws IOException {
        
        SlaInfoBuilder builder = new SlaInfoBuilder(new RulesExtractor());
        
        File damFile = new File(this.getClass().getResource("/DemoDAM.yml").getFile());
        String damString = Files.toString(damFile, Charsets.UTF_8);
        builder.build(damString);
    }
    
    @Test
    public void testBuildFromDamAndRules() throws IOException {
        
        SlaInfoBuilder builder = new SlaInfoBuilder(new RulesExtractor());
        
        File damFile = new File(this.getClass().getResource("/DemoDAM2.yml").getFile());
        String damString = Files.toString(damFile, Charsets.UTF_8);

        File rulesFile = new File(this.getClass().getResource("/seacloudsRules.xml").getFile());
        String rulesString = Files.toString(rulesFile, Charsets.UTF_8);
        builder.build(damString, rulesString);
    }
}
