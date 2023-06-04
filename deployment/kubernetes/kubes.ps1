
# Default parameters
param ($action='create')

# authenticate with cluster
gcloud container clusters get-credentials primary --region=europe-west1-b

# Default or if ./kubes.ps1 create is been called
if ($action -eq 'create'){

# dbsecret template
$dbsecret = "
---

apiVersion: v1
kind: Secret
metadata:
  name: db-secret
type: Opaque
data:
  username: cm9vdC1kYXRhYmFzZQ==
  password: ZmtVOEVLalU0ag==
  database: databasesection
  dbconstr: dbconstrsection
---
  "

# Get db instance name
$name = Write-Output -n $(gcloud sql instances list);

# trimming to the instance name
$dbname = $name[1] -split '\s+' | Select-Object -First 1

# base64 value
$name = [System.Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes($dbname))

# put b64 string in secret
$dbsecret = $dbsecret.Replace("databasesection", $name)

# dbconnection string
$dbconstr = "jdbc:mysql://localhost:3306/ip2-testenv-database-team2"

# base64 value
$dbconstr = [System.Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes($dbconstr))

# put b64 string in secret
$dbsecret = $dbsecret.Replace("dbconstrsection", $dbconstr)

# create secret file
Write-Output $dbsecret > dbsecret.yml

# create autoregcreds.yml
python3.exe secretmaker.py

$projname = Write-Output -n $(gcloud projects list);

# trimming to the instance name
$projnamenew = $projname[1] -split '\s+' | Select-Object -First 1

# get db-name from earlier, make instance string
$idbnm = "${projnamenew}:europe-west1:CHANGEME=tcp:3306".Replace("CHANGEME",$dbname)

# Create backend kubernetes
Write-Output $(Get-Content gr6backend-template.yml).Replace("CHANGEME", $idbnm) > gr6backend.yml

# create all yaml files
    kubectl apply -f dbsecret.yml
    kubectl apply -f autoregcreds.yml
    kubectl apply -f gr6frontend.yml
    kubectl apply -f gr6ai.yml
    kubectl apply -f gr6backend.yml
    kubectl apply -f ingress.yml

} elseif ('d' -in $action) {
    
# delete all yaml files
    kubectl delete -f ingress.yml
    kubectl delete -f gr6backend.yml
    kubectl delete -f gr6ai.yml
    kubectl delete -f gr6frontend.yml
    kubectl delete -f autoregcreds.yml
    kubectl delete -f dbsecret.yml

}