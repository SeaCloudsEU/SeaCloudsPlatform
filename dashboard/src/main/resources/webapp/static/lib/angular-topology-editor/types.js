var Types = function() {

    /*
     * http://stackoverflow.com/questions/18416749/adding-fontawesome-icons-to-a-d3-graph
     */
    Graph.Node.decorate = function(dom_element) {
        var proto = Object.getPrototypeOf(this);
        var d3_element = d3.select(dom_element);
        d3_element.append("svg:text")
            .attr("x", 0)
            .attr("y", 10)
            .attr("class", "type-icon")
            .text(this.icon? this.icon : "\uf013");
        d3_element.append("svg:text")
            .attr("x", 0)
            .attr("y", 4)
            .attr("class", "type")
            .text(this.icon? "" : proto.label);
    };

    Graph.Node.update = function(dom_element) {
        var d3_element = d3.select(dom_element);

        d3_element.select("circle.node").
            attr("class", "node " + this.properties.status);

        d3_element.select(".nodelabel").text(this.label);
    };


    var Database = Object.create(Graph.Node).init({
        name : "Database",
        label : "DB",
        type : "Database",
        icon : "\uf1c0",
    });


    var BasicApplication = Object.create(Graph.Node).init({
        name : "BasicApplication",
        label : "app",
        type : "BasicApplication",
        icon : "\uf1b3",
    });


    var Host = Object.create(Graph.Node).init({
        name : "Host",
        label : "host",
        type : "Host",
        icon : "\uf233",
    });

    var WebApplication = Object.create(Graph.Node).init({
        name : "WebApplication",
        label : "www",
        type : "WebApplication",
        icon : "\uf0ac",
    });


    var RestService = Object.create(Graph.Node).init({
        name : "REST Service",
        label : "REST",
        type : "RestService",
    });


    var NoSql = Object.create(Graph.Node).init({
        name : "NoSQL Database",
        label : "NoSQL",
        type : "NoSQL",
    });

    var ApplicationServer = Object.create(Graph.Node).init({
        name : "Application Server",
        label : "AS",
        type : "ApplicationServer"
    });

    var Tomcat = Object.create(ApplicationServer).init({
        name : "Tomcat",
        label : "Tomcat",
        type : "Tomcat"
    });

    var Apache = Object.create(ApplicationServer).init({
        name : "Apache",
        label : "Apache",
        type : "Apache"
    });

    var Nginx = Object.create(ApplicationServer).init({
        name : "Nginx",
        label : "Nginx",
        type : "Nginx"
    });

    var Cloud = Object.create(Graph.Node).init({
        name : "Cloud",
        label : "Cloud",
        type : "Cloud",
        icon : "\uf0c2",
        identity : function() {
            return this.properties.apikey || this.properties.user || "";
        },
        credential: function() {
            return this.properties.password || "";
        }
    });

    var Module = Object.create(Graph.Node).init({
        name : "Module",
        label : "Module",
        type: "Module",
        icon : "\uf013"
    });

    return {
        BasicApplication: BasicApplication,
        ApplicationServer: ApplicationServer,
        WebApplication: WebApplication,
        Tomcat: Tomcat,
        Apache: Apache,
        Nginx: Nginx,
        Database: Database,
        RestService: RestService,
        NoSql : NoSql,
        Cloud: Cloud,
        Host : Host,
        Module : Module
    };
}();
