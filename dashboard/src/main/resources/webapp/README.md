# Monitoring Platform for Pisa Meeting. NOT FOR RELEASE.

![](dashboard-capture.jpg)

This dashboard is a proof of concept of SeaClouds Dashboard. 
This is a working progress release, a lot of bugs expected.


**NOTE:** BROOKLYN_ENDPOINT must be changed prior using it outside UMA Network.
**NOTE:** Sensor choice is not dynamic. It written directly in the app_monitor.html code,
also the array of sensor names, must match de entity order. For example:

```javascript
sensorNames = ["webapp.reqs.perSec.windowed", "mysql.queries.perSec.fromMysql"];
```

##SLA##

The sla tab renders sla-dashboard, a django project to show agreements and their status. 

**NOTE:** SLADASHBOARD_ENDPOINT (in js/config.js) must be changed.
**NOTE:** To avoid cross-domain issues, it is recommended to redirect /slagui and /static/slagui on the demo-dashboard server to the actual server.

An nginx configuration is below as an example:

    [...]
    http {
        include       mime.types;
        default_type  application/octet-stream;
    
        sendfile        on;
    
        server {
            listen       8082;
            server_name  localhost;
    
            location / {
                root   ~/projects/seaclouds/demo-dashboard;
                index  index.html index.htm;
            }
    
            location /slagui {
                proxy_pass  http://127.0.0.1:8000;
            }
    
            location /static/slagui {
                proxy_pass  http://127.0.0.1:8000;
            }
        }
    }
    [...]
