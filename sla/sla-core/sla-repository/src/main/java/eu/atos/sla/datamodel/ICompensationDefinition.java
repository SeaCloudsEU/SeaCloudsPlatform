/**
 * Copyright 2014 Atos
 * Contact: Atos <roman.sosa@atos.net>
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
package eu.atos.sla.datamodel;

import java.util.Date;

/**
 * This element expresses the reward or penalty to be assessed for meeting (or not) an objetive.
 *
 */
public interface ICompensationDefinition {

    public static enum CompensationKind {
        REWARD,
        CUSTOM_REWARD,
        PENALTY,
        CUSTOM_PENALTY,
        UNKNOWN
    }
    
    public interface IPenaltyDefinition extends ICompensationDefinition {
        
    }
    
    public interface IRewardDefinition extends ICompensationDefinition {
        
    }
    

    /*
     * Internally generated id
     */
    Long getId();

    /**
     * Type of compensation: reward or penalty.
     */
    CompensationKind getKind();
    
    /**
     * When present, defines the assessment interval as a duration.
     * 
     * One of timeInterval or count MUST be specified. 
     */
    Date getTimeInterval();
    
    /**
     * When present, defines the assessment interval as a service specific count, such as
     * number of invocations. 
     * 
     * One of timeInterval or count MUST be specified. 
     */
    Integer getCount();
    
    /**
     * Optional element that defines the unit for assessing penalty, such as USD.
     */
    String getValueUnit();

    /**
     * This element defines the assessment amount, which can be an integer, a float or an 
     * arbitrary domain-specific expression.
     */
    String getValueExpression();

    /**
     * In extended compensations, defines the domain-specific type of compensation, such as 
     * "discount", "terminate", "service"...
     */
    String getAction();

    /**
     * In extended compensations, defines the time interval where the action should take place. E.g., 
     * a discount of 10% with a validity of one day (i.e., the prize of that day will have a discount of a 10%).
     * 
     * The validity must be expressed in xs:duration format.
     * 
     * @see http://www.w3.org/TR/xmlschema-2/#duration
     */
    String getValidity();
}
