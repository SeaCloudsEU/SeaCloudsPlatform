package eu.seaclouds.monitor.monitoringdamgenerator.adpparsing;

import java.util.Iterator;
import java.util.List;

public class Metrics {
    
    private List<String> metrics;
    
    public Metrics(List<String> metrics){
        this.metrics = metrics;
    }
    
    public List<String> getMetrics(){
        return this.metrics;
    }
    
    
    @Override
    public String toString() {
        Iterator<String> iter = metrics.iterator();
        
        String toReturn = new String((String) iter.next());
        
        while(iter.hasNext()){
            toReturn = toReturn + ";" + iter.next();
        }

        return toReturn;
    }

}
