/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.seaclouds.planner.matchmaker;

import alien4cloud.model.components.*;
import alien4cloud.model.components.constraints.*;
import alien4cloud.model.topology.NodeTemplate;
import alien4cloud.tosca.model.ArchiveRoot;
import alien4cloud.tosca.parser.ParsingResult;
import eu.seaclouds.planner.matchmaker.constraints.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This class provides the implemantation of matchmaking facility for the Planner component of the SeaClouds European Project
 */
public class Matchmaker {

    private final static String REQUIREMENTS_PREFIX = "sc_req.";

    public Matchmaker(){}

    public PropertyValue getPropertyValue(String name, AbstractPropertyValue p){
        if(p instanceof ScalarPropertyValue){
            return new PropertyValue(name, ((ScalarPropertyValue) p).getValue());
        }
        //TODO: check if only ScalarPropertyValue is possible
        return null;
    }

    public Constraint getConstraint(String name, PropertyConstraint c){
        if(c instanceof EqualConstraint){
            return new ConstraintEqual<>(name, ((EqualConstraint) c).getEqual());
        }
        if(c instanceof GreaterThanConstraint){
            return new ConstraintGreaterThan<>(name, ((GreaterThanConstraint) c).getGreaterThan());
        }
        if(c instanceof GreaterOrEqualConstraint){
            return new ConstraintGreaterOrEqual<>(name, ((GreaterOrEqualConstraint) c).getGreaterOrEqual());
        }
        if(c instanceof LessThanConstraint){
            return new ConstraintLessThan<>(name, ((LessThanConstraint) c).getLessThan());
        }
        if(c instanceof LessOrEqualConstraint){
            return new ConstraintLessOrEqual<>(name, ((LessOrEqualConstraint) c).getLessOrEqual());
        }
        if(c instanceof ValidValuesConstraint){
            return new ConstraintValidValues<>(name, ((ValidValuesConstraint) c).getValidValuesTyped());
        }
        if(c instanceof InRangeConstraint){
            InRangeConstraint rc = (InRangeConstraint) c;
            return new ConstraintInRange<>(name, Integer.parseInt(rc.getRangeMinValue()), Integer.parseInt(rc.getRangeMaxValue()));
        }
        if(c instanceof LengthConstraint){
            return new ConstraintLength<>(name, ((LengthConstraint) c).getLength());
        }
        if(c instanceof MaxLengthConstraint){
            return new ConstraintMaxLength<>(name, ((MaxLengthConstraint) c).getMaxLength());
        }
        if(c instanceof MinLengthConstraint){
           return new ConstraintMinLength<>(name, ((MinLengthConstraint) c).getMinLength());
        }
        if(c instanceof PatternConstraint){
            return new ConstraintPattern<>(name, ((PatternConstraint) c).getCompiledPattern());
        }

        throw new UnsupportedOperationException("Unknown Constraint");
    }

    private boolean matchProperty(PropertyDefinition requirement, AbstractPropertyValue offered){
        PropertyValue v = this.getPropertyValue("", offered);
        for(PropertyConstraint c : requirement.getConstraints()){
            if(!this.getConstraint("", c).checkConstraint(v)) return false;
        }
        return true;
    }

    private boolean matchProperty(AbstractPropertyValue p1, AbstractPropertyValue p2){
        boolean def1 = p1.isDefinition();
        boolean def2 = p2.isDefinition();

        if(p1.isDefinition() && p2.isDefinition()){
            try{
                PropertyDefinition moduleDef = ((PropertyDefinition) ((IValue) p1));
                PropertyDefinition offeringDef = ((PropertyDefinition) ((IValue) p2));

                moduleDef.checkIfCompatibleOrFail(offeringDef);

            }catch (IncompatiblePropertyDefinitionException e){
                return false;
            }
        }
        return true;
    }

    private boolean match(IndexedNodeType module, NodeTemplate offering){
        //Check properties
        if(module.getProperties() == null) return true;

        PropertyValue offeringType = getPropertyValue("", offering.getProperties().get("resource_type"));

        // for each property of the module
        for(String p:module.getProperties().keySet()) {
            // if constraints contain software support and the offering is of type compute,
            // the constraint is considered satisfied
            if (!offering.getProperties().containsKey(p) &&
                    offeringType.getValue().equals("compute") &&
                    p.endsWith("_support"))
                continue;

            // if the offering does not specify a software version, the constraint is considered satisfied
            if (!offering.getProperties().containsKey(p) && p.endsWith("_version"))
                continue;

            if(!offering.getProperties().containsKey(p)) return false; //no info means rejection

            PropertyDefinition moduleProperty = module.getProperties().get(p); //TODO: check if this should be requirements instead of property
            AbstractPropertyValue offeringProperty = offering.getProperties().get(p);

            if(!matchProperty(moduleProperty, offeringProperty)) return false;
        }
        return true;
    }

    /**
     * Matches for each module the technical requirement with the available offerings.
     * @param aamModules the map of the application modules, taken from the abstract application modules.
     * @param offerings the map containing the available offerings.
     * @return for each module name, a set of suitable matched offerings.
     */
    @Deprecated
    public Map<String, HashSet<String>> match(Map<String, IndexedNodeType> aamModules, Map<String, NodeTemplate> offerings){
        Map<String, HashSet<String>> mathedOfferings = new HashMap<>();

        for(String moduleName: aamModules.keySet()){
            IndexedNodeType module = aamModules.get(moduleName);

            mathedOfferings.put(moduleName, new HashSet<String>());

            for(String offerName: offerings.keySet()){
                NodeTemplate offer = offerings.get(offerName);
                if(match(module, offer)){
                    mathedOfferings.get(moduleName).add(offerName);
                }
            }
        }
        return mathedOfferings;
    }

    /**
     *
     * @param aam
     * @param offerings
     * @return
     */
    public Map<String, HashSet<String>> match(ParsingResult<ArchiveRoot> aam, Map<String, Pair<NodeTemplate, String>> offerings){
        Map<String, HashSet<String>> matchingResult = new HashMap<>();

        Map<String, IndexedNodeType> aamTypes = aam.getResult().getNodeTypes();
        Map<String, NodeTemplate> aamTemplates = aam.getResult().getTopology().getNodeTemplates();

        for(String module:aamTemplates.keySet()){
            NodeTemplate aamNt = aamTemplates.get(module);
            if(isRequirementsNode(aamNt.getType())){
                matchingResult.put(module, new HashSet<String>());
                IndexedNodeType moduleType = aamTypes.get(aamNt.getType());

                HashSet<String> matchingOfferings = matchingResult.get(module);

                for(String offerID: offerings.keySet()){
                    NodeTemplate offerNt = offerings.get(offerID).first;
                    if(match(moduleType, offerNt)) matchingOfferings.add(offerID);
                }
            }
        }
        return matchingResult;
    }

    private boolean isRequirementsNode(String type) {
        return type.startsWith(REQUIREMENTS_PREFIX);
    }
}
