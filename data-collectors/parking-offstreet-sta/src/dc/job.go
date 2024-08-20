// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

// SPDX-License-Identifier: AGPL-3.0-or-later

package dc

import (
	"fmt"
	"log/slog"
	"os"
	"parking-offstreet-sta/bdplib"
	"strconv"
	"time"
)

const stationTypeParent string = "ParkingFacility"
const stationType string = "ParkingStation"

const shortStay string = "short_stay"
const subscribers string = "subscribers"

const dataTypeFreeShort string = "free_" + shortStay
const dataTypeFreeSubs string = "free_" + subscribers
const dataTypeFreeTotal string = "free"
const dataTypeOccupiedShort string = "occupied_" + shortStay
const dataTypeOccupiedSubs string = "occupied_" + subscribers
const dataTypeOccupiedTotal string = "occupied"

var origin string = os.Getenv("ORIGIN")

// use hardcoded default lat/lon locations, if API gives 0,0
// 608612 Brunico 46.792815456220296, 11.927622005916659
const brunicoId int = 608612
const brunicoLat float64 = 46.792815456220296
const brunicoLon float64 = 11.927622005916659

// 607440 Bressanone  46.713708199562525, 11.650497804658093
const bressanoneId int = 607440
const bressanoneLat float64 = 46.713708199562525
const bressanoneLon float64 = 11.650497804658093

// GetFacilityData returns data for multiple companies; this identifier filters out STA
const identifier string = "STA – Strutture Trasporto Alto Adige SpA Via dei Conciapelli, 60 39100  Bolzano UID: 00586190217"

func Job() {
	var parentStations []bdplib.Station
	// save stations by stationCode
	stations := make(map[string]bdplib.Station)

	var dataMapParent bdplib.DataMap
	var dataMap bdplib.DataMap

	facilities := GetFacilityData()

	ts := time.Now().UnixMilli()

	for _, facility := range facilities.Data.Facilities {

		if facility.ReceiptMerchant == identifier {
			parentStationCode := strconv.Itoa(facility.FacilityId)
			lat, lon := getLocationOrDefault(facility.FacilityId, facility.Latitude, facility.Longitude)
			parentStation := bdplib.CreateStation(parentStationCode, facility.Description, stationTypeParent, lat, lon, origin)
			parentStation.MetaData = map[string]interface{}{
				"IdCompany":    facility.IdCompany,
				"City":         facility.City,
				"Address":      facility.Address,
				"ZIPCode":      facility.ZIPCode,
				"Telephone1":   facility.Telephone1,
				"Telephone2":   facility.Telephone2,
				"municipality": facility.City,
			}

			// set City=Brunico for parking lot "Parcheggio Stazione Brunico Mobilitätszentrum"
			// old api gives wrongly Bolzano
			if parentStationCode == "608612" {
				parentStation.MetaData["City"] = "Brunico"
				parentStation.MetaData["municipality"] = "Brunico"
			}

			parentStations = append(parentStations, parentStation)

			freePlaces := GetFreePlacesData(facility.FacilityId)

			// total facility measurements
			freeTotalSum := 0
			occupiedTotalSum := 0
			capacityTotal := 0
			// total facility subscribers measurements
			freeSubscribersSum := 0
			occupiedSubscribersSum := 0
			capacitySubscribers := 0
			// total facility short stay measurements
			freeShortStaySum := 0
			occupiedShortStaySum := 0
			capacityShortStay := 0

			// freeplaces is array of a single categories data
			// if multiple parkNo exist, multiple entries for every parkNo and its categories exist
			// so iterating over freeplaces and checking if the station with the parkNo has already been created is needed
			for _, freePlace := range freePlaces.Data.FreePlaces {
				// create ParkingStation
				stationCode := parentStationCode + "_" + strconv.Itoa(freePlace.ParkNo)
				station, ok := stations[stationCode]
				if !ok {
					lat, lon := getLocationOrDefault(freePlace.FacilityId, freePlace.Latitude, freePlace.Longitude)
					station = bdplib.CreateStation(stationCode, fmt.Sprintf("%s %s", facility.Description, freePlace.FacilityDescription), stationType, lat, lon, origin)
					station.ParentStation = parentStation.Id

					station.MetaData = make(map[string]interface{})
					station.MetaData["FacilityDescription"] = freePlace.FacilityDescription
					station.MetaData["municipality"] = facility.City

					// set City=Brunico for parking lot "Parcheggio Stazione Brunico Mobilitätszentrum"
					// old api gives wrongly Bolzano
					if parentStationCode == "608612" {
						station.MetaData["municipality"] = "Brunico"
					}


					stations[stationCode] = station
					slog.Debug("Create station " + stationCode)
				}

				switch freePlace.CountingCategoryNo {
				// Short Stay
				case 1:
					station.MetaData["free_limit_"+shortStay] = freePlace.FreeLimit
					station.MetaData["occupancy_limit_"+shortStay] = freePlace.OccupancyLimit
					station.MetaData["capacity_"+shortStay] = freePlace.Capacity
					bdplib.AddRecord(stationCode, dataTypeFreeShort, bdplib.CreateRecord(ts, freePlace.FreePlaces, 600), &dataMap)
					bdplib.AddRecord(stationCode, dataTypeOccupiedShort, bdplib.CreateRecord(ts, freePlace.CurrentLevel, 600), &dataMap)
					// facility data
					freeShortStaySum += freePlace.FreePlaces
					occupiedShortStaySum += freePlace.CurrentLevel
					capacityShortStay += freePlace.Capacity
				// Subscribed
				case 2:
					station.MetaData["free_limit_"+subscribers] = freePlace.FreeLimit
					station.MetaData["occupancy_limit_"+subscribers] = freePlace.OccupancyLimit
					station.MetaData["capacity_"+subscribers] = freePlace.Capacity
					bdplib.AddRecord(stationCode, dataTypeFreeSubs, bdplib.CreateRecord(ts, freePlace.FreePlaces, 600), &dataMap)
					bdplib.AddRecord(stationCode, dataTypeOccupiedSubs, bdplib.CreateRecord(ts, freePlace.CurrentLevel, 600), &dataMap)
					// facility data
					freeSubscribersSum += freePlace.FreePlaces
					occupiedSubscribersSum += freePlace.CurrentLevel
					capacitySubscribers += freePlace.Capacity
				// Total
				default:
					station.MetaData["free_limit"] = freePlace.FreeLimit
					station.MetaData["occupancy_limit"] = freePlace.OccupancyLimit
					station.MetaData["capacity"] = freePlace.Capacity
					bdplib.AddRecord(stationCode, dataTypeFreeTotal, bdplib.CreateRecord(ts, freePlace.FreePlaces, 600), &dataMap)
					bdplib.AddRecord(stationCode, dataTypeOccupiedTotal, bdplib.CreateRecord(ts, freePlace.CurrentLevel, 600), &dataMap)
					// total facility data
					freeTotalSum += freePlace.FreePlaces
					occupiedTotalSum += freePlace.CurrentLevel
					capacityTotal += freePlace.Capacity
				}
			}

			// assign total facility data, if data is not 0
			if freeTotalSum > 0 {
				bdplib.AddRecord(parentStationCode, dataTypeFreeTotal, bdplib.CreateRecord(ts, freeTotalSum, 600), &dataMapParent)
			}
			if occupiedTotalSum > 0 {
				bdplib.AddRecord(parentStationCode, dataTypeOccupiedTotal, bdplib.CreateRecord(ts, occupiedTotalSum, 600), &dataMapParent)
			}
			if capacityTotal > 0 {
				parentStation.MetaData["capacity"] = capacityTotal
			}

			// subscribers
			if freeSubscribersSum > 0 {
				bdplib.AddRecord(parentStationCode, dataTypeFreeSubs, bdplib.CreateRecord(ts, freeSubscribersSum, 600), &dataMapParent)
			}
			if occupiedSubscribersSum > 0 {
				bdplib.AddRecord(parentStationCode, dataTypeOccupiedSubs, bdplib.CreateRecord(ts, occupiedTotalSum, 600), &dataMapParent)
			}
			if capacitySubscribers > 0 {
				parentStation.MetaData["capacity_"+subscribers] = capacityTotal
			}

			// short stay
			if freeShortStaySum > 0 {
				bdplib.AddRecord(parentStationCode, dataTypeFreeShort, bdplib.CreateRecord(ts, freeShortStaySum, 600), &dataMapParent)
			}
			if occupiedShortStaySum > 0 {
				bdplib.AddRecord(parentStationCode, dataTypeOccupiedShort, bdplib.CreateRecord(ts, occupiedShortStaySum, 600), &dataMapParent)
			}
			if capacityShortStay > 0 {
				parentStation.MetaData["capacity_"+shortStay] = capacityTotal
			}
		}
	}
	bdplib.SyncStations(stationTypeParent, parentStations)
	bdplib.SyncStations(stationType, values(stations))
	bdplib.PushData(stationTypeParent, dataMapParent)
	bdplib.PushData(stationType, dataMap)
}

