// SPDX-FileCopyrightText: (c) NOI Techpark <digital@noi.bz.it>

// SPDX-License-Identifier: AGPL-3.0-or-later

package main

import (
	"log/slog"
	"os"
	"time"

	"parking-offstreet-sta/dc"
	"parking-offstreet-sta/log"

	"github.com/go-co-op/gocron"
)

func main() {
	log.InitLogger()

	dc.SyncDataTypes()

	cron := os.Getenv("SCHEDULER_CRON")
	slog.Debug("Cron defined as: " + cron)

	if len(cron) == 0 {
		slog.Error("Cron job definition in env missing")
		os.Exit(1)
	}

	// call job once at startup
	dc.Job()

	// start cron job
	s := gocron.NewScheduler(time.UTC)
	s.CronWithSeconds(cron).Do(dc.Job)
	s.StartBlocking()
}
