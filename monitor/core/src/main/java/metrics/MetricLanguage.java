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
 * Created by Adrian on 17/10/2014.
 */
public abstract class MetricLanguage {
    /**
     * Adds a new translation into the dictionary
     * @param from the word in the source language
     * @param to the translated word
     */
   public abstract void addTranslation(String from, String to);

    /**
     * Retrieves the translation
      * @param input word to translate
     * @return translation if it's available otherwise null
     */
   public abstract String getTranslation(String input);

    /**
     * Retrieves the inverse translation
      * @param input word to translate
     * @return translation if it's available otherwise null
     */
   public abstract String getInverseTranslation(String input);

}
