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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import seaclouds.utils.toscamodel.*;
import seaclouds.utils.toscamodel.basictypes.IValueInteger;

import java.util.*;

/**
 * Created by pq on 26/04/2015.
 */
public class Optimizer {

    //we provide an example of Optimizer that generates two simple solutions
    public final int wantedSolutions = 3;

    /*
     * This method shows how to interact with the parser to compute the score of a generated solution.
     * @param aam Abstract Application Model
     * @param solution The solution, which here is a map consisting on: <String that identifies an AAM module, cloudOffering Node>
     * @return computed score of the provided solution
     */
    private double computeScore(IToscaEnvironment aam, Map<String, INodeType> solution){
        //here the score function for given solution shall be implemented.
        //As an example, we return as score the sum of all cpu cores in the solution
        int coreCount = 0;
        for (INodeType nodeType : solution.values()) {
            //For each node of the solution (i.e. for each cloud offering)
            //we get the value of the attribute named "cpu_count"
            IValue v = nodeType.allAttributes().get("cpu_count");
            if(v instanceof IValueInteger)
                coreCount += ((IValueInteger) v).get();
        }
        return coreCount;
    }


    /**
     * This method shows how to interact with the parser to create an Abstract Deployment Plan from a solution found by the optimization process
     * @param aam The Abstract Application Model
     * @param bindings The solution, which here is a map consisting on: <String that identifies an AAM module, cloudOffering Node selected for that module>
     * @return an Abstract Deployment Plan
     */
    private  IToscaEnvironment createADP(IToscaEnvironment aam, Map<String,INodeType> bindings){
        //we start by creating a new empty TOSCA environment (i.e. the ADP)
        IToscaEnvironment adp = Tosca.newEnvironment();

        //for each binding <AAM module String, cloud offering Node>
        for (Map.Entry<String, INodeType> binding : bindings.entrySet()) {
            //we get the Node of the AAM module that corresponds to the String
            INodeTemplate node = (INodeTemplate) aam.getNamedEntity(binding.getKey());

            // we get the cloud offering node. (Here we cast to INamedEntity. Which is basically any entity in tosca which have an associated, and unique, name (datatypes, relationships, node types and node templates)
            // we call it type because every cloud offering is a type by itself (see D3.2)
            INamedEntity type = (INamedEntity) binding.getValue();

            // we import the cloud offering type into the ADP, with all its supertypes and used data types.
            type = adp.importWithSupertypes(type);

            //we create a new nodeTemplate based on the cloud offering type (see D3.2 pag 22)
            INodeTemplate newNode = adp.newTemplate((INodeType) type);

            //we copy all the properties and attributes from one nodeTemplate to the new one we have just created.
            for (Map.Entry<String, IProperty> property : node.declaredProperties().entrySet()) {
                newNode = newNode.addProperty(property.getKey(),property.getValue().type(),property.getValue().defaultValue());
            }
            for (Map.Entry<String, IValue> attribute : node.declaredAttributes().entrySet()) {
                newNode.declaredAttributes().put(attribute.getKey(),attribute.getValue());
            }

        }

        //we import in the ADP all the logic nodes from the AAM
        for (INodeTemplate template : aam.getNodeTemplatesOfType((INodeType) aam.getNamedEntity("tosca.nodes.Logic"))) {
            adp.importWithSupertypes((INamedEntity)template.baseType());
        }

        //we import in the ADP all the deploy nodes from the AAM
        for (INodeTemplate template : aam.getNodeTemplatesOfType((INodeType) aam.getNamedEntity("tosca.nodes.Deploy"))) {
            adp.importWithSupertypes((INamedEntity)template);
        }
        return adp;
    }

    /**
     * This method shows how to interact with the parser to generate an Abstract Deployment Plan from the AAM and the result of the matchmaking
     * This method implements the Local Search algorithm, and uses the private methods shown above.
     * @param aam Abstract Application Model
     * @param matches Results of the matchmaking, it consists of a map of <AAM module String, list of cloud offerings that matches the AAM module)
     * @return Abstract Deployment Plan
     */
    public IToscaEnvironment optimizeLocal(IToscaEnvironment aam, Map<String,List<INodeType>> matches) {
        //we create a solution as a Hashmap where the string is the AAM module and the INodeType is the cloud offering that will be chosen.
        Map<String,INodeType> tentativeSolution = new HashMap<>();

        //for each module of the AAM
        for(String k :matches.keySet()) {
            //we get the list of Cloud Offerings that matches the module, and choose one at random.
            List<INodeType> m = matches.get(k);
            Random r = new Random();
            INodeType choice = m.get(r.nextInt(m.size()));
            tentativeSolution.put(k,choice);
        }
        //try local variations which may improve the score
        double score = computeScore(aam, tentativeSolution);
        boolean  solutionImproved;
        do {
            solutionImproved = false;
            for(String k : tentativeSolution.keySet()) {
                for(INodeType n: matches.get(k)) {
                    Map newTentative = new HashMap<>(tentativeSolution);
                    newTentative.put(k, n);
                    double newScore = computeScore(aam, newTentative);
                    if(newScore > score) {

                        tentativeSolution = newTentative;
                        score = newScore;
                        solutionImproved = true;
                    }

                }
            }
        } while (solutionImproved);

        return createADP(aam,tentativeSolution);
    }

