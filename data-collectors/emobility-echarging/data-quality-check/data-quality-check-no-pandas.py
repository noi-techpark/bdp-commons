import requests
import time
import configparser

config = configparser.ConfigParser()
config.read('.env')

DRIWE_TOKEN = config["keys"]["DRIWE_TOKEN"]
NEVICAM_KEY = config["keys"]["NEVICAM_KEY"]

ALPERIA = "http://api.alperia-emobility.eu/e-mobility/api/v3/chargingunits?includePartners=false"
ROUTE220 = "https://platform.evway.net/api/idm/getAllRoute220Stations"
NEVICAM = "https://mobility.nevicam.it/apiv0/m2"
DRIWE = "https://www.driwe.club/api/stations/metadata?auth-token=" + DRIWE_TOKEN


def get_odh_plugs(origin):
    ODH_URL = "https://mobility.api.opendatahub.bz.it/v2/flat%2Cnode/EChargingPlug/%2A/latest?limit=0&offset=0&shownull=false&select=pcode,porigin,smetadata,mvalue&where=pactive.eq.true,porigin.eq." + origin + "&distinct=true&timezone=UTC"
    response = requests.get(ODH_URL)

    return response.json()["data"]


def get_odh_stations(origin):
    ODH_URL = "https://mobility.api.opendatahub.bz.it/v2/flat%2Cnode/EChargingStation/%2A/latest?limit=0&offset=0&shownull=false&where=sactive.eq.true,sorigin.eq." + \
        origin + "&distinct=true&timezone=UTC"
    response = requests.get(ODH_URL)

    return response.json()["data"]


def get_dataprovider_data(url, api_key=None):
    headers = {
        "X-Caller-ID": "NOI-Techpark",
        "Accept": "application/json"
    }

    # nevicam
    if api_key:
        headers["apikey"] = api_key

    response = requests.get(
        url, headers=headers)

    print(response)

    json = response.json()
    return json


# def to_csv(data):
#     panda_file = pd.DataFrame(data)
#     # panda_file = pd.read_json(data)
#     panda_file.to_csv('csvfile3.csv', encoding='utf-8', index=False)


# def append_to_csv(data, csv_file):
#     df = pd.DataFrame(data)
#     df.to_csv(csv_file, mode='a',
#                       encoding='utf-8', index=False, header=False)

def append_to_csv(data, csv_file):
    file = open(csv_file, "a")
    for row in data:
        set = str(row["station_code"])
        if "odh_mvalue" in row:
            set += str(row["odh_mvalue"]) + ","
        else:
            set += "#,"

        if "dp_state" in row:
            set += str(row["dp_state"]) + ","
        else:
            set += "#,"

        if "problem" in row:
            set += str(row["problem"]) + ","
        else:
            set += "#,"

        set += str(row["timestamp"])
        file.write(set + "\n")
    file.close()

# checks if the stations data is represented correctly with the plugs data
# stations have value (current available plugs) and capacity (total plugs)
# every station can have multiple plugs
# every plug has value (0 or 1) for available or not
# so every stations capacity should have the same value as the sum of all its plugs
# and the stations value should be the sum of all its available plugs


def analyze_odh_stations_to_plugs(stations, plugs):
    error_counter = 0

    for station in stations:
        station_id = station["scode"]
        station_capacity = station["smetadata"]["capacity"]
        station_value = station["mvalue"]

        plug_counter = 0
        available_plug_counter = 0
        for plug in plugs:
            if plug["pcode"] == station_id:
                plug_counter += 1
                if plug["mvalue"] == 1:
                    available_plug_counter += 1

        if station_capacity != plug_counter:
            error_counter += 1
            print(station)
            print("Station has " + str(station_capacity) +
                  " capacity - Total plugs counted: " + str(plug_counter))

        if station_value != available_plug_counter:
            error_counter += 1
            print(station)
            print("Station has " + str(station_value) +
                  " plugs available - Available plugs: " + str(available_plug_counter))

    if error_counter > 0:
        print("Error: " + str(error_counter) + " errors found")
    else:
        print("No errors found")


# analyzes if the data from data-provider is saved correctly in the ODH
def analyze_plugs(odh, data_provider):
    # print("ODH: CHARGING - DATA PROVIDER: AVAILABLE\n")
    problem_counter = 0
    total_counter = 0
    for odh_plug in odh:
        station_code = odh_plug["pcode"]
        total_counter += 1

        for dp_station in data_provider:
            if dp_station["code"] == station_code:
                outlet_id = odh_plug["smetadata"]["outlets"][0]["id"]

                for charging_point in dp_station["chargingPoints"]:
                    if charging_point["outlets"][0]["id"] == outlet_id and odh_plug["mvalue"] == 0 and charging_point["state"] == "AVAILABLE":
                        # print("PROBLEM DETECTED")
                        # print(odh_plug)
                        # print(charging_point["state"] + " - " +
                        # 	dp_station["code"] + " " + str(charging_point))
                        print("id: " + str(station_code) + " mvalue: " +
                              str(odh_plug["mvalue"]) + " <-> state: " + charging_point["state"])
                        problem_counter += 1

    print("Number of problems: " + str(problem_counter) +
          " of total plugs: " + str(total_counter))

    print("--------------------------------------------------------------------------")

    print("ODH: AVAILABLE - DATA PROVIDER: CHARGING\n")
    problem_counter = 0
    total_counter = 0
    for odh_plug in odh:
        station_code = odh_plug["pcode"]
        total_counter += 1

        for dp_station in data_provider:
            if dp_station["code"] == station_code:
                outlet_id = odh_plug["smetadata"]["outlets"][0]["id"]

                for charging_point in dp_station["chargingPoints"]:
                    if charging_point["outlets"][0]["id"] == outlet_id and odh_plug["mvalue"] == 1 and charging_point["state"] != "AVAILABLE":
                        # print("PROBLEM DETECTED")
                        # print(odh_plug)
                        # print(charging_point["state"] + " - " +
                        # 	dp_station["code"] + " " + str(charging_point))
                        print("id: " + str(station_code) + " mvalue: " +
                              str(odh_plug["mvalue"]) + " <-> state: " + charging_point["state"])
                        problem_counter += 1

    print("Number of problems: " + str(problem_counter) +
          " of total plugs: " + str(total_counter))

    print("#########################################################################")


