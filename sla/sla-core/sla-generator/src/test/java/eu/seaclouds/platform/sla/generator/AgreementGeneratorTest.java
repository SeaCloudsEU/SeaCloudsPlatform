package eu.seaclouds.platform.sla.generator;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import eu.atos.sla.parser.ParserException;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.Template;
import eu.atos.sla.parser.xml.TemplateParser;

public class AgreementGeneratorTest {
    
    @Test
    public void testGenerate() throws JAXBException, IOException {
        
        TemplateParser templateParser = new TemplateParser();
        File file = new File(AgreementGeneratorTest.class.getResource("/template.xml").getFile());
        String templateString = Files.toString(file, Charsets.UTF_8);
        Template wsagTemplate;
        try {
            wsagTemplate = templateParser.getWsagObject(templateString);
        } catch (ParserException e) {
            throw new SlaGeneratorException(e.getMessage(), e);
        }
        Agreement wsagAgreement = new AgreementGenerator(wsagTemplate).generate();
        JaxbUtils.print(wsagAgreement);
    }

}
