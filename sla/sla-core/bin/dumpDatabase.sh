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

if [ "$0" != "bin/dumpDatabase.sh" ]; then
	echo "Must be executed from project root"
	exit 1
fi

function get_var() {
  local result

  result=$(grep "$1" configuration.properties | sed -e 's/.*= *\(.*\) *$/\1/') 
  echo $result
}

DB=$(get_var "db.name")
USER=$(get_var "db.username")
PWD=$(get_var "db.password")

mysqldump -d -p -u "$USER" "$DB" > sla-repository/src/main/resources/sql/10schema.sql
