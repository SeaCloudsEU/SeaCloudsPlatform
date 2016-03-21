/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.seaclouds.platform.planner.core.template.modifiers;


import eu.seaclouds.platform.planner.core.template.NodeTemplate;

import java.util.List;
import java.util.Map;

public class JavaPaasArtifactsModifier implements NodeTemplateFacadeModifier {

    private static final String TARGET_TYPE = "org.apache.brooklyn.entity.cloudfoundry.webapp.java.JavaCloudFoundryPaasWebApp";
    private static final String WARS_ROOT_ARTIFACT = "wars.root";
    private static final String APPLICATION_URL_ARTIFACT = "application-url";

    private boolean isApplicable(NodeTemplate nodeTemplate) {
        return (!nodeTemplate.isDeployedOnIaaS())
                && nodeTemplate.getType().equals(TARGET_TYPE);
    }

    @Override
    public void apply(NodeTemplate nodeTemplate) {
        if (isApplicable(nodeTemplate)) {
            modifyTargetArtifactProperty(nodeTemplate);
        }
    }

    private void modifyTargetArtifactProperty(NodeTemplate nodeTemplate) {
        List<Map<String, Object>> artifacts = nodeTemplate.getArtifacts();
        for (Map<String, Object> artifact : artifacts) {
            modifyWarsRootArtifact(artifact);
        }
    }

    private void modifyWarsRootArtifact(Map<String, Object> artifact) {
        if (artifact.containsKey(WARS_ROOT_ARTIFACT)) {
            String targetUrlArtifact = (String) artifact.get(WARS_ROOT_ARTIFACT);
            artifact.remove(WARS_ROOT_ARTIFACT);
            addApplicationUrlArtifact(artifact, targetUrlArtifact);
        }
    }

    private void addApplicationUrlArtifact(Map<String, Object> artifact, String targetUrlArtifac) {
        artifact.put(APPLICATION_URL_ARTIFACT, targetUrlArtifac);
    }

}
