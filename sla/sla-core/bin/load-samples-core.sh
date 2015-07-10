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
# To be executed from application root path
#

#####

# empty: debug disabled
DEBUG=

SLA_MANAGER_URL="http://localhost:8080/sla-service"

#####

shopt -s expand_aliases
alias curl='/usr/bin/curl -sLi '

function header() {
	echo
	echo
	echo "#### $1 #### "
}

debug() {
	[[ -n $DEBUG ]] && echo "$*" >&2
}

function curl_cmd() {
	[[ -n $DEBUG ]] && set -o xtrace
	curl -u user:password "$@"
	code=$?
	[[ -n $DEBUG ]] && set +o xtrace
	
	return $code
}

function curl_post() {
	#
	# $1: relative url
	# $2: file to send
	# $3: content type header
	# $4: accept header
	type_header=${3:+-H Content-type:$3}
	accept_header=${4:+-H Accept:$4}
	url="$SLA_MANAGER_URL/$1"
	out=$(curl_cmd "-d@$2" -X POST $type_header $accept_header "$url")
	code=$?
	debug $out
	filter_status=$(echo "$out" | grep -v "^HTTP\/1.1 100" | awk "/^HTTP\/1.1/ {print \$2;}" )
	filter_location=$(echo "$out" | grep -i "^location:" | sed -e "s/^location: *\(.*\)$/\1/i")
	debug "POST $url : $filter_status $filter_location"
	echo "$filter_status $filter_location"

	return $code
}

function curl_put() {
	# $1: relative url
	url="$SLA_MANAGER_URL/$1"
	out=$(curl_cmd -X PUT "$url")
	code=$?
	[[ -n $DEBUG ]] && echo "$out" >&2
	filter_status=$(echo "$out" | grep -v "^HTTP\/1.1 100" | awk "/^HTTP\/1.1/ {print \$2;}" )
	debug "PUT $url : $filter_status"
	echo "$filter_status"

	return $code
}

function check() {
	#$1 expected
	#$2 actual

	out=$(echo "$2" | grep "$1")
	if [ "$?" = "0" ]; then
		return $code
	else
		echo "ERROR: Expected: $1. Actual: $2"
		exit $code
	fi
}

function check_curl_post() {
	out=$(curl_post "$1" "$2" "$3" "$4")
	code=$?

	check "201" $out
}

function check_curl_put() {
	out=$(curl_put "$1" "$2" "$3" "$4")
	code=$?

	check "20." $out
}


echo serverurl=SLA_MANAGER_URL=$SLA_MANAGER_URL

header "Add provider-a xml" ####
check_curl_post "providers" "samples/provider-a.xml" "application/xml" "application/xml"


header "Add provider-b xml" ####
check_curl_post "providers" "samples/provider-b.xml" "application/xml" "application/xml"

header "Add template xml" ####
check_curl_post "templates" "samples/template-a.xml" "application/xml" "application/xml"

header "Add agreement xml" ####
check_curl_post "agreements" "samples/agreement-a.xml" "application/xml" "application/xml"

header "Start enforcement agreement-a" ####
check_curl_put "enforcements/agreement-a/start"

header "Stop enforcement agreement-a" ####
check_curl_put "enforcements/agreement-a/stop"
exit


#
echo; echo \#Add agreement04
#
curl -u user:password -H "Content-type: application/xml" -d@samples/agreement04.xml $SLA_MANAGER_URL/agreements -X POST
curl -u user:password -d@samples/enforcement04.xml -H "Content-type: application/xml" $SLA_MANAGER_URL/enforcements -X POST
curl -u user:password $SLA_MANAGER_URL/enforcements/agreement04/start -X PUT

#
echo; echo \#Add agreement05
#
curl -u user:password -H "Content-type: application/xml" -d@samples/agreement05.xml $SLA_MANAGER_URL/agreements -X POST
curl -u user:password -d@samples/enforcement05.xml -H "Content-type: application/xml" $SLA_MANAGER_URL/enforcements -X POST
#curl -u user:password $SLA_MANAGER_URL/enforcements/agreement05/start -X PUT

