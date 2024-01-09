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

type ApiResponse struct {
	Data Data
}

type Data struct {
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

const apiUrl = "https://www.onecenter.info/api/DAZ/GetFacilities"

func GetData() ApiResponse {

	var apiResponse ApiResponse

	req, err := http.NewRequest("GET", apiUrl, nil)
	if err != nil {
		slog.Error("error", err)
		return apiResponse
	}
	req.Header = http.Header{
		"Content-Type":  {"application/json"},
		"Authorization": {"Bearer " + GetToken()},
	}

	resp, err := http.DefaultClient.Do(req)
	if err != nil {
		slog.Error("error", err)
		return apiResponse
	}
	defer resp.Body.Close()

	slog.Info("Auth response code is: " + strconv.Itoa(resp.StatusCode))
	if resp.StatusCode == http.StatusOK {
		bodyBytes, err := io.ReadAll(resp.Body)
		if err != nil {
			slog.Error("error", err)
			return apiResponse
		}

		err = json.Unmarshal(bodyBytes, &apiResponse)
		if err != nil {
			slog.Error("error", err)
			return apiResponse
		}
	}
	return apiResponse
}
