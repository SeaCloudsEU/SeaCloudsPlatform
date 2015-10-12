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

package eu.seaclouds.monitor.monitoringDamGenerator.core.adpParsing;

public class Module {

    private static final double HOURS_IN_MONTH = 24 * 30;
    private double respTime = 0;
    private double availability = 0;
    private double cost = 0;
    private double workload = 0;
    private boolean isJavaApp;
    private String deploymentType;
    private String moduleName;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleNane) {
        this.moduleName = moduleNane;
    }

    public double getResponseTime() {
        return respTime;
    }

    public void setResponseTimeMillis(Double resp) {
        this.respTime = resp;
    }

    public boolean existResponseTimeRequirement() {
        return respTime != 0.0;
    }

    public double getAvailability() {
        return availability;
    }

    public void setAvailability(double availability) {
        this.availability = availability;
    }

    public boolean existAvailabilityRequirement() {
        return availability != 0.0;
    }

    public void setCostHour(double cost) {
        this.cost = cost;
    }

    public void setCostMonth(double c) {
        setCostHour(c / (30.0 * 24.0));
    }

    public double getCostHour() {

        return cost;
    }

    public Double getCostMonth() {
        return cost * HOURS_IN_MONTH;
    }

    public boolean existCostRequirement() {
        return cost != 0.0;
    }

    public void setWorkload(double applicationWorkload) {
        workload = applicationWorkload;

    }

    public double getWorkload() {
        return workload;
    }

    public boolean hasValidWorkload() {
        return workload > 0;
    }

    public void setWorkloadMinute(double wkl) {
        setWorkload(wkl / 60.0);
    }

    @Override
    public String toString() {
        String info = "";
        info += "RespTime=" + this.respTime + " Availability="
                + this.availability + " Cost=" + this.getCostMonth()
                + " Workload= " + this.getWorkload();

        return info;
    }

    public boolean isJavaApp() {
        return isJavaApp;
    }

    public void setJavaApp(boolean isJavaApp) {
        this.isJavaApp = isJavaApp;
    }

    public String getDeploymentType() {
        return deploymentType;
    }

    public void setDeploymentType(String deploymentType) {
        this.deploymentType = deploymentType;
    }

}
