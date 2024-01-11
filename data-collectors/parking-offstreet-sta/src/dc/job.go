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

// const stationTypeParent string = "ParkingFacility"
const stationType string = "ParkingStation"

var origin string = os.Getenv("ORIGIN")

const bzLat float64 = 46.49067
const bzLon float64 = 11.33982

// GetFacilityData returns data for multiple companies; this identifier filters out STA
const identifier string = "STA – Strutture Trasporto Alto Adige SpA Via dei Conciapelli, 60 39100  Bolzano UID: 00586190217"

func Job() {
	// var parentStations []bdplib.Station
	var stations []bdplib.Station

	var dataMap bdplib.DataMap
	var records []bdplib.Record

	// var parentDataMap bdplib.DataMap
	// var childDataMap bdplib.DataMap

	// get data
	facilities := GetFacilityData()
	// fmt.Printf("%+v\n", facilities)

	ts := time.Now().UnixMilli()

	for _, facility := range facilities.Data.Facilities {

		if facility.ReceiptMerchant == identifier {
			stationCode := strconv.Itoa(facility.FacilityId)
			station := bdplib.CreateStation(stationCode, facility.Description, stationType, bzLat, bzLon, origin)
			stations = append(stations, station)
			// fmt.Printf("%+v\n", facility)
			freePlaces := GetFreePlacesData(facility.FacilityId)
			// fmt.Printf("%+v\n", freePlace)
			// fmt.Println(strconv.Itoa(freePlace.Data.FreePlaces[0].FacilityId))

			for _, freePlace := range freePlaces.Data.FreePlaces {
				// category 3 is total
				if freePlace.CountingCategoryNo == 3 {
					records = append(records, bdplib.CreateRecord(ts, freePlace.FreePlaces, 600))
				}
			}
			bdplib.AssignRecords(stationCode, "free", records, &dataMap)
		}
	}

	// sync stations
	slog.Info("Sync stations amount: " + strconv.Itoa(len(stations)))
	bdplib.SyncStations(stationType, stations)
	slog.Info("Sync stations done.")

	// push data
	slog.Info("Sync records...")
	bdplib.PushData(stationType, dataMap)
	slog.Info("Sync records done.")
}

func SyncDataTypes() {
	var dataTypes []bdplib.DataType

	dataTypes = append(dataTypes, bdplib.CreateDataType("free", "", "Free parking slots", "Instantaneous"))
	dataTypes = append(dataTypes, bdplib.CreateDataType("occupied", "", "Occupied parking slots", "Instantaneous"))

	bdplib.SyncDataTypes(stationType, dataTypes)
}
