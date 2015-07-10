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
# sla-core MUST be running with env var ENFORCEMENT_TEST=1
#

SLA_MANAGER_URL="http://localhost:8080/sla-service"
XML="application/xml"
JSON="application/json"
in_case=0

		#####

#shopt -s expand_aliases
set -o history -o histexpand
#alias curl='/usr/bin/curl --trace-ascii /tmp/curl.out '

if [ "$0" != "bin/generate-appendix.sh" ]; then
	echo "Must be executed from project root"
	exit 1
fi
>&2 bin/restoreDatabase.sh

debug() {
	[[ -n $DEBUG ]] && echo "$*" >&2
}


function curl_cmd() {
	in_case=1
	CMD="/usr/bin/curl -u user:password ""$@"

	[[ -n $DEBUG ]] && set -o xtrace
	/usr/bin/curl --trace-ascii /tmp/curl.out -u user:password "$@" > /dev/null 2>&1
	local status=$?
	[[ -n $DEBUG ]] && set +o xtrace
	if [ "$?" != "0" ]; then
		return "-1"
	fi

	debug "$code"

	return $status
}

function assert() {
	#$1 expected

	local actual=$(cat /tmp/curl.out | grep -v "^..... HTTP\/1.1 100" | awk "/^.{5} HTTP\/1.1/ {print \$3;}")
	debug "assert(expected:'$1' actual:'$actual')"
	if [ -z "$expected" ]; then
		exit 0
	fi

	out=$(echo "$actual" | grep "$1")
	if [ "$?" = "0" ]; then
		return $code
	else
		echo "ERROR: Expected: $1. Actual: $actual"
		exit $code
	fi
}

function curl_post() {
	#
	# $1: relative url
	# $2: file to send
	# $3: expected code (optional)
	# $4: content type header (optional)
	# $5: accept header (optional)
	md_text "Content type: $4\n"

	local expected=${3:-""}
	local type_header=${4:+-H Content-type:$4}
	local accept_header=${5:+-H Accept:$5}
	local url="$SLA_MANAGER_URL/$1"
	curl_cmd "-d@samples/appendix/$2" -X POST $type_header $accept_header "$url"

	assert "$expected"

	md_rest
	return $code
}

function curl_put() {
	# $1: relative url
	# $2: file to send
	# $3: expected code (optional)
	# $4: content type header (optional)
	md_text "Content type: $4\n"

	local expected=${3:-""}
	local url="$SLA_MANAGER_URL/$1"
	local file_param=${2:+-d@samples/appendix/$2}
	local type_header=${4:+-H Content-type:$3}
	curl_cmd -X PUT $file_param $type_header "$url"
	
	assert "$expected"

	md_rest
	return $code
}

function curl_get() {
	# $1: relative url
	# $2: query string (without ?)
	# $3: expected code (optional)
	# $4: accept header (optional)
	md_text "Accept: $3\n"

	local expected=${3:-""}
	local url="$SLA_MANAGER_URL/$1?$2"
	local accept_header=${4:+-H Accept:$4}
	curl_cmd -X GET $accept_header "$url"
	
	assert "$expected"

	md_rest
	return $code
}

function curl_delete() {
	# $1: relative url
	# $2: expected code (optional)
	md_text "\n"

	local expected=${2:-""}
	local url="$SLA_MANAGER_URL/$1"
	local accept_header="-H Accept:$XML"
	curl_cmd -X DELETE $accept_header "$url"
	
	assert "$expected"

	md_rest
	return $code
}

function md_rest() {
	local out=$(grep -e "^[0-9a-f]\{1,4\}:" /tmp/curl.out | sed -e's/^.\{5\} /\t/')

	local request=$(
	echo "$out" | awk -e'BEGIN{flag=1}/^\tHTTP\/1./{flag=0}{if (flag && ($0 !~ /(^\t?$)|Expect: 100/)) print;}'
	)

	local response=$(
		echo "$out" | awk -e'BEGIN{flag=0}/^\tHTTP\/1\.. [^1]/{flag=1}{if (flag && $0 !~ /^\t?0?$/) print;}'
	)

	echo -e "\t\$ $CMD"
	echo
	echo "$request"
	echo
	echo "$response"
	echo 
	#get_status
}

function md_text() {
	[ "$in_case" = "1" ] && md_hr
	echo -e "$@"
	in_case=0
}

function md_hr() {
	echo "---"
	echo
}

