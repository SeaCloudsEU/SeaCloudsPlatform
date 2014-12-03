<!DOCTYPE html>
<html>

<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>SeaClouds Dashboard - Deployer</title>

    <!-- Core CSS - Include with every page -->
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/font-awesome.min.css" rel="stylesheet">


    <!-- SB Admin CSS - Include with every page -->
    <link href="css/sb-admin.css" rel="stylesheet">

    <!-- SeaClouds configuration constants -->
    <script src="js/config.js"></script>


</head>

<body>

<div id="wrapper">

    <nav class="navbar navbar-default navbar-fixed-top" role="navigation"
         style="margin-bottom: 0">
        <div class="navbar-header">
            <a class="navbar-brand extrnLink"
               href="http://www.seaclouds-project.eu/"> <img
                    class="img-responsive " src="img/seaclouds-header.png"
                    style="max-width: 100px; margin-top: -18px;" alt="SeaClouds project main website">
            </a>
            <button type="button" class="navbar-toggle" data-toggle="collapse"
                    data-target=".sidebar-collapse">
                <span class="sr-only">Toggle navigation</span> <span
                    class="icon-bar"></span> <span class="icon-bar"></span> <span
                    class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="index.html">SeaClouds Dashboard</a>
        </div>
        <!-- /.navbar-header -->

        <!-- TODO: Get alert number from Monitoring API -->
        <ul class="nav navbar-top-links navbar-right">

            <li id ="loading-spinner"></li>
            <li class="divider"></li>
            <li class="divider"></li>
            <li class="divider"></li>


            <li class="dropdown"><a class="dropdown-toggle"
                                    data-toggle="dropdown" href="#"><span class="badge">0</span><i
                    class="fa fa-bell fa-fw"></i> <i class="fa fa-caret-down"></i> </a>
                <ul class="dropdown-menu dropdown-alerts">
                    <li><a href="#">
                        <div>
                            <i class="fa fa-exclamation fa-fw"></i>This feature will be
                            available in prior releases<span
                                class="pull-right text-muted small">Just now</span>
                        </div>
                    </a></li>
                    <li class="divider"></li>

                    <!-- TODO: Open all alerts page -->
                    <li class="disabled"><a class="text-center" href="#"> <strong>See
                        All Alerts</strong> <i class="fa fa-angle-right"></i>
                    </a></li>
                </ul>
            </li>
            <!-- /.dropdown-alerts -->
            <!-- /.dropdown -->
            <li class="dropdown"><a class="dropdown-toggle"
                                    data-toggle="dropdown" href="#"> <i class="fa fa-user fa-fw"></i>
                <i class="fa fa-caret-down"></i>
            </a>
                <ul class="dropdown-menu dropdown-user">
                    <li class="disabled"><a href="#"><i
                            class="fa fa-user fa-fw"></i>User Profile</a></li>
                    <li class="disabled"><a href="#"><i
                            class="fa fa-gear fa-fw"></i>Settings</a></li>
                    <li class="divider"></li>
                    <li><a href="#"><i class="fa fa-exclamation fa-fw"></i>
                        This feature will be available in prior releases</a></li>
                </ul>
                <!-- /.dropdown-user --></li>
            <!-- /.dropdown -->
        </ul>
        <!-- /.navbar-top-links -->

        <div class="navbar-default navbar-static-side" role="navigation">
            <div class="sidebar-collapse">
                <ul class="nav" id="side-menu">
                    <li><a href="index.html"><i class="fa fa-dashboard fa-home"></i>&nbsp;Home</a></li>
                    <li><a href="not-available.html" class=""><i class="fa fa-pencil-square-o"></i>&nbsp;Module Profile Designer</a></li>
                    <li><a href="discoverer-and-planner.html" class=""><i class="fa fa-code-fork"></i>&nbsp;Discoverer & Planner</a></li>
                    <li><a href="deployer.jsp" class=""><strong><i class="fa fa-download"></i>&nbsp;Deployer</strong></a></li>
                    <li><a href="monitor.jsp" class=""><i class="fa fa-dashboard"></i>&nbsp;Monitor</a></li>
                    <li><a href="sla.html" class=""><i class="fa fa-file-text-o"></i>&nbsp;SLA Service</a></li>

                </ul>
                <!-- /#side-menu -->
            </div>
            <!-- /.sidebar-collapse -->
        </div>
        <!-- /.navbar-static-side -->
    </nav>

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header">
                    Deployer
                    <small>Manage applications</small>
                </h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>
        <!-- /.row -->
        <div class="row" id="page-content">

            <div class="col-lg-12">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <i class="fa fa-cloud-download"></i> Deploy new application defined in YAML format.
                        <a aria-expanded="false" data-toggle="collapse" data-parent="#accordion" data-target ="#app-status-collapsable"><i class="fa fa-chevron-down"></i></a>
                    </div>
                    <!-- /.panel-heading -->
                    <div class="panel-collapse collapse  " id="app-status-collapsable" >
                        <div class="panel-body" id="yaml-input-panel-body">
                            <form role="form" onreset="resetForm()" onsubmit="submitYaml()">

                                <div class="form-group" id="text-form-group">
                                    <label for="yaml-input-textarea">Paste your YAML here</label>
                                    <textarea id="yaml-input-textarea" class="form-control" rows="3" onchange="switchFormInputTo('yaml-input-textarea')"></textarea>
                                </div>


                                <div class="form-group" id="file-form-group">
                                    <label for="yaml-input-file">Choose a YAML File</label>
                                    <input type="file" id="yaml-input-file" onchange="switchFormInputTo('yaml-input-file')">
                                </div>

                                <div class="pull-right">
                                    <button type="reset" class="btn btn-default">Clear</button>
                                    <button type="submit" class="btn btn-primary">Submit</button>
                                </div>

                            </form>
                        </div>
                    </div>

                </div>
            </div>
            <div class="col-lg-12" id="deployed-applications">
            </div>
        </div>
    </div>

