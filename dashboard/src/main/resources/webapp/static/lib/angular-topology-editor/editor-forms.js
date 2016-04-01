var EditorForms = (function() {

    var commonset = Object.create(Forms.Fieldset);
    var codetechset = Object.create(Forms.Fieldset);
    var databasetechset = Object.create(Forms.Fieldset);
    var nonfunctionalset = Object.create(Forms.Fieldset);
    var infrastructureset = Object.create(Forms.Fieldset);
    var operationsset = Object.create(Forms.Fieldset);

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
        "Europe": "Europe",
        "America": "America",
        "Asia": "Asia",
        "Oceania": "Oceania"
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

    commonset.commonset = function(fieldsetid) {
        this.setup(fieldsetid);

        return this;
    }

    commonset.load = function(node) {
        $("#name").val(node.name);
        $("#label").val(node.label);
        $("#common-frontend").val(node.properties.frontend? "1" : "");
    };

    commonset.store = function(node) {
        node.name = $("#name").val();
        node.properties.frontend = Boolean($("#common-frontend").val());
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

    var initialize_fieldssets = function() {
        commonset.commonset("set-common");
        codetechset.codetechset("set-code-tech");
        databasetechset.databasetechset("set-database-tech");
        nonfunctionalset.nonfunctionalset("set-nonfunctional");
        infrastructureset.infrastructureset("set-infrastructure");
        operationsset.operationsset("set-operations");
    };

    return {
        initialize_fieldssets: initialize_fieldssets,
        commonset: commonset,
        codetechset: codetechset,
        databasetechset: databasetechset,
        nonfunctionalset: nonfunctionalset,
        infrastructureset: infrastructureset,
        operationsset: operationsset,
    }
})();
