/**
 * Copyright 2014 deib-polimi
 * Contact: deib-polimi <marco.miglierina@polimi.it>
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
package core.modaCloudsMonitoring.deploymentPlan;


import it.polimi.modaclouds.monitoring.monitoring_manager.server.Model;
import it.polimi.modaclouds.qos_models.monitoring_ontology.CloudProvider;
import it.polimi.modaclouds.qos_models.monitoring_ontology.InternalComponent;
import it.polimi.modaclouds.qos_models.monitoring_ontology.Method;
import it.polimi.modaclouds.qos_models.monitoring_ontology.VM;

import com.google.gson.Gson;

import core.TxtFileWriter;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */
public class DeploymentPlan  {

	//Constants.
	private static final String DEPLOYMENT_PLAN_FILE = "resources/dukes-tutoring.json";


	private static String create(){

		Model model = new Model();
			
		CloudProvider cloud = new CloudProvider();
		model.add( cloud );
		cloud.setId( "Cloud1" );

		Method method1 = new Method();
		model.add( method1 );
		method1.setId( "createTutoringSession1" );
		method1.setType( "createTutoringSession" );

		VM applicationServerVM = new VM();
		model.add( applicationServerVM );
		applicationServerVM.setNumberOfCPUs( 6 );
		applicationServerVM.setId("ApplicationServerVM1");
		applicationServerVM.setType( "ApplicationServerVM" );
		applicationServerVM.setCloudProvider( cloud.getId() );

		InternalComponent glassfishServer = new InternalComponent();
		model.add( glassfishServer );
		glassfishServer.setId( "Glassfish-server1" );
		glassfishServer.setType( "Glassfish-server" );
		glassfishServer.addRequiredComponent( applicationServerVM.getId() );

		InternalComponent db = new InternalComponent();
		model.add( db );
		db.setId( "DB1" );
		db.setType( "DB" );
		db.addRequiredComponent( glassfishServer.getId() );

		InternalComponent dukesWebapp = new InternalComponent();
		model.add( dukesWebapp );
		dukesWebapp.setId( "dukes-webapp1" );
		dukesWebapp.setType( "dukes-webapp" );
		dukesWebapp.addProvidedMethod( method1.getId() );
		dukesWebapp.addRequiredComponent( db.getId() );
		dukesWebapp.addRequiredComponent( glassfishServer.getId() );


		Gson gson = new Gson();
		String json = gson.toJson( model );


		return json;
	}

	public static void main(String[] args) {
		
		String model = create();

		TxtFileWriter.write( model, DEPLOYMENT_PLAN_FILE );

		System.out.println( "[INFO] Deployment plan generation: SUCCESS." );
	}
}
