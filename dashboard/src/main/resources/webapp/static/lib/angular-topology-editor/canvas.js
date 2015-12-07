/**
 * This is a proof of concept of topology editor using Graph.Node objects.
 *
 * Nodes and links can be added to the canvas.
 * Nodes can be moved. Nodes can be linked with other nodes.
 * The action to trigger when dragging a node (move or link) depends on
 * an init parameter, and can be modified while pressing a mod key (shift, ctrl)
 *
 * To initialize the canvas: Canvas.init(id, parameters). See below for a
 * description of parameters.
 *
 * Example:
 *    c = Canvas()
 *    canvas.init("canvas", {
 *       width: 400,
 *       height: 400,
 *       linkbydefault: true,
 *       addlinkcallback : function(link) {
 *           log.debug(link);
 *           return link;
 *       },
 *       changehandler : function(newmodel) {
 *           console.log("model has changed");
 *       }
 *   });
 *
 * To update the canvas when nodes are added/modified/removed: Canvas.restart()
 *
 * Graph.Node is augmented. See functions's implementation for more details:
 * - decorate() : used to generate a different decoration for each
 *   node type (for example, add an image). By default, it appends a text
 *   with the node index. It may be overwritten in a kind basis.
 * - popovertitle(): used to generate the title of the popover when clicking
 *   on a node. By default, it returns the node name.
 * - popovercontent(): used to generate the content of the popover when
 *   clicking on a node. By default, it returns a list containing type and
 *   index.
 *
 * A lot of ideas (and how to achieve some things) taken from :
 *
 * - http://bl.ocks.org/rkirsling/5001347
 * - http://bl.ocks.org/cjrd/6863459
 *
 * The following prefixes in variables are used:
 *   j_ : jquery selections
 *   d3_ : d3 selections
 *   g_ : Graph.* nodes (or children)
 *
 * @author roman.sosa@atos.net
 */

/*
 * A click gesture generates mousedown, mouseup and click events, in that order.
 * Preventing the mousedown event from propagating is not sufficient to prevent
 * the click event from propagating too. This must be done in a click event
 * listener.
 */

"use strict";

Graph.Link.popovertitle = function(i) {
    if (this.behaviour && this.behaviour.popovertitle) {
        return this.behaviour.popovertitle.call(this, i);
    }
    return "";
};

Graph.Link.popovercontent = function(i) {
    if (this.behaviour && this.behaviour.popovercontent) {
        return this.behaviour.popovercontent.call(this, i);
    }
    return "";
};

Graph.Node.decorate = function(dom_element, i) {
    var d3_element = d3.select(dom_element);
    d3_element.append("svg:text")
        .attr("x", 0)
        .attr("y", 9)
        .attr("class", "type-icon")
        .text(i);
};

Graph.Node.update = function(dom_element, i) {};

Graph.Node.popovertitle = function(i) {
    if (this.behaviour && this.behaviour.popovertitle) {
        return this.behaviour.popovertitle.call(this, i);
    }
    return this.name;
};

Graph.Node.popovercontent = function(i) {
    if (this.behaviour && this.behaviour.popovercontent) {
        return this.behaviour.popovercontent.call(this, i);
    }
    var content =
        "<dt>Type</dt><dd>" + this.type + "</dd>" +
        "<dt>Index</dt><dd>" + i + "</dd>";
    return "<dl>" + content + "</dl>";
};

