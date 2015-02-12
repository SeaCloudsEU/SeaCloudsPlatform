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


package eu.seaclouds.platform.planner.optimizerTest.nfp;


import java.util.ArrayList;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import eu.seaclouds.platform.planner.optimizer.CloudOffer;
import eu.seaclouds.platform.planner.optimizer.Solution;
import eu.seaclouds.platform.planner.optimizer.SuitableOptions;
import eu.seaclouds.platform.planner.optimizer.Topology;
import eu.seaclouds.platform.planner.optimizer.TopologyElement;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityAnalyzer;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityInformation;




public class QualityAnalyzerTest {

	private static QualityAnalyzer analyzer;

	//private static final double MAX_MILLIS_EXECUTING = 20000;
	
	static Logger log = LoggerFactory.getLogger(QualityAnalyzerTest.class);
	
	
@BeforeClass
public void createObjects() {
	
	log.info("Starting TEST quality analyzer");
	analyzer= new QualityAnalyzer();
	
	final String dir = System.getProperty("user.dir");
	log.debug("Trying to open files: current executino dir = " + dir);
	
	
}



@Test
public void testPerformanceEvaluation(){
	
	Solution bestSol=createSolution();
	Topology topology = createTopology();
	
	double workload=10;
	
	SuitableOptions cloudCharacteristics=createSuitableOptions();
	
	QualityInformation qInfo = analyzer.computePerformance(bestSol, topology, workload, cloudCharacteristics);
	
	Assert.assertTrue("Compute performance returned null", qInfo!=null);
	
	if(qInfo!=null){
		log.info("Testing performance. Returned application response time is " +  qInfo.getResponseTime());
	}
	
	

}









private Solution createSolution() {
	
	Solution sol= new Solution();
	sol.addItem("Module1", "CloudOffer1");
	sol.addItem("Module2", "CloudOffer2");
	
	return sol;
	
}


private Topology createTopology() {

	TopologyElement e1= new TopologyElement("Module1");
	TopologyElement e2= new TopologyElement("Module2");
	
	e1.addElementCalled(e2);
	
	Topology topology=new Topology();
	topology.addModule(e1);
	topology.addModule(e2);
	
	return topology;
	
	
}

private SuitableOptions createSuitableOptions() {

	CloudOffer offer1= new CloudOffer("CloudOffer1", 20, 0.99, 2);
	CloudOffer offer2= new CloudOffer("CloudOffer2", 30, 0.95, 2);
	
	SuitableOptions solutions = new SuitableOptions();
	
	ArrayList<String> optionsNamesMod1 = new ArrayList<String>();
	optionsNamesMod1.add("CloudOffer1");
	
	ArrayList<String> optionsNamesMod2 = new ArrayList<String>();
	optionsNamesMod2.add("CloudOffer2");
	
	ArrayList<CloudOffer> optionsMod1 = new ArrayList<CloudOffer>();
	optionsMod1.add(offer1);
	
	ArrayList<CloudOffer> optionsMod2 = new ArrayList<CloudOffer>();
	optionsMod2.add(offer2);
	
	solutions.addSuitableOptions("Module1", optionsNamesMod1, optionsMod1);
	solutions.addSuitableOptions("Module2", optionsNamesMod2, optionsMod2);
	
	return solutions;
}





@AfterClass
public void testFinishced(){
	log.info("Test finished");
}
	
}
