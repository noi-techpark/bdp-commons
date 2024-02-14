// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

// SPDX-License-Identifier: AGPL-3.0-or-later

package log

import (
	"log/slog"
	"os"
	"strings"
)

// read logger level from env and uses INFO as default
func InitLogger() {

	logLevel := os.Getenv("LOG_LEVEL")

	level := new(slog.LevelVar)

	level.Set(parseLogLevel(logLevel))

	logger := slog.New(slog.NewJSONHandler(os.Stdout, &slog.HandlerOptions{
		Level: level,
	}))
	slog.SetDefault(logger)

	slog.Info("Start logger with level: " + logLevel)
}

func parseLogLevel(level string) slog.Level {
	switch strings.ToUpper(level) {
	case "DEBUG":
		return slog.LevelDebug
	case "WARNING":
		return slog.LevelWarn
	case "ERROR":
		return slog.LevelError
	case "INFO":
		return slog.LevelInfo
	default:
		return slog.LevelInfo
	}
}
