#!/bin/bash
set -u
[ -z "$BROOKLYN_HOME" ] && { echo "Need to set BROOKLYN_HOME"; exit 1; }

echo "Copy additional entities to ${BROOKLYN_HOME}/lib/dropins"
wget -q -O ${BROOKLYN_HOME}/lib/dropins/brooklyn-modaclouds-0.1.0-SNAPSHOT.jar "http://oss.sonatype.org/service/local/artifact/maven/redirect?r=snapshots&g=eu.seaclouds-project&a=brooklyn-modaclouds&v=LATEST" --content-disposition
echo "Copied!"

DIR=$(cd "$(dirname "$0")" && pwd)
BLUEPRINT="$DIR/seaclouds.yaml"
PATTERN="s#\(privateKeyFile:\).*\$#\1 $DIR/seaclouds_id_rsa#"
if [ -e "$BLUEPRINT" ];then
    sed -i -e "$PATTERN" "$BLUEPRINT"
    echo "Set 'privateKeyFile: $DIR/seaclouds_id_rsa' in '$BLUEPRINT'"
else
    echo "Blueprint $BLUEPRINT doens't exist";
fi