function md_title() {
	# $1: importance
	# $2: title
	# $3: anchor
	in_case=0
	local hashes="#"
	for (( i = 1; i < $1; i++ )); do
		hashes="$hashes#"
	done
	local anchor=${3:+<a name=\"$3\"></a>}
	echo "$hashes$2$anchor$hashes"
	echo
}

function get_status() {
	filter_status=$(awk "/^.{5} HTTP\/1.1 [^1]/ {print \$3;}" /tmp/curl.out)
	echo "$filter_status"
}

md_title 1 "Appendix REST API examples"
md_title 2 "Providers" "providers"				###### PROVIDERS

md_title 3 "Create a provider"					###

curl_post "providers" "provider01.xml" "201" "$XML" "$XML"
curl_post "providers" "provider02.xml" "201" "$XML" "$XML"
curl_post "providers" "provider03.json" "201" "$JSON" "$JSON"

md_text "Provider exists."
curl_post "providers" "provider02.xml" "409" "$XML" "$XML"

md_title 3 "Get a provider"						###
curl_get "providers/provider02" "" "200" "$XML"
curl_get "providers/provider02" "" "200" "$JSON"

md_text "Provider not exists."
curl_get "providers/notexists" "" "404"  "$XML"

md_title 3 "Get all the providers"				###
curl_get "providers" "" "200" "$XML"
curl_get "providers" "" "200" "$JSON"

md_title 3 "Delete a provider"					###
curl_delete "providers/provider03" "200"

md_text "Provider not exists"
curl_delete "providers/notexists" "404"


md_title 2 "Templates" "templates"				###### TEMPLATES

md_title 3 "Create a template"					###
curl_post "templates" "template01.xml" "201" "$XML" "$XML"
curl_post "templates" "template02.json" "201" "$JSON" "$JSON"
curl_post "templates" "template02b.xml" "201" "$XML" "$XML"

md_text "Template exists."
curl_post "templates" "template01.xml" "409" "$XML" "$XML"

md_text "Linked provider not exists."
curl_post "templates" "template03.xml" "409" "$XML" "$XML"

md_title 3 "Get a template"						###
curl_get "templates/template02" "" "200" "$XML"
curl_get "templates/template02" "" "200" "$JSON"

md_text "Template not exists."
curl_get "templates/notexists" "" "404" "$XML"

md_title 3 "Get all the templates"				###
curl_get "templates" "" "200" "$XML"
curl_get "templates" "" "200" "$JSON"

md_title 3 "Delete a template"					###
curl_delete "templates/template02b" "200"

md_text "Template not exists"
curl_delete "templates/notexists" "404"


md_title 2 "Agremeents" "agreements"			###### AGREEMENTS

md_title 3 "Create an agreement"				###
curl_post "agreements" "agreement01.xml" "201" "$XML" "$XML"
curl_post "agreements" "agreement02.json" "201" "$JSON" "$JSON"
curl_post "agreements" "agreement02b.xml" "201" "$XML" "$XML"

md_text "Linked provider not exists."
curl_post "agreements" "agreement03.xml" "409" "$XML" "$XML"
md_text "Linked template not exists."
curl_post "agreements" "agreement04.xml" "409" "$XML" "$XML"

md_text "Agreement exists."
curl_post "agreements" "agreement01.xml" "409" "$XML" "$XML"

md_title 3 "Get an agreement"					###
curl_get "agreements/agreement01" "" "200" "$XML"
curl_get "agreements/agreement01" "" "200" "$JSON"


md_title 3 "Get all the agreements"				###
curl_get "agreements" "" "200" "$XML"
curl_get "agreements" "" "200" "$JSON"

md_title 3 "Get agreement status"				###
curl_get "agreements/agreement02/guaranteestatus" "" "200" "$XML"
curl_get "agreements/agreement02/guaranteestatus" "" "200" "$JSON"


md_title 3 "Delete an agreement"				###
curl_delete "agreements/agreement02b" "200"

md_text "Agreement not exists"
curl_delete "agreements/notexists" "404"

md_title 3 "Get agreement status"				###
curl_get "agreements/agreement02/guaranteestatus" "" "200" "$XML"
curl_get "agreements/agreement02/guaranteestatus" "" "200" "$JSON"


md_title 2 "Enforcement Jobs" "enforcements"	###### ENFORCEMENTS

md_title 3 "Start enforcement job"				###
curl_put "enforcements/agreement02/start" "" "202"

md_title 3 "Stop enforcement job"				###
curl_put "enforcements/agreement02/stop" "" "200"


md_title 2 "Violations" "violations"			###### VIOLATIONS

# Generate some violations and penalties
curl_put "enforcements/agreement01/start" "" "202"
curl_post "enforcement-test/agreement01" "metric01.json" "202" "$JSON"
sleep 5

md_title 3 "Get all the violations"				###

curl_get "violations" "" "200" "$XML"
curl_get "violations" "" "200" "$JSON"

md_title 2 "Penalties" "penalties"				###### PENALTIES

md_title 3 "Get all the penalties"				###

curl_get "penalties" "" "200" "$XML"
curl_get "penalties" "" "200" "$JSON"
