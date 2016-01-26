JSON='{
    "format": "INFLUXDB",
    "protocol": "HTTP",
    "callbackUrl": "http://'$MODACLOUDS_TOWER4CLOUDS_INFLUXDB_IP':'$MODACLOUDS_TOWER4CLOUDS_INFLUXDB_PORT'"
    }'

echo "$JSON" | curl -X POST -i -H "Content-type: application/json" http://"$MODACLOUDS_TOWER4CLOUDS_MANAGER_IP":8170/v1/metrics/AvarageResponseTime_"$MODULE_ID"/observers -d @-
