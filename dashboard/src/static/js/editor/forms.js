 /**
 * This is a proof of concept of topology editor using Graph.Node objects.
 *
 * This file declares Form and FieldSet objects to create/edit Graph.Node 
 * objects. 
 * 
 * A Form is a group of Fieldsets and is backed by an html form and html 
 * fieldsets. The same html form may be used by several Forms.
 *
 * Basic behaviour is implemented: 
 * - show/hide form
 * - collapse/expand fieldsets
 *
 * Usage:
 *   var webappform = Object.create(Forms.Form);
 *   var commonset = Object.create(Fieldset);
 *   commonset.store = function() { ... };
 *   commonset.load = function() { ... };
 *   $(document).ready(function() {
 *     commonset.setup("set-common");
 *     webappform.setup(
 *         Graph.WebApplication,
 *         document.getElementById("add-form"),
 *         "Web application",
 *         [commonset],
 *     );
 *   );
 * 
 * The following prefixes in variables are used:
 *   j_ : jquery selections
 *   d3_ : d3 selections
 *   g_ : Graph.* nodes (or children)
 * 
 * @author roman.sosa@atos.net
 */
var Forms = (function() {
    var DURATION = 200;
    
    /*
     * Create node form. 
     * It requires a wrapping div with an id attribute.
     * 
     * It handles the fieldsets to show.
     */
    var Form = {
        /*
         * nodeprototype: prototype of the node that this form creates
         * div: DOM modal div containing form
         * title: string to show as header of form
         * fieldsets: array of Fieldset objects that comprise the form
         */
        setup: function(nodeprototype, div, title, fieldsets) {
            var self = this;
            this.nodeprototype = nodeprototype;
            this.div = div;
            this.title = title;
            this.fieldsets = fieldsets;
            
            this.$div = $(div);
            this.$allsets = this.$div.find("fieldset");
            this.form = this.$div.find("form")[0];
            this.$title = this.$div.find(".modal-title");
            
            this.node = undefined;
            this.acceptcallback = undefined;
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
            this.$allsets.hide();
            $.map(this.fieldsets, function (set, i) { set.show(); });
            
            $.map(this.fieldsets, function (set, i) { set.expand( (i == 0) ); });
            
            this.$title.text(this.title);
            this.$div.modal();
        },
        show: function(acceptcallback) {
            this.acceptcallback = acceptcallback;
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
            log.info( (isnewnode? "Add" : "Edit") + " node " + node.toString());
            $.map(this.fieldsets, function(set) { set.store(node); });
            
            this.acceptcallback(node);
            this.node = undefined;
        },
        createnode: function() {
            name = $("#name").val();
            label = $("#label").val();
        
            var isnewnode = (this.node === undefined);
            var n = isnewnode? 
                Object.create(this.nodeprototype).init({
                    name: name, 
                    label: label}):
                this.node;
            this.ok_impl(n, isnewnode);    
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
        },
        radioval: function(name, value) {
            var selection = 'input[name=' + name + ']';
            if (value === undefined) {
                return $(selection + ':checked').val();
            }
            else {
                $(selection).
                    prop(
                        "checked", 
                        function() { return $(this).val() == value; }
                    );
            }
        }
    };
    
    /*
     * A DynamicTable handles the management of html dynamic tables, providing
     * basic functionality like adding rows and deleting rows, serializing
     * the content as a json, and loading json content into the table.
     * 
     * The first row in the first tbody at initialization time is used as a
     * template for every added row from then. Every element within the row 
     * with an id attribute is given an unique id attribute. Likewise, every 
     * element with a name attribute is given an unique name attribute, and 
     * is used to generate the json serialization.
     * 
     * Any element in the table with attribute data-action="add" is assigned a 
     * click event handler that adds a row to the table.
     * 
     * Any element in a row with attribute data-action="delete" is assigned a 
     * click event handler that deletes the row where the element is placed.
     * 
     * TODO: load/serialize radiobuttons and checkboxes
     */
    var DynamicTable = {
        setup: function(tableid) {
            var self = this;
            this.id = tableid;
            this.counter = 0;
            var elem = document.getElementById(tableid);
            if (!elem) {
                console.warn("DynamicTable.setup. Element[id=" + 
                    tableid + "] not found");
                return;
            }
            this.$table = $(document.getElementById(tableid));
            this.$tbody = this.$table.find("tbody").first();
            var $row = this.$tbody.find("tr").first();
            this.rowhtml = $row[0].outerHTML;
            
            /*
            * change attributes to row elements
            */
            this._changerow($row);
            this.counter++;
            
            /*
            * assign default behaviour addrow to buttons with data-action="add"
            */
            this.$table.find("[data-action='add']").on("click", function() {
               self.addrow();
            });
            return this;
        },
        addrow: function() {
            var $row = $(this.rowhtml);
            this._changerow($row);
            this.$tbody.append($row);
            return $row;
        },
        deleterow: function($row) {
            $row.remove();
        },
        serialize: function() {
            var result = [];
            this.$tbody.find("tr").each(function() {
                var item = {};
                var empty = true;
                var $tr = $(this);
                $tr.find("[name]").each(function() {
                    var $f = $(this);
                    var name = $f.attr("name");
                    var key = name.substring(0, name.lastIndexOf("__"));
                    var value = $f.val();
                    item[key] = value;
                    
                    if (value !== undefined && value !== "") {
                        empty = false;
                    }
                });
                if (!empty) {
                    result.push(item);
                }
            });
            return result;
        },
        reset: function() {
            this.$tbody.find("tr").remove();
            this.counter = 0;
        },
        load: function(serialized) {
            this.reset();
            if (serialized === undefined) {
                return;
            }
            for (var i = 0; i < serialized.length; i++) {
                var item = serialized[i];
                var $row = this.addrow();
                $row.find("[name]").each(function() {
                    var $f = $(this);
                    var name = $f.attr("name");
                    var key = name.substring(0, name.lastIndexOf("__"));
                    $f.val(item[key]);
                });
            }
        },
        _changerow: function($row) {
            var counter = this.counter;
            var self = this;
            
            $row.find("[name]").attr("name", function() { 
                return this.name + '__' + counter; 
            });
            $row.find("[id]").attr("id", function() { 
                return this.id + '__' + counter; 
            });
            $row.find("[data-action='delete']").on("click", function() {
                self.deleterow( $(this).parents("tr").first());
            });
        },
    };
    
    /*
     * NOT TESTED
     */
    var radioval = function($inputs, value) {
        if (value === undefined) {
            return $(inputs).filter(':checked').val();
        }
        else {
            $inputs.prop(
                "checked", 
                function() { return $(this).val() == value; }
            );
        }
    };

    var populate_select = function($select, options) {
        if ($select.length == 0) {
            log.warn("populate_select: empty set");
        }
        $select.find('option').remove();
        $.each(options, function(key, value) {
            $('<option>').val(key).text(value).appendTo($select);
        }); 
    };
    
    var populate_select_from_array = function($select, options) {
        if ($select.length == 0) {
            log.warn("populate_select_from_array: empty list");
        }
        $select.find('option').remove();
        $.each(options, function(key, value) {
            $('<option>').val(value).text(value).appendTo($select);
        }); 
    };
    
    return {
        Form: Form,
        Fieldset: Fieldset,
        DynamicTable: DynamicTable,
        radioval: radioval,
        populate_select: populate_select,
        populate_select_from_array: populate_select_from_array,
    };
})();
