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

    var LOCATION_STATIC = "STATIC";
    var LOCATION_DYNAMIC = "DYNAMIC";

    var INFRASTRUCTURE_IAAS = "compute";
    var INFRASTRUCTURE_PAAS = "platform";

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

    var language_container_options = {
        "": { "": "" },
        "JAVA": {
                "": "",
                "webapp.jboss.JBoss6Server": "JBoss 6",
                "webapp.jboss.JBoss7Server": "JBoss 7",
                "webapp.jetty.Jetty6Server": "Jetty 6",
                "webapp.tomcat.TomcatServer": "Tomcat",
                "webapp.tomcat.Tomcat8Server": "Tomcat 8"
            },
        "PYTHON": {
                "": ""
            },
        "RUBY": {
                "": ""
            },
        ".NET": {
                "": ""
            },
        "PHP": {
                "": "",
                "php.httpd.PhpHttpdServer": "Apache"
            }
    }

    var database_options = {
        "": "",
        "database.mysql.MySqlNode": "MySql",
        "database.mariadb.MariaDbNode": "mariadb",
        "database.postgresql.PostgreSqlNode": "PostgreSQL",
        "nosql.mongodb.MongoDBServer": "MongoDB",
        "nosql.redis.RedisStore": "Redis",
    };

    var database_version_options = {
        "": [ "" ],
        "database.mysql.MySqlNode": [ 5.0, 5.1, 5.2, 5.3, 5.4, 5.5, 5.6 ],
        "database.mariadb.MariaDbNode": [5.0, 5.1, 5.2, 5.3, 5.4, 5.5, 5.6 ],
        "database.postgresql.PostgreSqlNode": [ 8, 9.0, 9.1, 9.2, 9.3, 9.4 ],
        "nosql.mongodb.MongoDBServer": [ 1, 2, 3 ],
        "nosql.redis.RedisStore": [ 1, 2, 3 ]
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

    var qos_metrics = {
        "AverageResponseTime": "Average Response Time",
        "AverageThroughput": "Average Throughput",
        "AppAvailable": "Availability",
    };

    var qos_operators = {
        "": "",
        "less_than": "<",
        "less_or_equal": "<=",
        "equal": "=",
        "greater_or_equal": ">=",
        "greater_than": ">",
        "in_range": "between",
    };

    var benchmark_platforms = {
        "": "",
        "Amazon_EC2_m1_small_us_east_1": "Amazon AWS small instance",
        "Amazon_EC2_m1_medium_us_east_1": "Amazon AWS medium instance",
        "Amazon_EC2_m1_large_us_east_1": "Amazon AWS large instance",
        "Microsoft_Azure_Virtual_Machines_A0_eu_north": "Microsoft Azure A0 instance (small, 1 core) ",
        "Microsoft_Azure_Virtual_Machines_A6_eu_north": "Microsoft Azure A6 instance (large, 4 cores) ",
        "Microsoft_Azure_Virtual_Machines_D4_eu_north": "Microsoft Azure A0 instance (very large, 8 core) ",
        "Rackspace_Cloud_Servers_general1_1_ORD": "Rackspace servers general small (1 core)",
        "Rackspace_Cloud_Servers_general1_2_ORD": "Rackspace servers general medium (2 cores)",
        "Rackspace_Cloud_Servers_general1_4_ORD": "Rackspace servers general large (4 cores)",
        "Rackspace_Cloud_Servers_general1_8_ORD": "Rackspace servers general very large (8 cores)"
    };

    var operation_types = {
        "seaclouds.relations.databaseconnections.jdbc": "JDBC connection",
        "seaclouds.relations.databaseconnections.php": "PHP-db connection",
        "seaclouds.relation.connection.endpoint.host": "HTTP connection"
    };

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


    function init(canvas) {
        this.canvas = canvas;

        initialize_fieldssets();
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
        link.behaviour = LinkBehaviour
        activeform = linkform;
        activeform.reset();
        activeform.load(link);

        activeform.show(function(editlink) {
            accept();
        });
    }

    //var qos_table;
    //var link_operations_table;

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

    commonset.commonset = function(fieldsetid) {
        this.setup(fieldsetid);

        return this;
    }

    commonset.load = function(node) {
        $("#name").val(node.name);
        $("#label").val(node.label);
    };

    commonset.store = function(node) {
        node.name = $("#name").val();
    };

    codetechset.codetechset = function(fieldsetid) {
        this.setup(fieldsetid);
        Forms.populate_select($("#code-language"), language_options);

        $('#code-language').change(function() {
            Forms.populate_select_from_array(
                $('#code-min-version'),
                language_version_options[$('#code-language').val()]
            );
            Forms.populate_select_from_array(
                $('#code-max-version'),
                language_version_options[$('#code-language').val()]
            );
            Forms.populate_select(
                $('#code-container'),
                language_container_options[$('#code-language').val()]
            );
        });

        return this;
    }

    codetechset.load = function(node) {
        $("#code-language").val(node.properties.language);
        $("#code-artifact").val(node.properties.artifact);

        Forms.populate_select_from_array(
            $('#code-min-version'),
            language_version_options[node.properties.language] || ""
        );
        Forms.populate_select_from_array(
            $('#code-max-version'),
            language_version_options[node.properties.language] || ""
        );
        $("#code-min-version").val(node.properties.min_version);
        $("#code-max-version").val(node.properties.max_version);

        Forms.populate_select(
            $('#code-container'),
            language_container_options[node.properties.language] || ""
        );
        $("#code-container").val(node.properties.container);
    };

    codetechset.store = function(node) {
        node.properties.language = $("#code-language").val();
        node.properties.artifact = $("#code-artifact").val();
        node.properties.versions = $("#code-version").val();
        node.properties.min_version = $("#code-min-version").val();
        node.properties.max_version = $("#code-max-version").val();
        node.properties.container = $("#code-container").val();
    };

    databasetechset.databasetechset = function(fieldsetid) {
        this.setup(fieldsetid);
        Forms.populate_select($("#database-category"), database_options);

        $('#database-category').change(function() {
            Forms.populate_select_from_array(
                $('#database-min-version'),
                database_version_options[$('#database-category').val()]
            );
            Forms.populate_select_from_array(
                $('#database-max-version'),
                database_version_options[$('#database-category').val()]
            );
        });
        return this;
    }

    databasetechset.load = function(node) {
        $("#database-category").val(node.properties.category);
        $("#database-artifact").val(node.properties.artifact);
        Forms.populate_select_from_array(
            $('#database-min-version'),
            database_version_options[node.properties.category] || ""
        );
        Forms.populate_select_from_array(
            $('#database-max-version'),
            database_version_options[node.properties.category] || ""
        );
        $("#database-min-version").val(node.properties.min_version);
        $("#database-max-version").val(node.properties.max_version);

        $("#database-name").val(node.properties.db_name);
        $("#database-user").val(node.properties.db_user);
        $("#database-pwd").val(node.properties.db_password);
    };

    databasetechset.store = function(node) {
        node.properties.category = $("#database-category").val();
        node.properties.artifact = $("#database-artifact").val();
        node.properties.min_version = $("#database-min-version").val();
        node.properties.max_version = $("#database-max-version").val();
        node.properties.db_name = $("#database-name").val();
        node.properties.db_user = $("#database-user").val();
        node.properties.db_password = $("#database-pwd").val();
    };

    nonfunctionalset.nonfunctionalset = function(fieldsetid) {
        this.setup(fieldsetid);
        Forms.populate_select($("#nf-qos-metric"), qos_metrics);
        Forms.populate_select($("#nf-qos-operator"), qos_operators);
        Forms.populate_select($("#nf-benchmark-platform"), benchmark_platforms);
        /*
         * Important: populate selects in table before creating DynamicTable
         */
        this.qos_table = Object.create(Forms.DynamicTable).setup("nf-qos");

        return this;
    }

    nonfunctionalset.load = function(node) {
        this.qos_table.load(node.properties.qos);
        $("#nf-benchmark-responsetime").val(node.properties.benchmark_rt);
        $("#nf-benchmark-platform").val(node.properties.benchmark_platform);
        $("#nf-autoscale").val(node.properties.autoscale? "1" : "");
    };

    nonfunctionalset.store = function(node) {
        node.properties.qos = this.qos_table.serialize();
        node.properties.benchmark_rt = $("#nf-benchmark-responsetime").val();
        node.properties.benchmark_platform = $("#nf-benchmark-platform").val();
        node.properties.autoscale = Boolean($("#nf-autoscale").val());
    };


    infrastructureset.infrastructureset = function(fieldsetid) {
        this.setup(fieldsetid);

        var self = this;
        $('input[type=radio][name=infrastructure]').change(function() {
            self.showproperties(true);
        });

        Forms.populate_select($("#nf-location-static-options"), location_static_options);
        Forms.populate_select($("#nf-location-dynamic-options"), location_dynamic_options);
        $('input[type=radio][name=nf-location]').change(function() {
            self.showlocation(true);
        });

        return this;
    }

    infrastructureset.store = function(node) {
        node.properties.infrastructure = this.getinfrastructure();
        node.properties.location = this.getlocation();

        if (node.properties.infrastructure == INFRASTRUCTURE_IAAS) {
            node.properties.num_cpus = $("#infrastructure-num-cpus").val();
            node.properties.disk_size = $("#infrastructure-disk-size").val();
        }
        if (node.properties.location == LOCATION_STATIC) {
            node.properties.location_option = $("#nf-location-static-options").val();
        }
        else if (node.properties.location == LOCATION_DYNAMIC) {
            node.properties.location_option = $("#nf-location-dynamic-options").val();
        }
    };

    infrastructureset.load = function(node) {
        this.radioval("infrastructure", node.properties.infrastructure);
        this.radioval("nf-location", node.properties.location);
        if (node.properties.infrastructure == INFRASTRUCTURE_IAAS) {
            $("#infrastructure-num-cpus").val(node.properties.num_cpus);
            $("#infrastructure-disk-size").val(node.properties.disk_size);
        }
        if(node.properties.location == LOCATION_STATIC) {
            $("#nf-location-static-options").val(node.properties.location_option);
        }
        else if (node.properties.location == LOCATION_DYNAMIC) {
            $("#nf-location-dynamic-options").val(node.properties.location_option);
        }
    };

    infrastructureset.expand = function(toexpand) {
        if (!toexpand) {
            this.expand_impl(false);
            return;
        }
        /*
         * expand manually
         */
        var filterdivs = function () {
            var result =
                this.id != "nf-location-static-div" &&
                this.id != "nf-location-dynamic-div" &&
                this.id != "infrastructure-iaas-div";
            return result;
        };
        this.$legend.siblings().filter(filterdivs).show(Forms.DURATION);

        this.showproperties();
        this.showlocation();
    };

    infrastructureset.showproperties = function (toanimate) {
        var infrastructuretype = this.radioval("infrastructure");
        log.debug("showproperties(" + toanimate + ") - infrastructuretype = " +
            infrastructuretype);

        var duration = toanimate? Forms.DURATION : 0;

        var toggle = function ($item, toshow) {
            if (toshow) {
                $item.show(duration);
            }
            else {
                $item.hide(duration);
            }
        };
        toggle(
            $("#infrastructure-iaas-div"),
            infrastructuretype == INFRASTRUCTURE_IAAS
        );
    };

    infrastructureset.getlocation = function() {
        var locationvalue = this.radioval("nf-location");
        return locationvalue;
    };

    infrastructureset.getinfrastructure = function() {
        var value = this.radioval("infrastructure");
        return value;
    };

    infrastructureset.showlocation = function (toanimate) {
        var locationvalue = this.radioval("nf-location");
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


    operationsset.operationsset = function(fieldsetid) {
        this.setup(fieldsetid);

        Forms.populate_select($("#operation-type"), operation_types);
        return this;
    }

    operationsset.load = function(link) {
        $("#operation-calls").val(link.properties.calls);
        $("#operation-env-var").val(link.properties.env_var);
        $("#operation-type").val(link.properties.operation_type);
    };

    operationsset.store = function(link) {
        link.properties.calls = $("#operation-calls").val();
        link.properties.env_var = $("#operation-env-var").val();
        link.properties.operation_type = $("#operation-type").val();
    };

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

        this.canvas.fromjson(json, typemap);
    };

    function initialize_fieldssets() {
        commonset.commonset("set-common");
        codetechset.codetechset("set-code-tech");
        databasetechset.databasetechset("set-database-tech");
        nonfunctionalset.nonfunctionalset("set-nonfunctional");
        infrastructureset.infrastructureset("set-infrastructure");
        operationsset.operationsset("set-operations");

    }

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
