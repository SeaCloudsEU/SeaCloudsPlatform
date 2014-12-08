var log = Log.getLogger("editor").setLevel(Log.DEBUG);

var language_options = {
    "JAVA": "Java",
    "PYTHON": "Python",
    "RUBY": "Ruby",
    ".NET": ".Net"
};


function ids2dom(ids) {
    var result = [];
    
    for (var i in ids) {
        var id = ids[i];
        var elem = document.getElementById(id);
        if (elem === null) {
            log.warn("element " + id + " not found");
        }
        else {
            result.push(elem);
        }
    }
    return result;
}

function populate_select($select, options) {
    $select.find('option').remove();
    $.each(options, function(key, value) {
        $('<option>').val(key).text(value).appendTo($select);
    }); 
}

var Form = {
    /*
     * div: DOM modal div containing form
     * title: string to show as header of form
     * sets: array of fieldset ids that comprise the form
     */
    setup: function(div, title, sets) {
        this.div = div;
        this.title = title;
        
        this.$sets = $(ids2dom(sets));
        this.$div = $(div);
        this.$allsets = this.$div.find("fieldset");
        this.form = this.$div.find("form")[0];
        this.$title = this.$div.find(".modal-title");
        
        return this;
    },
    reset: function() {
        this.form.reset();
    },
    show_impl: function() {
        this.reset();
        this.$allsets.hide();
        this.$sets.show();
        /*
         * TODO: change
         */
        $('legend').siblings().hide();
        $('#set-common').find('legend').siblings().toggle();
        
        this.$title.text(this.title);
        this.$div.modal();
    },
    show: function() {
        this.show_impl();
    },
    hide: function() {
        this.$div.modal('hide');
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
    
    var n = Object.create(Canvas.WebApplication).init(name, label, language);
    log.info("Adding webapplication node " + n.toString());
    Canvas.nodes.push(n);
    Canvas.restart();
};


databaseform.createnode = function() {
    name = $("#name").val();
    label = $("#label").val();
    category = $("#database-category").val();
    
    var n = Object.create(Canvas.Database).init(name, label, category);
    log.info("Adding database node " + n.toString());
    Canvas.nodes.push(n);
    Canvas.restart();
};


restform.createnode = function() {
    name = $("#name").val();
    label = $("#label").val();
    language = $("#code-language").val();
    
    var n = Object.create(Canvas.RestService).init(name, label, language);
    log.info("Adding rest node " + n.toString());
    Canvas.nodes.push(n);
    Canvas.restart();
};


$(document).ready(function() {
    log.debug("ready");
    
    webappform.setup(
        document.getElementById("add-form"),
        "Add new web application",
        ["set-common", "set-code-tech", "set-nonfunctional", "set-infrastructure"]
    );
    databaseform.setup(
        document.getElementById("add-form"),
        "Add new database",
        ["set-common", "set-database-tech", "set-nonfunctional", "set-infrastructure"]
    );
    restform.setup(
        document.getElementById("add-form"),
        "Add new REST service",
        ["set-common", "set-code-tech", "set-nonfunctional", "set-infrastructure"]);
    
    var buttonform_map = {
        "add-webapplication": webappform,
        "add-database": databaseform,
        "add-restservice": restform,
    };
    
    $("#add-buttons a").on("click", function() {
        activeform = buttonform_map[this.id];
        activeform.show();
    });
    
    $("div.modal-footer button.btn-primary").on("click", function() {
        activeform.hide();
        activeform.createnode();
        activeform = undefined;
    });
    
    populate_select($("#code-language"), language_options);
    
    /*
     * Collapsible fieldsets
     */
    $('legend').click(function() {
        $(this).siblings().toggle();
        return false;
    });

    
    var n0 = Object.create(Canvas.WebApplication).init("name0", "L0", "Java");
    Canvas.nodes.push(n0);
    Canvas.restart();
});
