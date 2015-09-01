/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package eu.seaclouds;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.cli.Main;

/**
 * This class provides a static main entry point for launching a custom Brooklyn-based app.
 * <p>
 * It inherits the standard Brooklyn CLI options from {@link brooklyn.cli.Main}, plus adds a few more shortcuts for
 * favourite blueprints to the {@link brooklyn.cli.Main.LaunchCommand}.
 */
public class SeaCloudsMain extends Main {

    private static final Logger log = LoggerFactory.getLogger(SeaCloudsMain.class);

    public static void main(String... args) {
        log.debug("CLI invoked with args " + Arrays.asList(args));
        new SeaCloudsMain().execCli(args);
    }

    @Override
    protected String cliScriptName() {
        return "start.sh";
    }

}
