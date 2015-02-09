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


package eu.seaclouds.platform.planner.optimizerTest;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import eu.seaclouds.platform.planner.optimizer.Optimizer;



public class OptimizerTest {

	private static Optimizer optimizer;
	private static String appModel;
	private static String suitableCloudOffer;
	
@BeforeClass
public void createObjects() {
	System.out.println("Starting TEST optimizer");
	optimizer= new Optimizer();
	
	//TODO: Load appModel and suitableCloudOffer YAML into a string
	
}



@Test
public void testPresenceSolution(){
	String dam = optimizer.optimize(appModel, suitableCloudOffer);
}
	
	
}
