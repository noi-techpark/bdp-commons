// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

// SPDX-License-Identifier: AGPL-3.0-or-later

package dc

import (
	"os"
	"parking-offstreet-sta/bdplib"
	"strconv"
	"time"
)

const stationTypeParent string = "ParkingFacility"
const stationType string = "ParkingStation"

const dataTypeShort string = "free_short_stay"
const dataTypeSubs string = "free_subscribers"
const dataTypeTotal string = "free"

var origin string = os.Getenv("ORIGIN")

const bzLat float64 = 46.49067
const bzLon float64 = 11.33982

// GetFacilityData returns data for multiple companies; this identifier filters out STA
const identifier string = "STA – Strutture Trasporto Alto Adige SpA Via dei Conciapelli, 60 39100  Bolzano UID: 00586190217"

func Job() {
	var stations []bdplib.Station

	var dataMap bdplib.DataMap

	var recordsShort []bdplib.Record
	var recordsSubs []bdplib.Record
	var recordsTotal []bdplib.Record

	// get data
	facilities := GetFacilityData()
	// fmt.Printf("%+v\n", facilities)

	ts := time.Now().UnixMilli()

	for _, facility := range facilities.Data.Facilities {

		if facility.ReceiptMerchant == identifier {
			parentStationCode := strconv.Itoa(facility.FacilityId)

			parentStation := bdplib.CreateStation(parentStationCode, facility.Description, stationTypeParent, bzLat, bzLon, origin)
			stations = append(stations, parentStation)

			freePlaces := GetFreePlacesData(facility.FacilityId)
			// fmt.Printf("%+v\n", freePlace)
			// fmt.Println(strconv.Itoa(freePlace.Data.FreePlaces[0].FacilityId))

			for _, freePlace := range freePlaces.Data.FreePlaces {
				parkNo := strconv.Itoa(freePlace.ParkNo)
				stationCode := parentStationCode + "_" + parkNo
				station := bdplib.CreateStation(stationCode, facility.Description, stationType, bzLat, bzLon, origin)
				station.ParentStation = parentStation.Id
				stations = append(stations, station)

				switch freePlace.CountingCategoryNo {
				case 1:
					recordsShort = append(recordsShort, bdplib.CreateRecord(ts, freePlace.FreePlaces, 600))
				case 2:
					recordsSubs = append(recordsSubs, bdplib.CreateRecord(ts, freePlace.FreePlaces, 600))
				default:
					recordsTotal = append(recordsTotal, bdplib.CreateRecord(ts, freePlace.FreePlaces, 600))
				}
				bdplib.AddRecords(stationCode, dataTypeShort, recordsShort, &dataMap)
				bdplib.AddRecords(stationCode, dataTypeSubs, recordsSubs, &dataMap)
				bdplib.AddRecords(stationCode, dataTypeTotal, recordsTotal, &dataMap)
			}
		}
	}

	bdplib.SyncStations(stations)
	bdplib.PushData(stationType, dataMap)
}

func SyncDataTypes() {
	var dataTypes []bdplib.DataType

	dataTypes = append(dataTypes, bdplib.CreateDataType(dataTypeShort, "", "Amount of free 'short stay' parking slots", "Instantaneous"))
	dataTypes = append(dataTypes, bdplib.CreateDataType(dataTypeSubs, "", "Amount of 'subscribed' parking slots", "Instantaneous"))
	dataTypes = append(dataTypes, bdplib.CreateDataType(dataTypeTotal, "", "Amount of free parking slots", "Instantaneous"))

	bdplib.SyncDataTypes(stationType, dataTypes)
}
