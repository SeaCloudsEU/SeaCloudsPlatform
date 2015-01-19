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
/**
 * This is a proof of concept of topology editor.
 * 
 * It assumes there is an element with id "canvas" in the document.
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

var Canvas = (function() {

    var WebApplication = Object.create(Graph.WebApplication);
    var Database = Object.create(Graph.Database); 
    var RestService = Object.create(Graph.RestService); 
    
    Graph.Node.popovertitle = function() {
        return this.name;
    };
    
    Graph.Node.popovercontent = function() {
        function item(key, value) {
            var result = "";
            if (value !== undefined && value !== "") {
                result = "<dt>" + key + "</dt><dd>" + value + "</dd>";
            }
            return result;
        };

        var location = this.location;
        if (location !== undefined && location !== "") {
            location = location + " - " + this.location_option;
        }
        
        var terms = [
            [ "Type", this.type],
            [ "Name", this.name],
            [ "Category", this.category],
            [ "Language", this.language],
            [ "Cost", this.cost],
            [ "Location", location],
            [ "QoS", this.qos],
            [ "Infrastructure", this.infrastructure]
        ];
        
        var content = "";
        for (i in terms) {
            term = terms[i];
            content += item(term[0], term[1]);
        }
        return "<dl>" + content + "</dl>";
    };

    /*
     * http://stackoverflow.com/questions/18416749/adding-fontawesome-icons-to-a-d3-graph
     */

    WebApplication.decorate = function(dom_element) {
        d3_element = d3.select(dom_element);
        d3_element.append("svg:text")
            .attr("x", 0)
            .attr("y", 9)
            .attr("class", "type-icon")
            .text("\uf0ac");
    };
    
    Graph.Database.decorate = function(dom_element) {
        d3_element = d3.select(dom_element);
        d3_element.append("svg:text")
            .attr("x", 0)
            .attr("y", 9)
            .attr("class", "type-icon")
            .text("\uf1c0");
    };

    Graph.RestService.decorate = function(dom_element) {
        d3_element = d3.select(dom_element);
        d3_element.append("svg:text")
            .attr("x", 0)
            .attr("y", 10)
            .attr("class", "type-icon")
            .text("\uf013");
        d3_element.append("svg:text")
            .attr("x", 0)
            .attr("y", 4)
            .attr("class", "type")
            .text("REST");
    };
    
    var WIDTH = 960,
        HEIGHT = 500;
        
    var NODE_RADIUS = 20;
        
    var fill = d3.scale.category20();
    
    var log = Log.getLogger("Canvas").setLevel(Log.DEBUG);
    
    var force = d3.layout.force()
        .size([WIDTH, HEIGHT])
        .nodes([])
        .linkDistance(100)
        .charge(-800)
        .on("tick", tick);
    
    var svg = d3.select("#canvas").append("svg")
        .attr("width", WIDTH)
        .attr("height", HEIGHT)
        .on("mousemove", mousemove)
        // .on("mousedown", mousedown)
        .on("click", click);
    
    svg.append("rect")
        .attr("width", WIDTH)
        .attr("height", HEIGHT);
    
    // define arrow markers for graph links
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
        
    
    // line displayed when dragging new nodes
    var drag_line = svg.append('svg:path')
        .attr('class', 'link dragline hidden')
        .attr('d', 'M0,0L0,0');
      
    var g_nodes = force.nodes(),
        links = force.links();
        
    var counter = g_nodes.length;
        
    var link = svg.append("svg:g").selectAll(".link");
    var node = svg.append("svg:g").selectAll("g");
    
    var srcnode = undefined;
    var linking = 0;
    /*
    var cursor = svg.append("circle")
        .attr("r", 30)
        .attr("transform", "translate(-100,-100)")
        .attr("class", "cursor");
    */
    
    var drag = force.drag()
        .origin(function(d){
            return {x: d.x, y: d.y};
        })
        .on("dragstart", dragstart)
        .on("dragend", dragend)
        .on("drag", dragmove)
        .on("drag.force", null)
        ;
    
    d3.select(window)
        .on('keydown', keydown)
        .on('keyup', keyup);
    
    restart();
    
    
    function mousemove() {
        if(!linking) {
            return;
        }
    
        var mousex = d3.mouse(this)[0],
            mousey = d3.mouse(this)[1];
            
        // update drag line
        drag_line.attr(
            'd', 
            'M' + srcnode.x + ',' + srcnode.y + 'L' + mousex + ',' + mousey
        );
    
        restart();
    }
    
    
    function mousedown(d3node, d) {
    
        log.debug("mousedown " + "d3node="+d3node.label + " d=" + d);
        if (d3.event.shiftKey) {
            drag_line.classed("hidden", false);
                // .attr('d', 'M' + d.x + ',' + d.y + 'L' + d.x + ',' + d.y);
            return;
        }
        // restart();  
    }
    
    
    function mouseup(d3node, d) {
        /*
         * if linking, d3node is dest node, else src node.
         */
        log.debug("mouseup " + "d3node="+d3node.label + " d=" + d);
        
        if (linking && d3node !== srcnode) {
            
            links.push({source: srcnode, target: d3node});
            drag_line.classed("hidden", true);
            restart();
        }
    }
    
    
    function dragclick() {
        log.debug("dragclick");
    }
    
    
    function click() {
        /*
         * Couldn't manage to defaultPrevent on linking.
         */
        if (d3.event.defaultPrevented) {
            log.debug("click prevented");
            linking = false;
            return; // click suppressed
        }
        log.debug("click");
    }
    
    
    function dragstart(selected) {
        var e = d3.event.sourceEvent;
    
        if(e.shiftKey || e.ctrlKey) {
            linking = true;
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
        
        var e = d3.event;
        
        // if (e.keyCode === 17) {
            // node
                // .on('mousedown.drag', null)
                // .on('touchstart.drag', null);
            // linking = true;
        // }
    }
    
    
    function keyup() {
        var e = d3.event;
        
        if (e.keyCode === 17) {
            // node.call(force.drag);
        }
    }
    
    
    function tick() {
        
      link.attr('d', function(d) {
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
        
      // link.attr("x1", function(d) { return d.source.x; })
          // .attr("y1", function(d) { return d.source.y; })
          // .attr("x2", function(d) { return d.target.x; })
          // .attr("y2", function(d) { return d.target.y; });
    
      node.attr('transform', function(d) {
        return 'translate(' + d.x + ',' + d.y + ')';
      });
    }
    
    
    function restart() {
        
        /*
         * handle links
         */
        link = link.data(links);
    
        link.enter().append("svg:path")
            .attr("class", "link")
            .style("marker-end", "url(#end-arrow)");
    
        // remove old links
        link.exit().remove();
    
        /*
         * handle nodes
         * 
         * TODO: Evaluate use second arg = function(d) { return d.name; }
         */
        node = node.data(g_nodes);
    
        newnodes = node.enter().append("svg:g");
        newnodes.append("circle")
            .attr("class", "node")
            .attr("r", NODE_RADIUS);

        newnodes.each(function(d, i) {
            var g_node = g_nodes[i];
            g_node.decorate(this);
        });
        // newnodes.append("svg:text")
            // .attr("x", 0)
            // .attr("y", 4)
            // .attr("class", "type")
            // .text(function(d) { return d.kindlabel(); });
            
        newnodes.append("svg:text")
            .attr("x", 0)
            .attr("y", 30)
            .text(function(d) { return d.label; });
    
        newnodes.each(function(d, i) {
            $(this).popover(
                {
                    'container' : 'body',
                    'placement' : 'auto right',
                    'title'     : function() { return d.popovertitle(i); },
                    'content'   : function() { return d.popovercontent(); },
                    // 'title'     : d.popovertitle(i),
                    // 'content'   : d.popovercontent(),
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
                $(this).popover("toggle");
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
    
        // remove old nodes
        node.exit().remove();
    
        force.start();
    }

    return {
        nodes: g_nodes,
        restart: restart,
        WebApplication: WebApplication,
        Database: Database,
        RestService: RestService
    };
})();