    /**
     * This method shows how to interact with the parser to generate an Abstract Deployment Plan from the AAM and the result of the matchmaking
     * This method implements a Full search, and uses the private methods shown above.
     * @param aam Abstract Application Model
     * @param matches Results of the matchmaking, it consists of a map of <AAM module String, list of cloud offerings that matches the AAM module)
     * @return Abstract Deployment Plan
     */
    public List<IToscaEnvironment> optimizeFullSearch(IToscaEnvironment aam, Map<String,List<INodeType>> matches) {
        PriorityQueue<Solution> solutions = new PriorityQueue<>();
        Map<String,INodeType> tentativeSolution = new HashMap<>();
        Iterator<INodeType> cursor[] = new Iterator[matches.size()];
        String cursorName[] = new String[matches.size()];
        int level = 0;
        for(Map.Entry<String,List<INodeType>> entry :matches.entrySet()) {
            cursorName[level] = entry.getKey();
            cursor[level] = entry.getValue().iterator();
            tentativeSolution.put(cursorName[level],cursor[level].next());
        }
        while (level < cursor.length) {
            solutions.offer(new Solution(aam,tentativeSolution));
            while(solutions.size() > wantedSolutions)
                solutions.poll();
            level = 0;
            while(level < cursor.length && !cursor[level].hasNext()) {
                cursor[level] = matches.get(cursorName[level]).iterator();
                tentativeSolution.put(cursorName[level],cursor[level].next());
                level ++;
            }
            if(cursor[level].hasNext())
                tentativeSolution.put(cursorName[level],cursor[level].next());
        }
        List<IToscaEnvironment> ret = new ArrayList<>();
        for (Solution solution : solutions) {
            ret.add(createADP(aam,solution.value));
        }
        return ret;
    }


    /**
     * This method shows how to interact with the parser to generate an Abstract Deployment Plan from the AAM and the result of the matchmaking
     * This method implements a Full search - Cartesian, and uses the private methods shown above.
     * @param aam Abstract Application Model
     * @param matches Results of the matchmaking, it consists of a map of <AAM module String, list of cloud offerings that matches the AAM module)
     * @return Abstract Deployment Plan
     */
    public List<IToscaEnvironment> optimize(IToscaEnvironment aam, Map<String, List<INodeType>> matches) {
        List<String> labels = new ArrayList<>();
        List<Set<INodeType>> values = new ArrayList<>();
        for (Map.Entry<String, List<INodeType>> matchable : matches.entrySet()) {
            labels.add(matchable.getKey());
            values.add(new HashSet<>(matchable.getValue()));
        }
        Set<List<INodeType>> allSolutions = Sets.cartesianProduct(values);

        PriorityQueue<Solution> solutions = new PriorityQueue<>();
        for (List<INodeType> solution : allSolutions) {
            Iterator<INodeType> it1 = solution.iterator();
            Iterator<String> it2 = labels.iterator();
            HashMap<String,INodeType> s = new HashMap<>();
            while(it1.hasNext()&& it2.hasNext())
                s.put(it2.next(), it1.next());
            solutions.offer(new Solution(aam, s));
            while(solutions.size() > wantedSolutions)
                solutions.poll();
        }

        List<IToscaEnvironment> ret = new ArrayList<>();

        for (Solution solution : solutions) {
            ret.add(createADP(aam,solution.value));
        }
        return ret;
    }

    class Solution implements Comparable<Solution> {
        public final Map<String,INodeType> value;
        public final double score;
        public Solution(IToscaEnvironment env, Map<String,INodeType> value) {
            this.value = value;
            this.score = computeScore(env, value);
        }

        @Override
        public int compareTo(Solution o) {
            if(this.score == o.score)
                return 0;
            return this.score>o.score?1:-1;
        }
    }

}
