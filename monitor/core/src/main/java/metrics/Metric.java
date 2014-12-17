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
package metrics;

/**
 * Created by Adrian on 15/10/2014.
 */

/**
 * This class represents a metric given its id, description and type {@link T}
 * @param <T> Type of the metric value.
 */
public class Metric<T> {
    private String id;
    private String description;
    private Class<T> type;

    Metric(String id,  String description, Class<T> type) {
        this.id = id;
        this.type = type;
        this.description = description;
    }

    public String getId(){
        return this.id;
    }

    public Class<T> getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metric that = (Metric) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
