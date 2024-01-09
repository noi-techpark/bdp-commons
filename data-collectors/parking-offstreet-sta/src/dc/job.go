// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

// SPDX-License-Identifier: AGPL-3.0-or-later

package dc

import (
	"parking-offstreet-sta/lib"
)

const stationTypeParent string = "ParkingFacility"
const stationTypeChild string = "ParkingStation"
const origin string = "skidata"

func Job() {
	var parentStations []lib.Station
	var childStations []lib.Station

	// var parentDataMap lib.DataMap
	// var childDataMap lib.DataMap

	// get data

	// sync stations
	lib.SyncStations(stationTypeParent, parentStations)
	lib.SyncStations(stationTypeParent, childStations)

	// push data
	// lib.PushData(stationTypeModel, modelDataMap)

}

func DataTypes() {
	var dataTypes []lib.DataType

	dataTypes = append(dataTypes, lib.CreateDataType("free", "", "Free parking slots", "Instantaneous"))
	dataTypes = append(dataTypes, lib.CreateDataType("occupied", "", "Occupied parking slots", "Instantaneous"))

	lib.SyncDataTypes(stationTypeChild, dataTypes)
}
