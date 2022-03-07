#!/bin/python3
import requests
import datetime
import os
from dotenv import load_dotenv

load_dotenv()
URL = os.getenv("A22_CONNECTOR_URL")
USR = os.getenv("A22_CONNECTOR_USR")
PWD = os.getenv("A22_CONNECTOR_PWD")


x = requests.post(
    f"{URL}/token", json={"request": {"username": USR, "password": PWD}}
)

session_id = x.json()["SubscribeResult"]["sessionId"]

from_data = datetime.datetime(2021, 1, 1, 0, 0, 0).strftime("%s") + "000"
to_data = datetime.datetime(2021, 1, 2, 0, 0, 0).strftime("%s") + "000"

x = requests.post(
    f"{URL}/eventi/lista/storici",
    json={
        "request": {
            "sessionId": f"{session_id}",
            "fromData": f"/Date({from_data})/",
            "toData": f"/Date({to_data})/",
        }
    },
)

candidates = {}

for e in x.json()["Eventi_ListaStoriciResult"]:
    id = e["id"]
    if id in candidates:
        print(e)
        print(candidates[id])
    else:
        candidates[id] = e

