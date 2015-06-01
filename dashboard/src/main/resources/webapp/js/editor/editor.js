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
var log = Log.getLogger("editor").setLevel(Log.DEBUG);

var DURATION = 200;

var LOCATION_STATIC = "STATIC";
var LOCATION_DYNAMIC = "DYNAMIC";

var language_options = {
    "JAVA": "Java",
    "PYTHON": "Python",
    "RUBY": "Ruby",
    ".NET": ".Net",
    "PHP": "Php",
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


function populate_select($select, options) {
    $select.find('option').remove();
    $.each(options, function(key, value) {
        $('<option>').val(key).text(value).appendTo($select);
    }); 
}

Graph.Node.popovertitle = function(i) {
    return this.name + "&nbsp;&nbsp;&nbsp;" +
        '<button type="button" class="popover-edit" data-action="edit" data-nodeindex="' + i + '">' + 
        '<span aria-hidden="true" class="fa fa-edit"></span></button>';
};


/*
 * Create node form. 
 * It requires a wrapping div with an id attribute.
 * 
 * It handles the fieldsets to show.
 */
var Form = {
    /*
     * div: DOM modal div containing form
     * title: string to show as header of form
     * fieldsets: array of Fieldset objects that comprise the form
     * sets: array of fieldsets that comprise the form
     */
    setup: function(div, title, fieldsets, sets) {
        var self = this;
        this.div = div;
        this.title = title;
        this.fieldsets = fieldsets;
        
        this.$div = $(div);
        this.$allsets = this.$div.find("fieldset");
        this.form = this.$div.find("form")[0];
        this.$title = this.$div.find(".modal-title");
        
        this.node = undefined;
        return this;
    },
    reset: function() {
        this.node = undefined;
        this.form.reset();
        /*
         * Additional reset actions (like hide sections by default)
         */
        $.map(this.fieldsets, function (set, i) { set.reset(); });
    },
    show_impl: function() {
        this.$div.find("button.btn-primary").text(
            this.node === undefined? "Add" : "Edit");
        // this.reset();
        this.$allsets.hide();
        $.map(this.fieldsets, function (set, i) { set.show(); });
        
        $.map(this.fieldsets, function (set, i) { set.expand( (i == 0) ); });
        
        this.$title.text(this.title);
        this.$div.modal();
    },
    show: function() {
        this.show_impl();
    },
    hide: function() {
        this.$div.modal('hide');
    },
    load: function(node) {
        this.node = node;
        $.map(this.fieldsets, function(set, i) { set.load(node); });
    },
    ok_impl: function(node, isnewnode) {
        log.info(isnewnode? "Add" : "Edit" + " node " + node.toString());
        $.map(this.fieldsets, function(set) { set.store(node); });
        
        if (isnewnode) {
            Canvas.nodes.push(node);
        }
        else {
            var index = Canvas.nodes.indexOf(node);
            Canvas.nodes[index] = node;
        }
        Canvas.restart();
        this.node = undefined;
    }
};

/*
 * A Fieldset groups controls (wrapped by a fieldset element)
 * It requires the fieldset element have an id attribute and a legend element.
 * 
 * It handles the collapsibility of fieldsets.
 */
var Fieldset = {
    setup: function(id) {
        var self = this;
        this.id = id;
        elem = document.getElementById(id);
        this.$fieldset = $(elem);
        this.$legend = this.$fieldset.find('legend');
        
        this.$legend.click(function() {
            var visible = self.$legend.siblings().first().is(":visible");
            log.debug("Toggling fieldset visibility to " + !visible);
            self.expand(!visible);
            return false;
        });
    },
    show: function() {
        this.$fieldset.show(DURATION);
    },
    hide: function() {
        this.$fieldset.hide(DURATION);
    },
    expand_impl: function(toexpand) {
        if (toexpand) {
            this.$legend.siblings().show(DURATION);
        }
        else {
            this.$legend.siblings().hide(DURATION);
        }
    },
    expand: function(toexpand) {
        this.expand_impl(toexpand);
    },
    reset: function() {
        /* 
         * does nothing: to overwrite on child
         */ 
    },
    store: function(node) {
        /*
         * does nothing: to overwrite on child
         */
    },
    load: function(node) {
        /*
         * does nothing: to overwrite on child
         */
    }
};


var webappform = Object.create(Form);
var databaseform = Object.create(Form);
var restform = Object.create(Form);

var activeform = undefined;


webappform.createnode = function() {
    name = $("#name").val();
    label = $("#label").val();
    language = $("#code-language").val();

    var isnewnode = (this.node === undefined);
    var n = isnewnode? 
        Object.create(Canvas.WebApplication).init(name, label, language):
        this.node;
    this.ok_impl(n, isnewnode);    
};


databaseform.createnode = function() {
    name = $("#name").val();
    label = $("#label").val();
    category = $("#database-category").val();
    
    var isnewnode = (this.node === undefined);
    var n = isnewnode? 
        Object.create(Canvas.Database).init(name, label, category):
        this.node;
    this.ok_impl(n, isnewnode);    
};


restform.createnode = function() {
    name = $("#name").val();
    label = $("#label").val();
    language = $("#code-language").val();

    var isnewnode = (this.node === undefined);
    var n = isnewnode? 
        Object.create(Canvas.RestService).init(name, label, language):
        this.node;
    this.ok_impl(n, isnewnode);    
};


var commonset = Object.create(Fieldset);
var codetechset = Object.create(Fieldset);
var databasetechset = Object.create(Fieldset);
var nonfunctionalset = Object.create(Fieldset);
var infrastructureset = Object.create(Fieldset); 

commonset.load = function(node) {
    $("#name").val(node.name);
    $("#label").val(node.label);
};

commonset.store = function(node) {
    node.name = $("#name").val();
    node.label = $("#label").val();
};

codetechset.load = function(node) {
    $("#code-language").val(node.language);
};

codetechset.store = function(node) {
    node.language = $("#code-language").val();
};

databasetechset.load = function(node) {
    $("#database-category").val(node.category);
};

databasetechset.store = function(node) {
    node.category = $("#database-category").val();
};

nonfunctionalset.load = function(node) {
    $("#nf-cost").val(node.cost);
    var $location = $('input:radio[name=nf-location]');
    $location.prop("checked", function() { return $(this).val() == node.location;});
    if(node.location == LOCATION_STATIC) {
        $("#nf-location-static-options").val(node.location_option);
    }
    else if (node.location == LOCATION_DYNAMIC) {
        $("#nf-location-dynamic-options").val(node.location_option);
    }
    $("#nf-qos").val(node.qos);
};

nonfunctionalset.store = function(node) {
    node.cost = $("#nf-cost").val();
    node.location = this.getlocation();
    if (node.location == LOCATION_STATIC) {
        node.location_option = $("#nf-location-static-options").val();
    }
    else if (node.location == LOCATION_DYNAMIC) {
        node.location_option = $("#nf-location-dynamic-options").val();
    }
    node.qos = $("#nf-qos").val();
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
        console.log("Filtering " + this.id + ": " + result);
        return result;
    };
    this.$legend.siblings().filter(filterlocationdivs).show(DURATION);
    
    this.showlocation();
};

