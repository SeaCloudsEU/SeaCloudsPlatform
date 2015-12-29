sudo apt-get update
sudo apt-get install unzip
sudo apt-get install openjdk-7-jre
wget -O data-collector-2.0.4.jar "https://github.com/imperial-modaclouds/modaclouds-data-collectors/releases/download/2.0.4/data-collector-2.0.4.jar"
wget -O hyperic-sigar-1.6.4.zip "http://sourceforge.net/projects/sigar/files/sigar/1.6/hyperic-sigar-1.6.4.zip/download?use_mirror=switch"
unzip hyperic-sigar-1.6.4.zip

JSON='{
    "format": "INFLUXDB",
    "protocol": "HTTP",
    "callbackUrl": "http://'$MODACLOUDS_TOWER4CLOUDS_INFLUXDB_IP':'$MODACLOUDS_TOWER4CLOUDS_INFLUXDB_PORT'"
    }'

echo "$JSON" | curl -X POST -i -H "Content-type: application/json" http://"$MODACLOUDS_TOWER4CLOUDS_MANAGER_IP":8170/v1/metrics/AverageCpuUtilization_"$MODACLOUDS_TOWER4CLOUDS_VM_TYPE"/observers -d @-
echo "$JSON" | curl -X POST -i -H "Content-type: application/json" http://"$MODACLOUDS_TOWER4CLOUDS_MANAGER_IP":8170/v1/metrics/AverageRamUtilization_"$MODACLOUDS_TOWER4CLOUDS_VM_TYPE"/observers -d @-

nohup java -Djava.library.path=./hyperic-sigar-1.6.4/sigar-bin/lib/ -jar data-collector-2.0.4.jar tower4clouds > dc.out 2>&1 &

