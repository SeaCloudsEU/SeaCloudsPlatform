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
 * Node objects
 * 
 * var d1 = Object.create(Graph.Database).init("NAME", "LABEL");
 * console.log(d1.toString());
 * 
 * @author roman.sosa@atos.net
 */

var Graph = (function () {

    var format = function () {
      var i = 1, args = arguments;
      return args[0].replace(/{}/g, function () {
        return typeof args[i] != 'undefined' ? args[i++] : '';
      });
    };

    /*
     * http://davidwalsh.name/javascript-objects-deconstruction
     * 
     * http://toddmotto.com/mastering-the-module-pattern/
     */

    var Node = {
        setup: function(name, label) {
            this.name = name;
            this.label = label;
            this.type = "Node";
        },
        toString: function(args) {
            return format(
                "Node(name='{}',label='{}',type='{}')",
                this.name, 
                this.label,
                this.type
            );
        },
    };
    
    var Database = Object.create(Node);
    
    Database.init = function(name, label, category) {
        this.setup(name, label);
        this.type = "Database";
        this.category = category;
    
        return this;
    };
    
    Database.toString = function() {
        return format(
            "Database(name='{}', label='{}', category='{}')", 
            this.name, 
            this.label,
            this.category
        );
    };
    
    var WebApplication = Object.create(Node);
    
    WebApplication.init = function(name, label, language) {
        this.setup(name, label);
        this.type = "WebApplication";
        this.language = language;
    
        return this;
    };
    
    WebApplication.toString = function() {
        return format(
            "WebApplication(name='{}', label='{}', language='{}')",
            this.name,
            this.label,
            this.language
        );
    };
    
    var RestService = Object.create(Node);
    
    RestService.init = function(name, label, language) {
        this.setup(name, label);
        this.type = "WebApplication";
        this.language = language;
    
        return this;
    };
    
    RestService.toString = function() {
        return format(
            "RestService(name='{}', label='{}', language='{}')",
            this.name,
            this.label,
            this.language
        );
    };
    
    return {
        Node: Node,
        Graph: Graph,
        WebApplication: WebApplication,
        Database: Database,
        RestService: RestService,
    };
})();


/*
console.log(Node.toString.call(d1));
console.log(Object.getPrototypeOf(d1) + "");
console.log(Object.getPrototypeOf(Database) + "");
console.log(Object.getPrototypeOf(Node) + "");
*/
/*
function Node(name, label, kind) {
    this.name = name;
    this.label = label;
};



function Graph(nodes, arcs) {
    this.nodes = nodes;
    this.arcs = arcs;
};

Graph.prototype = {
    
    addNode: function(node) {
        
    },
    
    addArc: function(srcnode, destnode) {
        for (arc in this.arcs) {
            if (srcnode == arc.source && destnode == arc.target) {
                console.log("Already appended arc");
                return;
            }
            if (srcnode == arc.target && destnode == arc.source) {
                console.log("Already appended inverted arc");
                return;
            }
        }
        this.arcs.push( {source: srcnode, target: destnode});
    },
};


var d1 = Object.create(Graph.Database).init("NAME", "LABEL");

var d2 = Object.create(Graph.Database).init("D2", "LABEL2");

var w1 = Object.create(Graph.WebApplication).init("WebApplication1", "W1", "java");

console.log(d1);
console.log(d1.toString());
console.log(d2.toString());
console.log(w1.toString());

*/
