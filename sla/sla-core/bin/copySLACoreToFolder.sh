#!/usr/bin/env bash
#
# Copyright 2014 Atos
# Contact: Atos <roman.sosa@atos.net>
#
#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.
#

#
# Copy slacore project to another location.
#
# The following tasks are performed:
#   copy source tree to $1
#     from sla-personalization only pom.xml and src/main/resources/personalization-context.xml
#   rename groudId to $2
#
# Usage: 
#   $0 <dir> <groupId>
#     <dir>: destination directory; parent pom.xml will be here
#     <groupId>: if specified, change the groupId of slacore projects to this value.
#
if [ "$0" != "bin/copySLACoreToFolder.sh" ]; then
	echo "Must be executed from project root"
	exit 1
fi

if [ $# -lt 1 ]; then
	echo "Usage: $0 <destdir> [<groupId>]"
	exit 1
fi

if [ -e "$1" ]; then
	echo "$1 exists; exiting"
	exit 1
fi

### copy source tree ###
echo -e "Starting copy: \n  source=$(pwd)\n  dest=$1"

EXCLUDE=$(cat << 'EOF'
target
.*
configuration.properties
sla-personalization
EOF
)

echo "$EXCLUDE" | rsync -a --exclude-from=- ./* "$1"

# sla-personalization is an special case
res="sla-personalization/src/main/resources"
mkdir -p "$1/$res"
cp "sla-personalization/pom.xml" "$1/sla-personalization"
cp "$res/personalization-context.xml" "$1/$res"

### rename groupId ###
if [ -n "$2" ]; then
	echo "Renaming groupId to $2"
	find "$1" -name pom.xml -exec sed -i -e"s/<groupId>eu.atos.sla<\/groupId>/<groupId>$2<\/groupId>/" {} \;
fi
