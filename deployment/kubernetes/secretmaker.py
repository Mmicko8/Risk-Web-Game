import base64

input='''---
apiVersion: v1
kind: Secret
metadata:
  name: registry-credentials-rcnamereplace
  namespace: default
type: kubernetes.io/dockerconfigjson
data: 
  .dockerconfigjson: dcjreplace
'''

configjson = '''{
    "auths": {
        "https://registry.gitlab.com":{
            "username":"gitlabusername",
            "password":"gitlabpassword",
            "auth":"gitlabauth"
    	}
    }
}
'''

secrets=[
    ['frontend','gitlab+deploy-token-1619563','yUd-_1jHK7d93pTvReye'],
    ['backend','gitlab+deploy-token-1619565','95DfHkAuoB7Vff8XaVyL'],
    ['ai','gitlab+deploy-token-1619568','knUVupABBUGd5nhU1FTC']]

file=""""""

def ciad(username,password):
    auth = authmaker(username,password)
    newl = configjson.replace('gitlabusername',username)
    newl = newl.replace('gitlabpassword',password)
    newl = newl.replace('gitlabauth',auth)
    a = base64.b64encode(newl.encode('ascii'))
    a = a.decode('utf-8')
    return a

# correct
def authmaker(username,password):
    a = (username + ":" + password)
    a_bytes = base64.b64encode(a.encode('ascii'))
    a_bytes = a_bytes.decode('utf-8')
    return a_bytes


for deployment in secrets:
    newjson = ciad(deployment[1],deployment[2])
    newinput = input.replace('rcnamereplace',deployment[0])
    newinput = newinput.replace('dcjreplace',newjson)
    file += newinput

f = open('autoregcreds.yml','w')
f.write(file)
f.close()
