// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.airquality;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

public class FileNameComparator implements Comparator<File> {

	@Override
	public int compare(File a, File b) {
		try {
			long aTime = asTime(a.getName());
			long bTime = asTime(b.getName());
			return aTime != bTime ? aTime > bTime ? 1 : -1 : 0;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	private static long asTime(String filename) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyddMM_HHmm");
		return formatter.parse(filename.substring(0, filename.lastIndexOf("."))).getTime();
	}
}
