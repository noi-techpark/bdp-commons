// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package info.datatellers.appatn.helpers;

public class APPADataFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2570038310755348075L;

	public APPADataFormatException(String message) {
		super(message);
	}

	public APPADataFormatException(String message, Throwable throwable) {
		super(message, throwable);
	}
}