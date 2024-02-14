// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

// SPDX-License-Identifier: AGPL-3.0-or-later

package bdplib

import (
	"encoding/json"
	"io"
	"log/slog"
	"net/http"
	"net/url"
	"os"
	"strconv"
	"strings"
	"time"
)

type Token struct {
	AccessToken      string `json:"access_token"`
	ExpiresIn        int64  `json:"expires_in"`
	NotBeforePolicy  int64  `json:"not-before-policy"`
	RefreshExpiresIn int64  `json:"refresh_expires_in"`
	TokenType        string `json:"token_type"`
	RefreshToken     string `json:"refresh_token"`
	Scope            string
}

var tokenUri string = os.Getenv("OAUTH_TOKEN_URI")
var clientId string = os.Getenv("OAUTH_CLIENT_ID")
var clientSecret string = os.Getenv("OAUTH_CLIENT_SECRET")

var token Token

var tokenExpiry int64

func GetToken() string {
	ts := time.Now().Unix()

	if len(token.AccessToken) == 0 || ts > tokenExpiry {
		// if no token is available or refreshToken is expired, get new token
		newToken()
	}

	return token.AccessToken
}

func newToken() {
	slog.Info("Getting new token...")
	params := url.Values{}
	params.Add("client_id", clientId)
	params.Add("client_secret", clientSecret)
	params.Add("grant_type", "client_credentials")

	authRequest(params)

	slog.Info("Getting new token done.")
}

func authRequest(params url.Values) {
	body := strings.NewReader(params.Encode())

	req, err := http.NewRequest("POST", tokenUri, body)
	if err != nil {
		slog.Error("error", err)
		return
	}
	req.Header.Set("Content-Type", "application/x-www-form-urlencoded")

	resp, err := http.DefaultClient.Do(req)
	if err != nil {
		slog.Error("error", err)
		return
	}
	defer resp.Body.Close()

	slog.Info("Auth response code is: " + strconv.Itoa(resp.StatusCode))
	if resp.StatusCode == http.StatusOK {
		bodyBytes, err := io.ReadAll(resp.Body)
		if err != nil {
			slog.Error("error", err)
			return
		}

		err = json.Unmarshal(bodyBytes, &token)
		if err != nil {
			slog.Error("error", err)
			return
		}
	}

	// calculate token expiry timestamp with 600 seconds margin
	tokenExpiry = time.Now().Unix() + token.ExpiresIn - 600

	slog.Debug("auth token expires in " + strconv.FormatInt(tokenExpiry, 10))
}
