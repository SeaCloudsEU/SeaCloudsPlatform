if [ $# -lt 3 ]; then
    echo "Usage: $0 agreementid stream metricValue"
	exit 1
fi
SLA_URL=${SLA_URL:-http://localhost:8080/sla-service}
./genviolation.py $2 $3 | curl -X POST -d@- $SLA_URL/metrics/$1
