package eu.seaclouds.platform.planner.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class PlannerResponse {
    private String aam;
    private String plans;

    public PlannerResponse() {}

    public PlannerResponse(String aam, String plans) {
        this.aam = aam;
        this.plans = plans;
    }


    @JsonProperty
    public String getAam() { return aam; }

    @JsonProperty
    public void setAam(String aam) { this.aam = aam; }

    @JsonProperty("adps")
    public String getPlans(){
        return this.plans;
    }

    @JsonProperty("adps")
    public void setPlans(String plans) { this.plans = plans; }

}
