import requests
import os
import pandas as pd

DATA_PROVIDER_URL = os.getenv("DATA_PROVIDER_URL")
# ODH_URL = os.getenv(
	# "ODH_URL", "https://mobility.api.opendatahub.bz.it/v2/flat%2Cnode/EChargingPlug/%2A/latest?limit=0&offset=0&shownull=false&distinct=true&timezone=UTC&origin=ALPERIA")

# ODH_URL = os.getenv(
# 	"ODH_URL", "https://mobility.api.opendatahub.bz.it/v2/flat%2Cnode/EChargingStation/%2A/latest?limit=0&offset=0&shownull=false&distinct=true&timezone=UTC&origin=ALPERIA")

ORIGIN = "ALPERIA"
ODH_URL = os.getenv(
	"ODH_URL", "https://mobility.api.opendatahub.bz.it/v2/flat%2Cnode/EChargingPlug/%2A/latest?limit=0&offset=0&shownull=false&select=pcode,smetadata,mvalue&where=pactive.eq.true&distinct=true&timezone=UTC&origin=" + ORIGIN)


ALPERIA = "http://api.alperia-emobility.eu/e-mobility/api/v3/chargingunits?includePartners=false"
ROUTE220 = "https://platform.evway.net/api/idm/getAllRoute220Stations"


def get_odh_plugs():
	response = requests.get(ODH_URL)

	return response.json()["data"]


def get_data_from_data_provider():
	headers = {
		"X-Caller-ID": "NOI-Techpark",
		"Accept": "application/json"
	}

	response = requests.get(
		ALPERIA, headers=headers)

	json = response.json()

	# data = []
	# for station in json:
	# 	for cp in station["chargingPoints"]:
	# 		data.append(cp)

	return json

def to_csv(data):
	panda_file = pd.DataFrame(data)
	# panda_file = pd.read_json(data)
	panda_file.to_csv('csvfile3.csv', encoding='utf-8', index=False)


data_provider = get_data_from_data_provider()
odh = get_odh_plugs()

print("ODH: CHARGING - DATA PROVIDER: AVAILABLE\n")
problem_counter = 0
for odh_plug in odh:
	id = odh_plug["pcode"]
	for dp_station in data_provider:
		if dp_station["code"] == id:
			outlet_id = odh_plug["smetadata"]["outlets"][0]["id"]
			for charging_point in dp_station["chargingPoints"]:
				if charging_point["outlets"][0]["id"] == outlet_id:
					if odh_plug["mvalue"] == 0 and charging_point["state"] != "CHARGING" and charging_point["state"] != "FAULT":
						print("PROBLEM DETECTED")
						print(odh_plug)
						print(charging_point["state"] + " - " + dp_station["code"] + " " + str(charging_point))
						problem_counter += 1

print("Number of problems: " + str(problem_counter))

print("\n\n-------------------\n\n")

print("ODH: AVAILABLE - DATA PROVIDER: CHARGING\n")
problem_counter = 0
for odh_plug in odh:
	id = odh_plug["pcode"]
	for dp_station in data_provider:
		if dp_station["code"] == id:
			outlet_id = odh_plug["smetadata"]["outlets"][0]["id"]
			for charging_point in dp_station["chargingPoints"]:
				if charging_point["outlets"][0]["id"] == outlet_id:
					if odh_plug["mvalue"] == 1 and charging_point["state"] != "AVAILABLE" and charging_point["state"] != "FAULT":
						print("PROBLEM DETECTED")
						print(odh_plug)
						print(charging_point["state"] + " - " + dp_station["code"] + " " + str(charging_point))
						problem_counter += 1

print("Number of problems: " + str(problem_counter))