</div>

<!-- Core Scripts - Include with every page -->
<script src="js/lib/jquery-1.10.2.js"></script>
<script src="js/lib/jquery.metisMenu.js"></script>
<script src="js/lib/bootstrap.min.js"></script>
<script src="js/lib/spin.min.js"></script>

<!-- SB Admin Scripts - Include with every page -->
<script src="js/sb-admin.js"></script>


<script type="text/javascript">
    var SPINNER = new Spinner({lines: 13, length: 6, width: 2, radius: 5, top: "-5px"}).spin(document.getElementById("loading-spinner"));
    var CONTENT_ID = "deployed-applications";

    $(document).ready(function() {
        updatePage();
        setInterval(updatePage, 3000);
    });

    function updatePage(){
        SPINNER.spin(document.getElementById("loading-spinner"));
        displayApplicationOverview();
    }

    // Deployed applications

    function displayApplicationOverview(){
        var boxHTML = "";
        $.get("servlets/listApplications", function getApplications(response) {
            if (response.length > 0) {
                $.each(response, function (appIdx, app) {
                    // FIXME: The servlet provides all the location stuff
                    boxHTML += generateAppOverviewBox(app);
                })
            } else {
                boxHTML = "<h1 class=\"text-center text-warning\">No applications running.</h1>";
            }
        }).done(function(){
            $('#' + CONTENT_ID).html(boxHTML);
            SPINNER.stop();
        }).fail(function(){
            boxHTML = "<h1 class=\"text-center text-danger\">Deployer module is not available in this moment.</h1>";
            $('#page-content').html(boxHTML)
            SPINNER.stop();
        });;
    }

    function generateAppOverviewBox(application) {
        // External container
        var appHTML = "<div class=\"col-lg-6\"><div class=\"panel panel-default\">";

        // Header
        appHTML += "<div class=\"panel-heading clearfix\">";
        appHTML += "<i class=\"fa fa-gears fa-fw\"><\/i>" + application.id +
                "<button type=\"button\" class=\"btn btn-danger navbar-right\" onClick=deleteApp('"+ application.id  + "')>Remove</button>";
        appHTML += "</div>";

        // Body
        appHTML += "<div class=\"panel-body\" id=\"information-panel\">";
        appHTML += "<strong>Type: </strong> " +  application.spec.type  + "<br>";
        appHTML += "<strong>Name: </strong> " +  application.spec.name + "<br>";
        appHTML += "<strong>Status: </strong> " +  application.status + "<br>";
        appHTML += "<strong>Locations: </strong> ";
        $.each( application.spec.locations, function(idx, location){
            appHTML += location.name + " ";
        })
        appHTML +="<br>";
        appHTML += "</div>";

        // External container
        appHTML += "</div>";
        appHTML += "</div>";

        return appHTML
    }


    function deleteApp(applicationId){
        // http://stackoverflow.com/a/15089299

        $.ajax({
            url: "servlets/removeApplication" + "?" + $.param({application : applicationId}),
            type: 'DELETE',
            success: function(result) {
                console.log(res);
                updatePage();
            }
        });

    }


    // Deployer new app form

    var CURRENT_FORM_INPUT = undefined;
    function resetForm(){
        $("#yaml-input-file").prop('disabled', false);
        $("#yaml-input-textarea").prop('disabled', false);

    }

    function switchFormInputTo(enabledForm){
        CURRENT_FORM_INPUT = enabledForm;
    }

    function submitYaml(){
        if(CURRENT_FORM_INPUT == "yaml-input-textarea") {
            $.post("servlets/addAplication", {yaml: $("#yaml-input-textarea").val()}).done(function(res) {
                console.log(res);
            });

        } else {
            var unparsedFile = $("#yaml-input-file").prop('files')[0];
            var reader = new FileReader();

            reader.onload = function(theFile) {
                $.post("servlets/addAplication", {yaml: theFile.target.result}).done(function(res) {
                    console.log(res);
                });

            }

            reader.readAsText(unparsedFile);
        }
    }


</script>
</body>


</html>