def map_plugs(odh, data_provider):

    time_stamp = time.strftime("%Y-%m-%d %H:%M:%S", time.gmtime())

    plugs = []
    for odh_plug in odh:
        plug = {}
        plug["station_code"] = odh_plug["pcode"]
        for dp_station in data_provider:
            if str(dp_station["code"]) == str(plug["station_code"]):
                plug["plug_id"] = odh_plug["smetadata"]["outlets"][0]["id"]

                for charging_point in dp_station["chargingPoints"]:
                    if charging_point["outlets"][0]["id"] == plug["plug_id"]:
                        plug["odh_mvalue"] = odh_plug["mvalue"]
                        plug["dp_state"] = charging_point["state"]
                        plug["problem"] = odh_plug["mvalue"] == 1 and charging_point["state"] == "AVAILABLE"
                        plug["timestamp"] = time_stamp
                        plugs.append(plug)
    return plugs


while True:
    print("ALPERIA " + time.strftime("%Y-%m-%d %H:%M:%S", time.gmtime()))
    odh_alperia_plugs = get_odh_plugs("ALPERIA")
    data_provider = get_dataprovider_data(ALPERIA)
    plugs = map_plugs(odh_alperia_plugs, data_provider)
    append_to_csv(plugs, "alperia2.csv")

    print("route220 " + time.strftime("%Y-%m-%d %H:%M:%S", time.gmtime()))
    odh_route220_plugs = get_odh_plugs("route220")
    data_provider_route220 = get_dataprovider_data(ROUTE220)
    plugs = map_plugs(odh_route220_plugs, data_provider_route220)
    append_to_csv(plugs, "route2202.csv")

    print("DRIWE " + time.strftime("%Y-%m-%d %H:%M:%S", time.gmtime()))
    odh_driwe_plugs = get_odh_plugs("DRIWE")
    data_provider_driwe = get_dataprovider_data(DRIWE)
    plugs = map_plugs(odh_driwe_plugs, data_provider_driwe)
    append_to_csv(plugs, "driwe2.csv")

    time.sleep(300)

while True:
    print("ALPERIA " + time.strftime("%Y-%m-%d %H:%M:%S", time.gmtime()))
    odh_alperia_plugs = get_odh_plugs("ALPERIA")
    data_provider = get_dataprovider_data(ALPERIA)
    analyze_plugs(odh_alperia_plugs, data_provider)

    print("route220 " + time.strftime("%Y-%m-%d %H:%M:%S", time.gmtime()))
    odh_route220_plugs = get_odh_plugs("route220")
    data_provider_route220 = get_dataprovider_data(ROUTE220)
    analyze_plugs(odh_route220_plugs, data_provider_route220)

    print("DRIWE " + time.strftime("%Y-%m-%d %H:%M:%S", time.gmtime()))
    odh_driwe_plugs = get_odh_plugs("DRIVE")
    data_provider_driwe = get_dataprovider_data(DRIWE)
    analyze_plugs(odh_driwe_plugs, data_provider_driwe)

    time.sleep(300)


print("ROUTE220")
odh_route220_stations = get_odh_stations("route220")
odh_route220_plugs = get_odh_plugs("route220")

print("ANALYZE ODH STATIONS <-> PLUGS")
analyze_odh_stations_to_plugs(odh_route220_stations, odh_route220_plugs)

data_provider_route220 = get_dataprovider_data(ROUTE220)
analyze_plugs(odh_route220_plugs, data_provider_route220)


print("DRIWE")
odh_driwe_stations = get_odh_stations("DRIVE")
odh_driwe_plugs = get_odh_plugs("DRIVE")

print("ANALYZE ODH STATIONS <-> PLUGS")
analyze_odh_stations_to_plugs(odh_driwe_stations, odh_driwe_plugs)

data_provider_driwe = get_dataprovider_data(DRIWE)
analyze_plugs(odh_driwe_plugs, data_provider_driwe)


# print("\n\n------------ PLUGS -------------\n\n")

# print("ALPERIA")
# data_provider = get_dataprovider_data(ALPERIA)
# odh = get_odh_plugs("ALPERIA")
# analyze_plugs(odh, data_provider)

# print("ROUTE220")
# data_provider_route220 = get_dataprovider_data(ROUTE220)
# odh_route220 = get_odh_plugs("route220")
# analyze_plugs(odh_route220, data_provider_route220)

# print("DRIWE")
# data_provider_driwe = get_dataprovider_data(DRIWE)
# odh_driwe = get_odh_plugs("DRIVE")
# analyze_plugs(odh_driwe, data_provider_driwe)

# Gives 502 Bad Gateway Error
# print("NEVICAM")
# data_provider_nevicam = get_dataprovider_data(NEVICAM,NEVICAM_KEY)
# odh_nevicam = get_odh_plugs("Nevicam")
# analyze_plugs(odh_nevicam, data_provider_nevicam)
