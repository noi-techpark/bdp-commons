package dc

import (
	"encoding/json"
	"log/slog"
)

type Forecast struct {
	Info           Info           `json:"info"`
	Municipalities []Municipality `json:"municipalities"`
}

type Info struct {
	Model            string `json:"model"`
	CurrentModelRun  string `json:"currentModelRun"`
	NextModelRun     string `json:"nextModelRun"`
	FileName         string `json:"fileName"`
	FileCreationDate string `json:"fileCreationDate"`
	AbsTempMin       int    `json:"absTempMin"`
	AbsTempMax       int    `json:"absTempMax"`
	AbsPrecMin       int    `json:"absPrecMin"`
	AbsPrecMax       int    `json:"absPrecMax"`
}

type Municipality struct {
	Code       string   `json:"code"`
	NameDe     string   `json:"nameDe"`
	NameIt     string   `json:"nameIt"`
	NameEn     string   `json:"nameEn"`
	NameRm     string   `json:"nameRm"`
	TempMin24  ValueSet `json:"tempMin24"`
	TempMax24  ValueSet `json:"tempMax24"`
	Temp3      ValueSet `json:"temp3"`
	Ssd24      ValueSet `json:"ssd24"`
	PrecProb3  ValueSet `json:"precProb3"`
	PrecProb24 ValueSet `json:"precProb24"`
	PrecSum3   ValueSet `json:"precSum3"`
	PrecSum24  ValueSet `json:"precSum24"`
	Symbols3   ValueSet `json:"symbols3"`
	Symbols24  ValueSet `json:"symbols24"`
	WindDir3   ValueSet `json:"windDir3"`
	WindSpd3   ValueSet `json:"windSpd3"`
}

type ValueSet struct {
	NameDe string  `json:"nameDe"`
	NameIt string  `json:"nameIt"`
	NameEn string  `json:"nameEn"`
	NameRm string  `json:"nameRm"`
	Unit   string  `json:"unit"`
	Data   []Value `json:"data"`
}

type Value struct {
	Date  string      `json:"date"`
	Value interface{} `json:"value"`
}

func Mapping(data []byte) Forecast {
	var forecast Forecast

	err := json.Unmarshal(data, &forecast)
	if err != nil {
		slog.Error("error", err)
	}

	return forecast
}

func MapQuantitative(value string) string {
	switch value {
	case "a_n":
	case "a_d":
		return "sunny"
	case "b_n":
	case "b_d":
		return "partly cloudy"
	case "c_n":
	case "c_d":
		return "cloudy"
	case "d_n":
	case "d_d":
		return "very cloudy"
	case "e_n":
	case "e_d":
		return "overcast"
	case "f_n":
	case "f_d":
		return "cloudy with moderate rain"
	case "g_n":
	case "g_d":
		return "cloudy with intense rain"
	case "h_n":
	case "h_d":
		return "overcast with moderate rain"
	case "i_n":
	case "i_d":
		return "overcast with intense rain"
	case "j_n":
	case "j_d":
		return "overcast with light rain"
	case "k_n":
	case "k_d":
		return "translucent cloudy"
	case "l_n":
	case "l_d":
		return "cloudy with light snow"
	case "m_n":
	case "m_d":
		return "cloudy with heavy snow"
	case "n_n":
	case "n_d":
		return "overcast with light snow"
	case "o_n":
	case "o_d":
		return "overcast with moderate snow"
	case "p_n":
	case "p_d":
		return "overcast with intense snow"
	case "q_n":
	case "q_d":
		return "cloudy with rain and snow"
	case "r_n":
	case "r_d":
		return "overcast with rain and snow"
	case "s_n":
	case "s_d":
		return "low cloudiness"
	case "t_n":
	case "t_d":
		return "fog"
	case "u_n":
	case "u_d":
		return "cloudy, thunderstorms with moderate showers"
	case "v_n":
	case "v_d":
		return "cloudy, thunderstorms with intense showers"
	case "w_n":
	case "w_d":
		return "cloudy, thunderstorms with moderate snowy and rainy showers"
	case "x_n":
	case "x_d":
		return "cloudy, thunderstorms with intense snowy and rainy showers"
	case "y_n":
	case "y_d":
		return "cloudy, thunderstorms with moderate snowy showers"
	default:
		slog.Error("No mapping configured for value: " + value)
	}
	return ""
}
