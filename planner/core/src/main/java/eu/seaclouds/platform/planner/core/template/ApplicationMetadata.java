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
package eu.seaclouds.platform.planner.core.template;

import org.apache.brooklyn.util.collections.MutableList;
import org.apache.brooklyn.util.text.Identifiers;
import org.apache.brooklyn.util.text.Strings;

import java.util.List;
import java.util.Map;

public class ApplicationMetadata {

    public static final String IMPORTS = "imports";
    public static final String TOSCA_NORMATIVE_TYPES = "tosca-normative-types";
    public static final String TOSCA_NORMATIVE_TYPES_VERSION = "1.0.0.wd06-SNAPSHOT";
    public static final String SEACLOUDS_NODE_TYPES = "seaclouds-types";
    public static final String SEACLOUDS_NODE_TYPES_VERSION = "0.8.0-SNAPSHOT";

    public static final String TEMPLATE_NAME = "template_name";
    public static final String TEMPLATE_NAME_PREFIX = "seaclouds.app.";
    public static final String TEMPLATE_VERSION = "template_version";
    public static final String DEFAULT_TEMPLATE_VERSION = "1.0.0-SNAPSHOT";
    private final Map<String, Object> template;
    private String templateName;
    private String templateVersion;
    private List<String> imports;

    public ApplicationMetadata(Map<String, Object> template) {
        this.template = template;
        init();
    }

    private void init() {
        initTemplateName();
        initTemplateVersion();
        initImports();
    }

    private void initTemplateVersion() {
        this.templateVersion = Strings.isBlank((String) template.get(TEMPLATE_VERSION))
                ? DEFAULT_TEMPLATE_VERSION
                : (String) template.get(TEMPLATE_VERSION);
    }

    private void initTemplateName() {
        this.templateName = Strings.isBlank((String) template.get(TEMPLATE_NAME))
                ? TEMPLATE_NAME_PREFIX + Identifiers.makeRandomId(8)
                : (String) template.get(TEMPLATE_NAME);
    }

    private void initImports() {
        this.imports = (template.get(IMPORTS) != null)
                ? (List<String>) template.get(IMPORTS)
                : MutableList.<String>of();

        fixNormativeTypesVersion();
        imports.add(SEACLOUDS_NODE_TYPES + ":" + SEACLOUDS_NODE_TYPES_VERSION);
    }

    private void fixNormativeTypesVersion() {
        String importedNormativeTypes = null;
        for (String dependency : imports) {
            if (dependency.contains(TOSCA_NORMATIVE_TYPES)) {
                importedNormativeTypes = dependency;
            }
        }
        if ((importedNormativeTypes != null) && (!importedNormativeTypes.equals(TOSCA_NORMATIVE_TYPES + ":" + TOSCA_NORMATIVE_TYPES_VERSION))) {
            imports.remove(importedNormativeTypes);
            imports.add(TOSCA_NORMATIVE_TYPES + ":" + TOSCA_NORMATIVE_TYPES_VERSION);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> normalizeMetadata() {
        addTemplateImports();
        addTemplateName();
        addTemplateVersion();
        return template;
    }

    private void addTemplateName() {
        template.put(TEMPLATE_NAME, templateName);
    }

    private void addTemplateVersion() {
        template.put(TEMPLATE_VERSION, templateVersion);
    }

    private void addTemplateImports(){
        template.put(IMPORTS, imports);
    }


}
