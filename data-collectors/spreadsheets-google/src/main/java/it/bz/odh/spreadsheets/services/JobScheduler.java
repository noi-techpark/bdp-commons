// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.odh.spreadsheets.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class JobScheduler {

	@Lazy
	@Autowired
	private SpreadsheetWatcher watcher;

    public void watchBluetoothBoxesSpreadsheet() {
		watcher.registerWatch();
	}

}