func getLocationOrDefault(facilityId int, lat float64, lon float64) (float64, float64) {
	if lat != 0 && lon != 0 {
		return lat, lon
	}
	if facilityId == brunicoId {
		return brunicoLat, brunicoLon
	}
	if facilityId == bressanoneId {
		return bressanoneLat, bressanoneLon
	}
	slog.Info("No default location found for facilityID" + strconv.Itoa(facilityId))
	return lat, lon
}

func SyncDataTypes() {
	var dataTypes []bdplib.DataType
	// free
	dataTypes = append(dataTypes, bdplib.CreateDataType(dataTypeFreeShort, "", "Amount of free 'short stay' parking slots", "Instantaneous"))
	dataTypes = append(dataTypes, bdplib.CreateDataType(dataTypeFreeSubs, "", "Amount of free 'subscribed' parking slots", "Instantaneous"))
	dataTypes = append(dataTypes, bdplib.CreateDataType(dataTypeFreeTotal, "", "Amount of free parking slots", "Instantaneous"))
	// occupied
	dataTypes = append(dataTypes, bdplib.CreateDataType(dataTypeOccupiedShort, "", "Amount of occupied 'short stay' parking slots", "Instantaneous"))
	dataTypes = append(dataTypes, bdplib.CreateDataType(dataTypeOccupiedSubs, "", "Amount of occupied 'subscribed' parking slots", "Instantaneous"))
	dataTypes = append(dataTypes, bdplib.CreateDataType(dataTypeOccupiedTotal, "", "Amount of occupied parking slots", "Instantaneous"))

	bdplib.SyncDataTypes(stationType, dataTypes)
}

// to extract values array from map, without external dependency
// https://stackoverflow.com/questions/13422578/in-go-how-to-get-a-slice-of-values-from-a-map
func values[M ~map[K]V, K comparable, V any](m M) []V {
	r := make([]V, 0, len(m))
	for _, v := range m {
		r = append(r, v)
	}
	return r
}
