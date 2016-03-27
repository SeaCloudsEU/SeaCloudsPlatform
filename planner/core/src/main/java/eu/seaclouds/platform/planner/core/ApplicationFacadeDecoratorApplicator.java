package eu.seaclouds.platform.planner.core;

import eu.seaclouds.platform.planner.core.decorators.MissingPolicyTypesDecorator;
import eu.seaclouds.platform.planner.core.decorators.MonitoringInformationDecorator;
import eu.seaclouds.platform.planner.core.decorators.SeaCloudsManagmentPolicyDecorator;
import eu.seaclouds.platform.planner.core.decorators.SlaInformationDecorator;

public class ApplicationFacadeDecoratorApplicator {

    public void applyDecorators(ApplicationFacade applicationFacade) {
        MonitoringInformationDecorator monitoringInformationDecorator =
                new MonitoringInformationDecorator();
        monitoringInformationDecorator.apply(applicationFacade);

        SlaInformationDecorator slaInformationDecorator = new SlaInformationDecorator();
        slaInformationDecorator.apply(applicationFacade);

        SeaCloudsManagmentPolicyDecorator seaCloudsManagmentPolicyDecorator =
                new SeaCloudsManagmentPolicyDecorator();
        seaCloudsManagmentPolicyDecorator.apply(applicationFacade);

        MissingPolicyTypesDecorator missingPolicyTypesDecorator = new MissingPolicyTypesDecorator();
        missingPolicyTypesDecorator.apply(applicationFacade);
    }

}
