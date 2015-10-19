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

var Status = (function() {

    var canvas;

    /*
     * Override some Link and Node methods regarding Canvas.
     */

    var LinkBehaviour = {
        popovercontent : function(i) {
        }
    };

    var NodeBehaviour = {

        popovertitle: function(i) {
            return this.name;
        },

        popovercontent: function(i) {

            var terms = [
                [ "Type", this.type],
                [ "Status", this.properties.status],
            ];

            var content = "";
            for (var i = 0; i < terms.length; i++) {
                var key = terms[i][0];
                var value = terms[i][1];
                content += "<dt>" + key + "</dt><dd>" + value + "</dd>";
            }
            return "<dl>" + content + "</dl>";
        }
    };


    function init(canvas) {
        this.canvas = canvas;
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

    fromjson = function(json) {
        /*
         * Autogenerate typemap
         */
        typemap = {};
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

    return {
        init : init,
        addlinkcallback: addlinkcallback,
        fromjson: fromjson,
        canvas: canvas,
        NodeBehaviour: NodeBehaviour,
        LinkBehaviour: LinkBehaviour,
    };
})();
