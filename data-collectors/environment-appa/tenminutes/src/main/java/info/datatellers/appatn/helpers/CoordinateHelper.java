package info.datatellers.appatn.helpers;

public class CoordinateHelper {
	
	public double[] UTMtoDecimal(int zone, double easting, double northing) {
		return UTMtoDecimal(zone, easting, northing, true);
	}

	public double[] UTMtoDecimal(int zone, double easting, double northing, boolean north) {
		
		if(!north) {
			northing = northing - 10000000;
		}

		double latitude = (northing / 6366197.724 / 0.9996
				+ (1 + 0.006739496742 * Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2)
						- 0.006739496742 * Math.sin(northing / 6366197.724 / 0.9996)
								* Math.cos(northing / 6366197.724 / 0.9996) * (Math
										.atan(Math
												.cos(Math
														.atan((Math
																.exp((easting - 500000) / (0.9996 * 6399593.625
																		/ Math.sqrt((1 + 0.006739496742 * Math.pow(Math
																				.cos(northing / 6366197.724 / 0.9996),
																				2))))
																		* (1 - 0.006739496742 * Math.pow((easting
																				- 500000)
																				/ (0.9996 * 6399593.625 / Math.sqrt(
																						(1 + 0.006739496742 * Math.pow(
																								Math.cos(northing
																										/ 6366197.724
																										/ 0.9996),
																								2)))),
																				2) / 2
																				* Math.pow(
																						Math.cos(northing / 6366197.724
																								/ 0.9996),
																						2)
																				/ 3))
																- Math.exp(-(easting - 500000)
																		/ (0.9996 * 6399593.625 / Math
																				.sqrt((1 + 0.006739496742 * Math.pow(
																						Math.cos(northing / 6366197.724
																								/ 0.9996),
																						2))))
																		* (1 - 0.006739496742 * Math.pow((easting
																				- 500000)
																				/ (0.9996 * 6399593.625 / Math.sqrt(
																						(1 + 0.006739496742 * Math.pow(
																								Math.cos(northing
																										/ 6366197.724
																										/ 0.9996),
																								2)))),
																				2) / 2
																				* Math.pow(
																						Math.cos(northing / 6366197.724
																								/ 0.9996),
																						2)
																				/ 3)))
																/ 2
																/ Math.cos((northing - 0.9996 * 6399593.625 * (northing
																		/ 6366197.724 / 0.9996
																		- 0.006739496742 * 3 / 4 * (northing
																				/ 6366197.724 / 0.9996
																				+ Math.sin(2
																						* northing / 6366197.724
																						/ 0.9996) / 2)
																		+ Math.pow(0.006739496742 * 3 / 4, 2) * 5 / 3
																				* (3 * (northing
																						/ 6366197.724 / 0.9996
																						+ Math.sin(2 * northing
																								/ 6366197.724 / 0.9996)
																								/ 2)
																						+ Math.sin(2 * northing
																								/ 6366197.724 / 0.9996)
																								* Math.pow(Math
																										.cos(northing
																												/ 6366197.724
																												/ 0.9996),
																										2))
																				/ 4
																		- Math.pow(0.006739496742 * 3 / 4, 3)
																				* 35 / 27
																				* (5 * (3
																						* (northing / 6366197.724
																								/ 0.9996
																								+ Math.sin(2 * northing
																										/ 6366197.724
																										/ 0.9996) / 2)
																						+ Math.sin(2 * northing
																								/ 6366197.724 / 0.9996)
																								* Math.pow(
																										Math.cos(northing
																												/ 6366197.724
																												/ 0.9996),
																										2))
																						/ 4
																						+ Math.sin(2 * northing
																								/ 6366197.724 / 0.9996)
																								* Math.pow(
																										Math.cos(northing
																												/ 6366197.724
																												/ 0.9996),
																										2)
																								* Math.pow(Math.cos(
																										northing / 6366197.724
																												/ 0.9996),
																										2))
																				/ 3))
																		/ (0.9996 * 6399593.625 / Math.sqrt((1
																				+ 0.006739496742 * Math.pow(
																						Math.cos(northing
																								/ 6366197.724 / 0.9996),
																						2))))
																		* (1 - 0.006739496742 * Math.pow((easting
																				- 500000)
																				/ (0.9996 * 6399593.625 / Math.sqrt(
																						(1 + 0.006739496742 * Math.pow(
																								Math.cos(northing
																										/ 6366197.724
																										/ 0.9996),
																								2)))),
																				2) / 2
																				* Math.pow(
																						Math.cos(northing / 6366197.724
																								/ 0.9996),
																						2))
																		+ northing / 6366197.724 / 0.9996)))
												* Math.tan((northing - 0.9996 * 6399593.625 * (northing / 6366197.724 / 0.9996
														- 0.006739496742 * 3 / 4 * (northing / 6366197.724
																/ 0.9996
																+ Math.sin(2 * northing / 6366197.724 / 0.9996) / 2)
														+ Math.pow(0.006739496742 * 3 / 4, 2) * 5 / 3 * (3
																* (northing / 6366197.724 / 0.9996
																		+ Math.sin(2 * northing / 6366197.724 / 0.9996)
																				/ 2)
																+ Math.sin(
																		2 * northing / 6366197.724 / 0.9996)
																		* Math.pow(
																				Math.cos(northing / 6366197.724 / 0.9996),
																				2))
																/ 4
														- Math.pow(0.006739496742 * 3 / 4, 3) * 35 / 27 * (5 * (3
																* (northing / 6366197.724 / 0.9996
																		+ Math.sin(2 * northing / 6366197.724 / 0.9996)
																				/ 2)
																+ Math.sin(2 * northing / 6366197.724 / 0.9996) * Math
																		.pow(Math.cos(northing / 6366197.724 / 0.9996), 2))
																/ 4
																+ Math.sin(2 * northing / 6366197.724 / 0.9996) * Math
																		.pow(Math.cos(northing / 6366197.724 / 0.9996), 2)
																		* Math.pow(
																				Math.cos(northing / 6366197.724 / 0.9996),
																				2))
																/ 3))
														/ (0.9996 * 6399593.625
																/ Math.sqrt((1 + 0.006739496742 * Math.pow(
																		Math.cos(northing / 6366197.724 / 0.9996), 2))))
														* (1 - 0.006739496742
																* Math.pow((easting - 500000) / (0.9996 * 6399593.625
																		/ Math.sqrt((1 + 0.006739496742 * Math.pow(
																				Math.cos(northing / 6366197.724 / 0.9996),
																				2)))),
																		2)
																/ 2
																* Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2))
														+ northing / 6366197.724 / 0.9996))
										- northing / 6366197.724 / 0.9996)
								* 3 / 2)
						* (Math.atan(Math
								.cos(Math.atan((Math.exp((easting - 500000) / (0.9996 * 6399593.625 / Math.sqrt(
										(1 + 0.006739496742 * Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2))))
										* (1 - 0.006739496742
												* Math.pow(
														(easting - 500000) / (0.9996 * 6399593.625
																/ Math.sqrt((1 + 0.006739496742 * Math.pow(
																		Math.cos(northing / 6366197.724 / 0.9996), 2)))),
														2)
												/ 2 * Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2) / 3))
										- Math.exp(-(easting - 500000) / (0.9996 * 6399593.625 / Math.sqrt((1
												+ 0.006739496742
														* Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2))))
												* (1 - 0.006739496742 * Math.pow(
														(easting - 500000) / (0.9996 * 6399593.625
																/ Math.sqrt((1 + 0.006739496742 * Math.pow(
																		Math.cos(northing / 6366197.724 / 0.9996), 2)))),
														2) / 2 * Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2)
														/ 3)))
										/ 2
										/ Math.cos((northing - 0.9996 * 6399593.625 * (northing / 6366197.724 / 0.9996
												- 0.006739496742 * 3 / 4
														* (northing / 6366197.724 / 0.9996
																+ Math.sin(2 * northing / 6366197.724 / 0.9996) / 2)
												+ Math.pow(0.006739496742 * 3 / 4, 2) * 5 / 3 * (3
														* (northing / 6366197.724 / 0.9996
																+ Math.sin(2 * northing / 6366197.724 / 0.9996) / 2)
														+ Math.sin(2 * northing / 6366197.724 / 0.9996)
																* Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2))
														/ 4
												- Math.pow(0.006739496742 * 3 / 4, 3) * 35 / 27 * (5 * (3
														* (northing / 6366197.724 / 0.9996
																+ Math.sin(2 * northing / 6366197.724 / 0.9996) / 2)
														+ Math.sin(2 * northing / 6366197.724 / 0.9996)
																* Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2))
														/ 4
														+ Math.sin(2 * northing / 6366197.724 / 0.9996)
																* Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2)
																* Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2))
														/ 3))
												/ (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742
														* Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2))))
												* (1 - 0.006739496742 * Math.pow(
														(easting - 500000) / (0.9996 * 6399593.625
																/ Math.sqrt((1 + 0.006739496742 * Math.pow(
																		Math.cos(northing / 6366197.724 / 0.9996), 2)))),
														2) / 2 * Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2))
												+ northing / 6366197.724 / 0.9996)))
								* Math.tan((northing - 0.9996 * 6399593.625 * (northing / 6366197.724 / 0.9996
										- 0.006739496742 * 3 / 4
												* (northing / 6366197.724 / 0.9996
														+ Math.sin(2 * northing / 6366197.724 / 0.9996) / 2)
										+ Math.pow(0.006739496742 * 3 / 4, 2) * 5 / 3 * (3
												* (northing / 6366197.724 / 0.9996
														+ Math.sin(2 * northing / 6366197.724 / 0.9996) / 2)
												+ Math.sin(2 * northing / 6366197.724 / 0.9996)
														* Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2))
												/ 4
										- Math.pow(0.006739496742 * 3 / 4, 3) * 35 / 27 * (5
												* (3 * (northing / 6366197.724 / 0.9996
														+ Math.sin(2 * northing / 6366197.724 / 0.9996) / 2)
														+ Math.sin(2 * northing / 6366197.724 / 0.9996)
																* Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2))
												/ 4
												+ Math.sin(2 * northing / 6366197.724 / 0.9996)
														* Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2)
														* Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2))
												/ 3))
										/ (0.9996 * 6399593.625
												/ Math.sqrt((1 + 0.006739496742
														* Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2))))
										* (1 - 0.006739496742
												* Math.pow(
														(easting - 500000) / (0.9996 * 6399593.625
																/ Math.sqrt((1 + 0.006739496742 * Math.pow(
																		Math.cos(northing / 6366197.724 / 0.9996), 2)))),
														2)
												/ 2 * Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2))
										+ northing / 6366197.724 / 0.9996))
								- northing / 6366197.724 / 0.9996))
				* 180 / Math.PI;
		latitude = Math.round(latitude * 10000000);
		latitude = latitude / 10000000;

		double longitude = Math
				.atan((Math
						.exp((easting - 500000) / (0.9996 * 6399593.625 / Math
								.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2)))) * (1
										- 0.006739496742
												* Math.pow(
														(easting - 500000) / (0.9996 * 6399593.625
																/ Math.sqrt((1 + 0.006739496742 * Math.pow(
																		Math.cos(northing / 6366197.724 / 0.9996), 2)))),
														2)
												/ 2 * Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2) / 3))
						- Math.exp(-(easting - 500000) / (0.9996 * 6399593.625 / Math
								.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2)))) * (1
										- 0.006739496742
												* Math.pow(
														(easting - 500000) / (0.9996 * 6399593.625
																/ Math.sqrt((1 + 0.006739496742 * Math.pow(
																		Math.cos(northing / 6366197.724 / 0.9996), 2)))),
														2)
												/ 2 * Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2) / 3)))
						/ 2
						/ Math.cos((northing - 0.9996 * 6399593.625 * (northing / 6366197.724 / 0.9996
								- 0.006739496742 * 3 / 4
										* (northing / 6366197.724 / 0.9996
												+ Math.sin(2 * northing / 6366197.724 / 0.9996) / 2)
								+ Math.pow(0.006739496742 * 3 / 4, 2) * 5 / 3
										* (3 * (northing / 6366197.724 / 0.9996
												+ Math.sin(2 * northing / 6366197.724 / 0.9996) / 2)
												+ Math.sin(2 * northing / 6366197.724 / 0.9996)
														* Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2))
										/ 4
								- Math.pow(0.006739496742 * 3 / 4, 3) * 35 / 27 * (5
										* (3 * (northing / 6366197.724 / 0.9996
												+ Math.sin(2 * northing / 6366197.724 / 0.9996) / 2)
												+ Math.sin(2 * northing / 6366197.724 / 0.9996)
														* Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2))
										/ 4
										+ Math.sin(2 * northing / 6366197.724 / 0.9996)
												* Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2)
												* Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2))
										/ 3))
								/ (0.9996 * 6399593.625 / Math.sqrt((1
										+ 0.006739496742 * Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2))))
								* (1 - 0.006739496742
										* Math.pow((easting - 500000) / (0.9996 * 6399593.625
												/ Math.sqrt((1 + 0.006739496742
														* Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2)))),
												2)
										/ 2 * Math.pow(Math.cos(northing / 6366197.724 / 0.9996), 2))
								+ northing / 6366197.724 / 0.9996))
				* 180 / Math.PI + zone * 6 - 183;
		longitude = Math.round(longitude * 10000000);
		longitude = longitude / 10000000;
		
		double[] result = {longitude, latitude};
				
		return result;
	}
}
