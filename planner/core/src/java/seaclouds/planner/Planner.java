package seaclouds.planner;

import seaclouds.utils.toscamodel.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class Planner {
    final private OptimizerExample optimizer = new OptimizerExample();
    final private IToscaEnvironment discoverer = Tosca.newEnvironment();
    final private Matchmaker matchmaker = new Matchmaker(discoverer);

    public Planner() {

    }

    List<IToscaEnvironment> plan(IToscaEnvironment aam) {
        InputStream stream = this.getClass().getResourceAsStream("../../../../aam.yaml");
        aam.readFile(new InputStreamReader(stream));

        Map<String, List<INodeType>> matches = matchmaker.Match(aam);
        return optimizer.optimize(aam, matches);
    }
}
