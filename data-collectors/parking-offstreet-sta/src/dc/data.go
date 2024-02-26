// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

// SPDX-License-Identifier: AGPL-3.0-or-later

package dc

import (
	"encoding/json"
	"io"
	"log/slog"
	"net/http"
	"strconv"
)

type FacilityResponse struct {
	Data FacilityData
}

type FacilityData struct {
	Status     string
	Facilities []Facility
}

type Facility struct {
	IdCompany       int
	FacilityId      int
	Description     string
	City            string
	Address         string
	ZIPCode         string
	Telephone1      string
	Telephone2      string
	PostNumber      int
	ReceiptMerchant string
	Web             string
}

type FreePlaceResponse struct {
	Data FreePlaceData
}

type FreePlaceData struct {
	Status     string
	FreePlaces []FreePlace
}

type FreePlace struct {
	FacilityId          int
	FacilityDescription string
	ParkNo              int
	CountingCategoryNo  int
	CountingCategory    string
	FreeLimit           int
	OccupancyLimit      int
	CurrentLevel        int
	Reservation         int
	Capacity            int
	FreePlaces          int
	Latitude            float64
	Longitude           float64
}

const facilityUrl = "https://www.onecenter.info/api/DAZ/GetFacilities"
const freePlacesUrl = "https://www.onecenter.info/api/DAZ/FacilityFreePlaces?FacilityID="

func GetFacilityData() FacilityResponse {
	var response FacilityResponse
	getData(facilityUrl, &response)
	return response
}

func GetFreePlacesData(facilityId int) FreePlaceResponse {
	var response FreePlaceResponse
	getData(freePlacesUrl+strconv.Itoa(facilityId), &response)
	return response
}

func getData(url string, response interface{}) {

	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		slog.Error("error", err)
	}
	req.Header = http.Header{
		"Content-Type":  {"application/json"},
		"Authorization": {"Bearer " + GetToken()},
	}

	resp, err := http.DefaultClient.Do(req)
	if err != nil {
		slog.Error("error", err)
	}
	defer resp.Body.Close()

	slog.Info("Auth response code is: " + strconv.Itoa(resp.StatusCode))
	if resp.StatusCode == http.StatusOK {
		bodyBytes, err := io.ReadAll(resp.Body)
		if err != nil {
			slog.Error("error", err)
		}

		err = json.Unmarshal(bodyBytes, &response)
		if err != nil {
			slog.Error("error", err)
		}
	}
}
