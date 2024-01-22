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

const dataTypeFreeShort string = "free_" + shortStay
const dataTypeFreeSubs string = "free_" + Subscribers
const dataTypeFreeTotal string = "free"
const dataTypeOccupiedShort string = "occupied_" + shortStay
const dataTypeOccupiedSubs string = "occupied_" + Subscribers
const dataTypeOccupiedTotal string = "occupied"

var origin string = os.Getenv("ORIGIN")

const bzLat float64 = 46.49067
const bzLon float64 = 11.33982

// GetFacilityData returns data for multiple companies; this identifier filters out STA
const identifier string = "STA – Strutture Trasporto Alto Adige SpA Via dei Conciapelli, 60 39100  Bolzano UID: 00586190217"

func Job() {
	var parentStations []bdplib.Station
	// save stations by stationCode
	stations := make(map[string]bdplib.Station)

	var dataMapParent bdplib.DataMap
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

			// total facility measurements
			freeTotalSum := 0
			occupiedTotalSum := 0
			capacityTotal := 0

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

				// free
				var recordsFreeShort []bdplib.Record
				var recordsFreeSubs []bdplib.Record
				var recordsFreeTotal []bdplib.Record
				// occupied
				var recordsOccupiedShort []bdplib.Record
				var recordsOccupiedSubs []bdplib.Record
				var recordsOccupiedTotal []bdplib.Record

				switch freePlace.CountingCategoryNo {
				// Short Stay
				case 1:
					station.MetaData["FreeLimit_"+shortStay] = freePlace.FreeLimit
					station.MetaData["OccupancyLimit_"+shortStay] = freePlace.OccupancyLimit
					station.MetaData["Capacity_"+shortStay] = freePlace.Capacity
					recordsFreeShort = append(recordsFreeShort, bdplib.CreateRecord(ts, freePlace.FreePlaces, 600))
					recordsOccupiedShort = append(recordsOccupiedShort, bdplib.CreateRecord(ts, freePlace.CurrentLevel, 600))
				// Subscribed
				case 2:
					station.MetaData["FreeLimit_"+Subscribers] = freePlace.FreeLimit
					station.MetaData["OccupancyLimit_"+Subscribers] = freePlace.OccupancyLimit
					station.MetaData["Capacity_"+Subscribers] = freePlace.Capacity
					recordsFreeSubs = append(recordsFreeSubs, bdplib.CreateRecord(ts, freePlace.FreePlaces, 600))
					recordsOccupiedSubs = append(recordsOccupiedSubs, bdplib.CreateRecord(ts, freePlace.CurrentLevel, 600))
				// Total
				default:
					station.MetaData["FreeLimit"] = freePlace.FreeLimit
					station.MetaData["OccupancyLimit"] = freePlace.OccupancyLimit
					station.MetaData["Capacity"] = freePlace.Capacity
					recordsFreeTotal = append(recordsFreeTotal, bdplib.CreateRecord(ts, freePlace.FreePlaces, 600))
					recordsOccupiedTotal = append(recordsOccupiedTotal, bdplib.CreateRecord(ts, freePlace.CurrentLevel, 600))

					// facility data
					freeTotalSum += freePlace.FreePlaces
					occupiedTotalSum += freePlace.CurrentLevel
					capacityTotal += freePlace.Capacity
				}
				// free
				bdplib.AddRecords(stationCode, dataTypeFreeShort, recordsFreeShort, &dataMap)
				bdplib.AddRecords(stationCode, dataTypeFreeSubs, recordsFreeSubs, &dataMap)
				bdplib.AddRecords(stationCode, dataTypeFreeTotal, recordsFreeTotal, &dataMap)
				// occupied
				bdplib.AddRecords(stationCode, dataTypeOccupiedShort, recordsOccupiedShort, &dataMap)
				bdplib.AddRecords(stationCode, dataTypeOccupiedSubs, recordsOccupiedSubs, &dataMap)
				bdplib.AddRecords(stationCode, dataTypeOccupiedTotal, recordsOccupiedTotal, &dataMap)

			}
			// assign total facility data, if data is not 0
			if freeTotalSum > 0 {
				bdplib.AddRecords(parentStationCode, dataTypeFreeTotal, []bdplib.Record{bdplib.CreateRecord(ts, freeTotalSum, 600)}, &dataMapParent)
			}
			if occupiedTotalSum > 0 {
				bdplib.AddRecords(parentStationCode, dataTypeOccupiedTotal, []bdplib.Record{bdplib.CreateRecord(ts, occupiedTotalSum, 600)}, &dataMapParent)
			}
			if capacityTotal > 0 {
				parentStation.MetaData["Capacity"] = capacityTotal
			}
		}
	}
	bdplib.SyncStations(stationTypeParent, parentStations)
	bdplib.SyncStations(stationType, values(stations))
	bdplib.PushData(stationTypeParent, dataMapParent)
	bdplib.PushData(stationType, dataMap)
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