var Canvas = (function() {

    var WIDTH = 960,
        HEIGHT = 500;

    var NODE_RADIUS = 20;

    /*
     * These variables store parameter values
     */
    var
        width,              /* canvas width. default: WIDTH */
        height,             /* canvas height. default: HEIGHT */
        linkingenabled,     /* set to false for a RO canvas. def:true */
        linkbydefault,      /* default mode: T: links; F: drag node. def: F
                             * Press mod keys to swith mode.*/
        changehandler,      /* Function to call on add, delete or firechange */
        addlinkcallback;    /* callback to control link to add */

    /*
     * All these variables are initialized in init()
     */

    var self,
        force,              /* d3 layout force */
        div,                /* container div */
        svg,                /* svg element to add to div id="canvas" */
        drag_line,          /* line that appears when linking */
        g_nodes,            /* array of Graph.Nodes */
        links,              /* array of links between nodes */
        d3_links,           /* d3 selection of links */
        d3_nodes,           /* d3 selection of nodes */
        srcnode,            /* source node when linking */
        linking,            /* true if linking */
        model,              /* store json model (tojson) of the canvas */
        drag;               /* d3 drag (used for dragging nodes) */

    function init(id, parameters) {

        self = this;

        var p = parameters || {};

        width = p.width || WIDTH;
        height = p.height || HEIGHT;
        linkingenabled = _get(p.linkingenabled, true);
        linkbydefault = _get(p.linkbydefault, false);
        addlinkcallback = p.addlinkcallback;
        changehandler = p.changehandler;

        force = d3.layout.force()
            .size([width, height])
            .nodes([])
            .linkDistance(100)
            .charge(-800)
            .on("tick", tick);

        /*
         * Append svg to canvas
         */
        div = d3.select("#" + id);
        svg = div.append("svg")
            .attr("width", width)
            .attr("height", height)
            .on("mousemove", mousemove)
            .on("click", click);

        svg.append("rect")
            .attr("id", "#" + id + "-rect")
            .attr("width", width)
            .attr("height", height);

        /*
         * define arrow markers for graph links
         */
        svg.append('svg:defs').append('svg:marker')
            .attr('id', 'end-arrow')
            .attr('viewBox', '0 -5 10 10')
            .attr('refX', 6)
            .attr('markerWidth', 3)
            .attr('markerHeight', 3)
            .attr('orient', 'auto')
          .append('svg:path')
            .attr('d', 'M0,-5L10,0L0,5')
            .attr('fill', '#000');
        svg.append('svg:defs').append('svg:marker')
            .attr('id', 'start-arrow')
            .attr('viewBox', '0 -5 10 10')
            .attr('refX', 4)
            .attr('markerWidth', 3)
            .attr('markerHeight', 3)
            .attr('orient', 'auto')
          .append('svg:path')
            .attr('d', 'M10,-5L0,0L10,5')
            .attr('fill', '#000');

        /*
         * line displayed when dragging new nodes
         */
        drag_line = svg.append('svg:path')
            .attr('class', 'link dragline hidden')
            .attr('d', 'M0,0L0,0');

        g_nodes = force.nodes();
        links = force.links();

        /*
         * Append g to svg to store links
         */
        d3_links = svg.append("svg:g").attr("class", "linkgroup").selectAll(".link");

        /*
         * Append g to svg to store nodes
         */
        d3_nodes = svg.append("svg:g").attr("class", "nodegroup").selectAll("g");

        srcnode = undefined;
        linking = 0;

        drag = force.drag()
            .origin(function(d){
                return {x: d.x, y: d.y};
            })
            .on("dragstart", dragstart)
            .on("dragend", dragend)
            .on("drag", dragmove)
            .on("drag.force", null)
            ;

        /*
         * register key events
         */
        d3.select(window)
            .on('keydown', keydown)
            .on('keyup', keyup)
            .on("resize", resize);
        restart();
        model = tojson();
        log.debug("Canvas(" + width + "," + height + ") initialized");
    }

    function _get(value, defaultvalue) {
        return value !== undefined? value : defaultvalue;
    }

    function mousemove() {
        if(!linking) {
            return;
        }

        var mousex = d3.mouse(this)[0],
            mousey = d3.mouse(this)[1];

        // update drag line
        drag_line.classed('hidden', false);
        drag_line.attr(
            'd',
            'M' + srcnode.x + ',' + srcnode.y + 'L' + mousex + ',' + mousey
        );
    }


    function mousedown(d3node, d) {

        log.debug("mousedown " + "d3node="+d3node.label + " d=" + d);
        if (d3.event.shiftKey) {
            return;
        }
    }


    function mouseup(d3node, d) {
        /*
         * if linking, d3node is dest node, else src node.
         */
        log.debug("mouseup " + "d3node="+d3node.label + " d=" + d);

        if (linking && d3node !== srcnode) {
            var targetnode = d3node;
            var link = Object.create(Graph.Link).setup(srcnode, targetnode);
            if (addlinkcallback !== undefined) {
                addlinkcallback(self, link, function() {
                    self.addlink(link);
                    self.restart();
                });
            }
            else {
                addlink(link);
            }
            drag_line.classed("hidden", true);
            restart();
        }
    }


    function dragclick() {
        log.debug("dragclick");
    }


    function click() {
        log.debug("click");
    }


    function dragstart(selected) {
        var e = d3.event.sourceEvent;
        var modpressed = (e.shiftKey || e.ctrlKey);
        linking = linkingenabled &&
            ((linkbydefault && !modpressed) || (!linkbydefault && modpressed));

        var mousex = d3.mouse(this)[0],
            mousey = d3.mouse(this)[1];

        if(linking) {
            srcnode = selected;
            log.debug('linkstart - srcnode = ' + selected.label);
            return;
        }

        log.debug("dragstart");
        selected.fixed = true;
        g_nodes.forEach(function(node) {
            if (node !== selected) {
                node.fixed = false;
            }
        });
    }


    function dragmove(selected) {

        if (!linking) {
            log.debug("dragmove " + selected.label);
            selected.px += d3.event.dx;
            selected.py +=  d3.event.dy;
            restart();
        }
    }


    function dragend(selected) {

        var e = d3.event.sourceEvent;

        if(linking) {
            log.debug("dragend - linking");
            srcnode = undefined;
            drag_line.classed("hidden", true);
            linking = false;
            /*
             * firefox does not enter click when linking, so reset is done
             * here
             */
        }
        else {
            log.debug("dragend - moving");
        }
    }


    function keydown() {
    }


    function keyup() {
    }


    function tick() {

      d3_links.attr('d', function(d) {
        var deltaX = d.target.x - d.source.x,
            deltaY = d.target.y - d.source.y,
            dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY),
            normX = deltaX / dist,
            normY = deltaY / dist,
            sourcePadding = NODE_RADIUS,
            targetPadding = NODE_RADIUS + 4,
            sourceX = d.source.x + (sourcePadding * normX),
            sourceY = d.source.y + (sourcePadding * normY),
            targetX = d.target.x - (targetPadding * normX),
            targetY = d.target.y - (targetPadding * normY);
        return 'M' + sourceX + ',' + sourceY + 'L' + targetX + ',' + targetY;
      });

      d3_nodes.attr('transform', function(d) {
        return 'translate(' + d.x + ',' + d.y + ')';
      });
    }


    function resize(width, height) {
        if (width === undefined) {
            width = div.node().clientWidth;
            height = div.node().clientHeight;
        }
        svg
            .attr("width", width)
            .attr("height", height);
        svg.select("rect")
            .attr("width", width)
            .attr("height", height);
        force.size([width, height]).resume();

        return [ width, height ];
    }


    function restart() {

        /*
         * handle links
         */
        d3_links = d3_links.data(links, function(l) {
            return l.source.name + "-" + l.target.name;
        });

        var newlinks = d3_links.enter().append("svg:path");

        newlinks
            .attr("class", function(l) { return "link " + (l.type || "");})
            .style("marker-end", "url(#end-arrow)");

        newlinks.attr("data-popover", "true").each(function(d, i) {
            $(this).popover(
                {
                    'container' : 'body',
                    'placement' : 'auto right',
                    'title'     : function() {
                        return d.popovertitle(links.indexOf(d));
                    },
                    'content'   : function() {
                        return d.popovercontent(links.indexOf(d));
                    },
                    'html'      : true,
                    'trigger'   : 'manual'
                }
            );
        });
        newlinks.on("click", function(d, i) {
            var e = d3.event;

            if (e.defaultPrevented) {
                log.debug("link.click prevented");
                return; // click suppressed
            }

            log.debug("link.click");
            if(e.shiftKey || e.ctrlKey){
            }else{
                log.debug("Popover " + d.toString());
                var self = this;
                $('[data-popover]').each(function (i) {
                    $(this).not(self).popover('hide');
                });
                $(this).popover("toggle");
            }
            e.stopPropagation();
        });


        /*
         * remove old links
         */
        d3_links.exit().remove();

        /*
         * handle nodes
         *
         * TODO: Evaluate use second arg = function(d) { return d.name; }
         */
        var selection = svg.select("g.nodegroup").selectAll("g");
        var data = selection.data(g_nodes, function(n) { return n.name; });
        d3_nodes = data;

        data.select("text.nodelabel").text(function(d) { return d.label; });

        var newnodes = data.enter().append("svg:g");
        newnodes.append("circle")
            .attr("class", "node")
            .attr("r", NODE_RADIUS);

        newnodes.each(function(d, i) {
            var g_node = g_nodes[i];
            g_node.decorate(this, i);
        });

        newnodes.append("svg:text")
            .attr("x", 0)
            .attr("y", 30)
            .attr("class", "nodelabel")
            .text(function(d) { return d.label; });

        newnodes.attr("data-popover", "true").each(function(d, i) {
            $(this).popover(
                {
                    'container' : 'body',
                    'placement' : 'auto right',
                    'title'     : function() { return d.popovertitle(d.index); },
                    'content'   : function() { return d.popovercontent(d.index); },
                    'html'      : true,
                    'trigger'   : 'manual'
                }
            );
        });

        newnodes.on("click", function(d, i) {
            var e = d3.event;

            if (e.defaultPrevented) {
                log.debug("node.click prevented");
                return; // click suppressed
            }

            log.debug("node.click");
            if(e.shiftKey || e.ctrlKey){
            }else{
                log.debug("Popover " + d.toString());
                var self = this;
                $('[data-popover]').each(function (i) {
                    $(this).not(self).popover('hide');
                });
                $(this).popover('toggle');
            }
            e.stopPropagation();
        })
        .on("mousedown", function(d){
            mousedown.call(d3.select(this), d);
        })
        .on("mouseup", function(d){
            mouseup.call(d3.select(this), d);
        })
        .on('mousemove.drag', null);

        newnodes.call(force.drag);

        d3_nodes.each(function(d, i) {
            var g_node = g_nodes[i];
            g_node.update(this, i);
        });

        /*
         * remove old nodes
         */
        data.exit().remove();

        force.start();
    }

    function getnode(idx) {
        return g_nodes[idx];
    }


    function getnodebyname(name) {
        return _search(g_nodes, function(node) {
            return node.name === name;
        });
    }


    function getlink(id) {
        return links[id];
    }


    function getlinkbynodes(source, target) {
        return _search(links, function(link) {
            return link.source === source && link.target === target;
        });
    }

    function addnode(node) {
        _addnode(node);
        firechange();
    }

    function _addnode(node) {
        log.info("Adding node " + node.toString());
        g_nodes.push(node);
    }

    function addlink(link) {
        log.info("Adding link " + link.toString());
        _addlink(link);
    }

    function _addlink(link) {
        if (link) {
            links.push(link);
            firechange();
        }
    }

    function linknodes(source, target, type) {
        log.info("Adding link{source=" + source.label +
            " target=" + target.label + "}");
        var newlink = Object.create(Graph.Link).setup(source, target);
        newlink.type = type;
        addlink(newlink);

        return newlink;
    }


    function _search(array, filter) {
        for (var i = 0; i < array.length; i++) {
            var item = array[i];

            if (filter(item)) {
                return item;
            }
        }
    }


    function _remove(array, filter) {
        for (var i = 0; i < array.length; ) {
            var item = array[i];

            if (filter(item)) {
                array.splice(i, 1);
            }
            else {
                i++;
            }
        }
    }

    function removenode(node) {
        log.info("Removing node " + node.label);

        _remove(links, function(link) {
            return (link.source === node || link.target === node);
        });

        _remove(g_nodes, function(n) {
            return n === node;
        });
        firechange();
        restart();
    }

    function removelink(link) {
        log.info("Removing link [source=" + link.source.label +
                ", target=" + link.target.label + "]");
        _remove(links, function(l) {
            return l === link;
        });
        firechange();
        restart();
    }

    /*
     * Notify externally to the canvas that a node/link has changed
     */
    function firechange() {
        /*
         * Internally, this method is used also on other change methods:
         * addnode, removenode, addlink, removelink
         */
        model = tojson();
        if (changehandler !== undefined) {
            changehandler(model);
        }
    }

    var tojson = function() {
        var result = {
            nodes: [],
            links: [],
        };
        for (var i = 0; i < g_nodes.length; i++) {
            var node = g_nodes[i].toJson();
            node.behaviour = g_nodes[i].behaviour;
            result.nodes.push(node);
        }
        for (var i = 0; i < links.length; i++) {
            var link = links[i].toJson();
            link.behaviour = links[i].behaviour;
            result.links.push(link);
        }
        return result;
    };

    var fromjson = function(json, typeMap) {
        g_nodes.length = 0;
        links.length = 0;
        var nodes = [];
        var nodesmap = {};
        for (var i = 0; i < json.nodes.length; i++) {
            var jsonnode = json.nodes[i];
            var node = nodefromjson(jsonnode, typeMap);
            _addnode(node);
            nodesmap[node.name] = node;
        }

        for (var i = 0; i < json.links.length; i++) {
            var jsonlink = json.links[i];
            var link = linkfromjson(jsonlink, nodesmap);
            _addlink(link);
        }
        firechange();
    };

    var nodefromjson = function(json, typeMap) {
        var prototype = typeMap[json.type];
        var node = Object.create(prototype);
        node.init( { name: json.name, label: json.label, type: json.type });
        node.properties = json.properties;
        node.behaviour = json.behaviour;
        return node;
    };

    var linkfromjson = function(json, nodeMap) {
        var link = Object.create(Graph.Link);
        var source = nodeMap[json.source];
        var target = nodeMap[json.target];

        if (source === undefined) {
            log.error("Node[name=" + json.source + "] not found");
        }
        if (target === undefined) {
            log.error("Node[name=" + json.target + "] not found");
        }
        link.setup(source, target);
        link.properties = json.properties;
        link.behaviour = json.behaviour;

        return link;
    };

    return {
        init: init,
        restart: restart,
        getnode: getnode,
        getnodebyname: getnodebyname,
        getlink: getlink,
        getlinkbynodes: getlinkbynodes,
        addnode: addnode,
        addlink: addlink,
        linknodes: linknodes,
        removenode: removenode,
        removelink: removelink,
        tojson: tojson,
        fromjson: fromjson,
        resize: resize,
        firechange: firechange,
        nodes: function() { return g_nodes.slice(); },
        links: function() { return links.slice(); }
    };
});
