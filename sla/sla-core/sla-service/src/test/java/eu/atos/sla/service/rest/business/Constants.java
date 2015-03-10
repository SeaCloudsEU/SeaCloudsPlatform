/**
 * Copyright 2015 SeaClouds
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
package eu.atos.sla.service.rest.business;

public class Constants {
	public static final StringBuilder agreementTest = new StringBuilder();
	
	public static final StringBuilder templateTest = new StringBuilder();

	public Constants() {

		agreementTest.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		agreementTest.append("<wsag:Agreement xmlns:wsag=\"http://www.ggf.org/namespaces/ws-agreement\"");
		agreementTest.append("        AgreementId=\"agreement02\">");
		agreementTest.append("        <wsag:Name>ExampleAgreement</wsag:Name>");
		agreementTest.append("        <wsag:Context>");
		agreementTest.append("                <wsag:AgreementInitiator>RandomClient</wsag:AgreementInitiator>");
		agreementTest.append("                <wsag:AgreementResponder>provider-prueba</wsag:AgreementResponder>");
		agreementTest.append("                <wsag:ServiceProvider>AgreementResponder</wsag:ServiceProvider>");
		agreementTest.append("                <wsag:ExpirationTime>2014-03-07T12:00:00</wsag:ExpirationTime>");
		agreementTest.append("                <wsag:TemplateId>contract-template-2007-12-04</wsag:TemplateId>");
		agreementTest.append("        </wsag:Context>");
		agreementTest.append("        <wsag:Terms>");
		agreementTest.append("                <wsag:All>");
		agreementTest.append("                        <!-- FUNCTIONAL DESCRIPTION -->");
		agreementTest.append("                        <wsag:ServiceDescriptionTerm Name=\"SDTName1\" ServiceName=\"ServiceName\">");
		agreementTest.append("                                DSL expression");
		agreementTest.append("                        </wsag:ServiceDescriptionTerm>");
		agreementTest.append("                        <wsag:ServiceDescriptionTerm Name=\"SDTName2\" ServiceName=\"ServiceName\">");
		agreementTest.append("                                DSL expression");
		agreementTest.append("                        </wsag:ServiceDescriptionTerm>");
		agreementTest.append("                        <!-- OPTIONAL SERVICE REFERENCE -->");
		agreementTest.append("                        <!-- OPTIONAL SERVICE PROPERTIES : non funcional properties-->");
		agreementTest.append("                        <wsag:ServiceProperties Name=\"NonFunctional\" ServiceName=\"ServiceName\">");
		agreementTest.append("                                <wsag:Variables>");
		agreementTest.append("                                        <wsag:Variable Name=\"ResponseTime\" Metric=\"xs:double\">");
		agreementTest.append("                                                <wsag:Location>qos:ResponseTime</wsag:Location>");
		agreementTest.append("                                        </wsag:Variable>");
		agreementTest.append("                                        <wsag:Variable Name=\"Performance\" Metric=\"xs:double\">");
		agreementTest.append("                                                <wsag:Location>qos:Performance</wsag:Location>");
		agreementTest.append("                                        </wsag:Variable>");
		agreementTest.append("                                </wsag:Variables>");
		agreementTest.append("                        </wsag:ServiceProperties>");
		agreementTest.append("                        <wsag:GuaranteeTerm Name=\"GT_UPTIME\">");
		agreementTest.append("                                <wsag:ServiceScope ServiceName=\"ServiceName\"/>");
		agreementTest.append("                                <!-- The qualifying conditions that must be met before the guarantee is evaluated -->");
		agreementTest.append("                                <!--");
		agreementTest.append("                                <wsag:QualifyingCondition>state EQ 'ready'</wsag:QualifyingCondition>");
		agreementTest.append("                                -->");
		agreementTest.append("                                <wsag:ServiceLevelObjective>");
		agreementTest.append("                                        <wsag:KPITarget>");
		agreementTest.append("                                               <wsag:KPIName>ResponseTime</wsag:KPIName> <!--  same name as property for the moment -->");
		agreementTest.append("                                               <wsag:CustomServiceLevel>{\"constraint\" : \"ResponseTime LT 0.9\"}</wsag:CustomServiceLevel> <!--  the ServiceProperty is referenced here -->");
		agreementTest.append("                                        </wsag:KPITarget>");
		agreementTest.append("                               </wsag:ServiceLevelObjective>");
		agreementTest.append("                        </wsag:GuaranteeTerm>");
		agreementTest.append("                        <wsag:GuaranteeTerm Name=\"GT_Performance\">");
		agreementTest.append("                               <wsag:ServiceScope ServiceName=\"ServiceName\"/>");
		agreementTest.append("                               <wsag:ServiceLevelObjective>");
		agreementTest.append("                                        <wsag:KPITarget>");
		agreementTest.append("                                                <wsag:KPIName>Performance</wsag:KPIName> <!--  same name as property for the moment -->");
		agreementTest.append("                                                <wsag:CustomServiceLevel>{\"constraint\" : \"Performance GT 0.1\"}</wsag:CustomServiceLevel>");
		agreementTest.append("                                       </wsag:KPITarget>");
		agreementTest.append("                                </wsag:ServiceLevelObjective>");
		agreementTest.append("                                <wsag:BusinessValueList>");
		agreementTest.append("                                       <wsag:Importante>3</wsag:Importante>    <!-- optional importance (integer) -->");
		agreementTest.append("                                       <wsag:Penalty>");
		agreementTest.append("                                                <wsag:AssessmentInterval>");
		agreementTest.append("                                                       <wsag:Count>10</wsag:Count>");
		agreementTest.append("                                                </wsag:AssessmentInterval>");
		agreementTest.append("                                                <wsag:ValueUnit>EUR</wsag:ValueUnit>");
		agreementTest.append("                                                <wsag:ValueExpression>99</wsag:ValueExpression>");
		agreementTest.append("                                       </wsag:Penalty>");
		agreementTest.append("                                        <wsag:Reward></wsag:Reward>");
		agreementTest.append("                                        <wsag:Preference></wsag:Preference>");
		agreementTest.append("                                        <wsag:CustomBusinessValue></wsag:CustomBusinessValue>");
		agreementTest.append("                                </wsag:BusinessValueList>");
		agreementTest.append("                        </wsag:GuaranteeTerm>");
		agreementTest.append("                </wsag:All>");
		agreementTest.append("        </wsag:Terms>");
		agreementTest.append("</wsag:Agreement>");
		
		
		//Template 
		
		
		templateTest.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		templateTest.append("<!--");
		templateTest.append("From http://serviceqos.wikispaces.com/WSAgExample");
		templateTest.append(" -->");
		templateTest.append("<wsag:Template xmlns:wsag=\"http://www.ggf.org/namespaces/ws-agreement\" TemplateId=\"contract-template-2007-12-04\">");
		templateTest.append("   <wsag:Name>ExampleTemplate</wsag:Name>");
		templateTest.append("   <wsag:Context>");
		templateTest.append("      <wsag:AgreementInitiator>Provider</wsag:AgreementInitiator>");
		templateTest.append("      <wsag:ServiceProvider>AgreementInitiator</wsag:ServiceProvider>");
		templateTest.append("      <wsag:ExpirationTime>2013-12-15-1200</wsag:ExpirationTime>");
		templateTest.append("      <wsag:TemplateId>contract-template-2013-12-15</wsag:TemplateId>");
		templateTest.append("   </wsag:Context>");
		templateTest.append("   <wsag:Terms>");
		templateTest.append("      <wsag:All>");
		templateTest.append("         <!-- functional description -->");
		templateTest.append("         <wsag:ServiceDescriptionTerm");
		templateTest.append("            wsag:Name=\"General\"");
		templateTest.append("            wsag:ServiceName=\"Service0001\">");
		templateTest.append("            A GPS service");
		templateTest.append("         </wsag:ServiceDescriptionTerm>");
		templateTest.append("         <wsag:ServiceDescriptionTerm");
		templateTest.append("            wsag:Name=\"GetCoordsOperation\"");
		templateTest.append("            wsag:ServiceName=\"GPSService0001\">");
		templateTest.append("            operation to call to get the coords");
		templateTest.append("         </wsag:ServiceDescriptionTerm>");
		templateTest.append("         <!-- domain specific reference to a service (additional or optional to SDT) -->");
		templateTest.append("         <wsag:ServiceReference");
		templateTest.append("            wsag:Name=\"CoordsRequest\"");
		templateTest.append("            wsag:ServiceName=\"GPSService0001\">");
		templateTest.append("        <wsag:EndpointReference>");
		templateTest.append("               <wsag:Address>http://www.gps.com/coordsservice/getcoords</wsag:Address>");
		templateTest.append("               <wsag:ServiceName>gps:CoordsRequest</wsag:ServiceName>");
		templateTest.append("            </wsag:EndpointReference>");
		templateTest.append("         </wsag:ServiceReference>");
		templateTest.append("         <!-- non-functional properties -->");
		templateTest.append("         <wsag:ServiceProperties");
		templateTest.append("            wsag:Name=\"AvailabilityProperties\"");
		templateTest.append("            wsag:ServiceName=\"GPS0001\">");
		templateTest.append("            <wsag:Variables>");
		templateTest.append("               <wsag:Variable");
		templateTest.append("                  wsag:Name=\"ResponseTime\"");
		templateTest.append("                  wsag:Metric=\"metric:Duration\">");
		templateTest.append("                  <wsag:Location>qos:ResponseTime</wsag:Location>");
		templateTest.append("               </wsag:Variable>");
		templateTest.append("            </wsag:Variables>");
		templateTest.append("         </wsag:ServiceProperties>");
		templateTest.append("         <wsag:ServiceProperties");
		templateTest.append("            wsag:Name=\"UsabilityProperties\"");
		templateTest.append("            wsag:ServiceName=\"GPS0001\">");
		templateTest.append("            <wsag:Variables>");
		templateTest.append("               <wsag:Variable");
		templateTest.append("                  wsag:Name=\"CoordDerivation\"");
		templateTest.append("                  wsag:Metric=\"metric:CoordDerivationMetric\">");
		templateTest.append("                  <wsag:Location>qos:CoordDerivation</wsag:Location>");
		templateTest.append("               </wsag:Variable>");
		templateTest.append("            </wsag:Variables>");
		templateTest.append("         </wsag:ServiceProperties>");
		templateTest.append("         <!-- statements to offered service level(s) -->");
		templateTest.append("         <wsag:GuaranteeTerm");
		templateTest.append("            Name=\"FastReaction\" Obligated=\"ServiceProvider\">");
		templateTest.append("            <wsag:ServiceScope ServiceName=\"GPS0001\">");
		templateTest.append("               http://www.gps.com/coordsservice/getcoords");
		templateTest.append("            </wsag:ServiceScope>");
		templateTest.append("            <wsag:QualifyingCondition>");
		templateTest.append("               applied when current time in week working hours");
		templateTest.append("            </wsag:QualifyingCondition>");
		templateTest.append("            <wsag:ServiceLevelObjective>");
		templateTest.append("               <wsag:KPITarget>");
		templateTest.append("                  <wsag:KPIName>FastResponseTime</wsag:KPIName>");
		templateTest.append("                              <wsag:Target>");
		templateTest.append("                     //Variable/@Name=\"ResponseTime\" LOWERTHAN 1 second");
		templateTest.append("                  </wsag:Target>");
		templateTest.append("               </wsag:KPITarget>");
		templateTest.append("            </wsag:ServiceLevelObjective>");
		templateTest.append("         </wsag:GuaranteeTerm>");
		templateTest.append("      </wsag:All>");
		templateTest.append("   </wsag:Terms>");
		templateTest.append("</wsag:Template>");
		

	}
}
