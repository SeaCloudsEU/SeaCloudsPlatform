/*
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
/**
 * log4j-like logger.
 * 
 * Usage:
 *   var log = Log.getLogger("my-package").setLevel(Log.DEBUG);
 *   log.debug("debug message");
 *   log.info("info message");
 *   log.warn("warn message");
 * 
 * 
 * Comments:
 *   log.getLogger calls are cached. This allows to define a default level
 *   for the logger in the module, and overwrite later in main body.
 * 
 *   It needs the existence of console.log function.
 * 
 * @author roman.sosa@atos.net
 */

var Log = (function(){

    var DEBUG = 0;
    var INFO = 10;
    var WARN = 20;
    var ERROR = 30;
    var NONE = 100;
    
    var label = {
    };
    label[DEBUG] = "DEBUG";
    label[INFO] = "INFO";
    label[WARN] = "WARN";
    label[ERROR] = "ERROR";
    label[NONE] = "NONE";
    
    var loggers = {};
    
    var Logger = {
        
        init: function(category) {
            this.category = category;
            this.level = INFO;
            
            return this;
        },
        setLevel: function(level) {
            if (typeof(level) != "number") {
                this.warn("Invalid level setting " + this.category + 
                          " level to '" + level + "'");
            }
            else {
                this.level = level;
                var str = (label[level] !== undefined? label[level] : level); 
                logger.debug("Setting " + this.category + 
                           " level to '" + str + "'");
            }
            return this;
        },
        _log: function(level, str) {
            console.log(label[level] + " - " + this.category + ": " + str);
        },
        debug: function(str) {
            if (this.level <= DEBUG) {
                this._log(DEBUG, str);
            }
        },
        info: function(str) {
            if (this.level <= INFO) {
                this._log(INFO, str);
            }
        },
        warn: function(str) {
            if (this.level <= WARN) {
                this._log(WARN, str);
            }
        },
        error: function(str) {
            if (this.level <= ERROR) {
                this._log(ERROR, str);
            }
        },
        toString: function(args) {
            return "<Logger(category='" + this.category + 
                "',level='" + this.level + "')>";
        },
    };

    var getLogger = function(category) {
        if (loggers[category] === undefined) {
            loggers[category] = Object.create(Logger).init(category);
        }
        return loggers[category];
    };

    var logger = getLogger("Log");
    // logger.setLevel(DEBUG);
    
    return {
        getLogger: getLogger,
        DEBUG: DEBUG,
        INFO: INFO,
        WARN: WARN,
        ERROR: ERROR,
        NONE: NONE,
        loggers: loggers
    };
})();
