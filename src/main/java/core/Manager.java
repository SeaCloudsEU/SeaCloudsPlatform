package core;


import model.Application;
import model.exceptions.ApplicationNotFoundException;
import model.exceptions.DuplicatedConnectorException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Adrian on 14/10/2014.
 */
public class Manager {

    private Manager instance;
    private List<Application> applicationList;
    private Map<Application, List<Connector>> connectorMap;

    private Manager(){
        applicationList = new LinkedList<>();
    }

    public Manager getInstance(){
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }

    public void registerApplication(Application app){
        applicationList.add(app);
    }

    public void addMonitoringAgent(Application app, Connector connector){
        int idx = applicationList.indexOf(app);
        if (idx == -1){
            throw new ApplicationNotFoundException(app.getId().toString());
        }else{
            List<Connector> connectorList = connectorMap.get(applicationList.get(idx));
            if(connectorList.contains(connector)) {
                //TODO: Add connector toString to get pretty error msgs
                throw new DuplicatedConnectorException(connector.toString(), app.getName());
            }else{
                connectorList.add(connector);
            }
        }
    }
}
