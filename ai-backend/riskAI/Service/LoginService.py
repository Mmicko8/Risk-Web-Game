import configparser
import os

import requests


backendUrl = os.environ.get("backend_url")
ai_api_path = os.environ.get("ai_api_path")

def login():
    print(backendUrl+ai_api_path)
    response = requests.post(f"{backendUrl+ai_api_path}/player/login",
                             json={"username": "Julius Caesar", "password": "password"})
    print(response.headers)
    return response.headers["Authorization"]
