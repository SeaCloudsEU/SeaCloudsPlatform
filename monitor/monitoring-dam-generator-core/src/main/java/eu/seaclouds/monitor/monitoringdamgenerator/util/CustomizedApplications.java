package eu.seaclouds.monitor.monitoringdamgenerator.util;

import java.util.ArrayList;
import java.util.List;

public class CustomizedApplications {

    private static List<String> customizedApplications=new ArrayList<String>();
    
    
    static {
        customizedApplications.add("NuroApplication");
    }
    
    
    public static List<String> getCustomizedApplications(){
        return customizedApplications;
    }
    
    public static boolean isCustomized(String application){
        return customizedApplications.contains(application);
    }
}