// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.noi.a22elaborations;

public class Vehicle
{

	private String stationcode;
	private long timestamp;
	private double distance;
	private double headway;
	private double length;
	private int axles;
	private boolean against_traffic;
	private int class_nr;
	private double speed;
	private int direction;
	private double vmed_50;
	private boolean classe_1_avg;
	private boolean classe_1_count;
	private boolean classe_2_avg;
	private boolean classe_2_count;
	private boolean classe_3_avg;
	private boolean classe_3_count;
	private boolean classe_4_avg;
	private boolean classe_4_count;
	private boolean classe_5_avg;
	private boolean classe_5_count;
	private boolean classe_6_avg;
	private boolean classe_6_count;
	private boolean classe_7_avg;
	private boolean classe_7_count;
	private boolean classe_8_avg;
	private boolean classe_8_count;
	private boolean classe_9_avg;
	private boolean classe_9_count;
	private int nr_classes_count;
	private int nr_classes_avg;

	public Vehicle(String stationcode, long timestamp, double distance, double headway, double length, int axles,
			boolean against_traffic, int class_nr, double speed, int direction, double vmed_50, boolean classe_1_avg,
			boolean classe_1_count, boolean classe_2_avg, boolean classe_2_count, boolean classe_3_avg,
			boolean classe_3_count, boolean classe_4_avg, boolean classe_4_count, boolean classe_5_avg,
			boolean classe_5_count, boolean classe_6_avg, boolean classe_6_count, boolean classe_7_avg,
			boolean classe_7_count, boolean classe_8_avg, boolean classe_8_count, boolean classe_9_avg,
			boolean classe_9_count, int nr_classes, int nr_classes_avg)
	{
		this.stationcode = stationcode;
		this.timestamp = timestamp;
		this.distance = distance;
		this.headway = headway;
		this.length = length;
		this.axles = axles;
		this.against_traffic = against_traffic;
		this.class_nr = class_nr;
		this.speed = speed;
		this.direction = direction;
		this.vmed_50 = vmed_50;
		this.classe_1_avg = classe_1_avg;
		this.classe_1_count = classe_1_count;
		this.classe_2_avg = classe_2_avg;
		this.classe_2_count = classe_2_count;
		this.classe_3_avg = classe_3_avg;
		this.classe_3_count = classe_3_count;
		this.classe_4_avg = classe_4_avg;
		this.classe_4_count = classe_4_count;
		this.classe_5_avg = classe_5_avg;
		this.classe_5_count = classe_5_count;
		this.classe_6_avg = classe_6_avg;
		this.classe_6_count = classe_6_count;
		this.classe_7_avg = classe_7_avg;
		this.classe_7_count = classe_7_count;
		this.classe_8_avg = classe_8_avg;
		this.classe_8_count = classe_8_count;
		this.classe_9_avg = classe_9_avg;
		this.classe_9_count = classe_9_count;
		this.nr_classes_count = nr_classes;
		this.nr_classes_avg = nr_classes_avg;
	}

	public String getStationcode()
	{
		return stationcode;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public double getDistance()
	{
		return distance;
	}

	public double getHeadway()
	{
		return headway;
	}

	public double getLength()
	{
		return length;
	}

	public int getAxles()
	{
		return axles;
	}

	public boolean isAgainst_traffic()
	{
		return against_traffic;
	}

	public int getClass_nr()
	{
		return class_nr;
	}

	public double getSpeed()
	{
		return speed;
	}

	public int getDirection()
	{
		return direction;
	}

	public double getVmed_50()
	{
		return vmed_50;
	}

	public boolean isClasse_1_avg()
	{
		return classe_1_avg;
	}

	public boolean isClasse_1_count()
	{
		return classe_1_count;
	}

	public boolean isClasse_2_avg()
	{
		return classe_2_avg;
	}

	public boolean isClasse_2_count()
	{
		return classe_2_count;
	}

	public boolean isClasse_3_avg()
	{
		return classe_3_avg;
	}

	public boolean isClasse_3_count()
	{
		return classe_3_count;
	}

	public boolean isClasse_4_avg()
	{
		return classe_4_avg;
	}

	public boolean isClasse_4_count()
	{
		return classe_4_count;
	}

	public boolean isClasse_5_avg()
	{
		return classe_5_avg;
	}

	public boolean isClasse_5_count()
	{
		return classe_5_count;
	}

	public boolean isClasse_6_avg()
	{
		return classe_6_avg;
	}

	public boolean isClasse_6_count()
	{
		return classe_6_count;
	}

	public boolean isClasse_7_avg()
	{
		return classe_7_avg;
	}

	public boolean isClasse_7_count()
	{
		return classe_7_count;
	}

	public boolean isClasse_8_avg()
	{
		return classe_8_avg;
	}

	public boolean isClasse_8_count()
	{
		return classe_8_count;
	}

	public boolean isClasse_9_avg()
	{
		return classe_9_avg;
	}

	public boolean isClasse_9_count()
	{
		return classe_9_count;
	}

	public int getNr_classes_count()
	{
		return nr_classes_count;
	}

	public int getNr_classes_avg()
	{
		return nr_classes_avg;
	}

}