nonfunctionalset.getlocation = function() {
    var locationvalue = $('input[name=nf-location]:checked').val();
    return locationvalue;
};

nonfunctionalset.showlocation = function (toanimate) {
    var locationvalue = this.getlocation();
    log.debug("showlocation(" + toanimate + ") - locationvalue = " + locationvalue);
    
    var duration = toanimate? DURATION : 0;
    
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
    node.infrastructure = $('input[name=infrastructure]:checked').val();
};

$(document).ready(function() {
    log.debug("ready");
    
    /*
     * Initialize fieldsets
     */
    commonset.setup("set-common");
    codetechset.setup("set-code-tech");
    databasetechset.setup("set-database-tech");
    nonfunctionalset.setup("set-nonfunctional");
    infrastructureset.setup("set-infrastructure");
    
    /*
     * Initialize forms
     */
    webappform.setup(
        document.getElementById("add-form"),
        "Web application",
        [commonset, codetechset, nonfunctionalset, infrastructureset],
        ["set-common", "set-code-tech", "set-nonfunctional", "set-infrastructure"]
    );
    databaseform.setup(
        document.getElementById("add-form"),
        "Database",
        [commonset, databasetechset, nonfunctionalset, infrastructureset],
        ["set-common", "set-database-tech", "set-nonfunctional", "set-infrastructure"]
    );
    restform.setup(
        document.getElementById("add-form"),
        "REST service",
        [commonset, codetechset, nonfunctionalset, infrastructureset],
        ["set-common", "set-code-tech", "set-nonfunctional", "set-infrastructure"]);
    
    var buttonform_map = {
        "add-webapplication": webappform,
        "add-database": databaseform,
        "add-restservice": restform,
        "WebApplication": webappform,
        "Database": databaseform,
        "RestService": restform,
    };

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
        activeform.show();
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
    
    /*
     * Populate controls 
     */
    populate_select($("#code-language"), language_options);
    populate_select($("#database-category"), database_options);
    populate_select($("#nf-cost"), cost_options);
    populate_select($("#nf-location-static-options"), location_static_options);
    populate_select($("#nf-location-dynamic-options"), location_dynamic_options);
  
    /*
     * Additional behaviour
     */
    $('input[type=radio][name=nf-location]').change(function() {
        nonfunctionalset.showlocation(true);
    });

    $('body').on('click', '.popover button', function () {
        var index = this.getAttribute("data-nodeindex");
        var node = Canvas.nodes[index];
        var action = this.getAttribute("data-action");
        
        log.debug("Popover button click: action=" + action + 
        " nodeid=" + index + 
        " node=" + node.toString());

        /*
         * hide popovers on canvas
         * 
         * WARNING (&TODO): Change to non-implementation dependent
         */
        $("[aria-describedby]").popover('hide');
        
        if (action == "edit") {
            /*
             * and load form
             */
            activeform = buttonform_map[node.type];
            activeform.reset();
            activeform.load(node);
            activeform.show();
        }
    });    
    var n0 = Object.create(Canvas.WebApplication).init("PHP Node", "PHP", "PHP");
    Canvas.nodes.push(n0);
    Canvas.restart();
});
