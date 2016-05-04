package eu.seaclouds.platform.planner.core.application;


import eu.seaclouds.platform.planner.core.application.decorators.MissingPolicyTypesDecorator;
import eu.seaclouds.platform.planner.core.application.decorators.MonitoringInformationDecorator;
import eu.seaclouds.platform.planner.core.application.decorators.SeaCloudsManagementPolicyDecorator;
import eu.seaclouds.platform.planner.core.application.decorators.SeaCloudsMonitoringInitializerPolicyDecorator;
import eu.seaclouds.platform.planner.core.application.decorators.SlaInformationDecorator;

public class ApplicationFacadeDecoratorApplicator {

    public void applyDecorators(ApplicationFacade applicationFacade) {
        new MonitoringInformationDecorator().apply(applicationFacade);
        new SlaInformationDecorator().apply(applicationFacade);
        new SeaCloudsManagementPolicyDecorator().apply(applicationFacade);
        new SeaCloudsMonitoringInitializerPolicyDecorator().apply(applicationFacade);
        new MissingPolicyTypesDecorator().apply(applicationFacade);
    }

}
