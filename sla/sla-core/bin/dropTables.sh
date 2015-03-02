#
# Copyright 2015 SeaClouds
# Contact: SeaClouds
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

DROP=$(mysql -p"_atossla_" -u atossla atossla <<< "show tables" | grep -v "Tables" | sed -e's/\(.*\)/drop table \1; /')
SQL=$(echo "SET FOREIGN_KEY_CHECKS=0;" && echo $DROP)
echo "$SQL" | mysql -p -u atossla atossla
