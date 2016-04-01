package eu.seaclouds.platform.planner.core.application;


import eu.seaclouds.platform.planner.core.application.decorators.MissingPolicyTypesDecorator;
import eu.seaclouds.platform.planner.core.application.decorators.MonitoringInformationDecorator;
import eu.seaclouds.platform.planner.core.application.decorators.SeaCloudsManagementPolicyDecorator;
import eu.seaclouds.platform.planner.core.application.decorators.SlaInformationDecorator;

public class ApplicationFacadeDecoratorApplicator {

    public void applyDecorators(ApplicationFacade applicationFacade) {
        MonitoringInformationDecorator monitoringInformationDecorator =
                new MonitoringInformationDecorator();
        monitoringInformationDecorator.apply(applicationFacade);

        SlaInformationDecorator slaInformationDecorator = new SlaInformationDecorator();
        slaInformationDecorator.apply(applicationFacade);

        SeaCloudsManagementPolicyDecorator seaCloudsManagmentPolicyDecorator =
                new SeaCloudsManagementPolicyDecorator();
        seaCloudsManagmentPolicyDecorator.apply(applicationFacade);

        MissingPolicyTypesDecorator missingPolicyTypesDecorator = new MissingPolicyTypesDecorator();
        missingPolicyTypesDecorator.apply(applicationFacade);
    }

}
