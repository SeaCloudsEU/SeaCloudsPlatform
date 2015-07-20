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

function get_var() {
  local result

  result=$(grep "$1" configuration.properties | sed -e 's/.*= *\(.*\) *$/\1/') 
  echo $result
}

DB=$(get_var "db.name")
USER=$(get_var "db.username")
PWD=$(get_var "db.password")

DROP=$(mysql -p"$PWD" -u "$USER" "$DB" <<< "show tables" | grep -v "Tables" | sed -e's/\(.*\)/drop table \1; /')
SQL=$(echo "SET FOREIGN_KEY_CHECKS=0;" && echo $DROP)
echo "$SQL" | mysql -p -u "$USER" "$DB"
