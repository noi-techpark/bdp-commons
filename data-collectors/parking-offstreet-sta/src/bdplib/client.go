// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

// SPDX-License-Identifier: AGPL-3.0-or-later

package bdplib

import (
	"bufio"
	"bytes"
	"encoding/json"
	"log/slog"
	"net/http"
	"os"
	"strconv"
)

type Provenance struct {
	Lineage              string `json:"lineage"`
	DataCollector        string `json:"dataCollector"`
	DataCollectorVersion string `json:"dataCollectorVersion"`
}

type DataType struct {
	Name        string            `json:"name"`
	Unit        string            `json:"unit"`
	Description string            `json:"description"`
	Rtype       string            `json:"rType"`
	Period      uint32            `json:"period"`
	Metadata    map[string]string `json:"metadata"`
}

type Station struct {
	Id            string            `json:"id"`
	Name          string            `json:"name"`
	StationType   string            `json:"stationType"`
	Latitude      float64           `json:"latitude"`
	Longitude     float64           `json:"longitude"`
	Origin        string            `json:"origin"`
	ParentStation string            `json:"parentStation"`
	Metadata      map[string]string `json:"metadata"`
}

type DataMap struct {
	Name       string             `json:"name"`
	Data       []Record           `json:"data"`
	Branch     map[string]DataMap `json:"branch"`
	Provenance string             `json:"provenance"`
}

type Record struct {
	Value     interface{} `json:"value"`
	Period    uint64      `json:"period"`
	Timestamp int64       `json:"timestamp"`
	Type      string      `json:"_t"`
}

const syncDataTypesPath string = "/syncDataTypes"
const syncStationsPath string = "/syncStations"
const pushRecordsPath string = "/pushRecords"
const getDateOfLastRecordPath string = "/getDateOfLastRecord"
const stationsPath string = "/stations"
const provenancePath string = "/provenance"

var provenanceUuid string

var baseUri string = os.Getenv("BASE_URI")

var prv string = os.Getenv("PROVENANCE_VERSION")
var prn string = os.Getenv("PROVENANCE_NAME")

var origin string = os.Getenv("ORIGIN")

func SyncDataTypes(stationType string, dataTypes []DataType) {
	pushProvenance()

	slog.Debug("Syncing data types...")

	url := baseUri + syncDataTypesPath + "?stationType=" + stationType + "&prn=" + prn + "&prv=" + prv

	postToWriter(dataTypes, url)

	slog.Debug("Syncing data types done.")
}

func SyncStations(stationType string, stations []Station) {
	pushProvenance()

	slog.Info("Syncing " + strconv.Itoa(len(stations)) + " " + stationType + " stations...")

	url := baseUri + syncStationsPath + "/" + stationType + "?prn=" + prn + "&prv=" + prv

	postToWriter(stations, url)

	slog.Info("Syncing stations done.")
}

func PushData(stationType string, dataMap DataMap) {
	pushProvenance()

	slog.Info("Pushing records...")

	url := baseUri + pushRecordsPath + "/" + stationType + "?prn=" + prn + "&prv=" + prv

	postToWriter(dataMap, url)

	slog.Info("Pushing records done.")
}

func CreateDataType(name string, unit string, description string, rtype string) DataType {
	// TODO add some checks
	return DataType{
		Name:        name,
		Unit:        unit,
		Description: description,
		Rtype:       rtype,
	}
}

func CreateStation(id string, name string, stationType string, lat float64, lon float64, origin string) Station {
	// TODO add some checks
	var station = Station{
		Name:        name,
		StationType: stationType,
		Latitude:    lat,
		Longitude:   lon,
		Origin:      origin,
		Id:          id,
		// Metadata:      metaData,
		// ParentStation: parentStation,
	}
	return station
}

func CreateRecord(ts int64, value interface{}, period uint64) Record {
	// TODO add some checks
	var record = Record{
		Value:     value,
		Timestamp: ts,
		Period:    period,
		Type:      "it.bz.idm.bdp.dto.SimpleRecordDto",
	}
	return record
}

func createDataMap() DataMap {
	var dataMap = DataMap{
		Name:       "(default)",
		Provenance: provenanceUuid,
		Branch:     make(map[string]DataMap),
	}
	return dataMap
}

func AddRecords(stationCode string, dataType string, records []Record, dataMap *DataMap) {

	if dataMap.Name == "" {
		*dataMap = createDataMap()
	}

	if dataMap.Branch[stationCode].Name == "" {
		dataMap.Branch[stationCode] = DataMap{
			Name:   "(default)",
			Branch: make(map[string]DataMap),
		}
		slog.Debug("new station in branch " + stationCode)
	}

	if dataMap.Branch[stationCode].Branch[dataType].Name == "" {
		dataMap.Branch[stationCode].Branch[dataType] = DataMap{
			Name: "(default)",
			Data: records,
		}
		// to assign a value to a struct in a map, this code part is needed
		// https://stackoverflow.com/a/69006398/8794667
	} else if entry, ok := dataMap.Branch[stationCode].Branch[dataType]; ok {
		entry.Data = append(entry.Data, records...)
		dataMap.Branch[stationCode].Branch[dataType] = entry
	}
}

func postToWriter(data interface{}, fullUrl string) (string, error) {
	json, err := json.Marshal(data)
	if err != nil {
		slog.Error("error", err)
	}

	client := http.Client{}
	req, err := http.NewRequest("POST", fullUrl, bytes.NewBuffer(json))
	if err != nil {
		slog.Error("error", err)
	}

	req.Header = http.Header{
		"Content-Type":  {"application/json"},
		"Authorization": {"Bearer " + GetToken()},
	}

	res, err := client.Do(req)
	if err != nil {
		slog.Error("error", err)
	}

	slog.Info("Writer post response code: " + res.Status)

	scanner := bufio.NewScanner(res.Body)
	for i := 0; scanner.Scan() && i < 5; i++ {
		return scanner.Text(), nil
	}

	err = scanner.Err()
	if err != nil {
		slog.Error("error", err)
	}
	return "", err
}

func pushProvenance() {
	if len(provenanceUuid) > 0 {
		return
	}

	slog.Info("Pushing provenance...")
	slog.Info("prv: " + prv + " prn: " + prn)

	var provenance = Provenance{
		DataCollector:        prn,
		DataCollectorVersion: prv,
		Lineage:              origin,
	}

	url := baseUri + provenancePath + "?&prn=" + prn + "&prv=" + prv

	res, err := postToWriter(provenance, url)

	if err != nil {
		slog.Error("error", err)
	}

	provenanceUuid = res

	slog.Info("Pushing provenance done.")
}
