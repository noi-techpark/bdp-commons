// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

// SPDX-License-Identifier: AGPL-3.0-or-later

package dc

import (
	"log/slog"
	"os"
	"parking-offstreet-sta/bdplib"
	"strconv"
	"time"
)

const stationTypeParent string = "ParkingFacility"
const stationType string = "ParkingStation"

const shortStay string = "ShortStay"
const Subscribers string = "Subscribers"

const dataTypeShort string = "free_" + shortStay
const dataTypeSubs string = "free_" + Subscribers
const dataTypeTotal string = "free"

var origin string = os.Getenv("ORIGIN")

const bzLat float64 = 46.49067
const bzLon float64 = 11.33982

// GetFacilityData returns data for multiple companies; this identifier filters out STA
const identifier string = "STA – Strutture Trasporto Alto Adige SpA Via dei Conciapelli, 60 39100  Bolzano UID: 00586190217"

func Job() {
	var parentStations []bdplib.Station
	// save stations by stationCode
	stations := make(map[string]bdplib.Station)

	var dataMap bdplib.DataMap

	// get data
	facilities := GetFacilityData()

	ts := time.Now().UnixMilli()

	for _, facility := range facilities.Data.Facilities {

		if facility.ReceiptMerchant == identifier {
			parentStationCode := strconv.Itoa(facility.FacilityId)

			parentStation := bdplib.CreateStation(parentStationCode, facility.Description, stationTypeParent, bzLat, bzLon, origin)
			parentStation.MetaData = map[string]interface{}{
				"IdCompany":  facility.FacilityId,
				"City":       facility.City,
				"Address":    facility.Address,
				"ZIPCode":    facility.ZIPCode,
				"Telephone1": facility.Telephone1,
				"Telephone2": facility.Telephone2,
			}
			parentStations = append(parentStations, parentStation)

			freePlaces := GetFreePlacesData(facility.FacilityId)

			// freeplaces is array of a single categories data
			// if multiple parkNo exist, multiple entries for every parkNo and its categories exist
			// so iterating over freeplaces and checking if the station with the parkNo has already been created is needed
			for _, freePlace := range freePlaces.Data.FreePlaces {
				stationCode := parentStationCode + "_" + strconv.Itoa(freePlace.ParkNo)

				station, ok := stations[stationCode]
				if !ok {
					station = bdplib.CreateStation(stationCode, facility.Description, stationType, bzLat, bzLon, origin)
					station.ParentStation = parentStation.Id
					station.MetaData = make(map[string]interface{})
					stations[stationCode] = station
					slog.Debug("Create station " + stationCode)
				}

				// map metadata and create records
				var recordsShort []bdplib.Record
				var recordsSubs []bdplib.Record
				var recordsTotal []bdplib.Record

				switch freePlace.CountingCategoryNo {
				// Short Stay
				case 1:
					station.MetaData["FreeLimit_"+shortStay] = freePlace.FreeLimit
					station.MetaData["OccupancyLimit_"+shortStay] = freePlace.OccupancyLimit
					station.MetaData["Capacity_"+shortStay] = freePlace.Capacity
					recordsShort = append(recordsShort, bdplib.CreateRecord(ts, freePlace.FreePlaces, 600))
				// Subscribed
				case 2:
					station.MetaData["FreeLimit_"+Subscribers] = freePlace.FreeLimit
					station.MetaData["OccupancyLimit_"+Subscribers] = freePlace.OccupancyLimit
					station.MetaData["Capacity_"+Subscribers] = freePlace.Capacity
					recordsSubs = append(recordsSubs, bdplib.CreateRecord(ts, freePlace.FreePlaces, 600))
				// Total
				default:
					station.MetaData["FreeLimit"] = freePlace.FreeLimit
					station.MetaData["OccupancyLimit"] = freePlace.OccupancyLimit
					station.MetaData["Capacity"] = freePlace.Capacity
					recordsTotal = append(recordsTotal, bdplib.CreateRecord(ts, freePlace.FreePlaces, 600))
				}

				bdplib.AddRecords(stationCode, dataTypeShort, recordsShort, &dataMap)
				bdplib.AddRecords(stationCode, dataTypeSubs, recordsSubs, &dataMap)
				bdplib.AddRecords(stationCode, dataTypeTotal, recordsTotal, &dataMap)
			}
		}
	}

	bdplib.SyncStations(stationTypeParent, parentStations)
	bdplib.SyncStations(stationType, values(stations))
	bdplib.PushData(stationType, dataMap)
}

func SyncDataTypes() {
	var dataTypes []bdplib.DataType

	dataTypes = append(dataTypes, bdplib.CreateDataType(dataTypeShort, "", "Amount of free 'short stay' parking slots", "Instantaneous"))
	dataTypes = append(dataTypes, bdplib.CreateDataType(dataTypeSubs, "", "Amount of 'subscribed' parking slots", "Instantaneous"))
	dataTypes = append(dataTypes, bdplib.CreateDataType(dataTypeTotal, "", "Amount of free parking slots", "Instantaneous"))

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
