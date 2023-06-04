#!/bin/bash

#Team settings
address="127.0.0.1" #grafana ip
Grafport="9000" #default 3000

#prometheus settings
AppSiteUrl='localhost:5000'
job1Name="amountActive"
job2Name="amountFinished"
job3Name="amountOpen"
job4Name="amountClosed"

metricPath1="/api/game/monitoring/amountActive"
metricPath2="/api/game/monitoring/amountFinished"
metricPath3="/api/game/monitoring/amountOpen"
metricPath4=" /api/game/monitoring/amountClosed"

YML="global:
  scrape_interval: 15s
  scrape_timeout: 10s
  evaluation_interval: 1m
scrape_configs:
- job_name: prometheus
  honor_timestamps: true
  scrape_interval: 5s
  scrape_timeout: 5s
  metrics_path: /metrics
  scheme: http
  static_configs:
  - targets:
    - localhost:9090
    
- job_name: $job1Name
  honor_timestamps: true
  scrape_interval: 10s
  scrape_timeout: 10s
  metrics_path: $metricPath1
  scheme: http
  tls_config:
    insecure_skip_verify: true
  static_configs:
  - targets:
    - $AppSiteUrl
    
- job_name: $job2Name
  honor_timestamps: true
  scrape_interval: 10s
  scrape_timeout: 10s
  metrics_path: $metricPath2
  scheme: http
  tls_config:
    insecure_skip_verify: true
  static_configs:
  - targets:
    - $AppSiteUrl

- job_name: $job3Name
  honor_timestamps: true
  scrape_interval: 10s
  scrape_timeout: 10s
  metrics_path: $metricPath3
  scheme: http
  tls_config:
    insecure_skip_verify: true
  static_configs:
  - targets:
    - $AppSiteUrl
    
- job_name: $job4Name
  honor_timestamps: true
  scrape_interval: 10s
  scrape_timeout: 10s
  metrics_path: $metricPath4
  scheme: http
  tls_config:
    insecure_skip_verify: true
  static_configs:
  - targets:
    - $AppSiteUrl"


##vars
id=2 #<org id of new org>
keyname="Key$RANDOM" #API key name

#Constants
fullip="$address:$Grafport"
prometheusPort="9090"
user="admin"
pass="Jos*456" #default admin
jsonfilepath="$1" #dashboard json ./Team-1.json
GPATH=$(which grafana-server)
PPATH=$(which prometheus)


##install dependancys
if [ "$GPATH" == "" ]; then
echo "grafana not found installing...."
apt-get install -y apt-transport-https
apt-get install -y software-properties-common wget
wget -q -O - https://packages.grafana.com/gpg.key | apt-key add -
apt-get update
apt-get install grafana
fi

##enable grafana and ports
if [ "$(sudo systemctl status grafana-server.service | grep active)" != "active" ]; then
systemctl daemon-reload
systemctl start grafana-server
systemctl enable grafana-server.service
systemctl start grafana-server.service
fi

##Configure promethius for endpoints
#https://stackoverflow.com/questions/63798620/can-grafana-monitor-endpoints
#https://medium.com/geekculture/monitoring-websites-using-grafana-and-prometheus-69ccf936310c
#https://codeblog.dotsandbrackets.com/scraping-application-metrics-prometheus/


##setup prometheus
if [ "$PPATH" == "" ]; then
apt install prometheus -y
fi

touch ./prometheus.yml
echo "$YML" > ./prometheus.yml

#TODO: run in background. ----------------------------------------------------------------------------------------------------------------------
prometheus --web.enable-lifecycle --config.file=./prometheus.yml --web.listen-address="0.0.0.0:$prometheusPort" &

##fetch API key
#https://grafana.com/docs/grafana/v9.0/developers/http_api/create-api-tokens-for-org/#how-to-create-a-new-organization-and-an-api-token
curl -X POST -H "Content-Type: application/json" \
-d '{"name":"apiorg"}' "http://$user:$pass@$fullip/api/orgs"

curl -X POST -H "Content-Type: application/json" \
-d '{"loginOrEmail":"admin", "role": "Admin"}' "http://$user:$pass@$fullip/api/orgs/$id/users"

curl -X POST "http://$user:$pass@$fullip/api/user/using/$id"

resp="$(curl -X POST -H "Content-Type: application/json" -d '{"name":"'"$keyname"'", "role": "Admin"}' "http://$user:$pass@$fullip/api/auth/keys")"
echo "$resp"

authk=$(echo "$resp" | cut -d ',' -f 3 | cut -d ' ' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)

##add prometheus to grafana as datasource
#grafana-cli add-data-source --name="Prometheus" --type="prometheus" --url="http://$fullip"
curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer $authk"\
-d '{"name":"Prometheus","type":"prometheus","url":"http://0.0.0.0:'$prometheusPort'"}' \
"http://$fullip/api/datasources"

#send json file to grafana server
echo "$authk"
curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer $authk" -d @$jsonfilepath "$fullip/api/dashboards/db"