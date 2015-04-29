/*
 * Copyright 2015 Universita' di Pisa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package seaclouds.planner;

import seaclouds.utils.toscamodel.*;
import seaclouds.utils.toscamodel.impl.NodeTemplate;

import java.util.*;

/**
 * Created by pq on 17/04/2015.
 */
public class Matchmaker {
    final IToscaEnvironment offeringEnvironment; //would initialize with a connection to the discoverer database
    Matchmaker(IToscaEnvironment offerings){
        offeringEnvironment = offerings;
    }
    public Map<String, List<INodeType>> Match(IToscaEnvironment aam) {
        //workflow to read a Tosca file with AAM and compare them with cloud offerings from discoverer
        INodeType snc = (INodeType) aam.getNamedEntity("seaclouds.nodes.Compute");
        INodeType snp = (INodeType) aam.getNamedEntity("seaclouds.nodes.Platform");

        List<INodeTemplate> matchableTopology = new ArrayList<INodeTemplate>();

        for (INodeTemplate t : aam.getNodeTemplatesOfType(snc)) {
            matchableTopology.add(t);
        }
        for (INodeTemplate t : aam.getNodeTemplatesOfType(snp)) {
            matchableTopology.add(t);
        }

        Map<String,List<INodeType>> matchmaking = new HashMap<>();
        for (INodeTemplate e: matchableTopology) {
            INodeType aamType = e.baseType();
            String templateName = ((INamedEntity)aamType).name();
            INodeType offeringType =(INodeType) offeringEnvironment.getNamedEntity(templateName);
            while(offeringType == null)
            {
                aamType = aamType.baseType();
                offeringType = (INodeType) offeringEnvironment.getNamedEntity(templateName);
            }

            Iterable<INodeType> potentialOfferings = offeringEnvironment.getNodeTypesDerivingFrom(offeringType);
            ArrayList<INodeType> validOfferings = new ArrayList<>();
            for (INodeType o : potentialOfferings)
            {
                boolean valid = true;
                for (Map.Entry<String,IProperty> entry : aamType.allProperties().entrySet()) {
                    IValue offeringValue = o.allAttributes().get(entry.getKey());
                    IValue aamValue = e.allAttributes().get(entry.getKey());
                    boolean constraintIsValid = true;
                    //for (IConstraint constraint : o.allProperties().get(entry.getKey()).constraints()) {
                    //   constraintIsValid = constraintIsValid && constraint.verify(offeringValue);
                    //}
                    // this should compare using partial ordering
                    //if (!MatchMaker.betterThan(offeringValue,entry.getValue()))

                    if(!constraintIsValid || aamValue!= null && !aamValue.equals(offeringValue))
                    {
                        valid = false;
                        break;
                    }
                }
                if(valid)
                    validOfferings.add(o);
            }
            matchmaking.put(((INamedEntity) e).name(),validOfferings);
        }

        return matchmaking;

    }}
