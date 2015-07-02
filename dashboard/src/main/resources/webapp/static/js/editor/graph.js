/**
 * Node objects
 * 
 * var d1 = Object.create(Graph.Node).init({
 *     name: "NAME",
 *     label: "LABEL",
 *     properties: { weight: 40 }
 * });
 * console.log(d1.toString());
 * 
 * A simple json-serialization is implemented. The serialized values are:
 * name, label, properties.
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

    var Link = {
        source: undefined,
        target: undefined,
        properties: undefined,
        setup: function(source, target) {
            this.source = source;
            this.target = target;
            this.properties = {};
            return this;
        },
        toString: function() {
            return format("Link(source='{}',label='{}')",
                this.source.label,
                this.target.label
            );
        },
        toJson: function(args) {
            return {
                source: this.source.name,
                target: this.target.name,
                properties: this.properties,
            };
        }
    
    };
    
    var Node = {
        name: "<Node>",
        label: "",
        type: "Node",
        properties: undefined,
        init: function(values) {
            this.properties = {};
            for (var key in values) {
                var value = values[key];
                
                this[key] = value;
            }
            return this;
        },
        toString: function(args) {
            return format(
                "Node(name='{}',label='{}',type='{}')",
                this.name, 
                this.label,
                this.type
            );
        },
        toJson: function(args) {
            return {
                name: this.name,
                label: this.label,
                type: this.type,
                properties: this.properties,
            };
        }
    };

    return {
        Link: Link,
        Node: Node,
        Graph: Graph,
        format: format,
    };
})();
