package seaclouds.planner;

import seaclouds.utils.toscamodel.IToscaEnvironment;
import seaclouds.utils.toscamodel.Tosca;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Discoverer {
    private final IToscaEnvironment offerings;
    private static final Discoverer instance = new Discoverer();
    public static Discoverer instance() {
        return instance;
    }

    private Discoverer() {
        offerings = Tosca.newEnvironment();
        try {
            //configure discoverer
            InputStream stream;
            CodeSource src = this.getClass().getProtectionDomain().getCodeSource();
            if (src != null) {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                ZipEntry e = zip.getNextEntry();
                while (e != null) {
                    String name = e.getName();
                    if (name.startsWith("seaclouds/planner/offerings/") && name.endsWith(".yaml")){
                        stream = this.getClass().getResourceAsStream(name);
                        offerings.readFile(new InputStreamReader(stream));
                    }
                }

            }
            //stream = this.getClass().getResourceAsStream("offerings/amazon_c1_xlarge.yaml");
            //offerings.readFile(new InputStreamReader(stream));
            //stream = this.getClass().getResourceAsStream("offerings/platform_offerings_test.yaml");
            //offerings.readFile(new InputStreamReader(stream));
            //stream = this.getClass().getResourceAsStream("input/hp_cloud_serv.yaml");
            //offerings.readFile(new InputStreamReader(stream), true);
        } catch (IOException e) {

        }
    }

    public IToscaEnvironment getOfferings(){
        return  offerings;
    }
}
