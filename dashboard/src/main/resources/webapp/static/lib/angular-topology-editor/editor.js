/**
 * This is a proof of concept of topology editor using Graph.Node objects.
 *
 *
 * The following prefixes in variables are used:
 *   j_ : jquery selections
 *   d3_ : d3 selections
 *   g_ : Graph.* nodes (or children)
 *
 * @author roman.sosa@atos.net
 */
"use strict";

var Editor = (function() {

    var canvas;

    /*
     * Override some Link and Node methods regarding Canvas.
     */

    var LinkBehaviour = {

        popovertitle: function(i) {
            return " " +
                '<button type="button" class="popover-edit" data-action="edit" data-linkindex="' + i + '">' +
                '<span aria-hidden="true" class="fa fa-edit"></span></button>' +
                '<button type="button" class="popover-edit" data-action="delete" data-linkindex="' + i + '">' +
                '<span aria-hidden="true" class="fa fa-remove"></span></button>';
        },

        popovercontent: function(i) {

            var content = "";
            content += item("operations", this.properties.calls);
            return "<dl>" + content + "</dl>";
        }
    };

    var NodeBehaviour = {

        popovertitle : function (i) {
             return this.name + "&nbsp;&nbsp;&nbsp;" +
                 '<button type="button" class="popover-edit" data-action="edit" data-nodeindex="' + i + '">' +
                 '<span aria-hidden="true" class="fa fa-edit"></span></button>' +
                 '<button type="button" class="popover-edit" data-action="delete" data-nodeindex="' + i + '">' +
                 '<span aria-hidden="true" class="fa fa-remove"></span></button>';
        },

        popovercontent: function(i) {
            var location = this.properties.location;
            if (location !== undefined && location !== "") {
                location = location + " - " + this.location_option;
            }

            var terms = [
                [ "Type", this.type],
                [ "Name", this.name],
                [ "Category", this.properties.category],
                [ "Language", this.properties.language],
                [ "Min version", this.properties.min_version],
                [ "Max version", this.properties.max_version],
                [ "Artifact", this.properties.artifact],
                [ "Cost", this.properties.cost],
                [ "Location", location],
                [ "QoS", this.properties.qos],
                [ "Infrastructure", this.properties.infrastructure]
            ];

            var content = "";
            for (var i = 0; i < terms.length; i++) {
                var term = terms[i];
                content += item(term[0], term[1]);
            }
            return "<dl>" + content + "</dl>";
        }
    };

    function item(key, value) {
        var result = "";
        if (value !== undefined && value !== "") {
            if (Array.isArray(value)) {
                var aux = "<ul>";
                for (var i = 0; i < value.length; i++) {
                    var o = value[i];
                    aux += "<li>";
                    if (key == "QoS") {
                        aux += o.metric + " " + o.operator + " " + o.threshold;
                    }
                    else if (key == "operations") {
                        aux += o["source-operation-name"] + "->"
                            + o["target-operation-name"]
                            + ": " + o["operation-calls"] + " calls";
                    }
                    else {
                        aux += JSON.stringify(value[i]);
                    }
                }
                aux += "</ul>";
                value = aux;
            }
            else if (typeof(value) == "object") {
                value = JSON.stringify(value);
            }
            result = "<dt>" + key + "</dt><dd>" + value + "</dd>";
        }
        return result;
    };

    function init(canvas_param) {
        canvas = canvas_param;

        EditorForms.initialize_fieldssets();
        initialize_forms();

        /*
         * Edit/Delete node
         */
        $('body').off('click', '.popover button[data-nodeindex]');
        $('body').on('click', '.popover button[data-nodeindex]', function () {
            var index = this.getAttribute("data-nodeindex");

            if (index === undefined) {
                return;
            };
            var node = index !== undefined? canvas.getnode(index) : undefined;
            var action = this.getAttribute("data-action");

            log.debug("Popover button click: action=" + action +
                " nodeid=" + index +
                " node=" + node.toString());

            /*
             * hide all popovers
             */
            $('[data-popover]').popover('hide');

            if (action == "edit") {
                /*
                 * and load form
                 */
                activeform = buttonform_map[node.type];
                activeform.reset();
                activeform.load(node);
                activeform.show(editnodecallback);
            }
            else if (action == "delete") {
                canvas.removenode(node);
            }
        });

        /*
         * Edit/delete link
         */
        $('body').off('click', '.popover button[data-linkindex]');
        $('body').on('click', '.popover button[data-linkindex]', function () {
            var index = this.getAttribute("data-linkindex");

            if (index === undefined) {
                return;
            }
            var link = canvas.getlink(index);
            var action = this.getAttribute("data-action");

            log.debug("Popover link click: action=" + action +
                " linkid=" + index +
                " link=" + link.toString());

            /*
             * hide all popovers
             */
            $('[data-popover]').popover('hide');

            if (action == "delete") {
                canvas.removelink(link);
            }
            else if (action == "edit") {
                activeform = linkform;
                activeform.reset();
                activeform.load(link);
                activeform.show(function(editlink) {
                    canvas.restart();
                    canvas.firechange();
                });
            }
        });

        $("#add-buttons- [data-type]").on("click", function() {
            var datatype = this.getAttribute("data-type");
            if (datatype === undefined) {
                log.warn("Button " + this.id + " does not have data-type attribute");
            }

            $("[aria-describedby]").popover('hide');

            activeform = buttonform_map[datatype];
            activeform.reset();
            activeform.show(addnodecallback);
        });

        /*
         * Main "Add..." button behaviour
         */
        $("#add-buttons a").on("click", function() {
            var datatype = this.getAttribute("data-type");
            if (datatype === undefined) {
                log.warn("Button " + this.id + " does not have data-type attribute");
            }

            /*
             * Hide popovers TODO: Change
             */
            $("[aria-describedby]").popover('hide');

            activeform = buttonform_map[datatype];
            activeform.reset();
            activeform.show(addnodecallback);
        });

        /*
         * In form "Create node" button behaviour (assigned externally because
         * button is shared among forms).
         */
        $("div.modal-footer button.btn-primary").on("click", function() {
            activeform.hide();
            activeform.createnode();
            activeform = undefined;
        });

    }

    function addnodecallback(node) {
        checkfrontendnode(node);
        canvas.addnode(node);
        canvas.restart();
    }

    function editnodecallback(node) {
        checkfrontendnode(node);
        canvas.restart();
        canvas.firechange();
    }

    function addlinkcallback(canvas, link, accept) {
        /*
         * check if is a valid link
         */
        if (canvas.getlinkbynodes(link.source, link.target) ||
            canvas.getlinkbynodes(link.target, link.source) ||
            link.source.type === Types.Database.type ||
            link.target.type === Types.Cloud.type) {
            return undefined;
        }
        link.behaviour = LinkBehaviour
        activeform = linkform;
        activeform.reset();
        activeform.load(link);

        activeform.show(function(editlink) {
            accept();
        });
    }

    /*
     * Check if the just edited node is set as frontend. If so,
     * resets frontend from rest of nodes
     */
    function checkfrontendnode(editednode) {
        if (editednode.properties.frontend) {
            canvas.nodes().
                filter(function(n) { return n != editednode; }).
                forEach(function(n) { n.properties.frontend = false; });
        }
    }

    //var qos_table;
    //var link_operations_table;

    var activeform = undefined;
    var webappform = Object.create(Forms.Form);
    var databaseform = Object.create(Forms.Form);
    var restform = Object.create(Forms.Form);
    var linkform = Object.create(Forms.Form);

    var commonset = EditorForms.commonset;
    var codetechset = EditorForms.codetechset;
    var databasetechset = EditorForms.databasetechset;
    var nonfunctionalset = EditorForms.nonfunctionalset;
    var infrastructureset = EditorForms.infrastructureset;
    var operationsset = EditorForms.operationsset;

    var buttonform_map;

    var fromjson = function(json) {
        /*
         * Autogenerate typemap
         */
        var typemap = {};
        for (var i in Types) {
            typemap[i] =  Types[i];
        }

        for (var i = 0; i < json.nodes.length; i++) {
            json.nodes[i].behaviour = NodeBehaviour;
        }

        for (var i = 0; i < json.links.length; i++) {
            json.links[i].behaviour = LinkBehaviour;
        }

        canvas.fromjson(json, typemap);
    };

    function initialize_forms() {
        webappform.setup(
            Types.WebApplication,
            NodeBehaviour,
            document.getElementById("add-form"),
            "Web application",
            [commonset, codetechset, nonfunctionalset, infrastructureset],
            ["set-common", "set-code-tech", "set-nonfunctional", "set-infrastructure"]
        );
        databaseform.setup(
            Types.Database,
            NodeBehaviour,
            document.getElementById("add-form"),
            "Database",
            [commonset, databasetechset, nonfunctionalset, infrastructureset],
            ["set-common", "set-database-tech", "set-nonfunctional", "set-infrastructure"]
        );
        restform.setup(
            Types.RestService,
            NodeBehaviour,
            document.getElementById("add-form"),
            "REST service",
            [commonset, codetechset, nonfunctionalset, infrastructureset],
            ["set-common", "set-code-tech", "set-nonfunctional", "set-infrastructure"]
        );
        linkform.setup(
            Graph.Link,
            LinkBehaviour,
            document.getElementById("update-link-form"),
            "Link",
            [operationsset],
            ["set-operations"]
        );
        buttonform_map = {
            "add-webapplication": webappform,
            "add-database": databaseform,
            "add-restservice": restform,
            "WebApplication": webappform,
            "Database": databaseform,
            "RestService": restform,
        };
    }

    return {
        init : init,
        addlinkcallback: addlinkcallback,
        fromjson: fromjson,
        NodeBehaviour: NodeBehaviour,
        LinkBehaviour: LinkBehaviour,
    };
})();
