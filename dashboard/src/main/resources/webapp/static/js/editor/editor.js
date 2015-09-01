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

var Editor = (function() {

    var canvas;

    var LOCATION_STATIC = "STATIC";
    var LOCATION_DYNAMIC = "DYNAMIC";
    
    var language_options = {
        "": "",
        "JAVA": "Java",
        "PYTHON": "Python",
        "RUBY": "Ruby",
        ".NET": ".Net",
        "PHP": "Php",
    };
    
    var language_version_options = {
        "": [ "" ], 
        "JAVA": [ 4, 5, 6, 7, 8],
        "PYTHON": [2, 3],
        "RUBY": [1, 2],
        ".NET": [1, 2, 3, 4],
        "PHP": [5.1, 5.2, 5.3, 5.4, 5.5]
    };
    
    var database_options = {
        "MYSQL": "MySql",
        "ORACLE": "Oracle",
        "POSTGRESQL": "PostgreSQL",
        "MONGODB": "MongoDB",
        "REDIS": "Redis",
    };
    
    
    var cost_options = {
        "": "",
        "GOLD": "Gold",
        "SILVER": "Silver",
        "BRONZE": "Bronze",
    };
    
    
    var location_static_options = {
        "": "",
        "EUROPE": "Europe",
        "AMERICA": "America",
        "ASIA": "Asia"
    };
    
    
    var location_dynamic_options = {
        "": "",
        "FOLLOW_SUN": "Follow the sun",
        "FOLLOW_MOON": "Follow the moon",
        "FOLLOW_WIND": "Follow the wind",
        "FOLLOW_KILOWATT": "Follow the kilowatt",
    };
    
    var qos_operators = {
        "": "",
        "LT": "<",
        "LE": "<=",
        "EQ": "=",
        "GE": ">=",
        "GT": ">",
        "BETWEEN": "between",
    };

    /*
     * Override some Link and Node methods regarding Canvas.
     */
    
    Graph.Link.popovertitle = function(i) {
        return " " + 
            '<button type="button" class="popover-edit" data-action="edit" data-linkindex="' + i + '">' + 
            '<span aria-hidden="true" class="fa fa-edit"></span></button>' +
            '<button type="button" class="popover-edit" data-action="delete" data-linkindex="' + i + '">' + 
            '<span aria-hidden="true" class="fa fa-remove"></span></button>';
    };
    
    Graph.Link.popovercontent = function(i) {
        
        var content = "";
        content += item("operations", this.properties.operations);
        return "<dl>" + content + "</dl>";
    };
    
    Graph.Node.popovertitle = function(i) {
        return this.name + "&nbsp;&nbsp;&nbsp;" +
            '<button type="button" class="popover-edit" data-action="edit" data-nodeindex="' + i + '">' + 
            '<span aria-hidden="true" class="fa fa-edit"></span></button>' +
            '<button type="button" class="popover-edit" data-action="delete" data-nodeindex="' + i + '">' + 
            '<span aria-hidden="true" class="fa fa-remove"></span></button>';
    };
    
    Graph.Node.popovercontent = function(i) {
        var location = this.location;
        if (location !== undefined && location !== "") {
            location = location + " - " + this.location_option;
        }
        
        var terms = [
            [ "Type", this.type],
            [ "Name", this.name],
            [ "Category", this.category],
            [ "Language", this.language],
            [ "Versions", this.versions],
            [ "Artifact", this.artifact],
            [ "Cost", this.cost],
            [ "Location", location],
            [ "QoS", this.qos],
            [ "Infrastructure", this.infrastructure]
        ];
        
        var content = "";
        for (var i = 0; i < terms.length; i++) {
            var term = terms[i];
            content += item(term[0], term[1]);
        }
        return "<dl>" + content + "</dl>";
    };
    
    function item(key, value) {
        var result = "";
        if (value !== undefined && value !== "") {
            if (Array.isArray(value)) {
                aux = "<ul>";
                for (var i = 0; i < value.length; i++) {
                    o = value[i];
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


    function init(canvas) {
        this.canvas = canvas;

        populate_controls();
        initialize_fieldssets();
        initialize_forms();

        /*
         * Edit/Delete node
         */
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
                activeform.show(function(node) {
                    canvas.restart();
                    canvas.firechange();
                });
            }
            else if (action == "delete") {
                canvas.removenode(node);
            }
        });
        
        /*
         * Edit/delete link
         */
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
            activeform.show(function(node) {
                canvas.addnode(node);
                canvas.restart();
            });
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
            activeform.show(function(node) {
                canvas.addnode(node);
                canvas.restart();
            });
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
        activeform = linkform;
        activeform.reset();
        activeform.load(link);

        activeform.show(function(editlink) {
            accept();
        });
    }
    
    var qos_table;
    var link_operations_table;
    
    var activeform = undefined;
    var webappform = Object.create(Forms.Form);
    var databaseform = Object.create(Forms.Form);
    var restform = Object.create(Forms.Form);
    var linkform = Object.create(Forms.Form);
    
    var commonset = Object.create(Forms.Fieldset);
    var codetechset = Object.create(Forms.Fieldset);
    var databasetechset = Object.create(Forms.Fieldset);
    var nonfunctionalset = Object.create(Forms.Fieldset);
    var infrastructureset = Object.create(Forms.Fieldset);
    var operationsset = Object.create(Forms.Fieldset);

    var buttonform_map;

    commonset.load = function(node) {
        $("#name").val(node.name);
        $("#label").val(node.label);
    };
    
    commonset.store = function(node) {
        node.name = $("#name").val();
        node.label = $("#label").val();
    };
    
    codetechset.load = function(node) {
        $("#code-language").val(node.properties.language);
        $("#code-artifact").val(node.properties.artifact);
        Forms.populate_select_from_array(
            $('#code-version'),
            language_version_options[node.properties.language] || ""
        );
        $("#code-version").val(node.properties.versions);
    };
    
    codetechset.store = function(node) {
        node.properties.language = $("#code-language").val();
        node.properties.artifact = $("#code-artifact").val();
        node.properties.versions = $("#code-version").val();
    };
    
    databasetechset.load = function(node) {
        $("#database-category").val(node.properties.category);
        $("#database-artifact").val(node.properties.artifact);
    };
    
    databasetechset.store = function(node) {
        node.properties.category = $("#database-category").val();
        node.properties.artifact = $("#database-artifact").val();
    };
    
    nonfunctionalset.load = function(node) {
        $("#nf-cost").val(node.properties.cost);
        this.radioval("nf-location", node.properties.location);
        if(node.location == LOCATION_STATIC) {
            $("#nf-location-static-options").val(node.properties.location_option);
        }
        else if (node.location == LOCATION_DYNAMIC) {
            $("#nf-location-dynamic-options").val(node.properties.location_option);
        }
        qos_table.load(node.properties.qos);
    };
    
    nonfunctionalset.store = function(node) {
        node.properties.cost = $("#nf-cost").val();
        node.properties.location = this.getlocation();
        if (node.properties.location == LOCATION_STATIC) {
            node.properties.location_option = $("#nf-location-static-options").val();
        }
        else if (node.location == LOCATION_DYNAMIC) {
            node.properties.location_option = $("#nf-location-dynamic-options").val();
        }
        node.properties.qos = qos_table.serialize();
    };
    
    nonfunctionalset.expand = function(toexpand) {
        if (!toexpand) {
            this.expand_impl(false);
            return;
        }
        /* 
         * expand manually
         */
        var filterlocationdivs = function () {
            var result = 
                this.id != "nf-location-static-div" && 
                this.id != "nf-location-dynamic-div";
            return result;
        };
        this.$legend.siblings().filter(filterlocationdivs).show(Forms.DURATION);
        
        this.showlocation();
    };
    
    nonfunctionalset.getlocation = function() {
        var locationvalue = this.radioval("nf-location");
        return locationvalue;
    };
    
    nonfunctionalset.showlocation = function (toanimate) {
        var locationvalue = this.getlocation();
        log.debug("showlocation(" + toanimate + ") - locationvalue = " + locationvalue);
        
        var duration = toanimate? Forms.DURATION : 0;
        
        var togglelocation = function ($item, toshow) {
            if (toshow) {
                $item.show(duration);
            }
            else {
                $item.hide(duration);
            }
        };
        togglelocation(
            $("#nf-location-static-div"), 
            locationvalue == LOCATION_STATIC
        );
        togglelocation(
            $("#nf-location-dynamic-div"), 
            locationvalue == LOCATION_DYNAMIC
        );
    };
    
    infrastructureset.store = function(node) {
        node.properties.infrastructure = this.radioval("infrastructure");
    };
    
    infrastructureset.load = function(node) {
        this.radioval("infrastructure", node.properties.infrastructure);
    };
    
    operationsset.load = function(link) {
        link_operations_table.load(link.properties.operations);
    };
    
    operationsset.store = function(link) {
        link.properties.operations = link_operations_table.serialize();
    };
    
    fromjson = function(json) {
        /*
         * Autogenerate typemap
         */
        typemap = {};
        for (var i in Types) {
            typemap[i] =  Types[i];
        }
        
        this.canvas.fromjson(json, typemap);
    };
    
    function populate_controls() {
        Forms.populate_select($("#code-language"), language_options);
        Forms.populate_select($("#database-category"), database_options);
        Forms.populate_select($("#nf-cost"), cost_options);
        Forms.populate_select($("#nf-location-static-options"), location_static_options);
        Forms.populate_select($("#nf-location-dynamic-options"), location_dynamic_options);
        Forms.populate_select($("#nf-qos-operator"), qos_operators);
    }
    
    function initialize_fieldssets() {
        commonset.setup("set-common");
        codetechset.setup("set-code-tech");
        databasetechset.setup("set-database-tech");
        nonfunctionalset.setup("set-nonfunctional");
        infrastructureset.setup("set-infrastructure");
        operationsset.setup("set-operations");
    
        qos_table = Object.create(Forms.DynamicTable).setup("nf-qos");
        link_operations_table = Object.create(Forms.DynamicTable)
            .setup("link-operations");
    }
    
    function initialize_forms() {
        webappform.setup(
            Types.WebApplication,
            document.getElementById("add-form"),
            "Web application",
            [commonset, codetechset, nonfunctionalset, infrastructureset],
            ["set-common", "set-code-tech", "set-nonfunctional", "set-infrastructure"]
        );
        databaseform.setup(
            Types.Database,
            document.getElementById("add-form"),
            "Database",
            [commonset, databasetechset, nonfunctionalset, infrastructureset],
            ["set-common", "set-database-tech", "set-nonfunctional", "set-infrastructure"]
        );
        restform.setup(
            Types.RestService,
            document.getElementById("add-form"),
            "REST service",
            [commonset, codetechset, nonfunctionalset, infrastructureset],
            ["set-common", "set-code-tech", "set-nonfunctional", "set-infrastructure"]);
        
        linkform.setup(
            Graph.Link,
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
        /*
         * Additional behaviour
         */
        $('input[type=radio][name=nf-location]').change(function() {
            nonfunctionalset.showlocation(true);
        });
        
        $('#code-language').change(function() {
            Forms.populate_select_from_array(
                $('#code-version'),
                language_version_options[$('#code-language').val()]
            );
        });
    }
    
    return {
        init : init,
        addlinkcallback: addlinkcallback,
        fromjson: fromjson,
    };
})();
