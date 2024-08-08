// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.radelt.utils;

public class SlugUtils {

	public static String getSlug(String input) {
		if (input == null) {
			return null;
		}

		// Convert to lowercase
		String slug = input.toLowerCase();

		// Replace spaces with hyphens
		slug = slug.replaceAll("\\s+", "-");

		// Remove non-alphanumeric characters (except hyphens)
		slug = slug.replaceAll("[^a-z0-9\\-]", "");

		return slug;
	}
}
