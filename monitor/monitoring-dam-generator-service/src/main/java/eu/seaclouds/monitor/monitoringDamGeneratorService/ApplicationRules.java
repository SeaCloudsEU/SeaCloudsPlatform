package eu.seaclouds.monitor.monitoringDamGeneratorService;


public class ApplicationRules {
    
    private String rules;
    
    public ApplicationRules(String toSet){
        this.rules=toSet;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

}
