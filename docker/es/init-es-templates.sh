#!/bin/bash
set -e

ES_ADDRESS="${SERVER_ES_ADDRESS:-elasticsearch-single:9200}"
TEMPLATE_DIR="/init/template"

echo "Waiting for ElasticSearch at ${ES_ADDRESS} ..."
while true; do
    curl -s --connect-timeout 10 -o /dev/null "http://${ES_ADDRESS}/_cat/nodes" > /dev/null 2>&1
    if [ "$?" = "0" ]; then
        echo "ElasticSearch is ready."
        break
    fi
    echo "ElasticSearch not ready, retrying in 3s..."
    sleep 3
done

for template_file in "${TEMPLATE_DIR}"/*; do
    if [ ! -f "$template_file" ]; then
        continue
    fi
    template_name=$(basename "$template_file")
    echo "Creating ES template: ${template_name}"
    curl -s -o /dev/null -X POST \
        -H 'cache-control: no-cache' \
        -H 'content-type: application/json' \
        "http://${ES_ADDRESS}/_template/${template_name}" \
        -d @"${template_file}"
done

echo "ES template initialization completed."
