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
package eu.seaclouds.common.tosca;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.common.base.Supplier;

import alien4cloud.tosca.parser.ToscaParser;

public class ToscaParserSupplier implements Supplier<ToscaParser> {

    @Override
    public ToscaParser get() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        return applicationContext.getBean(ToscaParser.class);
    }

}
